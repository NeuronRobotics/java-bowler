package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace;

// TODO: Auto-generated Javadoc
/**
 * The Class PIDChannel.
 */
public class PIDChannel {
	
	/** The pid. */
	private IPidControlNamespace pid;
	
	/** The index. */
	private int index;
	
	/** The target value. */
	private int targetValue;
	
	/** The current cached position. */
	private int currentCachedPosition;
	
	/** The PID event listeners. */
	private ArrayList<IPIDEventListener> PIDEventListeners = new ArrayList<IPIDEventListener>();
	
	/**
	 * Instantiates a new PID channel.
	 *
	 * @param p the p
	 * @param i the i
	 */
	public PIDChannel(IPidControlNamespace p, int i) {
		setPid(p);
		index=i;
	}

	/**
	 * Sets the pid set point.
	 *
	 * @param setpoint the setpoint
	 * @param seconds the seconds
	 * @return true, if successful
	 */
	public boolean SetPIDSetPoint(int setpoint,double seconds){
		
		return getPid().SetPIDSetPoint(index, setpoint, seconds);
	}
	
	/**
	 * Sets the pid interpolated velocity.
	 *
	 * @param unitsPerSecond the units per second
	 * @param seconds the seconds
	 * @return true, if successful
	 * @throws PIDCommandException the PID command exception
	 */
	public boolean SetPIDInterpolatedVelocity( int unitsPerSecond, double seconds) throws PIDCommandException {
		return getPid().SetPIDInterpolatedVelocity(index, unitsPerSecond, seconds);
	}
	
	/**
	 * Sets the pd velocity.
	 *
	 * @param unitsPerSecond the units per second
	 * @param seconds the seconds
	 * @return true, if successful
	 * @throws PIDCommandException the PID command exception
	 */
	public boolean SetPDVelocity( int unitsPerSecond, double seconds) throws PIDCommandException {
		return getPid().SetPDVelocity(index, unitsPerSecond, seconds);
	}
	
	/**
	 * Gets the pid position.
	 *
	 * @return the int
	 */
	public int GetPIDPosition() {
		return getPid().GetPIDPosition(index);
	}
	
	/**
	 * Configure pid controller.
	 *
	 * @param config the config
	 * @return true, if successful
	 */
	public boolean ConfigurePIDController(PIDConfiguration config) {
		config.setGroup(index);
		return getPid().ConfigurePIDController(config);
	}

	
	/**
	 * Gets the PID configuration.
	 *
	 * @return the PID configuration
	 */
	public PIDConfiguration getPIDConfiguration() {
		return getPid().getPIDConfiguration(index);
	}
	
	/**
	 * Reset pid channel.
	 *
	 * @return true, if successful
	 */
	public boolean ResetPIDChannel() {
		return getPid().ResetPIDChannel(index,0);
	}

	
	/**
	 * Reset pid channel.
	 *
	 * @param valueToSetCurrentTo the value to set current to
	 * @return true, if successful
	 */
	public boolean ResetPIDChannel( int valueToSetCurrentTo) {
		return getPid().ResetPIDChannel(index,valueToSetCurrentTo);
	}

	/**
	 * Sets the pid.
	 *
	 * @param p the new pid
	 */
	public void setPid(IPidControlNamespace p) {
		pid = p;
		pid.addPIDEventListener(new IPIDEventListener() {
			@Override
			public void onPIDReset(int group, int currentValue) {
				if(group==index){
					firePIDResetEvent(index, currentValue);
				}
			}
			
			@Override
			public void onPIDLimitEvent(PIDLimitEvent e) {
				if(e.getGroup()==index){
					firePIDLimitEvent(e);
				}
			}
			
			@Override
			public void onPIDEvent(PIDEvent e) {
				if(e.getGroup()==index){
					firePIDEvent(e);
				}
			}
		});
	}


	/**
	 * Gets the pid.
	 *
	 * @return the pid
	 */
	public IPidControlNamespace getPid() {
		return pid;
	}
	
	/**
	 * Removes the pid event listener.
	 *
	 * @param l the l
	 */
	public void removePIDEventListener(IPIDEventListener l) {
			if(PIDEventListeners.contains(l))
				PIDEventListeners.remove(l);
		
	}
	
	/**
	 * Adds the pid event listener.
	 *
	 * @param l the l
	 */
	public void addPIDEventListener(IPIDEventListener l) {
			if(!PIDEventListeners.contains(l))
				PIDEventListeners.add(l);
		
	}
	
	/**
	 * Fire pid limit event.
	 *
	 * @param e the e
	 */
	public void firePIDLimitEvent(PIDLimitEvent e){
			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDLimitEvent(e);
	}
	
	/**
	 * Fire pid event.
	 *
	 * @param e the e
	 */
	public void firePIDEvent(PIDEvent e){

			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDEvent(e);
		
	}
	
	/**
	 * Fire pid reset event.
	 *
	 * @param group the group
	 * @param value the value
	 */
	public void firePIDResetEvent(int group,int value){
		for(IPIDEventListener l: PIDEventListeners)
			l.onPIDReset(group,value);
	}

	/**
	 * Flush.
	 *
	 * @param time the time
	 */
	public void flush(double time){
		SetPIDSetPoint(getCachedTargetValue(),time);
	}

	/**
	 * Sets the cached target value.
	 *
	 * @param targetValue the new cached target value
	 */
	public void setCachedTargetValue(int targetValue) {
		Log.info("Cacheing PID position group="+getGroup()+", setpoint="+targetValue+" ticks");
		this.targetValue = targetValue;
	}
	
	/**
	 * Gets the cached target value.
	 *
	 * @return the cached target value
	 */
	public int getCachedTargetValue() {
		return targetValue;
	}



	/**
	 * Sets the current cached position.
	 *
	 * @param currentCachedPosition the new current cached position
	 */
	public void setCurrentCachedPosition(int currentCachedPosition) {
		this.currentCachedPosition = currentCachedPosition;
	}



	/**
	 * Gets the current cached position.
	 *
	 * @return the current cached position
	 */
	public int getCurrentCachedPosition() {
		return currentCachedPosition;
	}

	/**
	 * Checks if is available.
	 *
	 * @return true, if is available
	 */
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return pid.isAvailable();
	}

	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public int getGroup() {
		// TODO Auto-generated method stub
		return index;
	}
	
}
