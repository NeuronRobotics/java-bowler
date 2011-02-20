package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.PPMReaderChannel;
import com.neuronrobotics.sdk.dyio.peripherals.PWMOutputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PPMReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		dyio.setMode(23, DyIOChannelMode.DIGITAL_IN);
		PPMReaderChannel pwm = new PPMReaderChannel(dyio.getChannel(23));
        System.exit(0);

	}

}
