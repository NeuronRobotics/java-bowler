package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.pid.PIDChannel;

public class PidLink extends AbstractLink{
	private PIDChannel channel;
	public PidLink(PIDChannel c,int home, int lowerLimit, int upperLimit, double scale) {
		super(home, lowerLimit, upperLimit, scale);
		setPIDChannel(c);
	}

	@Override
	public void cacheTargetValue() {
		channel.setCachedTargetValue(getTargetValue());
	}

	@Override
	public void flush(double time) {
		channel.flush(time);
	}

	@Override
	public int getCurrentPosition() {
		return channel.GetPIDPosition();
	}

	public void setPIDChannel(PIDChannel channel) {
		this.channel = channel;
	}

	public PIDChannel getPIDChannel() {
		return channel;
	}

}
