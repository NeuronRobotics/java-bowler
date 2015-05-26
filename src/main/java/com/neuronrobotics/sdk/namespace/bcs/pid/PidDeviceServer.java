package com.neuronrobotics.sdk.namespace.bcs.pid;

import java.io.IOException;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

public class PidDeviceServer extends BowlerAbstractServer {

	public PidDeviceServer(MACAddress mac,IPidControlNamespace device) {
		super(mac);
		addBowlerDeviceServerNamespace(new PidDeviceServerNamespace(mac, device));
		Log.enableInfoPrint();
		Log.info("Starting UDP");
		try {
			startNetworkServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String [] args){
		PidDeviceServer srv = new PidDeviceServer(new MACAddress(), new VirtualGenericPIDDevice(10000000) );
		
	}

}
