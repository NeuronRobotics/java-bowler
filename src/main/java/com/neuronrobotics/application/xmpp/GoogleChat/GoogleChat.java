package com.neuronrobotics.application.xmpp.GoogleChat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class GoogleChat {
	private Chat chat;
	GoogleChat(Chat chat) {
		this.chat=chat;
	}
	public void sendMessage(String body) throws XMPPException{
		Message msg = new Message(chat.getParticipant(), Message.Type.chat);
        msg.setBody(body);
        chat.sendMessage(msg);
        
	}
	public boolean isAlive(){
		if(chat!= null)
			return true;
		return false;
	}
	
	public void addMessageListener(MessageListener listener){
		chat.addMessageListener(listener);
	}
	
	public String toString(){
		String s="";
		
		return s;
	}
}
