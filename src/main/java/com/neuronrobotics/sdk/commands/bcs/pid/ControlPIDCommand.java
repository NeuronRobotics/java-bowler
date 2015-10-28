package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


// TODO: Auto-generated Javadoc
/**
 * The Class ControlPIDCommand.
 */
public class ControlPIDCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new control pid command.
	 *
	 * @param group the group
	 */
	public ControlPIDCommand(int group) {
		setOpCode("_pid");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(group);
	}
	
	/**
	 * Instantiates a new control pid command.
	 *
	 * @param group the group
	 * @param setpoint the setpoint
	 */
	public ControlPIDCommand(int group, int setpoint) {
		setOpCode("_pid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(setpoint);
		getCallingDataStorage().addAs32(0);
	}
	
	/**
	 * Instantiates a new control pid command.
	 *
	 * @param group the group
	 * @param setpoint the setpoint
	 * @param seconds the seconds
	 */
	public ControlPIDCommand(int group, int setpoint,double seconds) {
		setOpCode("_pid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(setpoint);
		getCallingDataStorage().addAs32((int)(seconds*1000));
	}

}
