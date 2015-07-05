package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoRotoryLink extends AbstractRotoryLink{
	private ServoChannel srv;
	
	public ServoRotoryLink(ServoChannel srv,LinkConfiguration conf) {
		super(conf);
		setServoChannel(srv);
	}

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
	public ServoChannel getServoChannel() {
		return srv;
	}
	public void save() {
		getServoChannel().SavePosition(getTargetValue());
	}


	@Override
	public void cacheTargetValue() {
		Log.debug("Caching servo value="+getTargetValue());
		getServoChannel().SetPosition(getTargetValue());
	}
	@Override
	public void flush(double time) {
		getServoChannel().SetPosition(getTargetValue(),(float) time);
		getServoChannel().getChannel().flush();
		fireLinkListener(getTargetValue());
	}
	@Override
	public int getCurrentPosition() {
		int val = getServoChannel().getValue();
		fireLinkListener(val);
		return val;
	}

	@Override
	public void flushAll(double time) {
		// TODO Auto-generated method stub
		getServoChannel().SetPosition(getTargetValue(),(float) time);
		getServoChannel().getChannel().getDevice().flushCache((float)time);
		fireLinkListener(getTargetValue());
	}
	
}
