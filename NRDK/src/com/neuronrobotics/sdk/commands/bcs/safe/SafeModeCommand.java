package com.neuronrobotics.sdk.commands.bcs.safe;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class SafeModeCommand extends BowlerAbstractCommand {

	public SafeModeCommand(){
		setOpCode("safe");
		setMethod(BowlerMethod.GET);
	}
	public SafeModeCommand(boolean enable,int msTime){
		setOpCode("safe");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(enable?1:0);
		getCallingDataStorage().addAs16(msTime);
	}
}
