package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


public class AsyncCommand extends BowlerAbstractCommand {
	public AsyncCommand(int channel) {
		setOpCode("asyn");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(channel);
	}
	public AsyncCommand(int channel,boolean isAsync) {
		setOpCode("asyn");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add((isAsync?1:0));
		
	}
	
}
