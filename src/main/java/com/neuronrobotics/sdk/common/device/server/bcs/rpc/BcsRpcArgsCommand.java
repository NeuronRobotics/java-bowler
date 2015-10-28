package com.neuronrobotics.sdk.common.device.server.bcs.rpc;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class BcsRpcArgsCommand.
 */
public class BcsRpcArgsCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new bcs rpc args command.
	 *
	 * @param ns the ns
	 * @param rpc the rpc
	 * @param downstreamMethod the downstream method
	 * @param downstreamArguments the downstream arguments
	 * @param upStreamMethod the up stream method
	 * @param upstreamArguments the upstream arguments
	 */
	public BcsRpcArgsCommand(int ns, int rpc, BowlerMethod downstreamMethod,
			BowlerDataType[] downstreamArguments, BowlerMethod upStreamMethod,
			BowlerDataType[] upstreamArguments) {
		setOpCode("args");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(ns);
		getCallingDataStorage().add(rpc);
		
		getCallingDataStorage().add(downstreamMethod);
		
		getCallingDataStorage().add(downstreamArguments.length);
		for(int i=0;i< downstreamArguments.length;i++){
			getCallingDataStorage().add(downstreamArguments[i]);
		}
		
		getCallingDataStorage().add(upStreamMethod);
		
		getCallingDataStorage().add(upstreamArguments.length);
		for(int i=0;i< upstreamArguments.length;i++){
			getCallingDataStorage().add(upstreamArguments[i]);
		}
		
	}


}
