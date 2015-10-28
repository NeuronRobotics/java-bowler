package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


// TODO: Auto-generated Javadoc
/**
 * The Class ResetPIDCommand.
 */
public class ResetPIDCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new reset pid command.
	 *
	 * @param group the group
	 */
	public ResetPIDCommand(char group) {
		setOpCode("rpid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
	}
	
	/**
	 * Instantiates a new reset pid command.
	 *
	 * @param group the group
	 * @param valueToSetCurrentTo the value to set current to
	 */
	public ResetPIDCommand(char group,int valueToSetCurrentTo) {
		setOpCode("rpid");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(valueToSetCurrentTo);
	}
}
