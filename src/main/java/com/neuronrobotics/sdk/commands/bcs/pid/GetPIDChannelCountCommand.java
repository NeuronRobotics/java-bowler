package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class GetPIDChannelCountCommand.
 */
public class GetPIDChannelCountCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new gets the pid channel count command.
	 */
	public GetPIDChannelCountCommand(){
		setOpCode("gpdc");
		setMethod(BowlerMethod.GET);
	}

}
