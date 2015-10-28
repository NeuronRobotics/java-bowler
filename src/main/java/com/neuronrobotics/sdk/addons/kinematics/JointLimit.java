package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEventType;

// TODO: Auto-generated Javadoc
/**
 * The Class JointLimit.
 */
public class JointLimit {
	
	/** The axis. */
	private int axis;
	
	/** The value. */
	private double value; 
	
	/** The time stamp. */
	private long timeStamp;
	
	/** The limit type. */
	private PIDLimitEventType limitType;
	
	/**
	 * Instantiates a new joint limit.
	 *
	 * @param axis the axis
	 * @param e the e
	 * @param linkConfiguration the link configuration
	 */
	public JointLimit(int axis, PIDLimitEvent e, LinkConfiguration linkConfiguration) {
		setAxis(axis);
		setValue(e.getValue()*linkConfiguration.getScale());
		setTimeStamp(e.getTimeStamp());
		setLimitType(e.getLimitType());
	}
	
	/**
	 * Sets the axis.
	 *
	 * @param axis the new axis
	 */
	public void setAxis(int axis) {
		this.axis = axis;
	}
	
	/**
	 * Gets the axis.
	 *
	 * @return the axis
	 */
	public int getAxis() {
		return axis;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Sets the time stamp.
	 *
	 * @param timeStamp the new time stamp
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Sets the limit type.
	 *
	 * @param limitType the new limit type
	 */
	public void setLimitType(PIDLimitEventType limitType) {
		this.limitType = limitType;
	}
	
	/**
	 * Gets the limit type.
	 *
	 * @return the limit type
	 */
	public PIDLimitEventType getLimitType() {
		return limitType;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "Axis="+getAxis()+" "+getLimitType().toString();
	}
}
