package com.neuronrobotics.application.xmpp;

import org.jivesoftware.smack.Chat;

public interface IConversation {
	public String onMessage(String input,Chat chat, String from);
}
