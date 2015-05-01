package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class KillAllPIDCommand extends BowlerAbstractCommand {
	public KillAllPIDCommand (){
		setOpCode("kpid");
		setMethod(BowlerMethod.CRITICAL);
	}
}
