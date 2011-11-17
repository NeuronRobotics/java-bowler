package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class SimpleLineFollow implements IAnalogInputListener {
	DyIO dyio;
	AnalogInputChannel leftSensor;
	AnalogInputChannel rightSensor;
	ServoChannel leftServo;
	ServoChannel rightServo;
	double leftValue = 0;
	double rightValue = 0;
	
	public SimpleLineFollow(){
		dyio = new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		leftSensor = new AnalogInputChannel(dyio, 13);
		leftSensor.configAdvancedAsyncNotEqual(10);
		leftSensor.addAnalogInputListener(this);
		
		rightSensor = new AnalogInputChannel(dyio, 12);
		rightSensor.configAdvancedAsyncNotEqual(10);
		rightSensor.addAnalogInputListener(this);
		
		leftServo = new ServoChannel(dyio, 10);
		rightServo = new ServoChannel(dyio, 11);
		
		dyio.setCachedMode(true);
		
		System.out.println("Begining line follow..");
		setVelocity(1, 1);
		
		while(!(leftValue>100 && rightValue>100)){
			ThreadUtil.wait(10);
		}
		leftSensor.removeAllAnalogInputListeners();
		rightSensor.removeAllAnalogInputListeners();
		System.out.println("Stop Condition!");
		setVelocity(0, 0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			new SimpleLineFollow();
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}

	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		if(chan==leftSensor)
			leftValue=value;
		if(chan==rightSensor)
			rightValue=value;
		setVelocity(1-(leftValue/1024), 1-(rightValue/1024));		
	}
	
	double scale=20;
	private void setVelocity(double l, double r){
		r*=-1;
		
		l=(l*scale)+127;
		r=(r*scale)+127;
		if(l>220)
			l=220;
		if(l<50)
			l=50;
		
		if(r>220)
			r=220;
		if(r<50)
			r=50;
		rightServo.SetPosition((int) r);
		leftServo.SetPosition((int) l);
		dyio.flushCache(0);
		System.out.println( "Setting velocity left="+l+" right="+r);
	}

}
