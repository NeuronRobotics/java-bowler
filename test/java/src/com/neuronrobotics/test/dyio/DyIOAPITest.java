package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DyIOAPITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		
		
	}

}
