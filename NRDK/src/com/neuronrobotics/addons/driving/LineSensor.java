package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;

public class LineSensor extends AbstractSensor implements IAnalogInputListener {
	private AnalogInputChannel left;
	private AnalogInputChannel middle;
	private AnalogInputChannel right;
	private double mVal=0,rVal=0,lVal=0;
	
	public LineSensor(AnalogInputChannel left, AnalogInputChannel middle, AnalogInputChannel right) {
		this.left=left;
		this.right=right;
		this.middle=middle;
		if (left != null){
			left.configAdvancedAsyncNotEqual(10);
			left.addAnalogInputListener(this);
		}
		if (middle != null){
			middle.configAdvancedAsyncNotEqual(10);
			middle.addAnalogInputListener(this);
		}
		if (right != null){
			right.configAdvancedAsyncNotEqual(10);
			right.addAnalogInputListener(this);	
		}
	}

	@Override
	public void StartSweep(double start, double stop, int increment) {
		// do nothing
	}

	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		if(chan == left)
			lVal=value;
		if(chan == middle)
			mVal=value;
		if(chan == right)
			rVal=value;
		fireLineSensorEvent((int)lVal, (int)mVal,(int)rVal, System.currentTimeMillis());
	}

}
