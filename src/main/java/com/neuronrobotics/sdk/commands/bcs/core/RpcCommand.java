package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class RpcCommand.
 */
public class RpcCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new rpc command.
	 *
	 * @param namespace the namespace
	 */
	public RpcCommand(int namespace){
		setOpCode("_rpc");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(namespace);
		getCallingDataStorage().add(0);// talk to the 0th RPC
	}

	/**
	 * Instantiates a new rpc command.
	 *
	 * @param namespace the namespace
	 * @param rpc the rpc
	 */
	public RpcCommand(int namespace,int rpc) {
		setOpCode("_rpc");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(namespace);
		getCallingDataStorage().add(rpc);
	}

}
