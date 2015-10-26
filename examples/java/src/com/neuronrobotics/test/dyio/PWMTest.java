package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.PWMOutputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PWMTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		PWMOutputChannel pwm = new PWMOutputChannel(dyio.getChannel(4));
		//Loop 10 times setting the pwm output
		float time = 5;
		for(int i = 0; i < 10; i++) {
			System.out.println("Moving.");
			// Set the duty cycle to either 100% or 50%
			int pos = ((i%2==0)?100:50);
			pwm.setValue(pos);
			// pause between cycles so that the changes are visible
			try {
				Thread.sleep((long) (time*1000));
			} catch (InterruptedException e) {}
		}
        dyio.disconnect();
        System.exit(0);

	}

}
