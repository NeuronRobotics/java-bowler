package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class ConnectionDialogTest {
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		Log.enableDebugPrint(true);
		dyio.ping();
        dyio.disconnect();
		System.out.println("Connection OK!");
		System.exit(0);
		//while(true);
	}

}
