package com.neuronrobotics.sdk.common.device.server.bcs.rpc;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class BcsRpcCommand.
 */
public class BcsRpcCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new bcs rpc command.
	 *
	 * @param ns the ns
	 * @param rpc the rpc
	 * @param size the size
	 * @param rpc2 the rpc2
	 */
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
