package com.neuronrobotics.sdk.commands.neuronrobotics.dyio;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class GetAllChannelValuesCommand.
 */
public class GetAllChannelValuesCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new gets the all channel values command.
	 */
	public GetAllChannelValuesCommand() {
		setOpCode("gacv");
		setMethod(BowlerMethod.GET);
	}
}
