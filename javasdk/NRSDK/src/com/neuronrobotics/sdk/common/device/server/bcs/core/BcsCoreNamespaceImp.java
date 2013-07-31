package com.neuronrobotics.sdk.common.device.server.bcs.core;

import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;

public class BcsCoreNamespaceImp extends BowlerAbstractDeviceServerNamespace{

	
	private BowlerAbstractServer server;

	public BcsCoreNamespaceImp(BowlerAbstractServer server, MACAddress mac){
		super( mac ,"bcs.core.*;;");
		this.server = server;
		rpc.add(new RpcEncapsulation(0, 
				getNamespace() , 
				"_png", 
				BowlerMethod.GET, 
				new BowlerDataType[]{}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{}));
		
		rpc.add(new RpcEncapsulation(0, 
				getNamespace() , 
				"_nms", 
				BowlerMethod.GET, 
				new BowlerDataType[]{BowlerDataType.I08}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{BowlerDataType.ASCII,BowlerDataType.I08}));
	}
	/**
	 * This is the Core namespace processor
	 */

	@Override
	public Object[] process(Object[] data, String rpc, BowlerMethod method) {
		//System.out.println("Rx >> "+data);
		if(data== null)
			return new Object[0];
		if(rpc.contains("_png")){
			Object[] back = new Object[0];
			return back;
		}if(rpc.contains("_nms")){
			int nsIndex = 	(Integer) data[0];
			Object[] back = new Object[2];
			
			back[0] = server.getNamespaces().get(nsIndex).getNamespace();
			back[1] = server.getNamespaces().size();

			return back;
		}
		return new Object[0];
	}

}
