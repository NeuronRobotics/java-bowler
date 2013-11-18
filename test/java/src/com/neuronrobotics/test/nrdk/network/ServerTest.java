package com.neuronrobotics.test.nrdk.network;

import java.io.IOException;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.network.BowlerTCPServer;

public class ServerTest implements IBowlerDatagramListener{
	BowlerTCPServer srv;
	public ServerTest(){
		System.out.println("Starting tcp server..");
		try {
			srv = new BowlerTCPServer(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		srv.connect();
		srv.addDatagramListener(this);
		
	}
	
	
//	@Override
//	public void onAllResponse(BowlerDatagram data) {
//
//		System.out.println("Got Packet:\n"+data);
//		srv.sendAsync(data);
//
//	}

	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String [] args){
		try{
			new ServerTest();
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("###SERVER Failed out!");
			System.exit(1);
		}
	}

}
