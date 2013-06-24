package com.neuronrobotics.sdk.namespace.bcs.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.IPIDControl;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public abstract class GenericPidNamespaceImp implements IPIDControl{

	private ArrayList<IPIDEventListener> PIDEventListeners = new ArrayList<IPIDEventListener>();
	protected ArrayList<PIDChannel> channels = new ArrayList<PIDChannel>();
	protected long [] lastPacketTime = null;
	private BowlerAbstractDevice device;

	public GenericPidNamespaceImp(BowlerAbstractDevice device){
		this.setDevice(device);
		addPIDEventListener(new IPIDEventListener() {
			public void onPIDReset(int group, int currentValue) {}
			public void onPIDLimitEvent(PIDLimitEvent e) {}
			public void onPIDEvent(PIDEvent e) {
				getPIDChannel(e.getGroup()).setCurrentCachedPosition(e.getValue());
			}
		});
	}


	public int GetCachedPosition(int group) {
		return getPIDChannel(group).getCurrentCachedPosition();
	}

	public void SetCachedPosition(int group, int value) {

		getPIDChannel(group).setCurrentCachedPosition(value);
	}
	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#flushPIDChannels
	 */
	@Override
	public void flushPIDChannels(double time) {
		int [] data = new int[getNumberOfChannels()];
		for(int i=0;i<getNumberOfChannels();i++){
			data[i]=getPIDChannel(i).getCachedTargetValue();
		}
		Log.info("Flushing in "+time+"ms");
		SetAllPIDSetPoint(data, time);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPIDInterpolatedVelocity
	 */
	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond, double seconds) throws PIDCommandException {
		long dist = (long)unitsPerSecond*(long)seconds;
		long delt = ((long) (GetCachedPosition(group))-dist);
		if(delt>2147483646 || delt<-2147483646){
			throw new PIDCommandException("(Current Position) - (Velocity * Time) too large: "+delt+"\nTry resetting the encoders");
		}
		return SetPIDSetPoint(group, (int) delt, seconds);
	}

	/**
	 * Gets the number of PID channels availible to the system. It is determined by how many PID channels the device reports
	 * back after a calling GetAllPIDPosition();
	 * @return
	 */
	public int getNumberOfChannels(){
		return channels.size();
	}
	
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#addPIDEventListener
	 */
	public void addPIDEventListener(IPIDEventListener l) {
		synchronized(PIDEventListeners){
			if(!PIDEventListeners.contains(l))
				PIDEventListeners.add(l);
		}
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#removePIDEventListener
	 */
	public void removePIDEventListener(IPIDEventListener l) {
		synchronized(PIDEventListeners){
			if(PIDEventListeners.contains(l))
				PIDEventListeners.remove(l);
		}
	}
	public void firePIDLimitEvent(PIDLimitEvent e){
		synchronized(PIDEventListeners){
			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDLimitEvent(e);
		}
		//channels.get(e.getGroup()).firePIDLimitEvent(e);
	}
	public void firePIDEvent(PIDEvent e){
		if(lastPacketTime != null){
			if(lastPacketTime[e.getGroup()]>e.getTimeStamp()){
				Log.error("This event timestamp is out of date, aborting"+e);
				return;
			}else{
				//Log.info("Pid event "+e);
				lastPacketTime[e.getGroup()]=e.getTimeStamp();
			}
		}
		
		synchronized(PIDEventListeners){
			SetCachedPosition(e.getGroup(), e.getValue());
			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDEvent(e);
		}
		//channels.get(e.getGroup()).firePIDEvent(e);
	}
	public void firePIDResetEvent(int group,int value){
		SetCachedPosition(group, value);
		for(IPIDEventListener l: PIDEventListeners)
			l.onPIDReset(group,value);
		//channels.get(group).firePIDResetEvent(group, value);
	}

	public BowlerAbstractDevice getDevice() {
		return device;
	}

	public void setDevice(BowlerAbstractDevice device) {
		this.device = device;
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDChannel
	 */
	@Override
	public PIDChannel getPIDChannel(int group) {
		if(getNumberOfChannels()==0) {
			GetAllPIDPosition();
		}
		while(!(group < getNumberOfChannels() )){
			PIDChannel c =new PIDChannel(this,group);
			channels.add(c);
		}
		return channels.get(group);
	}
	@Override
	public boolean isAvailable() {
		return device.isAvailable();
	}

	public abstract void onAsyncResponse(BowlerDatagram data);


}
