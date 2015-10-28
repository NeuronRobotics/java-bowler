package com.neuronrobotics.sdk.namespace.bcs.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractPidNamespaceImp.
 */
public abstract class AbstractPidNamespaceImp implements IExtendedPIDControl {

	/** The PID event listeners. */
	private ArrayList<IPIDEventListener> PIDEventListeners = new ArrayList<IPIDEventListener>();
	
	/** The channels. */
	protected ArrayList<PIDChannel> channels = null;
	
	/** The last packet time. */
	protected long [] lastPacketTime = null;
	
	/** The device. */
	private BowlerAbstractDevice device;
	
	/** The channel count. */
	private Integer channelCount=null;
	
	/**
	 * Instantiates a new abstract pid namespace imp.
	 *
	 * @param device the device
	 */
	public AbstractPidNamespaceImp(BowlerAbstractDevice device){
		this.setDevice(device);
		addPIDEventListener(new IPIDEventListener() {
			public void onPIDReset(int group, int currentValue) {}
			public void onPIDLimitEvent(PIDLimitEvent e) {}
			public void onPIDEvent(PIDEvent e) {
				getPIDChannel(e.getGroup()).setCurrentCachedPosition(e.getValue());
			}
		});
	}


	/**
	 * Gets the cached position.
	 *
	 * @param group the group
	 * @return the int
	 */
	public int GetCachedPosition(int group) {
		return getPIDChannel(group).getCurrentCachedPosition();
	}

	/**
	 * Sets the cached position.
	 *
	 * @param group the group
	 * @param value the value
	 */
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
	 *
	 * @return the number of channels
	 */
	public int getNumberOfChannels(){
		return getChannels().size();
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
	
	/**
	 * Fire pid limit event.
	 *
	 * @param e the e
	 */
	public void firePIDLimitEvent(PIDLimitEvent e){
		synchronized(PIDEventListeners){
			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDLimitEvent(e);
		}
		//channels.get(e.getGroup()).firePIDLimitEvent(e);
	}
	
	/**
	 * Fire pid event.
	 *
	 * @param e the e
	 */
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
	
	/**
	 * Fire pid reset event.
	 *
	 * @param group the group
	 * @param value the value
	 */
	public void firePIDResetEvent(int group,int value){
		SetCachedPosition(group, value);
		for(IPIDEventListener l: PIDEventListeners)
			l.onPIDReset(group,value);
		//channels.get(group).firePIDResetEvent(group, value);
	}

	/**
	 * Gets the device.
	 *
	 * @return the device
	 */
	public BowlerAbstractDevice getDevice() {
		return device;
	}

	/**
	 * Sets the device.
	 *
	 * @param device the new device
	 */
	public void setDevice(BowlerAbstractDevice device) {
		this.device = device;
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDChannel
	 */
	@Override
	public PIDChannel getPIDChannel(int group) {
		if(getNumberOfChannels()==0) {
			getChannels();
		}
		while(!(group < getNumberOfChannels() )){
			PIDChannel c =new PIDChannel(this,group);
			getChannels().add(c);
		}
		return getChannels().get(group);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return device.isAvailable();
	}

	/**
	 * On async response.
	 *
	 * @param data the data
	 */
	public abstract void onAsyncResponse(BowlerDatagram data);


	/**
	 * Gets the channels.
	 *
	 * @return the channels
	 */
	public ArrayList<PIDChannel> getChannels() {
		if(channels==null){
			channels=new ArrayList<PIDChannel>();
			for(int i=0;i<getPIDChannelCount();i++){
				getPIDChannel(i);
			}
		}
		return channels;
	}


	/**
	 * Sets the channels.
	 *
	 * @param channels the new channels
	 */
	public void setChannels(ArrayList<PIDChannel> channels) {
		this.channels = channels;
	}


	/**
	 * Gets the channel count.
	 *
	 * @return the channel count
	 */
	public Integer getChannelCount() {
		return channelCount;
	}


	/**
	 * Sets the channel count.
	 *
	 * @param channelCount the new channel count
	 */
	public void setChannelCount(Integer channelCount) {
		if(channelCount == null)
			throw new RuntimeException("Must be set to a real value");
		this.channelCount = channelCount;
	}


}
