package com.neuronrobotics.sdk.common.device.server;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;

public class BowlerDeviceReServerNamespace extends BowlerAbstractDeviceServerNamespace{
	
	private BowlerAbstractConnection device;
	private BowlerAbstractServer server;

	public BowlerDeviceReServerNamespace(BowlerAbstractConnection device,BowlerAbstractServer server, boolean useAsync, int namespaceIndex, String namespaceString,GenericDevice gen){
		this.device = device;
		this.setServer(server);
		setNamespace(namespaceString);
		ArrayList<RpcEncapsulation> rpcEnc= gen.getRpcList(namespaceString);
		for (RpcEncapsulation r:rpcEnc){
			getRpcList().add(r);
		}
		if(useAsync){
			device.addDatagramListener(new IBowlerDatagramListener() {
				@Override
				public void onAsyncResponse(BowlerDatagram data) {
					getServer().pushAsyncPacket(data);
				}
			});
		}
		
	}

	public BowlerDatagram process(BowlerDatagram data){
		BowlerDatagram bd = device.send(data);
		return bd;
	}

	public BowlerAbstractServer getServer() {
		return server;
	}

	public void setServer(BowlerAbstractServer server) {
		this.server = server;
	}

}
