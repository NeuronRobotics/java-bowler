package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class GetDyIOChannelCountCommand.
 */
public class GetDyIOChannelCountCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new gets the dy io channel count command.
	 */
	public GetDyIOChannelCountCommand(){
		setOpCode("gchc");
		setMethod(BowlerMethod.GET);
	}
}
