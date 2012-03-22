package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.Log;

public class PIDChannel {
	private IPIDControl pid;
	private int index;
	private int targetValue;
	private int currentCachedPosition;
	
	private ArrayList<IPIDEventListener> PIDEventListeners = new ArrayList<IPIDEventListener>();
	
	public PIDChannel(IPIDControl p, int i) {
		setPid(p);
		index=i;
	}

	public boolean SetPIDSetPoint(int setpoint,double seconds){
		
		return getPid().SetPIDSetPoint(index, setpoint, seconds);
	}
	public boolean SetPIDInterpolatedVelocity( int unitsPerSecond, double seconds) throws PIDCommandException {
		return getPid().SetPIDInterpolatedVelocity(index, unitsPerSecond, seconds);
	}
	public boolean SetPDVelocity( int unitsPerSecond, double seconds) throws PIDCommandException {
		return getPid().SetPDVelocity(index, unitsPerSecond, seconds);
	}
	public int GetPIDPosition() {
		return getPid().GetPIDPosition(index);
	}
	
	public boolean ConfigurePIDController(PIDConfiguration config) {
		config.setGroup(index);
		return getPid().ConfigurePIDController(config);
	}

	
	public PIDConfiguration getPIDConfiguration() {
		return getPid().getPIDConfiguration(index);
	}
	
	public boolean ResetPIDChannel() {
		return getPid().ResetPIDChannel(index,0);
	}

	
	public boolean ResetPIDChannel( int valueToSetCurrentTo) {
		return getPid().ResetPIDChannel(index,valueToSetCurrentTo);
	}

	public void setPid(IPIDControl p) {
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


	public IPIDControl getPid() {
		return pid;
	}
	
	public void removePIDEventListener(IPIDEventListener l) {

			if(PIDEventListeners.contains(l))
				PIDEventListeners.remove(l);
		
	}
	
	public void addPIDEventListener(IPIDEventListener l) {

			if(!PIDEventListeners.contains(l))
				PIDEventListeners.add(l);
		
	}
	public void firePIDLimitEvent(PIDLimitEvent e){

			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDLimitEvent(e);
		
	}
	public void firePIDEvent(PIDEvent e){

			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDEvent(e);
		
	}
	public void firePIDResetEvent(int group,int value){
		for(IPIDEventListener l: PIDEventListeners)
			l.onPIDReset(group,value);
	}

	public void flush(double time){
		SetPIDSetPoint(getCachedTargetValue(),time);
	}

	public void setCachedTargetValue(int targetValue) {
		Log.info("Cacheing PID position group="+getGroup()+", setpoint="+targetValue+" ticks");
		this.targetValue = targetValue;
	}
	
	public int getCachedTargetValue() {
		return targetValue;
	}



	public void setCurrentCachedPosition(int currentCachedPosition) {
		this.currentCachedPosition = currentCachedPosition;
	}



	public int getCurrentCachedPosition() {
		return currentCachedPosition;
	}

	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return pid.isAvailable();
	}

	public int getGroup() {
		// TODO Auto-generated method stub
		return index;
	}
	
}
