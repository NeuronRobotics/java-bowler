package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class PDVelocityCommand.
 */
public class PDVelocityCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new PD velocity command.
	 *
	 * @param group the group
	 * @param ticksPerSecond the ticks per second
	 * @param seconds the seconds
	 */
	public PDVelocityCommand(int group, int ticksPerSecond, double seconds) {
		setOpCode("_vpd");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(ticksPerSecond);
		getCallingDataStorage().addAs32((int)(seconds*1000));
	}
}
