package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class LineTrack.
 */
public class LineTrack implements IRobotDriveEventListener,ISensorListener{
	
	/** The main robot. */
	AbstractRobotDrive mainRobot;
	
	/** The line. */
	AbstractSensor line;
	
	/** The drive vel. */
	private final int driveVel = 2;
	
	/**
	 * Run track.
	 *
	 * @param m the m
	 * @param l the l
	 */
	public void runTrack(AbstractRobotDrive m,AbstractSensor l) {
		mainRobot=m;
		line=l;
		mainRobot.addIRobotDriveEventListener(this);
		line.addSensorListener(this);
		mainRobot.DriveVelocityStraight(driveVel);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.ISensorListener#onRangeSensorEvent(com.neuronrobotics.addons.driving.AbstractSensor, java.util.ArrayList, long)
	 */
	@Override
	public void onRangeSensorEvent(AbstractSensor source,ArrayList<DataPoint> data, long timeStamp) {
		// Never gets called...
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.ISensorListener#onLineSensorEvent(com.neuronrobotics.addons.driving.AbstractSensor, java.lang.Integer, java.lang.Integer, java.lang.Integer, long)
	 */
	@Override
	public void onLineSensorEvent(AbstractSensor source, Integer left,Integer middle, Integer right, long timeStamp) {
		//System.out.println("Sensor Event left="+left+" middle="+middle+" right="+right);
		double diff = (double)(left-right);
		///System.out.println("Steer value ="+diff);
		if(left>500 && right>500){
			mainRobot.stopRobot();
		}
		if(diff<100 && diff>-100) {
			//System.out.println("Drive straight");
			mainRobot.DriveVelocityStraight(driveVel);
		}else {
			//System.out.println("turn");
			mainRobot.DriveVelocityArc((diff)/60, 2*((diff>0)?1:-1));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IRobotDriveEventListener#onDriveEvent(com.neuronrobotics.addons.driving.AbstractRobotDrive, double, double, double)
	 */
	@Override
	public void onDriveEvent(AbstractRobotDrive source, double x, double y,double orentation) {
		System.out.println("Drive Event: x="+x+" y="+y);
	}
}
