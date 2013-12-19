package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IDigitalInputListener;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DigitalInputTestAsync implements IDigitalInputListener{
	//The digital channel is a property of the class
	private DigitalInputChannel dip;

	public DigitalInputTestAsync() throws InterruptedException{
		//Start the dyio with serial dialog
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		//Instantiate a new digital channel
		//The second parameter tells the digital channel that is it an asynchronous channel
		for(int i=0;i<24;i++) {
			new DigitalInputChannel(dyio.getChannel(i),false);
		}
//		dip = new DigitalInputChannel(dyio.getChannel(0),true);	
//		//Add this instance of the Tester class to the digital channel 
//		dip.addDigitalInputListener(this);
//		//Run forever printing out digital events
		while (true){
			Thread.sleep(100);
		}
		//dyio.disconnect();
	}
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new DigitalInputTestAsync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	@Override
	public void onDigitalValueChange(DigitalInputChannel source, boolean isHigh) {
		//Check the source of the event
		if (source == dip)
			System.out.println("Digital event:"+isHigh);
	}

}
