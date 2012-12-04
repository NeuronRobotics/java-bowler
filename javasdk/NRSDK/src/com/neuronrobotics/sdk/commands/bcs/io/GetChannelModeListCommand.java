package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class GetChannelModeListCommand extends BowlerAbstractCommand {
	public GetChannelModeListCommand(int channel){
		setOpCode("gcml");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(channel);
	}
}
