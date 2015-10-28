package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class AsyncCommand.
 */
@Deprecated
public class AsyncCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new async command.
	 *
	 * @param channel the channel
	 */
	public AsyncCommand(int channel) {
		setOpCode("asyn");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(channel);
	}
	
	/**
	 * Instantiates a new async command.
	 *
	 * @param channel the channel
	 * @param isAsync the is async
	 */
	public AsyncCommand(int channel,boolean isAsync) {
		setOpCode("asyn");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add((isAsync?1:0));
		
	}
	
}
