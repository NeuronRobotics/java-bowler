package com.neuronrobotics.addons.driving;

import java.util.ArrayList;
import com.neuronrobotics.sdk.pid.IPIDEventListener;

public abstract class AbstractRobot implements IPIDEventListener{
	private ArrayList<IRobotDriveEventListener> dl = new  ArrayList<IRobotDriveEventListener> ();
	
	private ArrayList<ISensorListener> sensorListeners = new ArrayList<ISensorListener>();
	
	protected AbstractRangeSensor range=null;
	protected AbstractLineSensor line=null;
	
	private double currentX=0;
	private double currentY=0;
	private double currentTheta=Math.PI/2;
	
	/**
	 * Driving kinematics should be implemented in here
	 * Before driving, a reset for each drive wheel should be called
	 * @param cm how many centimeters should be driven
	 * @param seconds how many seconds it should take
	 */
	public abstract void DriveStraight(double cm,double seconds);
	/**
	 * Driving kinematics should be implemented in here
	 * Before driving, a reset for each drive wheel should be called
	 * NOTE This should obey the right-hand rule.
	 * @param cmRadius radius of curve (centimeters)
	 * @param degrees degrees of the arch to sweep through
	 * @param seconds how many seconds it should take
	 */
	public abstract void DriveArc(double cmRadius,double degrees,double seconds);
	
	
	
	/**
	 * 
	 * @param startDeg
	 * @param endDeg
	 * @param degPerStep
	 * @return if the command succeed
	 */
	public boolean StartSweep(float startDeg,float endDeg,int degPerStep){
		if(range==null)
			return false;
		return range.StartSweep(startDeg,endDeg, degPerStep);
	}
	
	public void setRangeSensor(AbstractRangeSensor range) {
		this.range=range;
	}
	public void setLineSensor(AbstractLineSensor line) {
		this.line=line;
	}
	
	
	public void setCurrentX(double currentX) {
		//System.out.println("Current X is: "+currentX);
		this.currentX = currentX;
	}
	public double getCurrentX() {
		return currentX;
	}
	public void setCurrentY(double currentY) {
		//System.out.println("Current Y is: "+currentY);
		this.currentY = currentY;
	}
	public double getCurrentY() {
		return currentY;
	}
	/**
	 * 
	 * @param currentTheta in radians
	 */
	public void setCurrentTheta(double currentTheta) {
		//System.out.println("Current orentation is: "+Math.toDegrees(currentTheta));
		this.currentTheta = currentTheta;
	}
	/**
	 * 
	 * @return current orentation in radians
	 */
	public double getCurrentOrentation() {
		return currentTheta;
	}
	
	public void fireDriveEvent(){
		for(IRobotDriveEventListener l:dl){
			l.onDriveEvent(currentX, currentY, currentTheta);
		}
	}
	
	public void addIRobotDriveEventListener(IRobotDriveEventListener l){
		if(!dl.contains(l))
			dl.add(l);
	}
	
	/**
	 * Add an IDriveListener that will be contacted with an   on
	 * each incoming data event.
	 * 
	 * @param l
	 */
	public void addSensorListener(ISensorListener l) {
		if(sensorListeners.contains(l)) {
			return;
		}
		sensorListeners.add(l);
	}
	/**
	 * Contact all of the sensorListeners with the given event.
	 * 
	 * 
	 */
	public void fireRangeSensorEvent(ArrayList<DataPoint> data,long timeStamp) {
		for(ISensorListener l : sensorListeners) {
			l.onRangeSensorEvent(data,timeStamp);
		}
	}

	/**
	 * Contact all of the sensorListeners with the given event.
	 * 
	 * 
	 */
	public void fireLineSensorEvent(Integer left,Integer middle,Integer right,long timeStamp) {
		for(ISensorListener l : sensorListeners) {
			l.onLineSensorEvent(left,middle,right,timeStamp);
		}
	}
	
}
