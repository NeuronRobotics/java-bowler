package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class PDVelocityCommand extends BowlerAbstractCommand {
	public PDVelocityCommand(int group, int ticksPerSecond, double seconds) {
		setOpCode("_vpd");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(group);
		getCallingDataStorage().addAs32(ticksPerSecond);
		getCallingDataStorage().addAs32((int)(seconds*1000));
	}
}
