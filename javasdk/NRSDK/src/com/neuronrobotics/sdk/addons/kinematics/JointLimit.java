package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEventType;

public class JointLimit {
	private int axis;
	private double value; 
	private long timeStamp;
	private PIDLimitEventType limitType;
	public JointLimit(int axis, PIDLimitEvent e, LinkConfiguration linkConfiguration) {
		setAxis(axis);
		setValue(e.getValue()*linkConfiguration.getScale());
		setTimeStamp(e.getTimeStamp());
		setLimitType(e.getLimitType());
	}
	public void setAxis(int axis) {
		this.axis = axis;
	}
	public int getAxis() {
		return axis;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getValue() {
		return value;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setLimitType(PIDLimitEventType limitType) {
		this.limitType = limitType;
	}
	public PIDLimitEventType getLimitType() {
		return limitType;
	}
	public String toString(){
		return "Axis="+getAxis()+" "+getLimitType().toString();
	}
}
