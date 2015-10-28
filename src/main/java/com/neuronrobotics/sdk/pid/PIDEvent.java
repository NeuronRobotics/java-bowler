package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class PIDEvent.
 */
public class PIDEvent {
	
	/** The channel. */
	private int channel;
	
	/** The ticks. */
	private int ticks; 
	
	/** The time stamp. */
	private long timeStamp;
	
	/** The velocity. */
	private int velocity;
	
	/**
	 * Instantiates a new PID event.
	 *
	 * @param chan the chan
	 * @param tick the tick
	 * @param time the time
	 * @param velocity the velocity
	 */
	public PIDEvent(int chan,int tick,long time,int velocity){
		setGroup(chan);
		setValue(tick);
		setTimeStamp(time);
		setVelocity(velocity);
	}
	
	/**
	 * Instantiates a new PID event.
	 *
	 * @param data the data
	 */
	public PIDEvent(BowlerDatagram data){
		if(!data.getRPC().contains("_pid"))
			throw new RuntimeException("Datagram is not a PID event");
		setGroup(data.getData().getByte(0));
		setValue(ByteList.convertToInt(data.getData().getBytes(1, 4),true));
		setTimeStamp(System.currentTimeMillis());
		setVelocity(ByteList.convertToInt(data.getData().getBytes(9, 4),true));
	}
	
	
	/**
	 * Sets the group.
	 *
	 * @param channel the new group
	 */
	public void setGroup(int channel) {
		this.channel = channel;
	}
	
	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public int getGroup() {
		return channel;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param ticks the new value
	 */
	public void setValue(int ticks) {
		this.ticks = ticks;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return ticks;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString(){
		return "PID Event: \n\tchan = "+channel+"\n\tvalue = "+ticks+"\n\ttime = "+timeStamp+"\n\tvelocity since last packet= "+velocity;
	}
	
	/**
	 * Sets the velocity.
	 *
	 * @param vel the new velocity
	 */
	public void setVelocity(int vel) {
		this.velocity = vel;
	}
	
	/**
	 * Gets the velocity.
	 *
	 * @return the velocity
	 */
	public int getVelocity() {
		return velocity;
	}
}
