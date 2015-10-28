package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class PuckBot.
 */
public class PuckBot extends AbstractRobotDrive{
	
	/** The pk. */
	private IPuckBotKinematics pk = new PuckBotDefaultKinematics();
	
	/** The right. */
	protected PIDChannel left, right;
	
	/** The flush data. */
	int[] flushData = null;
	
	/**
	 * Instantiates a new puck bot.
	 */
	protected PuckBot(){
		
	}
	
	/**
	 * Instantiates a new puck bot.
	 *
	 * @param left the left
	 * @param right the right
	 */
	public PuckBot(PIDChannel left,PIDChannel right) {
		setPIDChanels(left, right);
		flushData=left.getPid().GetAllPIDPosition();
	}
	
	/**
	 * Sets the pid chanels.
	 *
	 * @param pidChannelLeft the pid channel left
	 * @param pidChannelRight the pid channel right
	 */
	protected void setPIDChanels(PIDChannel pidChannelLeft, PIDChannel pidChannelRight) {
		left=pidChannelLeft;
		right=pidChannelRight;
		left.addPIDEventListener(this);
		right.addPIDEventListener(this);
	}
	
	/**
	 * Sets the encoder positions.
	 *
	 * @param d the d
	 */
	public void SetEncoderPositions(PuckBotDriveData d){
		left.ResetPIDChannel(0);
		right.ResetPIDChannel(0);
		left.setCachedTargetValue(d.getLeftEncoderData());
		right.setCachedTargetValue(d.getRightEncoderData());
		left.getPid().flushPIDChannels(d.getDriveTimeInSeconds());
	}
	
	/**
	 * Sets the encoder velocity.
	 *
	 * @param d the d
	 * @throws PIDCommandException the PID command exception
	 */
	public void SetEncoderVelocity(PuckBotVelocityData d) throws PIDCommandException{
		left.ResetPIDChannel(0);
		right.ResetPIDChannel(0);
		left.SetPDVelocity((int) d.getLeftTicksPerSecond(), 0);
		right.SetPDVelocity((int) d.getRightTicksPerSecond(), 0);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveStraight(double, double)
	 */
	@Override
	public void DriveStraight(double cm, double seconds) {
		SetEncoderPositions(getPuckBotKinematics().DriveStraight(cm, seconds));
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveArc(double, double, double)
	 */
	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		SetEncoderPositions(getPuckBotKinematics().DriveArc(cmRadius, degrees, seconds));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveVelocityArc(double, double)
	 */
	@Override
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		try {
			SetEncoderVelocity(getPuckBotKinematics().DriveVelocityArc(degreesPerSecond, cmRadius));
		} catch (PIDCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveVelocityStraight(double)
	 */
	@Override
	public void DriveVelocityStraight(double cmPerSecond) {
		try {
			SetEncoderVelocity(getPuckBotKinematics().DriveVelocityStraight(cmPerSecond));
		} catch (PIDCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDEvent(com.neuronrobotics.sdk.pid.PIDEvent)
	 */
	@Override
	public void onPIDEvent(PIDEvent e) {
		setRobotLocationUpdate(getPuckBotKinematics().onPIDEvent(e,left.getGroup(),right.getGroup()));
	}



	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDReset(int, int)
	 */
	@Override
	public void onPIDReset(int group, int currentValue) {
		if(group == left.getGroup())
			getPuckBotKinematics().onPIDResetLeft(currentValue);
		if(group == right.getGroup())
			getPuckBotKinematics().onPIDResetRight(currentValue);
	}
	
	/**
	 * Gets the max ticks per seconds.
	 *
	 * @return the max ticks per seconds
	 */
	protected double getMaxTicksPerSeconds() {
		return getPuckBotKinematics().getMaxTicksPerSeconds();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return left.isAvailable() && right.isAvailable();
	}

	/**
	 * Sets the puck bot kinematics.
	 *
	 * @param pk the new puck bot kinematics
	 */
	public void setPuckBotKinematics(IPuckBotKinematics pk) {
		this.pk = pk;
	}

	/**
	 * Gets the puck bot kinematics.
	 *
	 * @return the puck bot kinematics
	 */
	public IPuckBotKinematics getPuckBotKinematics() {
		return pk;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#disconnectDeviceImp()
	 */
	@Override
	public void disconnectDeviceImp() {
		// TODO Auto-generated method stub
		left.removePIDEventListener(this);
		right.removePIDEventListener(this);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#connectDeviceImp()
	 */
	@Override
	public boolean connectDeviceImp() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#getNamespacesImp()
	 */
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return null;
	}

}
