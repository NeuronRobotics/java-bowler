package com.neuronrobotics.application.xmpp.GoogleChat;

import com.neuronrobotics.application.xmpp.IConversation;
import com.neuronrobotics.application.xmpp.IConversationFactory;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating GoogleChatConversation objects.
 */
public class GoogleChatConversationFactory implements  IConversationFactory {
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.application.xmpp.IConversationFactory#getConversation()
	 */
	@Override
	public IConversation getConversation() {
		return new GoogleChatConversation();
	}

}
