package com.neuronrobotics.sdk.commands.neuronrobotics.dyio;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class GetAllChannelValuesCommand extends BowlerAbstractCommand {
	/**
	 * 
	 * 
	 * @param channel
	 */
	public GetAllChannelValuesCommand() {
		setOpCode("gacv");
		setMethod(BowlerMethod.GET);
	}
}
