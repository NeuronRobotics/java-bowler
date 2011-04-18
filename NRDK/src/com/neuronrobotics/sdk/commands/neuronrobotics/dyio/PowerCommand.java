package com.neuronrobotics.sdk.commands.neuronrobotics.dyio;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class PowerCommand extends BowlerAbstractCommand {
	/**
	 * 
	 * 
	 * @param channel
	 */
	public PowerCommand() {
		setOpCode("_pwr");
		setMethod(BowlerMethod.GET);
	}
}
