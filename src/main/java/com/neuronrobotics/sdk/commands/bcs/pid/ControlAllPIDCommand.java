package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


// TODO: Auto-generated Javadoc
/**
 * The Class ControlAllPIDCommand.
 */
public class ControlAllPIDCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new control all pid command.
	 */
	public ControlAllPIDCommand() {
		setOpCode("apid");
		setMethod(BowlerMethod.GET);

	}
	
	/**
	 * Instantiates a new control all pid command.
	 *
	 * @param setpoint the setpoint
	 */
	public ControlAllPIDCommand( int []setpoint) {
		setOpCode("apid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().addAs32(0);
		//getCallingDataStorage().add(setpoint.length);
		for(int i=0;i<setpoint.length;i++){
			getCallingDataStorage().addAs32(setpoint[i]);
		}
	}
	
	/**
	 * Instantiates a new control all pid command.
	 *
	 * @param setpoint the setpoint
	 * @param seconds the seconds
	 */
	public ControlAllPIDCommand(int []setpoint,double seconds) {
		setOpCode("apid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().addAs32((int)(seconds*1000));
		//getCallingDataStorage().add(setpoint.length);
		for(int i=0;i<setpoint.length;i++){
			getCallingDataStorage().addAs32(setpoint[i]);
		}
	}

}
