package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DyIONamespaceTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}

		
		dyio.getRevisions();
		
		String name = dyio.getInfo();
		
		dyio.setInfo("My DyIO");
		
		String newName = dyio.getInfo();
		

		dyio.setInfo(name);

		
		double volts = dyio.getBatteryVoltage(true);
		
		dyio.setServoPowerSafeMode(true);
		
		System.out.println("Name was: "+name+" set to "+newName);
		System.out.println("Set to "+newName);
		System.out.println("Voltage = "+volts+" bank A = "+dyio.getBankAState()+" bank B = "+dyio.getBankBState());
		//System.exit(0);
	}

}
