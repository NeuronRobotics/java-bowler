package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

// TODO: Auto-generated Javadoc
/**
 * The Class ServoRotoryLink.
 */
public class ServoRotoryLink extends AbstractRotoryLink{
	
	/** The srv. */
	private ServoChannel srv;
	
	/**
	 * Instantiates a new servo rotory link.
	 *
	 * @param srv the srv
	 * @param conf the conf
	 */
	public ServoRotoryLink(ServoChannel srv,LinkConfiguration conf) {
		super(conf);
		setServoChannel(srv);
	}

	/**
	 * Sets the servo channel.
	 *
	 * @param srv the new servo channel
	 */
	public void setServoChannel(ServoChannel srv) {
		//System.out.println("Setting new servo channel: "+srv.getChannel().getNumber());
		srv.getChannel().setCachedMode(true);
		srv.addIServoPositionUpdateListener(new IServoPositionUpdateListener() {
			@Override
			public void onServoPositionUpdate(ServoChannel srv, int position,double time) {
				fireLinkListener(position);
			}
		});
		this.srv = srv;
	}
	
	/**
	 * Gets the servo channel.
	 *
	 * @return the servo channel
	 */
	public ServoChannel getServoChannel() {
		return srv;
	}
	
	/**
	 * Save.
	 */
	public void save() {
		getServoChannel().SavePosition(getTargetValue());
	}


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#cacheTargetValueDevice()
	 */
	@Override
	public void cacheTargetValueDevice() {
		Log.debug("Caching servo value="+getTargetValue());
		getServoChannel().SetPosition(getTargetValue());
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flush(double)
	 */
	@Override
	public void flushDevice(double time) {
		getServoChannel().SetPosition(getTargetValue(),(float) time);
		getServoChannel().getChannel().flush();
		fireLinkListener(getTargetValue());
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#getCurrentPosition()
	 */
	@Override
	public int getCurrentPosition() {
		int val = getServoChannel().getValue();
		fireLinkListener(val);
		return val;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flushAll(double)
	 */
	@Override
	public void flushAllDevice(double time) {
		// TODO Auto-generated method stub
		getServoChannel().SetPosition(getTargetValue(),(float) time);
		getServoChannel().getChannel().getDevice().flushCache((float)time);
		fireLinkListener(getTargetValue());
	}
	
}
