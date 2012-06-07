package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AckermanBot;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class VirtualAckermanBot extends AckermanBot {
	private VirtualWorld world;
	VirtualGenericPIDDevice controller;

	public VirtualAckermanBot(VirtualWorld w,int botStartX ,int botStartY){
		init(w,botStartX,botStartY);
	}
	public VirtualAckermanBot(VirtualWorld w){
		init(w,300,300);
	}
	
	private void init(VirtualWorld w ,int botStartX ,int botStartY){
		world=w;
		world.addRobot(this,botStartX , botStartY);
		controller = new VirtualGenericPIDDevice(getMaxTicksPerSecond());
		controller.addPIDEventListener(new IPIDEventListener() {
			public void onPIDReset(int group, int currentValue) {}
			public void onPIDLimitEvent(PIDLimitEvent e) {}
			public void onPIDEvent(PIDEvent e) {
				world.updateMap();
			}
		});
		
		setPIDChanel(controller.getPIDChannel(0));
	}
	
	@Override
	public void setSteeringHardwareAngle(double s) {
		//do nothing
	}
}
