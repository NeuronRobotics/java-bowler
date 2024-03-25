package com.neuronrobotics.sdk.addons.kinematics.imu;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.time.TimeKeeper;

public class IMU extends TimeKeeper{
	private ArrayList<IMUUpdateListener> virtualListeneras = new ArrayList<IMUUpdateListener>();
	private ArrayList<IMUUpdateListener> hardwareListeneras = new ArrayList<IMUUpdateListener>();
	
	private IMUUpdate virtualState=new IMUUpdate(0.0,0.0,0.0,0.0,0.0,0.0,currentTimeMillis());
	private IMUUpdate hardwareState=new IMUUpdate(null,null,null,null,null,null,currentTimeMillis());
	
	public void addhardwareListeners(IMUUpdateListener l){
		if(!hardwareListeneras.contains(l))
			hardwareListeneras.add(l);
	}
	public void addvirtualListeners(IMUUpdateListener l){
		if(!virtualListeneras.contains(l))
			virtualListeneras.add(l);
	}
	
	public void removehardwareListeners(IMUUpdateListener l){
		if(hardwareListeneras.contains(l))
			hardwareListeneras.remove(l);
	}
	public void removevirtualListeners(IMUUpdateListener l){
		if(virtualListeneras.contains(l))
			virtualListeneras.remove(l);
	}
	public void clearhardwareListeners(){

			hardwareListeneras.clear();;
	}
	public void clearvirtualListeners(){
	
			virtualListeneras.clear();
	}
	public IMUUpdate getVirtualState() {
		
		return virtualState;
	}
	public void setVirtualState(IMUUpdate virtualState) {
		this.virtualState = virtualState;
		for(int i=0;i<virtualListeneras.size();i++){
			virtualListeneras.get(i).onIMUUpdate(virtualState);
		}
	}
	public IMUUpdate getHardwareState() {
		
		return hardwareState;
	}
	public void setHardwareState(IMUUpdate hardwareState) {
		this.hardwareState = hardwareState;
		for(int i=0;i<hardwareListeneras.size();i++){
			hardwareListeneras.get(i).onIMUUpdate(hardwareState);
		}
	}
	
	

}
