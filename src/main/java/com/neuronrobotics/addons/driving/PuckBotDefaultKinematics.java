package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class PuckBotDefaultKinematics.
 */
public class PuckBotDefaultKinematics implements IPuckBotKinematics{
	
	/** The Constant wheelBase. */
	private static final double wheelBase = 22.86; //cm
	
	/** The Constant wheelDiameter. */
	private static final double wheelDiameter = 7;//cm
	
	/** The Constant ticksPerRevolution. */
	private static final double ticksPerRevolution = 180;// t/r 
	
	/** The Constant cmToTickScale. */
	private static final double cmToTickScale = ticksPerRevolution*(1/(Math.PI*wheelDiameter));
	
	/** The right index. */
	private int leftIndex,rightIndex;
	
	/** The current location. */
	private RobotLocationData currentLocation=null;
	
	/** The right pid event. */
	private PIDEvent leftPidEvent=null,rightPidEvent=null;
	
	/** The right encoder value. */
	private int leftEncoderValue=0,rightEncoderValue=0;
	
	
	/**
	 * Ticks to cm.
	 *
	 * @param ticks the ticks
	 * @return the double
	 */
	public static double ticksToCm(int ticks) {
		return ((double)ticks)/ cmToTickScale;
	}
	
	/**
	 * Cm to ticks.
	 *
	 * @param cm the cm
	 * @return the int
	 */
	public static int cmToTicks(double cm) {
		return (int) (cm*cmToTickScale);
	}
	
	/**
	 * This is a full implementation of the PuckBot kinematics.
	 *
	 * @param cm the cm
	 * @param seconds the seconds
	 * @return the puck bot drive data
	 */
	@Override
	public PuckBotDriveData DriveStraight(double cm, double seconds) {
		int dist = cmToTicks(cm);
		return new PuckBotDriveData(dist, dist, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IPuckBotKinematics#DriveArc(double, double, double)
	 */
	@Override
	public PuckBotDriveData DriveArc(double cmRadius, double degrees,double seconds) {
		
		double ldist = 0;
		double rdist = 0;
		
		double rRadius = cmRadius + (wheelBase/2);
		double lRadius = cmRadius - (wheelBase/2);
		ldist = lRadius*(Math.PI*degrees)/180;
		rdist = rRadius*(Math.PI*degrees)/180;
		
		return new PuckBotDriveData(cmToTicks(ldist), cmToTicks(rdist), seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IPuckBotKinematics#DriveVelocityStraight(double)
	 */
	@Override
	public PuckBotVelocityData DriveVelocityStraight(double cmPerSecond) {
		int tps = cmToTicks(cmPerSecond);
		return new PuckBotVelocityData(tps, tps);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IPuckBotKinematics#DriveVelocityArc(double, double)
	 */
	@Override
	public PuckBotVelocityData DriveVelocityArc(double degreesPerSecond,double cmRadius) {	
		double rRadius = cmRadius + (wheelBase/2);
		double lRadius = cmRadius - (wheelBase/2);
		double ldist = lRadius*(Math.PI*degreesPerSecond)/180;
		double rdist = rRadius*(Math.PI*degreesPerSecond)/180;
		return new PuckBotVelocityData(cmToTicks(ldist), cmToTicks(rdist));
	}
	
	/**
	 * Pair.
	 *
	 * @param e the e
	 */
	private void pair(PIDEvent e) {
		
		if(rightPidEvent==null && leftPidEvent==null) {
			if(e.getGroup() == leftIndex) {
				leftPidEvent=e;
			}
			if(e.getGroup() == rightIndex) {
				rightPidEvent=e;
			}
		}
		if(rightPidEvent!=null && leftPidEvent==null) {
			if(e.getGroup() == rightIndex) {
				rightPidEvent = e;
				leftPidEvent = new PIDEvent(leftIndex, leftEncoderValue, e.getTimeStamp(), 0);
			}
			if(e.getGroup() == leftIndex) {
				if(e.getTimeStamp() == rightPidEvent.getTimeStamp()) {
					leftPidEvent = e;
				}else {
					leftPidEvent = e;
					rightPidEvent = null;
				}
			}
		}
		if(rightPidEvent==null && leftPidEvent!=null) {
			if(e.getGroup() == leftIndex) {
				rightPidEvent = new PIDEvent(rightIndex, rightEncoderValue, e.getTimeStamp(), 0);
				leftPidEvent = e;
			}
			if(e.getGroup() == rightIndex) {
				if(e.getTimeStamp() == leftPidEvent.getTimeStamp()) {
					rightPidEvent = e;
				}else {
					rightPidEvent = e;
					leftPidEvent = null;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IPuckBotKinematics#onPIDEvent(com.neuronrobotics.sdk.pid.PIDEvent, int, int)
	 */
	@Override
	public RobotLocationData onPIDEvent(PIDEvent e, int leftChannelNumber,int rightChannelNumber) {
		System.out.println("Got: "+e);
		leftIndex=leftChannelNumber;
		rightIndex=rightChannelNumber;
		
		//pairing
		pair(e);
		
		if(rightPidEvent==null || leftPidEvent==null) {
			currentLocation=null;
		}else {
			//Extract Encoder data
			double left = ticksToCm(leftPidEvent.getValue() - leftEncoderValue);
			double right= ticksToCm(rightPidEvent.getValue() - rightEncoderValue); 
			//Reset current values
			leftEncoderValue = leftPidEvent.getValue();
			rightEncoderValue = rightPidEvent.getValue();
			//clear event storage
			rightPidEvent=null;
			leftPidEvent=null;
			
			double x=0,y=0,o=0;
			
			//kinematics
			double distDiff = right-left;
			double arcDiff = (distDiff/(wheelBase/2));

			o=(arcDiff/2);
			if(distDiff == 0){
				//The robot moved exactly straight forward
				x = 0;
				y = right;//right and left the same
			}else{
				//Straight line approximation
				y=(right+left)/2;
				x=0;
				//END Straight line approximation
			}
			currentLocation = new RobotLocationData(x, y, o);

		}
		
		if(currentLocation==null)
			return new RobotLocationData(0, 0, 0);
		else
			return currentLocation;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IPuckBotKinematics#getMaxTicksPerSeconds()
	 */
	@Override
	public double getMaxTicksPerSeconds() {
		return 200;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IPuckBotKinematics#onPIDResetLeft(int)
	 */
	@Override
	public void onPIDResetLeft(int currentValue) {
		leftEncoderValue=currentValue;
		leftPidEvent=null;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IPuckBotKinematics#onPIDResetRight(int)
	 */
	@Override
	public void onPIDResetRight(int currentValue) {
		rightEncoderValue=currentValue;
		rightPidEvent=null;
	}
	
	

}
