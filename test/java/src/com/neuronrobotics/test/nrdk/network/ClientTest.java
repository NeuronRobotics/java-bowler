package com.neuronrobotics.test.nrdk.network;

import java.io.IOException;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.InvalidConnectionException;

import com.neuronrobotics.sdk.network.BowlerTCPClient;
//import com.neuronrobotics.sdk.network.BowlerTCPServer;

public class ClientTest extends BowlerAbstractDevice implements IBowlerDatagramListener{
	BowlerTCPClient clnt;
	public ClientTest(){
		try {
			clnt=new BowlerTCPClient("localhost",1965);
		} catch (Exception e) {
		}
		setConnection(clnt);
		connect();
		
		System.out.println("Pinging");
		for(int i=0;i<10;i++)
			System.out.println(ping());
		clnt.disconnect();
		System.out.println("done");
		System.exit(0);
	}
	
	@Override
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String [] args){
		try{
			new ClientTest();
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("###Client Failed out!");
			System.exit(1);
		}
	}

	@Override
	public boolean isAvailable() throws InvalidConnectionException {
		// TODO Auto-generated method stub
		return clnt.isConnected();
	}
}
