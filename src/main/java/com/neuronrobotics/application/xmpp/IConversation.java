package com.neuronrobotics.application.xmpp;

import org.jivesoftware.smack.Chat;

// TODO: Auto-generated Javadoc
/**
 * The Interface IConversation.
 */
public interface IConversation {
	
	/**
	 * On message.
	 *
	 * @param input the input
	 * @param chat the chat
	 * @param from the from
	 * @return the string
	 */
	public String onMessage(String input,Chat chat, String from);
}
