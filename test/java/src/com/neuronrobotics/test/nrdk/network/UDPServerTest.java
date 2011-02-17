package com.neuronrobotics.test.nrdk.network;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
//import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.InvalidConnectionException;

import com.neuronrobotics.sdk.network.BowlerUDPServer;

public class UDPServerTest extends BowlerAbstractDevice {
	BowlerUDPServer srv;
	public UDPServerTest(){
		System.out.println("Starting udp server..");
		srv = new BowlerUDPServer();
		setConnection(srv);
		connect();
	}
	
	
	public static void main(String [] args){
		try{
			new UDPServerTest();
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("###SERVER Failed out!");
			System.exit(1);
		}
	}


	@Override
	public boolean isAvailable() throws InvalidConnectionException {
		if(srv== null)
			return false;
		return srv.isConnected();
	}


	@Override
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}

}
