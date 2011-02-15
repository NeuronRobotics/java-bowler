package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


public class ResetPIDCommand extends BowlerAbstractCommand {
	public ResetPIDCommand(char group) {
		setOpCode("rpid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
	}
	public ResetPIDCommand(char group,int valueToSetCurrentTo) {
		setOpCode("rpid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(valueToSetCurrentTo);
	}
}
