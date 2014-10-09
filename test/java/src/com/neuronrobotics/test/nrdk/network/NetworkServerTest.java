package com.neuronrobotics.test.nrdk.network;

import java.io.IOException;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;
import com.neuronrobotics.sdk.network.BowlerUDPServer;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class NetworkServerTest extends BowlerAbstractServer {
	BowlerUDPServer srv;
	public NetworkServerTest() throws IOException{
		super(new MACAddress());
		Log.enableInfoPrint();
		Log.info("Starting Bowler Server");
		startNetworkServer();
		while(true){
			ThreadUtil.wait(100);
		}
	}
	
	
	public static void main(String [] args){
		try{
			new NetworkServerTest();
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("###SERVER Failed out!");
			System.exit(1);
		}
	}


	
}
