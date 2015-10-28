package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleLineFollow.
 */
public class SimpleLineFollow implements IAnalogInputListener {
	
	/** The dyio. */
	DyIO dyio;
	
	/** The left sensor. */
	AnalogInputChannel leftSensor;
	
	/** The right sensor. */
	AnalogInputChannel rightSensor;
	
	/** The left servo. */
	ServoChannel leftServo;
	
	/** The right servo. */
	ServoChannel rightServo;
	
	/** The left value. */
	double leftValue = 0;
	
	/** The right value. */
	double rightValue = 0;
	
	/** The lastr m value. */
	int lastrMValue =127;
	
	/** The lastl m value. */
	int lastlMValue =127;
	
	/** The r m value. */
	int rMValue =127;
	
	/** The l m value. */
	int lMValue =127;
	
	/**
	 * Instantiates a new simple line follow.
	 */
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
		
		while(true){
			if(leftValue>500 && rightValue>500)
				break;
			if(lastlMValue != lMValue ||lastrMValue != rMValue ){
				leftServo.SetPosition(lMValue);
				lastlMValue = lMValue;
				rightServo.SetPosition(rMValue);
				lastrMValue = rMValue;
				dyio.flushCache(0);
			}
			ThreadUtil.wait(10);
		}
		System.out.println("Stop Condition!");
		rightServo.SetPosition(127);
		leftServo.SetPosition(127);
		dyio.flushCache(0);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
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

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener#onAnalogValueChange(com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel, double)
	 */
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		if (value>1024)
			return;
		if(chan==leftSensor)
			leftValue=value;
		if(chan==rightSensor)
			rightValue=value;
		setVelocity(1-(leftValue/1024), 1-(rightValue/1024));
		//System.out.println( "Setting velocity left="+leftValue+" right="+rightValue);
	}
	
	/** The scale. */
	double scale=20;
	
	/**
	 * Sets the velocity.
	 *
	 * @param l the l
	 * @param r the r
	 */
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
		rMValue=(int) r;
		lMValue=(int) l;
		//dyio.flushCache(0);
		//System.out.println( "Setting velocity left="+l+" right="+r);
	}

}
