package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.PuckBot;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class VirtualPuckBot extends PuckBot{
	private VirtualWorld world;

	VirtualGenericPIDDevice controller;
	public VirtualPuckBot(VirtualWorld w,int botStartX ,int botStartY){
		init(w,botStartX,botStartY);
	}
	public VirtualPuckBot(VirtualWorld w){
		init(w,300,300);
	}
	private void init(VirtualWorld w ,int botStartX ,int botStartY){
		world=w;
		world.addRobot(this,botStartX , botStartY);
		controller = new VirtualGenericPIDDevice(getMaxTicksPerSeconds());
		controller.addPIDEventListener(new IPIDEventListener() {
			public void onPIDReset(int group, int currentValue) {}
			public void onPIDLimitEvent(PIDLimitEvent e) {}
			public void onPIDEvent(PIDEvent e) {
				world.updateMap();
			}
		});
		
		setPIDChanels(controller.getPIDChannel(0),controller.getPIDChannel(1));
		
	}



}
