package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class GetPIDChannelCountCommand extends BowlerAbstractCommand {

	public GetPIDChannelCountCommand(){
		setOpCode("gpdc");
		setMethod(BowlerMethod.GET);
	}

}
