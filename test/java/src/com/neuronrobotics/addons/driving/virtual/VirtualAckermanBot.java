package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AckermanBot;
import com.neuronrobotics.addons.driving.AckermanConfiguration;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class VirtualAckermanBot extends AckermanBot {
	private VirtualWorld world;
	private final AckermanConfiguration config = new AckermanConfiguration();
	VirtualPIDChannel controller;

	public VirtualAckermanBot(VirtualWorld w){
		
		world=w;
		world.addRobot(this);
		controller = new VirtualPIDChannel(config.getMaxTicksPerSeconds());
		controller.addPIDEventListener(new IPIDEventListener() {
			public void onPIDReset(int group, int currentValue) {}
			public void onPIDLimitEvent(PIDLimitEvent e) {}
			public void onPIDEvent(PIDEvent e) {
				world.updateMap();
			}
		});
		
		setPIRChanel(controller.getPIDChannel(0));
		
		setLineSensor(new VirtualLineSensor(this,w));
		setRangeSensor(new VirtualRangeSensor(this,w));
	}
	@Override
	public void setSteeringHardwareAngle(double s) {
		//do nothing
	}
}
