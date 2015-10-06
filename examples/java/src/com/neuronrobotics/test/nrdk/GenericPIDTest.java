package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.network.BowlerTCPClient;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;

public class GenericPIDTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Log.enableDebugPrint();
		GenericPIDDevice pid = new GenericPIDDevice();
		//if (!ConnectionDialog.getBowlerDevice(pid)){
		//	System.exit(1);
		//}
		try {
			try {
				pid.setConnection(new BowlerTCPClient("cortex.wpi.edu", 1965));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//pid.setConnection(new BowlerTCPClient("192.168.0.134", 1965));
			pid.GetAllPIDPosition();
			pid.GetPIDPosition(2);
			pid.disconnect();
			System.out.println("All OK!");
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pid.disconnect();
			System.exit(1);
		}
	}

}
