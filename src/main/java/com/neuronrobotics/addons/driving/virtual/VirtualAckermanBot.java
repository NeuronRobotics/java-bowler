package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AckermanBot;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualAckermanBot.
 */
public class VirtualAckermanBot extends AckermanBot {
	
	/** The world. */
	private VirtualWorld world;
	
	/** The controller. */
	VirtualGenericPIDDevice controller;

	/**
	 * Instantiates a new virtual ackerman bot.
	 *
	 * @param w the w
	 * @param botStartX the bot start x
	 * @param botStartY the bot start y
	 */
	public VirtualAckermanBot(VirtualWorld w,int botStartX ,int botStartY){
		init(w,botStartX,botStartY);
	}
	
	/**
	 * Instantiates a new virtual ackerman bot.
	 *
	 * @param w the w
	 */
	public VirtualAckermanBot(VirtualWorld w){
		init(w,300,300);
	}
	
	/**
	 * Inits the.
	 *
	 * @param w the w
	 * @param botStartX the bot start x
	 * @param botStartY the bot start y
	 */
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
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AckermanBot#setSteeringHardwareAngle(double)
	 */
	@Override
	public void setSteeringHardwareAngle(double s) {
		//do nothing
	}
}
