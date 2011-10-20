package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDChannel;

public abstract class AbstractPuckBot extends AbstractRobotDrive {
	protected final PuckBotConfiguration config = new PuckBotConfiguration();
	protected PIDChannel left, right;
	protected AbstractPuckBot(){
		
	}
	public AbstractPuckBot(PIDChannel driveLeft,PIDChannel driveRight) {
		setPIDChanels(driveLeft, driveRight);
	}
	
	protected void setPIDChanels(PIDChannel pidChannelLeft, PIDChannel pidChannelRight) {
		left=pidChannelLeft;
		right=pidChannelRight;
		left.addPIDEventListener(this);
		right.addPIDEventListener(this);
	}
	

}
