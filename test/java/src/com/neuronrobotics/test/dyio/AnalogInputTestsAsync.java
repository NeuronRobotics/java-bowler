package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class AnalogInputTestsAsync implements IAnalogInputListener{
	//The analog channel is a property of the class
	private AnalogInputChannel ana;

	public AnalogInputTestsAsync(){
		//Start the dyio with serial dialog
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		//Instantiate a new analog channel
		//The second parameter tells the analog channel that is it an asynchronous channel
		ana = new AnalogInputChannel(dyio.getChannel(11),true);	
		//Add this instance of the Tester class to the analog channel 
		ana.addAnalogInputListener(this);
		//Run forever printing out analog events
		while (true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new AnalogInputTestsAsync();
	}
	@Override
	public void onAnalogValueChange(AnalogInputChannel channel,double value) {
		//Check the source of the event
		if (channel == ana)
			System.out.println("Analog event:"+value);
	}

}
