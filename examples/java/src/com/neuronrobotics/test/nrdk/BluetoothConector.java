package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.wireless.bluetooth.BlueCoveManager;
import com.neuronrobotics.sdk.wireless.bluetooth.BluetoothSerialConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothConector.
 */
public class BluetoothConector {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DyIO dyio;
		BlueCoveManager manager = new BlueCoveManager();
		String devices[] = manager.getAvailableSerialDevices(true);
		com.neuronrobotics.sdk.common.Log.error("Devices: ");
		for (String d: devices) {
			com.neuronrobotics.sdk.common.Log.error(d);
		}
		if (devices.length > 0) {
			com.neuronrobotics.sdk.common.Log.error("Connecting to : "+devices[0]);
			dyio = new DyIO(new BluetoothSerialConnection(manager, devices[0]));
			dyio.connect();
			if(dyio.ping() )
				com.neuronrobotics.sdk.common.Log.error("All OK!");
			
		}
		System.exit(0);
	}

}
