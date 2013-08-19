package com.neuronrobotics.sdk.network;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.ISynchronousDatagramListener;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;
import com.neuronrobotics.sdk.common.device.server.BowlerDeviceReServerNamespace;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;

public class AbstractNetworkDeviceServer  extends BowlerAbstractServer{
	private GenericDevice gen;
	ArrayList<String> namespaces;
	
	public AbstractNetworkDeviceServer(GenericDevice device,boolean useAsync,BowlerAbstractConnection serverConnection) {
		super(device.getAddress());
		gen = device;
		namespaces = gen.getNamespaces();
		for(int i=0;i<namespaces.size();i++){
			BowlerAbstractDeviceServerNamespace ns = new BowlerDeviceReServerNamespace(device.getConnection(), this,useAsync,i,namespaces.get(i), gen);
			addBowlerDeviceServerNamespace(ns);
		}
		addServer(serverConnection);
	}
		
}
