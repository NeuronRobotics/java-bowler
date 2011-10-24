package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDEvent;

public class PuckBot extends AbstractRobotDrive{
	private PuckBotKinematics pk = new PuckBotKinematics(this);
	protected PIDChannel left, right;
	protected PuckBot(){
		
	}
	
	public PuckBot(PIDChannel left,PIDChannel right) {
		setPIDChanels(left, right);
	}
	
	protected void setPIDChanels(PIDChannel pidChannelLeft, PIDChannel pidChannelRight) {
		left=pidChannelLeft;
		right=pidChannelRight;
		left.addPIDEventListener(this);
		right.addPIDEventListener(this);
	}
	
	@Override
	public void DriveStraight(double cm, double seconds) {
		getPuckBotKinematics().DriveStraight(cm, seconds);
	}

	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		getPuckBotKinematics().DriveArc(cmRadius, degrees, seconds);
	}
	@Override
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		getPuckBotKinematics().DriveVelocityArc(degreesPerSecond, cmRadius);
	}

	@Override
	public void DriveVelocityStraight(double cmPerSecond) {
		getPuckBotKinematics().DriveVelocityStraight(cmPerSecond);
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		getPuckBotKinematics().onPIDEvent(e);	
	}

	@Override
	public void onPIDReset(int group, int currentValue) {
		if(group == left.getGroup())
			getPuckBotKinematics().onPIDResetLeft(currentValue);
		if(group == right.getGroup())
			getPuckBotKinematics().onPIDResetRight(currentValue);
	}
	
	protected double getMaxTicksPerSeconds() {
		return getPuckBotKinematics().getMaxTicksPerSeconds();
	}

	@Override
	public boolean isAvailable() {
		return left.isAvailable() && right.isAvailable();
	}

	public void setPuckBotKinematics(PuckBotKinematics pk) {
		this.pk = pk;
	}

	public PuckBotKinematics getPuckBotKinematics() {
		return pk;
	}

}
