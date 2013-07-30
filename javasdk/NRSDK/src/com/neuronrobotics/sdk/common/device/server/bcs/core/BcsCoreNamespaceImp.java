package com.neuronrobotics.sdk.common.device.server.bcs.core;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.NamespaceCommand;
import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;

public class BcsCoreNamespaceImp extends BowlerAbstractDeviceServerNamespace{

	
	private BowlerAbstractServer server;

	public BcsCoreNamespaceImp(BowlerAbstractServer server){
		this.server = server;
		String core ="bcs.core.*;;"; 
		setNamespace(core );
		rpc.add(new RpcEncapsulation(0, 
				core , 
				"_png", 
				BowlerMethod.GET, 
				new BowlerDataType[]{}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{}));
		
		rpc.add(new RpcEncapsulation(0, 
				core , 
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
	public BowlerDatagram process(BowlerDatagram data) {
		//System.out.println("Rx >> "+data);
		if(data== null)
			return null;
		if(data.getRPC().contains("_png")){
			return BowlerDatagramFactory.build(	new MACAddress("74:F7:26:01:01:01"), 
												new PingCommand());
		}if(data.getRPC().contains("_nms")){
			int index=0;
			if(data.getData().size()==1){
				index = data.getData().getUnsigned(0);
			}
			return BowlerDatagramFactory.build(	new MACAddress("74:F7:26:01:01:01"),
												new NamespaceCommand(index,server.getNamespaces().get(index).getNamespace()));
		}
		return null;
	}

}
