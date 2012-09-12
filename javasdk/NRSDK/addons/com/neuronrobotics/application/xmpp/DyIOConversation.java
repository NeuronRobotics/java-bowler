package com.neuronrobotics.application.xmpp;

import java.util.ArrayList;
import java.util.EnumSet;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;


public class DyIOConversation implements IConversation, MessageListener, IChannelEventListener {
	private ArrayList<ChatAsyncListener> listeners = new ArrayList<ChatAsyncListener>();
	
	
	public void processMessage(Chat chat, Message message) {
		Message msg = new Message(message.getFrom(), Message.Type.chat);
	    if(message.getType().equals(Message.Type.chat) && message.getBody() != null) {
	        System.out.println("Received: " + message.getBody()+" from: "+message.getFrom());
	        try {
	        	msg.setBody(onMessage(message.getBody(),chat, message.getFrom()));
	        	System.out.println("Sending: "+msg.getBody());
	            chat.sendMessage(msg);
	        } catch (XMPPException ex) {
	            ex.printStackTrace();
	            System.out.println("Failed to send message");
	        }
	    } else {
	        System.out.println("I got a message I didn't understand\n\n"+message.getType());
	    }
	}

	@Override
	public String onMessage(String input,Chat chat,String from) {
		String [] packet = input.split("\\ ");
		if(packet[0].toLowerCase().contains("ping")){
			return "ping: \n"+DyIORegestry.get().ping();
		}else if(packet[0].toLowerCase().contains("state")){
			return "state: \n"+DyIORegestry.get().toString();
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
				if(found && DyIORegestry.get().getChannel(port).canBeMode(m)){
					DyIORegestry.get().setMode(port, m);
					return "setMode "+port+" "+m.toSlug();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return "error: Mode not settible on channel #"+packet[1]+" mode options are:\n"+options;
		}else if(packet[0].toLowerCase().contains("setvalue")){
			int port = Integer.parseInt(packet[1]);
			int value = Integer.parseInt(packet[2]);
			DyIORegestry.get().getChannel(port).setValue(value);
			return "setValue "+port+" "+value;
		}else if(packet[0].toLowerCase().contains("getvalue")){
			int port = Integer.parseInt(packet[1]);
			int value = DyIORegestry.get().getChannel(port).getValue();
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
			DyIORegestry.get().getChannel(port).setAsync(true);
			DyIORegestry.get().getChannel(port).configAdvancedAsyncNotEqual(rate);
			DyIORegestry.get().getChannel(port).addChannelEventListener( getListener(chat, from));
			return "async "+port+" "+rate;
		}
		else if(packet[0].toLowerCase().contains("removeasync")){
			int port = Integer.parseInt(packet[1]);
			DyIORegestry.get().getChannel(port).removeChannelEventListener( getListener(chat, from));
			return "async removed "+port+" ";
		}
		else if(packet[0].toLowerCase().contains("reset")){
			for (int i=0;i<24;i++){
				DyIORegestry.get().getChannel(i).removeAllChannelEventListeners();
			}
			return "system reset!";
		}else{
			return help();
		}
	}
	
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
	
	private class ChatAsyncListener implements IChannelEventListener{
		private Chat chat;
		private String from;
		public ChatAsyncListener(Chat c,String from){
			setChat(c);
			this.setFrom(from);
		}
		public Chat getChat() {
			return chat;
		}
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
		public void setChat(Chat chat) {
			this.chat = chat;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getFrom() {
			return from;
		}
	}
	
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

	@Override
	public void onChannelEvent(DyIOChannelEvent e) {
		// TODO Auto-generated method stub
		
	}

}
