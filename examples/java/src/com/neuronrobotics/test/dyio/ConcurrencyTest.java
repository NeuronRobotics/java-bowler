package com.neuronrobotics.test.dyio;


import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class ConcurrencyTest implements IAnalogInputListener{
	private DigitalOutputChannel doc;
	private AnalogInputChannel ana;

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
	public static void main(String[] args){
		try{
			new ConcurrencyTest();
		}finally{
			System.exit(0);
		}
	}
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		// TODO Auto-generated method stub
		doc.setHigh(value>512);
	}
}
