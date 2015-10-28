package com.neuronrobotics.application.xmpp.GoogleChat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class GoogleChat.
 */
public class GoogleChat {
	
	/** The chat. */
	private Chat chat;
	
	/**
	 * Instantiates a new google chat.
	 *
	 * @param chat the chat
	 */
	GoogleChat(Chat chat) {
		this.chat=chat;
	}
	
	/**
	 * Send message.
	 *
	 * @param body the body
	 * @throws XMPPException the XMPP exception
	 */
	public void sendMessage(String body) throws XMPPException{
		Message msg = new Message(chat.getParticipant(), Message.Type.chat);
        msg.setBody(body);
        chat.sendMessage(msg);
        
	}
	
	/**
	 * Checks if is alive.
	 *
	 * @return true, if is alive
	 */
	public boolean isAlive(){
		if(chat!= null)
			return true;
		return false;
	}
	
	/**
	 * Adds the message listener.
	 *
	 * @param listener the listener
	 */
	public void addMessageListener(MessageListener listener){
		chat.addMessageListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s="";
		
		return s;
	}
}
