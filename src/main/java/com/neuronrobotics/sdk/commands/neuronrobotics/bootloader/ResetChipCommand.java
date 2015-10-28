package com.neuronrobotics.sdk.commands.neuronrobotics.bootloader;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


// TODO: Auto-generated Javadoc
/**
 * The Class ResetChipCommand.
 */
public class ResetChipCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new reset chip command.
	 */
	public ResetChipCommand() {
		setOpCode("rest");
		setMethod(BowlerMethod.CRITICAL);
	}
}
