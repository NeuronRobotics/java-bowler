package com.neuronrobotics.sdk.network;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.ISynchronousDatagramListener;

public abstract class AbstractUdpDeviceServer implements ISynchronousDatagramListener {

	private BowlerAbstractConnection device;
	private BowlerUDPServer srv;
	public AbstractUdpDeviceServer(BowlerAbstractConnection device) {
		this.device = device;
		device.addDatagramListener(new IBowlerDatagramListener() {
			@Override
			public void onAsyncResponse(BowlerDatagram data) {
				pushAsyncPacket(data);
			}
		});

		setServer(new BowlerUDPServer(1865));
		getServer().connect();
		getServer().addSynchronousDatagramListener(this);
		
		//Log.enableDebugPrint(true);
	}

	public BowlerUDPServer getServer() {
		return srv;
	}

	public void setServer(BowlerUDPServer srv) {
		this.srv = srv;
		srv.addSynchronousDatagramListener(this);
	}

	@Override
	public void onSyncReceive(BowlerDatagram data) {
		//System.out.println("Rx >> "+data);
		BowlerDatagram bd = process(data);
		if(bd == null){
			bd = device.send(data);
			//System.out.println("Tx << "+bd);
			pushAsyncPacket(bd);
		}else{
			pushAsyncPacket(bd);
		}
	}

	public void pushAsyncPacket(BowlerDatagram data) {
		getServer().sendAsync(data);
	}
	
	abstract public BowlerDatagram process(BowlerDatagram data);
	
}
