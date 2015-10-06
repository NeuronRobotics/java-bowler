package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.DCMotorOutputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DCMotorTest {
	
	public static void main(String[] args) throws InterruptedException {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		DCMotorOutputChannel dc = new DCMotorOutputChannel(dyio.getChannel(4));
		//Loop 10 times setting the dc output
		float time = 5;
		for(int i = 0; i < 10; i++) {
			System.out.println("Moving.");
			// Set the velocity from off to full
			int pos = ((i%2==0)?128:0);
			dc.setValue(pos);
			// pause between cycles so that the changes are visible
			Thread.sleep((long) (time*1000));
		}
                dyio.disconnect();
                System.exit(0);
	}

}
