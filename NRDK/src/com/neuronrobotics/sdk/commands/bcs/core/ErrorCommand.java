package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class ErrorCommand extends BowlerAbstractCommand {
	public ErrorCommand(int zone,int trace) {
		setMethod(BowlerMethod.STATUS);
		setOpCode("_err");
		getCallingDataStorage().add(zone);
		getCallingDataStorage().add(trace);
	}
}
