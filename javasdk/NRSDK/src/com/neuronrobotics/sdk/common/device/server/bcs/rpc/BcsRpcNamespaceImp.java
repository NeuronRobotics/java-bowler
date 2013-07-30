package com.neuronrobotics.sdk.common.device.server.bcs.rpc;

import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;

public class BcsRpcNamespaceImp extends BowlerAbstractDeviceServerNamespace{
	
	private BowlerAbstractServer server;
	
	public BcsRpcNamespaceImp(BowlerAbstractServer server){
		this.server = server;
		String bcsRpc ="bcs.rpc.*;;"; 
		setNamespace(bcsRpc );
		
		rpc.add(new RpcEncapsulation(1, 
				bcsRpc , 
				"_rpc", 
				BowlerMethod.GET, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08}, //rpc index
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08,//rpc index
										BowlerDataType.I08,// number of RPC's
										BowlerDataType.ASCII}));//RPC
		
		rpc.add(new RpcEncapsulation(1, 
				bcsRpc , 
				"args", 
				BowlerMethod.GET, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08}, //rpc index
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08,//rpc index
										BowlerDataType.I08,//Downstream method
										BowlerDataType.STR,//downstream arguments
										BowlerDataType.I08,//upstream method
										BowlerDataType.STR}));//upstream arguments
		
	}

	@Override
	public BowlerDatagram process(BowlerDatagram data) {
		//System.out.println("Rx >> "+data);
		if(data== null)
			return null;
		if(data.getRPC().contains("_rpc")){
			int ns = data.getData().getUnsigned(0);
			int rpc = data.getData().getUnsigned(1);
			
			return BowlerDatagramFactory.build(	new MACAddress("74:F7:26:01:01:01"), 
												new BcsRpcCommand(	ns,
																	rpc,
																	server.getNamespaces().size(),
																	server.getNamespaces().get(ns).getRpcList().get(rpc).getRpc()));
		}if(data.getRPC().contains("args")){
			int ns = data.getData().getUnsigned(0);
			int rpc = data.getData().getUnsigned(1);
			RpcEncapsulation myRpc = server.getNamespaces().get(ns).getRpcList().get(rpc);
			return BowlerDatagramFactory.build(	new MACAddress("74:F7:26:01:01:01"),
												new BcsRpcArgsCommand(	ns,
																		rpc,
																		myRpc.getDownstreamMethod(),
																		myRpc.getDownstreamArguments(),
																		myRpc.getUpStreamMethod(),
																		myRpc.getUpstreamArguments()
																		));
		}
		return null;
	}

	

}
