package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class CounterStepperTest.
 */
public class CounterStepperTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		//Instantiate a new counter input
		CounterOutputChannel stepper=new CounterOutputChannel(dyio.getChannel(23));
		//Loop forever printing out the satate of the button
		// Move 5 steps
		stepper.setValue(5);
        dyio.disconnect();
        System.exit(0);

	}

}
