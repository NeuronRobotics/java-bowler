package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.wireless.bluetooth.BlueCoveManager;
import com.neuronrobotics.sdk.wireless.bluetooth.BluetoothSerialConnection;

public class BluetoothConector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio;
		BlueCoveManager manager = new BlueCoveManager();
		String devices[] = manager.getAvailableSerialDevices(true);
		System.out.println("Devices: ");
		for (String d: devices) {
			System.out.println(d);
		}
		if (devices.length > 0) {
			System.out.println("Connecting to : "+devices[0]);
			dyio = new DyIO(new BluetoothSerialConnection(manager, devices[0]));
			dyio.connect();
			if(dyio.ping() )
				System.out.println("All OK!");
			
		}
		System.exit(0);
	}

}
