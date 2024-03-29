package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class PidPrismaticLink.
 */
public class PidPrismaticLink extends AbstractPrismaticLink{
	
	/** The channel. */
	private PIDChannel channel;
	
	/**
	 * Instantiates a new pid prismatic link.
	 *
	 * @param c the c
	 * @param conf the conf
	 * @param b 
	 */
	public PidPrismaticLink(PIDChannel c,LinkConfiguration conf, boolean b) {
		super(conf);
		if(!b) {
			conf.setPauseEvents(true);
			conf.setDeviceTheoreticalMax(Integer.MAX_VALUE);
			conf.setDeviceTheoreticalMin(Integer.MIN_VALUE);
			conf.setPauseEvents(false);
		}else {
//			conf.setDeviceTheoreticalMax(conf.getUpperLimit());
//			conf.setDeviceTheoreticalMin(conf.getLowerLimit());
		}
		setPIDChannel(c);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#cacheTargetValueDevice()
	 */
	@Override
	public void cacheTargetValueDevice() {
		channel.setCachedTargetValue((float)getTargetValue());
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flush(double)
	 */
	@Override
	public void flushDevice(double time) {
		channel.flush(time);
		fireLinkListener(getCurrentPosition());
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flushAll(double)
	 */
	@Override
	public void flushAllDevice(double time) {
		channel.getPid().flushPIDChannels(time);
		fireLinkListener(getCurrentPosition());
	}


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#getCurrentPosition()
	 */
	@Override
	public double getCurrentPosition() {
		float val=channel.GetPIDPosition();
		return val;
	}

	/**
	 * Sets the PID channel.
	 *
	 * @param channel the new PID channel
	 */
	public void setPIDChannel(PIDChannel channel) {
		channel.addPIDEventListener(new IPIDEventListener() {
			@Override
			public void onPIDReset(int group, float currentValue) {}
			
			@Override
			public void onPIDLimitEvent(PIDLimitEvent e) {}
			
			@Override
			public void onPIDEvent(PIDEvent e) {
				fireLinkListener(e.getValue());
			}
		});
		this.channel = channel;
	}

	/**
	 * Gets the PID channel.
	 *
	 * @return the PID channel
	 */
	public PIDChannel getPIDChannel() {
		return channel;
	}


}
