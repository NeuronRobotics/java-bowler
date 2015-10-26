package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ICounterOutputListener;

public class StepperPrismaticLink extends AbstractPrismaticLink {
	private CounterOutputChannel channel;
	
	public StepperPrismaticLink(CounterOutputChannel chan, LinkConfiguration conf) {
		super(conf);
		this.setChannel(chan);
	}

	@Override
	public void cacheTargetValue() {
		channel.setValue(getTargetValue());
	}

	@Override
	public void flush(double time) {
		channel.getChannel().setCachedTime((float)time);
		channel.getChannel().flush();
		
	}
	@Override
	public void flushAll(double time) {
		channel.getChannel().getDevice().flushCache((float) time);
	}

	@Override
	public int getCurrentPosition() {
		int val=channel.getValue();
		fireLinkListener(val);
		return val;
	}

	public void setChannel(CounterOutputChannel c) {
		channel = c;
		channel.getChannel().setCachedMode(true);
		channel.addCounterOutputListener(new ICounterOutputListener() {
			
			@Override
			public void onCounterValueChange(CounterOutputChannel source, int value) {
				if(source==channel) {
					fireLinkListener(value);
				}
			}
		});
	}

	public CounterOutputChannel getChannel() {
		return channel;
	}
}
