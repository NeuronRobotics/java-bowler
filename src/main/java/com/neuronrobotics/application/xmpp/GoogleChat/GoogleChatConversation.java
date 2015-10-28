package com.neuronrobotics.application.xmpp.GoogleChat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.neuronrobotics.application.xmpp.IConversation;


// TODO: Auto-generated Javadoc
/**
 * The Class GoogleChatConversation.
 */
public class GoogleChatConversation implements  MessageListener,IConversation{
	
	/** The num conversations. */
	private static int numConversations = 0;
	
	/** The my index. */
	private int myIndex = 0;
	
	/**
	 * Instantiates a new google chat conversation.
	 */
	public GoogleChatConversation(){
		 myIndex = numConversations++;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.application.xmpp.IConversation#onMessage(java.lang.String, org.jivesoftware.smack.Chat, java.lang.String)
	 */
	@Override
	public String onMessage(String input,Chat chat, String from) {
		
		return "I am Artillect bot index: "+myIndex+". You said: " + input;
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smack.MessageListener#processMessage(org.jivesoftware.smack.Chat, org.jivesoftware.smack.packet.Message)
	 */
	public void processMessage(Chat chat, Message message) {
		Message msg = new Message(message.getFrom(), Message.Type.chat);
	    if(message.getType().equals(Message.Type.chat) && message.getBody() != null) {
	        System.out.println("Received: " + message.getBody());
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

}
