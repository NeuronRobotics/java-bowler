package com.neuronrobotics.application.xmpp;


public class DyIOConversationFactory implements  IConversationFactory{

	@Override
	public IConversation getConversation() {
		System.out.println("Getting DyIO conversation");
		return new DyIOConversation();
	}

}
