package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


// TODO: Auto-generated Javadoc
/**
 * The Class ReadyCommand.
 */
public class ReadyCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new ready command.
	 *
	 * @param zone the zone
	 * @param trace the trace
	 */
	public ReadyCommand(int zone,int trace) {
		setMethod(BowlerMethod.STATUS);
		setOpCode("_rdy");
		getCallingDataStorage().add(zone);
		getCallingDataStorage().add(trace);
	}

}
