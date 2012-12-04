package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class GetChannelCountCommand extends BowlerAbstractCommand {
	public GetChannelCountCommand(){
		setOpCode("gchc");
		setMethod(BowlerMethod.GET);
	}
}
