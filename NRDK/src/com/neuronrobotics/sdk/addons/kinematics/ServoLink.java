package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoLink extends AbstractLink{
	private ServoChannel srv;
	
	public ServoLink(ServoChannel srv,int home, int lowerLimit, int upperLimit, double scale) {
		super(home, lowerLimit, upperLimit, scale);
		setServoChannel(srv);
	}

	public void setServoChannel(ServoChannel srv) {
		//System.out.println("Setting new servo channel: "+srv.getChannel().getNumber());
		srv.getChannel().setCachedMode(true);
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
		getServoChannel().SetPosition(getTargetValue());
	}
	@Override
	public void flush(double time) {
		getServoChannel().SetPosition(getTargetValue(),(float) time);
		getServoChannel().flush();	
	}
	@Override
	public int getCurrentPosition() {
		int val = getServoChannel().getValue();
		fireLinkListener(val);
		return val;
	}
	
}
