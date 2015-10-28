package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;

// TODO: Auto-generated Javadoc
/**
 * The Class LineSensor.
 */
public class LineSensor extends AbstractSensor implements IAnalogInputListener {
	
	/** The left. */
	private AnalogInputChannel left;
	
	/** The middle. */
	private AnalogInputChannel middle;
	
	/** The right. */
	private AnalogInputChannel right;
	
	/** The l val. */
	private double mVal=0,rVal=0,lVal=0;
	
	/**
	 * Instantiates a new line sensor.
	 *
	 * @param left the left
	 * @param middle the middle
	 * @param right the right
	 */
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

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractSensor#StartSweep(double, double, double)
	 */
	@Override
	public void StartSweep(double start, double stop, double increment) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener#onAnalogValueChange(com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel, double)
	 */
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
