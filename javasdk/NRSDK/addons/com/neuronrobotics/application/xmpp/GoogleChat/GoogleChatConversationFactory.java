package com.neuronrobotics.application.xmpp.GoogleChat;

import com.neuronrobotics.application.xmpp.IConversation;
import com.neuronrobotics.application.xmpp.IConversationFactory;


public class GoogleChatConversationFactory implements  IConversationFactory {
	
	@Override
	public IConversation getConversation() {
		return new GoogleChatConversation();
	}

}
