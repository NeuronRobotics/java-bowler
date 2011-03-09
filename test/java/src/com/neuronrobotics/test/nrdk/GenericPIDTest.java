package com.neuronrobotics.test.nrdk;

import java.io.IOException;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
import com.neuronrobotics.sdk.network.BowlerTCPClient;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class GenericPIDTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Log.enableDebugPrint(true);
		GenericPIDDevice pid = new GenericPIDDevice();
		//if (!ConnectionDialog.getBowlerDevice(pid)){
		//	System.exit(1);
		//}
		try {
			pid.setConnection(new BowlerTCPClient("cortex.wpi.edu", 1965));
			pid.GetAllPIDPosition();
			pid.GetPIDPosition(4);
			pid.disconnect();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pid.disconnect();
			System.exit(1);
		}
	}

}
