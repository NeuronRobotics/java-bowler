package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.IPuckBotKinematics;
import com.neuronrobotics.addons.driving.PuckBot;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualPuckBot.
 */
public class VirtualPuckBot extends PuckBot{
	
	/** The world. */
	private VirtualWorld world;

	/** The controller. */
	VirtualGenericPIDDevice controller;
	
	/**
	 * Instantiates a new virtual puck bot.
	 *
	 * @param w the w
	 * @param botStartX the bot start x
	 * @param botStartY the bot start y
	 */
	public VirtualPuckBot(VirtualWorld w,int botStartX ,int botStartY){
		init(w,botStartX,botStartY);
	}
	
	/**
	 * Instantiates a new virtual puck bot.
	 *
	 * @param w the w
	 */
	public VirtualPuckBot(VirtualWorld w){
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
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.PuckBot#setPuckBotKinematics(com.neuronrobotics.addons.driving.IPuckBotKinematics)
	 */
	@Override
	public void setPuckBotKinematics(IPuckBotKinematics pk) {
		super.setPuckBotKinematics(pk);
		controller.setMaxTicksPerSecond(pk.getMaxTicksPerSeconds());
	}
	

}
