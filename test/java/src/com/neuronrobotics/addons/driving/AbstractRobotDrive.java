package com.neuronrobotics.addons.driving;

import java.util.ArrayList;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public abstract class AbstractRobotDrive implements IPIDEventListener{
	private ArrayList<IRobotDriveEventListener> dl = new  ArrayList<IRobotDriveEventListener> ();
	
	private double currentX=0;
	private double currentY=0;
	private double currentOrentation=Math.PI/2;
	protected AbstractRobotDrive(){
		
	}
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
	public void setCurrentOrentation(double o) {
		//System.out.println("Current orentation is: "+Math.toDegrees(currentTheta));
		this.currentOrentation = o;
	}
	/**
	 * 
	 * @return current orentation in radians
	 */
	public double getCurrentOrentation() {
		return currentOrentation;
	}
	
	public void fireDriveEvent(){
		for(IRobotDriveEventListener l:dl){
			l.onDriveEvent(currentX, currentY, currentOrentation);
		}
	}
	
	public void addIRobotDriveEventListener(IRobotDriveEventListener l){
		if(!dl.contains(l))
			dl.add(l);
	}

	
	public double [] getPositionOffset(double deltLateral, double deltForward) {
		double [] back ={0,0};
		
		back[0] = getCurrentX();
		back[1] = getCurrentY();
		double o = getCurrentOrentation();
		
		back[0]+=deltForward*Math.cos(o);
		back[1]+=deltForward*Math.sin(o);
		
		back[0]-=deltLateral*Math.sin(o);
		back[1]+=deltLateral*Math.cos(o);
		
		return back;
	}
	
	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		// do nothing, drive motors have no limits
	}
	
}
