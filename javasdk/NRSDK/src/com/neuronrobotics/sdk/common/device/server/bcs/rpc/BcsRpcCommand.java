package com.neuronrobotics.sdk.common.device.server.bcs.rpc;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class BcsRpcCommand extends BowlerAbstractCommand {

	public BcsRpcCommand(int ns, int rpc, int size, String rpc2) {
		setOpCode("_rpc");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(ns);
		getCallingDataStorage().add(rpc);
		getCallingDataStorage().add(size);
		getCallingDataStorage().add(rpc2);
		getCallingDataStorage().add(0);
	}
}
