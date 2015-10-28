package com.neuronrobotics.test.dyio;


import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ConcurrencyTest.
 */
public class ConcurrencyTest implements IAnalogInputListener{
	
	/** The doc. */
	private DigitalOutputChannel doc;
	
	/** The ana. */
	private AnalogInputChannel ana;

	/**
	 * Instantiates a new concurrency test.
	 */
	public ConcurrencyTest() {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		doc = new DigitalOutputChannel(dyio.getChannel(1));
		ana = new AnalogInputChannel(dyio.getChannel(11),true);	
		//Add this instance of the Tester class to the analog channel 
		ana.addAnalogInputListener(this);
		while (true){
			ThreadUtil.wait(100);
		}

	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		try{
			new ConcurrencyTest();
		}finally{
			System.exit(0);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener#onAnalogValueChange(com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel, double)
	 */
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		// TODO Auto-generated method stub
		doc.setHigh(value>512);
	}
}
