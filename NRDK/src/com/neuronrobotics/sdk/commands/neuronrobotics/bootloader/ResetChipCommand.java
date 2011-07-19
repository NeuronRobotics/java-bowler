package com.neuronrobotics.sdk.commands.neuronrobotics.bootloader;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


public class ResetChipCommand extends BowlerAbstractCommand {
	
	public ResetChipCommand() {
		setOpCode("rest");
		setMethod(BowlerMethod.CRITICAL);
	}
}
