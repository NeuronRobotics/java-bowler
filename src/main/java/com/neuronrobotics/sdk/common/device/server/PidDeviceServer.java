package com.neuronrobotics.sdk.common.device.server;

import java.io.IOException;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;

public class PidDeviceServer extends BowlerAbstractServer {

	public PidDeviceServer(MACAddress mac) {
		super(mac);
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
