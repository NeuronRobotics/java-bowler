package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


public class ControlAllPIDCommand extends BowlerAbstractCommand {
	
	public ControlAllPIDCommand() {
		setOpCode("apid");
		setMethod(BowlerMethod.GET);
	}
	
	public ControlAllPIDCommand( int []setpoint) {
		setOpCode("apid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().addAs32(0);
		for(int i=0;i<setpoint.length;i++){
			getCallingDataStorage().addAs32(setpoint[i]);
		}
	}
	public ControlAllPIDCommand(int []setpoint,double seconds) {
		setOpCode("apid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().addAs32((int)(seconds*1000));
		for(int i=0;i<setpoint.length;i++){
			getCallingDataStorage().addAs32(setpoint[i]);
		}
	}

}
