package com.neuronrobotics.application.xmpp;

import com.neuronrobotics.application.xmpp.GoogleChat.IChatLog;

//  Auto-generated Javadoc
/**
 * A factory for creating DyIOConversation objects.
 */
public class DyIOConversationFactory implements IConversationFactory {

	/** The log. */
	private IChatLog log;

	/**
	 * Instantiates a new dy io conversation factory.
	 *
	 * @param mine
	 *            the mine
	 */
	public DyIOConversationFactory(IChatLog mine) {
		log = mine;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.neuronrobotics.application.xmpp.IConversationFactory#getConversation()
	 */
	@Override
	public IConversation getConversation() {
		com.neuronrobotics.sdk.common.Log.error("Getting DyIO conversation");
		return new DyIOConversation(log);
	}
}
