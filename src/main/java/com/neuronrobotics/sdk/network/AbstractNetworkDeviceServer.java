package com.neuronrobotics.sdk.network;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;
import com.neuronrobotics.sdk.common.device.server.BowlerDeviceReServerNamespace;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractNetworkDeviceServer.
 */
public class AbstractNetworkDeviceServer  extends BowlerAbstractServer{
	
	/** The gen. */
	private GenericDevice gen;
	
	/** The namespaces. */
	ArrayList<String> namespaces;
	
	/**
	 * Instantiates a new abstract network device server.
	 *
	 * @param device the device
	 * @param useAsync the use async
	 * @param serverConnection the server connection
	 */
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
