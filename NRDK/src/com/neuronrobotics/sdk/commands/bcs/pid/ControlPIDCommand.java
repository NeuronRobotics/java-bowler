package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


public class ControlPIDCommand extends BowlerAbstractCommand {
	
	public ControlPIDCommand(int group) {
		setOpCode("_pid");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(group);
	}
	
	public ControlPIDCommand(int group, int setpoint) {
		setOpCode("_pid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(setpoint);
		getCallingDataStorage().addAs32(0);
	}
	public ControlPIDCommand(int group, int setpoint,double seconds) {
		setOpCode("_pid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(setpoint);
		getCallingDataStorage().addAs32((int)(seconds*1000));
	}

}
