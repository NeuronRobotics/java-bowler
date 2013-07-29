package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

public class BowlerAbstractServer implements ISynchronousDatagramListener  {

	private BowlerAbstractConnection srv;
	
	private ArrayList<IBowlerDeviceServerNamespace> namespaces = new ArrayList<IBowlerDeviceServerNamespace>();
	
	public void addBowlerDeviceServerNamespace(IBowlerDeviceServerNamespace ns){
		if(!namespaces.contains(ns))
			namespaces.add(ns);
	}
	public void removeBowlerDeviceServerNamespace(IBowlerDeviceServerNamespace ns){
		if(namespaces.contains(ns))
			namespaces.remove(ns);
	}
	public BowlerDatagram process(BowlerDatagram data){
		if(namespaces.size()==0){
			throw new RuntimeException("No namespaces defined");
		}
		for(IBowlerDeviceServerNamespace n:namespaces){
			BowlerDatagram d= n.process(data);
			if (d!=null)
				return d;
		}
		
		return null;
	}
	
	public BowlerAbstractConnection getServer() {
		return srv;
	}

	public void setServer(BowlerAbstractConnection srv) {
		this.srv = srv;
		srv.addSynchronousDatagramListener(this);
	}
	
	@Override
	public void onSyncReceive(BowlerDatagram data) {
		System.out.println("Rx >> "+data);
		BowlerDatagram bd = process(data);
		if(bd != null){
			System.out.println("Tx << "+bd);
			pushAsyncPacket(bd);
		}
	}

	public void pushAsyncPacket(BowlerDatagram data) {
		getServer().sendAsync(data);
	}

}
