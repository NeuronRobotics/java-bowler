package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.commands.bcs.io.AsyncThreshholdEdgeType;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class AdvancedAsyncTest implements IAnalogInputListener{
	//The analog channel is a property of the class
	private AnalogInputChannel ana0;
	private AnalogInputChannel ana1;
	private AnalogInputChannel ana2;
	private AnalogInputChannel ana3;
	
	public AdvancedAsyncTest() {
		//Start the dyio with serial dialog
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		Log.enableDebugPrint();
		//Instantiate a new analog channel
		//The second parameter tells the analog channel that is it an asynchronous channel
		ana0 = new AnalogInputChannel(dyio.getChannel(8),true);	
		/**
		 * Setup as auto sample
		 * Take a sample evert 500ms and push an async packet
		 */
		//ana0.configAdvancedAsyncAutoSample(500);//Take a sample evert 500ms and push an async packet
		//Add this instance of the Tester class to the analog channel 
		ana0.addAnalogInputListener(this);
		
		//The second parameter tells the analog channel that is it an asynchronous channel
		ana1 = new AnalogInputChannel(dyio.getChannel(9),true);	
		/**
		 * Setup a dead-band async. 
		 * This will trigger if the value is outside the band from the last value sent.
		 */
		ana1.configAdvancedAsyncDeadBand(10, 50);
		//Add this instance of the Tester class to the analog channel 
		ana1.addAnalogInputListener(this);
		
		//The second parameter tells the analog channel that is it an asynchronous channel
		ana2 = new AnalogInputChannel(dyio.getChannel(10),true);
		/**
		 * Setup input with not-equal test
		 * This will trigger if the current value is not the same as the last value sent.
		 */
		ana2.configAdvancedAsyncNotEqual(10);
		//Add this instance of the Tester class to the analog channel 
		ana2.addAnalogInputListener(this);
		
		//The second parameter tells the analog channel that is it an asynchronous channel
		ana3 = new AnalogInputChannel(dyio.getChannel(11),true);	
		/**
		 * This is an edge trigger
		 * This will trigger is the value transitions from less then 300 to above/equal to 300. 
		 */
		ana3.configAdvancedAsyncTreshhold(10, 300, AsyncThreshholdEdgeType.RISING);
		//Add this instance of the Tester class to the analog channel 
		ana3.addAnalogInputListener(this);
		//Run forever printing out analog events
		while (true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new AdvancedAsyncTest();
	}
	@Override
	public void onAnalogValueChange(AnalogInputChannel channel,double value) {
		//Check the source of the event
		if (channel == ana0)
			System.out.println("Analog 0 event:"+value);
		if (channel == ana1)
			System.out.println("Analog 1 event:"+value);
		if (channel == ana2)
			System.out.println("Analog 2 event:"+value);
		if (channel == ana3)
			System.out.println("Analog 3 event:"+value);
	}
}
