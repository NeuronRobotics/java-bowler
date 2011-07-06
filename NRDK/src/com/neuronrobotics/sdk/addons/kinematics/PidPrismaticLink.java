package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class PidPrismaticLink extends AbstractPrismaticLink{
	private PIDChannel channel;
	public PidPrismaticLink(PIDChannel c,int home, int lowerLimit, int upperLimit, double scale) {
		super(home, lowerLimit, upperLimit, scale);
		setPIDChannel(c);
	}

	@Override
	public void cacheTargetValue() {
		channel.setCachedTargetValue(getTargetValue());
	}

	@Override
	public void flush(double time) {
		channel.getPid().flushPIDChannels(time);
	}

	@Override
	public int getCurrentPosition() {
		int val=channel.GetPIDPosition();
		fireLinkListener(val);
		return val;
	}

	public void setPIDChannel(PIDChannel channel) {
		channel.addPIDEventListener(new IPIDEventListener() {
			@Override
			public void onPIDReset(int group, int currentValue) {}
			
			@Override
			public void onPIDLimitEvent(PIDLimitEvent e) {}
			
			@Override
			public void onPIDEvent(PIDEvent e) {
				fireLinkListener(e.getValue());
			}
		});
		this.channel = channel;
	}

	public PIDChannel getPIDChannel() {
		return channel;
	}

}
