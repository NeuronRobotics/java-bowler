package com.neuronrobotics.application.xmpp;

import com.neuronrobotics.application.xmpp.GoogleChat.IChatLog;


public class DyIOConversationFactory implements  IConversationFactory{
	private IChatLog log;
	public DyIOConversationFactory(IChatLog mine) {
		log=mine;
	}

	@Override
	public IConversation getConversation() {
		System.out.println("Getting DyIO conversation");
		return new DyIOConversation(log);
	}
}
