package com.neuronrobotics.sdk.common.device.server.bcs.rpc;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class BcsRpcArgsCommand extends BowlerAbstractCommand {

	public BcsRpcArgsCommand(int ns, int rpc, BowlerMethod downstreamMethod,
			BowlerDataType[] downstreamArguments, BowlerMethod upStreamMethod,
			BowlerDataType[] upstreamArguments) {
		setOpCode("_rpc");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(ns);
		getCallingDataStorage().add(rpc);
		getCallingDataStorage().add(downstreamMethod);
		
		getCallingDataStorage().add(downstreamArguments.length);
		for(int i=0;i< downstreamArguments.length;i++){
			getCallingDataStorage().add(downstreamArguments[i]);
		}
		
		getCallingDataStorage().add(upstreamArguments.length);
		for(int i=0;i< upstreamArguments.length;i++){
			getCallingDataStorage().add(upstreamArguments[i]);
		}
		
	}


}
