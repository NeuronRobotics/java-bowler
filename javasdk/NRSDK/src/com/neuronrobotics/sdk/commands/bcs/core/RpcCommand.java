package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class RpcCommand extends BowlerAbstractCommand {

	public RpcCommand(int namespace){
		setOpCode("_rpc");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(namespace);
		getCallingDataStorage().add(0);// talk to the 0th RPC
	}

	public RpcCommand(int namespace,int rpc) {
		setOpCode("_rpc");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(namespace);
		getCallingDataStorage().add(rpc);
	}

}
