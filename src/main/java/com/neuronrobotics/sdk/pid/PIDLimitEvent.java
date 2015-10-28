package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class PIDLimitEvent.
 */
public class PIDLimitEvent {
	
	/** The channel. */
	private int channel;
	
	/** The ticks. */
	private int ticks; 
	
	/** The time stamp. */
	private long timeStamp;
	
	/** The limit type. */
	private PIDLimitEventType limitType;
	
	/**
	 * Instantiates a new PID limit event.
	 *
	 * @param chan the chan
	 * @param tick the tick
	 * @param type the type
	 * @param time the time
	 */
	public PIDLimitEvent(int chan,int tick,PIDLimitEventType type,long time){
		setGroup(chan);
		setLimitType(type);
		setValue(tick);
		setTimeStamp(time);
		
	}
	
	/**
	 * Instantiates a new PID limit event.
	 *
	 * @param data the data
	 */
	public PIDLimitEvent(BowlerDatagram data){
		if(!data.getRPC().contains("pidl"))
			throw new RuntimeException("Datagram is not a PID event");
		setGroup(data.getData().getByte(0));
		setLimitType(PIDLimitEventType.get(data.getData().getBytes(1, 1)[0]));
		setValue(ByteList.convertToInt(data.getData().getBytes(2, 4),true));
		setTimeStamp(ByteList.convertToInt(data.getData().getBytes(6, 4),false));
		
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
		return "chan="+channel+", Type="+limitType+", value="+ticks+", time="+timeStamp;
	}
	
	/**
	 * Sets the limit type.
	 *
	 * @param limitIndex the new limit type
	 */
	public void setLimitType(PIDLimitEventType limitIndex) {
		this.limitType = limitIndex;
	}
	
	/**
	 * Gets the limit type.
	 *
	 * @return the limit type
	 */
	public PIDLimitEventType getLimitType() {
		return limitType;
	}
}
