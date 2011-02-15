package com.neuronrobotics.sdk.addons.walker;

import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class Link {
	private double linkLen;
	private double scale;
	private int upperLimit;
	private int lowerLimit;
	private int home;
	private ServoChannel srv;
	//Position set point
	private double pos=0;
	//Actual servo position
	private int srvVal=0;
	//private float time=0;
	
	public Link(ServoChannel srv,int home,int lowerLimit,int upperLimit,double scale,double linkLen){
		this.setLinkLen(linkLen);
		this.scale=scale;
		this.upperLimit=upperLimit;
		this.lowerLimit=lowerLimit;
		this.home=home;
		this.srv=srv;
		//Home();
	}
	public void Home(){
		setPosition(this.home,2);
		updateServo(2);
	}
	private void setPosition(int val,float time) {
		if(val>upperLimit)
			val=upperLimit;
		if(val<lowerLimit) {
			//System.out.println("Attempting to set to value:"+val+" is below limit:"+lowerLimit);
			val=lowerLimit;
		}
		if(srvVal == val)
			return;
		srvVal=val;
	}
	public void updateServo(double time) {
		srv.SetPosition(srvVal, (float) time);
	}
	public void incrementAngle(double inc,double time){
		setAngle(pos+inc,time);
	}
	public void setAngle(double pos,double time) {
		this.pos = pos;
		setPosition(((int) (pos/scale))+home,(float) time);
	}
	public double getAngle() {
		return ((srvVal-home)*scale);
	}
	private void setLinkLen(double linkLen) {
		this.linkLen = linkLen;
	}
	public double getLinkLen() {
		return linkLen;
	}
	public void save() {
		srv.SavePosition(srvVal);
	}
	public double getMax() {
		// TODO Auto-generated method stub
		return (upperLimit-home)*scale;
	}
	public double getMin() {
		// TODO Auto-generated method stub
		return (lowerLimit-home)*scale;
	}
	public boolean isMax() {
		if(srvVal == upperLimit) {
			System.out.println("Servo value is :" +srvVal+" upper limit is"+ upperLimit);
			return true;
		}
		return false;
	}
	public boolean isMin() {
		if(srvVal == lowerLimit) {
			System.out.println("Servo value is :" +srvVal+" lower limit is"+ lowerLimit);
			return true;
		}
		return false;
	}
	public void flush() {
		srv.flush();	
	}
}
