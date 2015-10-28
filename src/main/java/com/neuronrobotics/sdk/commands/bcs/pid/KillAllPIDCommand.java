package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class KillAllPIDCommand.
 */
public class KillAllPIDCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new kill all pid command.
	 */
	public KillAllPIDCommand (){
		setOpCode("kpid");
		setMethod(BowlerMethod.CRITICAL);
	}
}
