package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;

public class PIDEvent {
	private int channel;
	private int ticks; 
	private long timeStamp;
	private int velocity;
	public PIDEvent(int chan,int tick,long time,int velocity){
		setGroup(chan);
		setValue(tick);
		setTimeStamp(time);
		setVelocity(velocity);
	}
	public PIDEvent(BowlerDatagram data){
		if(!data.getRPC().contains("_pid"))
			throw new RuntimeException("Datagram is not a PID event");
		setGroup(data.getData().getByte(0));
		setValue(ByteList.convertToInt(data.getData().getBytes(1, 4),true));
		setTimeStamp(ByteList.convertToInt(data.getData().getBytes(5, 4),false));
		setVelocity(ByteList.convertToInt(data.getData().getBytes(9, 4),true));
	}
	public void setGroup(int channel) {
		this.channel = channel;
	}
	public int getGroup() {
		return channel;
	}
	public void setValue(int ticks) {
		this.ticks = ticks;
	}
	public int getValue() {
		return ticks;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	@Override 
	public String toString(){
		return "PID Event: \n\tchan = "+channel+"\n\tvalue = "+ticks+"\n\ttime = "+timeStamp+"\n\tvelocity since last packet= "+velocity;
	}
	public void setVelocity(int vel) {
		this.velocity = vel;
	}
	public int getVelocity() {
		return velocity;
	}
}
