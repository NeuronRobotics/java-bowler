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
	/**
	 * This method will disable the brownout detect for the DyIO
	 * 
	 * @param channel
	 */
	public PowerCommand(boolean disableBrownOutDetect) {
		setOpCode("_pwr");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(disableBrownOutDetect?1:0);
	}
}
