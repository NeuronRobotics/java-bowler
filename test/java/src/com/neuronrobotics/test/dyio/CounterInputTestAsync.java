package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.CounterInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ICounterInputListener;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class CounterInputTestAsync implements ICounterInputListener{
	//The Counter channel is a property of the class
	private CounterInputChannel dip;

	public CounterInputTestAsync(){
		//Start the dyio with serial dialog
		DyIO dyio=new DyIO();
		//dyio.SetPrintModes(true, true);
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		//Instantiate a new Counter channel
		//The second parameter tells the Counter channel that is it an asynchronous channel
		dip = new CounterInputChannel(dyio.getChannel(23),true);	
		//Add this instance of the Tester class to the Counter channel 
		dip.addCounterInputListener(this);
		//Run forever printing out Counter events
		System.out.println("Running...");
		while (true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}

	}
	@Override
	public void onCounterValueChange(CounterInputChannel source, int value) {
		//Check the source of the event
		if (source == dip)
			System.out.println("Counter event:"+value);
	}

	public static void main(String[] args) throws InterruptedException {
		//Start the tester class
		new CounterInputTestAsync();
	}

}
