package com.neuronrobotics.sdk.network;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractServer;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDeviceReServerNamespace;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.ISynchronousDatagramListener;

public class AbstractUdpDeviceServer  extends BowlerAbstractServer implements ISynchronousDatagramListener {
	BowlerDeviceReServerNamespace devServer;
	
	public AbstractUdpDeviceServer(BowlerAbstractConnection device,boolean useAsync) {
		devServer = new BowlerDeviceReServerNamespace(device, this,useAsync);
		addBowlerDeviceServerNamespace(devServer);
		
		setServer(new BowlerUDPServer(1865));
		getServer().connect();
		getServer().addSynchronousDatagramListener(this);
	}
	
	
	
}
