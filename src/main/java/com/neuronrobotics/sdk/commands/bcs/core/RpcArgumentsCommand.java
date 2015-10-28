package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class RpcArgumentsCommand.
 */
public class RpcArgumentsCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new rpc arguments command.
	 *
	 * @param namespace the namespace
	 * @param rpc the rpc
	 */
	public RpcArgumentsCommand(int namespace,int rpc) {
		setOpCode("args");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(namespace);
		getCallingDataStorage().add(rpc);
	}
}
