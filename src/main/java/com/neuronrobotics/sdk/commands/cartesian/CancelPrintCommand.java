package com.neuronrobotics.sdk.commands.cartesian;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class CancelPrintCommand.
 */
public class CancelPrintCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new cancel print command.
	 */
	public CancelPrintCommand() {
		setOpCode("prcl");
		setMethod(BowlerMethod.POST);
	}
}
