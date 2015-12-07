package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;


// TODO: Auto-generated Javadoc
/**
 * The Class AnalogPrismaticLink.
 */
public class AnalogPrismaticLink  extends AbstractPrismaticLink implements IAnalogInputListener{
	
	/** The channel. */
	private AnalogInputChannel channel;
	
	/**
	 * Instantiates a new analog prismatic link.
	 *
	 * @param c the c
	 * @param conf the conf
	 */
	public AnalogPrismaticLink(AnalogInputChannel c,LinkConfiguration conf) {
		super(conf);
		
		setAnalogChannel(c);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#cacheTargetValueDevice()
	 */
	@Override
	public void cacheTargetValueDevice() {
		//ignore, input only
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flush(double)
	 */
	@Override
	public void flushDevice(double time) {
		//ignore, input only
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flushAll(double)
	 */
	@Override
	public void flushAllDevice(double time) {
		//ignore, input only
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#getCurrentPosition()
	 */
	@Override
	public int getCurrentPosition() {
		int val=getChannel().getValue();
		fireLinkListener(val);
		return val;
	}

	/**
	 * Sets the analog channel.
	 *
	 * @param channel the new analog channel
	 */
	public void setAnalogChannel(AnalogInputChannel channel) {
		channel.addAnalogInputListener(this);
		channel.configAdvancedAsyncNotEqual(10);
		//new RuntimeException().printStackTrace();
		this.channel = channel;
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public AnalogInputChannel getChannel() {
		return channel;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener#onAnalogValueChange(com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel, double)
	 */
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		if(chan == getChannel() ) {
			fireLinkListener((int) value);
		}
	}

}
