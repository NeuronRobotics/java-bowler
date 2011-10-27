package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDEvent;

public class PuckBot extends AbstractRobotDrive{
	private IPuckBotKinematics pk = new PuckBotDefaultKinematics();
	protected PIDChannel left, right;
	int[] flushData = null;
	protected PuckBot(){
		
	}
	
	public PuckBot(PIDChannel left,PIDChannel right) {
		setPIDChanels(left, right);
		flushData=left.getPid().GetAllPIDPosition();
	}
	
	protected void setPIDChanels(PIDChannel pidChannelLeft, PIDChannel pidChannelRight) {
		left=pidChannelLeft;
		right=pidChannelRight;
		left.addPIDEventListener(this);
		right.addPIDEventListener(this);
	}
	
	public void SetEncoderPositions(PuckBotDriveData d){
		left.setCachedTargetValue(d.getLeftEncoderData());
		right.setCachedTargetValue(d.getRightEncoderData());
		left.getPid().flushPIDChannels(d.getDriveTimeInSeconds());
	}
	public void SetEncoderVelocity(PuckBotVelocityData d) throws PIDCommandException{
		left.SetPDVelocity((int) d.getLeftTicksPerSecond(), 0);
		right.SetPDVelocity((int) d.getRightTicksPerSecond(), 0);
	}
	
	@Override
	public void DriveStraight(double cm, double seconds) {
		SetEncoderPositions(getPuckBotKinematics().DriveStraight(cm, seconds));
	}

	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		SetEncoderPositions(getPuckBotKinematics().DriveArc(cmRadius, degrees, seconds));
	}
	@Override
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		try {
			SetEncoderVelocity(getPuckBotKinematics().DriveVelocityArc(degreesPerSecond, cmRadius));
		} catch (PIDCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void DriveVelocityStraight(double cmPerSecond) {
		try {
			SetEncoderVelocity(getPuckBotKinematics().DriveVelocityStraight(cmPerSecond));
		} catch (PIDCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		RobotLocationData d= getPuckBotKinematics().onPIDEvent(e,left.getGroup(),right.getGroup());	
		double [] loc = getPositionOffset(d.getDeltaX(), d.getDeltaX());
		setCurrentX(loc[0]);
		setCurrentY(loc[1]);
		setCurrentOrentation( getCurrentOrentation()+d.getDeltaOrentation());
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

	public void setPuckBotKinematics(IPuckBotKinematics pk) {
		this.pk = pk;
	}

	public IPuckBotKinematics getPuckBotKinematics() {
		return pk;
	}

}
