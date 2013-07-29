package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

import com.neuronrobotics.sdk.genericdevice.GenericDevice;

public class BowlerDeviceReServerNamespace extends BowlerAbstractDeviceServerNamespace{
	
	private BowlerAbstractConnection device;
	private GenericDevice gen;
	private BowlerAbstractServer server;

	public BowlerDeviceReServerNamespace(BowlerAbstractConnection device,BowlerAbstractServer server, boolean useAsync){
		this.device = device;
		this.setServer(server);
		gen = new GenericDevice(device);
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

	@Override
	public ArrayList<String> getNamespaces() {
		return gen.getNamespaces();
	}

	@Override
	public ArrayList<RpcEncapsulation> getRpcList(String namespace) {
		return gen.getRpcList(namespace);
	}

}
