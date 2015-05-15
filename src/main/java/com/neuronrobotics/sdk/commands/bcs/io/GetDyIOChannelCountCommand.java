package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class GetDyIOChannelCountCommand extends BowlerAbstractCommand {
	public GetDyIOChannelCountCommand(){
		setOpCode("gchc");
		setMethod(BowlerMethod.GET);
	}
}
