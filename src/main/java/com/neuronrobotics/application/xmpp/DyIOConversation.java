package com.neuronrobotics.application.xmpp;

import java.util.ArrayList;
import java.util.EnumSet;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.neuronrobotics.application.xmpp.GoogleChat.IChatLog;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;


// TODO: Auto-generated Javadoc
/**
 * The Class DyIOConversation.
 */
public class DyIOConversation implements IConversation, MessageListener, IChannelEventListener {
	
	/** The listeners. */
	private ArrayList<ChatAsyncListener> listeners = new ArrayList<ChatAsyncListener>();
	
	/** The log. */
	private IChatLog log;
	
	/**
	 * Instantiates a new dy io conversation.
	 *
	 * @param log the log
	 */
	public DyIOConversation(IChatLog log) {
		this.log=log;
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smack.MessageListener#processMessage(org.jivesoftware.smack.Chat, org.jivesoftware.smack.packet.Message)
	 */
	public void processMessage(Chat chat, Message message) {
		Message msg = new Message(message.getFrom(), Message.Type.chat);
	    if(message.getType().equals(Message.Type.chat) && message.getBody() != null) {
	        System.out.println("Received: " + message.getBody()+" from: "+message.getFrom());
	        if(log!=null){
	        	log.onLogEvent(""+message.getFrom()+">> "+ message.getBody());
	        }
	        try {
	        	String ret =onMessage(message.getBody(),chat, message.getFrom());
	        	msg.setBody(ret);
	        	System.out.println("Sending: "+msg.getBody());
	        	 if(log!=null){
	 	        	log.onLogEvent(""+message.getFrom()+"<< "+ ret);
	 	        }
	            chat.sendMessage(msg);
	        } catch (XMPPException ex) {
	            ex.printStackTrace();
	            System.out.println("Failed to send message");
	        }
	    } else {
	        System.out.println("I got a message I didn't understand\n\n"+message.getType());
	    }
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.application.xmpp.IConversation#onMessage(java.lang.String, org.jivesoftware.smack.Chat, java.lang.String)
	 */
	@Override
	public String onMessage(String input,Chat chat,String from) {
		String [] packet = input.split("\\ ");
		if(packet[0].toLowerCase().contains("ping")){
			return "ping: \n"+((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).ping();
		}else if(packet[0].toLowerCase().contains("state")){
			return "state: \n"+((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).toString();
		}else if(packet[0].toLowerCase().contains("setmode")){
			DyIOChannelMode m = DyIOChannelMode.DIGITAL_IN;
			boolean found = false;
			String options = "";
			for(DyIOChannelMode cm : EnumSet.allOf(DyIOChannelMode.class)) {
				options+=cm.toSlug()+"\n";
				if(packet[2].toLowerCase().equals(cm.toSlug())){
					m=cm;
					found = true;
				}
			}
			try{
				int port = Integer.parseInt(packet[1]);
				if(found && ((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(port).canBeMode(m)){
					((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).setMode(port, m);
					return "setMode "+port+" "+m.toSlug();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return "error: Mode not settible on channel #"+packet[1]+" mode options are:\n"+options;
		}else if(packet[0].toLowerCase().contains("setvalue")){
			int port = Integer.parseInt(packet[1]);
			int value = Integer.parseInt(packet[2]);
			((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(port).setValue(value);
			return "setValue "+port+" "+value;
		}else if(packet[0].toLowerCase().contains("getvalue")){
			int port = Integer.parseInt(packet[1]);
			int value = ((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(port).getValue();
			return "getValue "+port+" "+value;
		}else if(packet[0].toLowerCase().contains("addasync")){
			int port = Integer.parseInt(packet[1]);
			int rate = 500;
			try{
				rate = Integer.parseInt(packet[2]);
			}catch (Exception ex){
				rate = 500;
			}
			if(rate < 500)
				rate = 500;
			((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(port).setAsync(true);
			((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(port).configAdvancedAsyncNotEqual(rate);
			((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(port).addChannelEventListener( getListener(chat, from));
			return "async "+port+" "+rate;
		}
		else if(packet[0].toLowerCase().contains("removeasync")){
			int port = Integer.parseInt(packet[1]);
			((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(port).removeChannelEventListener( getListener(chat, from));
			return "async removed "+port+" ";
		}
		else if(packet[0].toLowerCase().contains("reset")){
			for (int i=0;i<24;i++){
				((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(i).removeAllChannelEventListeners();
			}
			return "system reset!";
		}else{
			return help();
		}
	}
	
	/**
	 * Gets the listener.
	 *
	 * @param c the c
	 * @param from the from
	 * @return the listener
	 */
	private ChatAsyncListener getListener(Chat c,String from){
		ChatAsyncListener back=null;
		for(ChatAsyncListener l:listeners ){
			if(l.getFrom().equals(from) && l.getChat()==c){
				back = l;
				System.out.println("Found old listener");
			}
		}
		if(back == null){
			System.out.println("Adding new listener");
			back = new ChatAsyncListener(c, from);
			listeners.add(back);
		}
		return back;
	}
	
	/**
	 * The listener interface for receiving chatAsync events.
	 * The class that is interested in processing a chatAsync
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's  addChatAsyncListener  method. When
	 * the chatAsync event occurs, that object's appropriate
	 * method is invoked.
	 *
	 */
	private class ChatAsyncListener implements IChannelEventListener{
		
		/** The chat. */
		private Chat chat;
		
		/** The from. */
		private String from;
		
		/**
		 * Instantiates a new chat async listener.
		 *
		 * @param c the c
		 * @param from the from
		 */
		public ChatAsyncListener(Chat c,String from){
			setChat(c);
			this.setFrom(from);
		}
		
		/**
		 * Gets the chat.
		 *
		 * @return the chat
		 */
		public Chat getChat() {
			return chat;
		}
		
		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.dyio.IChannelEventListener#onChannelEvent(com.neuronrobotics.sdk.dyio.DyIOChannelEvent)
		 */
		@Override
		public void onChannelEvent(DyIOChannelEvent e) {
			Message msg = new Message(getFrom(), Message.Type.chat);
			String body = "asyncData "+e.getChannel().getChannelNumber()+" "+e.getValue();
			msg.setBody(body);
			System.err.println("async: "+msg.getBody());
            try {
				chat.sendMessage(msg);
			} catch (XMPPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		/**
		 * Sets the chat.
		 *
		 * @param chat the new chat
		 */
		public void setChat(Chat chat) {
			this.chat = chat;
		}
		
		/**
		 * Sets the from.
		 *
		 * @param from the new from
		 */
		public void setFrom(String from) {
			this.from = from;
		}
		
		/**
		 * Gets the from.
		 *
		 * @return the from
		 */
		public String getFrom() {
			return from;
		}
	}
	
	/**
	 * Help.
	 *
	 * @return the string
	 */
	private String help(){
		String s="This is a REPL loop for talking to the DyIO\n" +
				"Commands use a command name, which DyIO port your connected to, and a value\n" +
				"The 3 fields are seperated by a single space charrector\n" +
				"The name is a string, and the 2 data fields are integers\n" +
				"If a field is unused, it will be displayed as 'none'\n" +
				"Commands are: \n" ;
		s+="ping \tnone \tnone :returns ping message\n";
		s+="state \tnone \tnone :returns state information\n";
		s+="reset \tnone \tnone :returns none Removes all async listeners\n";
		s+="setMode \t(int)channel \t(String)mode :returns the mode if successful, 'error' if not sucessful\n";
		s+="setValue \t(int)channel \t(int)value :returns the value if successful, 'error' if not sucessful\n";
		s+="getValue \t(int)channel \tnone :returns (int)value\n";
		s+="addAsync \t(int)channel \t(int)update rate in Ms :returns (int)value: Async of any incoming data\n";
		s+="removeAsync \t(int)channel \tnone :returns none Removes one async listener\n";
		return s;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.IChannelEventListener#onChannelEvent(com.neuronrobotics.sdk.dyio.DyIOChannelEvent)
	 */
	@Override
	public void onChannelEvent(DyIOChannelEvent e) {
		// TODO Auto-generated method stub
		
	}

}
