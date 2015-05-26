package com.neuronrobotics.sdk.namespace.bcs.pid;

import java.io.IOException;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;

public class PidDeviceServer extends BowlerAbstractServer {

	public PidDeviceServer(MACAddress mac,IExtendedPIDControl device) {
		super(mac);
		addBowlerDeviceServerNamespace(new PidDeviceServerNamespace(mac, device));
		Log.info("Starting UDP");
		try {
			startNetworkServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

}
