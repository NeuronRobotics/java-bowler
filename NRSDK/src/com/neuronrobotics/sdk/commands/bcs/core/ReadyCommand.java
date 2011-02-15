package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


public class ReadyCommand extends BowlerAbstractCommand {
	public ReadyCommand(int zone,int trace) {
		setMethod(BowlerMethod.STATUS);
		setOpCode("_rdy");
		getCallingDataStorage().add(zone);
		getCallingDataStorage().add(trace);
	}

}
