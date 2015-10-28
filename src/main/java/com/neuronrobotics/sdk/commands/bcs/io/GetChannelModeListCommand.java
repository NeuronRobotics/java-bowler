package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class GetChannelModeListCommand.
 */
public class GetChannelModeListCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new gets the channel mode list command.
	 *
	 * @param channel the channel
	 */
	public GetChannelModeListCommand(int channel){
		setOpCode("gcml");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(channel);
	}
}
