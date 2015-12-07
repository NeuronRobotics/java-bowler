package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ICounterOutputListener;

// TODO: Auto-generated Javadoc
/**
 * The Class StepperPrismaticLink.
 */
public class StepperPrismaticLink extends AbstractPrismaticLink {
	
	/** The channel. */
	private CounterOutputChannel channel;
	
	/**
	 * Instantiates a new stepper prismatic link.
	 *
	 * @param chan the chan
	 * @param conf the conf
	 */
	public StepperPrismaticLink(CounterOutputChannel chan, LinkConfiguration conf) {
		super(conf);
		this.setChannel(chan);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#cacheTargetValueDevice()
	 */
	@Override
	public void cacheTargetValueDevice() {
		channel.setValue(getTargetValue());
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flush(double)
	 */
	@Override
	public void flushDevice(double time) {
		channel.getChannel().setCachedTime((float)time);
		channel.getChannel().flush();
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flushAll(double)
	 */
	@Override
	public void flushAllDevice(double time) {
		channel.getChannel().getDevice().flushCache((float) time);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#getCurrentPosition()
	 */
	@Override
	public int getCurrentPosition() {
		int val=channel.getValue();
		fireLinkListener(val);
		return val;
	}

	/**
	 * Sets the channel.
	 *
	 * @param c the new channel
	 */
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

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public CounterOutputChannel getChannel() {
		return channel;
	}
}
