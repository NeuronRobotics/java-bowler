package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;


public class AnalogRotoryLink  extends AbstractRotoryLink implements IAnalogInputListener{
	private AnalogInputChannel channel;
	public AnalogRotoryLink(AnalogInputChannel c,int home, int lowerLimit, int upperLimit, double scale) {
		super(home, lowerLimit, upperLimit, scale);
		setAnalogChannel(c);
	}

	@Override
	public void cacheTargetValue() {
		//ignore, input only
	}

	@Override
	public void flush(double time) {
		//ignore, input only
	}
	@Override
	public void flushAll(double time) {
		//ignore, input only
	}

	@Override
	public int getCurrentPosition() {
		int val=getChannel().getValue();
		fireLinkListener(val);
		return val;
	}

	public void setAnalogChannel(AnalogInputChannel channel) {
		channel.addAnalogInputListener(this);
		channel.configAdvancedAsyncNotEqual(10);
		this.channel = channel;
	}

	public AnalogInputChannel getChannel() {
		return channel;
	}

	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		if(chan == getChannel() ) {
			fireLinkListener((int) value);
		}
	}

}
