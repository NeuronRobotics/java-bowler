package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class ConnectionDialogTest.
 */
public class ConnectionDialogTest {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("Starting");
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.err.println("Dialog failed");
			System.exit(1);
		}
		Log.enableDebugPrint();
		dyio.ping();
        dyio.disconnect();
		System.out.println("Connection OK!");
		System.exit(0);
		//while(true);
	}

}
