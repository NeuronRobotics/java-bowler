package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class RpcArgumentsCommand extends BowlerAbstractCommand {

	public RpcArgumentsCommand(int namespace,int rpc) {
		setOpCode("args");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(namespace);
		getCallingDataStorage().add(rpc);
	}
}
