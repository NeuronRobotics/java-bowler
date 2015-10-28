package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class ErrorCommand.
 */
public class ErrorCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new error command.
	 *
	 * @param zone the zone
	 * @param trace the trace
	 */
	public ErrorCommand(int zone,int trace) {
		setMethod(BowlerMethod.STATUS);
		setOpCode("_err");
		getCallingDataStorage().add(zone);
		getCallingDataStorage().add(trace);
	}
}
