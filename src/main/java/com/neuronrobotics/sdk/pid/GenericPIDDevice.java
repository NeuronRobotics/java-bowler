package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.namespace.bcs.pid.GenericPidNamespaceImp;
import com.neuronrobotics.sdk.namespace.bcs.pid.IExtendedPIDControl;
import com.neuronrobotics.sdk.namespace.bcs.pid.LegacyPidNamespaceImp;
import com.neuronrobotics.sdk.namespace.bcs.pid.PidNamespaceImp;

/**
 * This class is a generic implementation of the PID system. This can be used as a template, superclass or internal object class for 
 * use with and device that implements the IPIDControl interface. 
 * @author hephaestus
 *
 */
public class GenericPIDDevice extends BowlerAbstractDevice implements IExtendedPIDControl {
	private boolean isInit=false;
	private GenericPidNamespaceImp implementation;
	
	public GenericPIDDevice() {
		setAddress(new MACAddress(MACAddress.BROADCAST));
	}
	public GenericPIDDevice(BowlerAbstractConnection connection) {
		setAddress(new MACAddress(MACAddress.BROADCAST));
		setConnection(connection);
	}
	
	@Override
	public void setConnection(BowlerAbstractConnection connection) {
		super.setConnection(connection);
		if(connection.isConnected())
			init();
	}
	
	@Override
	public boolean connect(){
		if(super.connect()){
			init();
			return true;
		}
		return false;
	}
	
	private void init(){
		if(isInit){
			return;
		}
		if(getImplementation() == null){
			
		}
		isInit = true;
	}

	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		return getImplementation().ResetPIDChannel(group, valueToSetCurrentTo);
	}

	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return getImplementation().ConfigurePIDController(config);
	}

	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		return getImplementation().getPIDConfiguration(group);
	}

	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		return getImplementation().ConfigurePDVelovityController(config);
	}

	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		return getImplementation().getPDVelocityConfiguration(group);
	}

	@Override
	public int getPIDChannelCount() {
		return getImplementation().getNumberOfChannels();
	}

	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		return getImplementation().SetPIDSetPoint(group, setpoint, seconds);
	}

	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		return getImplementation().SetAllPIDSetPoint(setpoints, seconds);
	}

	@Override
	public int GetPIDPosition(int group) {
		return getImplementation().GetPIDPosition(group);
	}

	@Override
	public int[] GetAllPIDPosition() {
		return getImplementation().GetAllPIDPosition();
	}

	@Override
	public void addPIDEventListener(IPIDEventListener l) {
		getImplementation().addPIDEventListener(l);
	}

	@Override
	public void removePIDEventListener(IPIDEventListener l) {
		getImplementation().removePIDEventListener(l);
	}

	@Override
	public void flushPIDChannels(double time) {
		getImplementation().flushPIDChannels(time);
	}

	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond,
			double seconds) throws PIDCommandException {
		return getImplementation().SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
	}

	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)
			throws PIDCommandException {
		return getImplementation().SetPDVelocity(group, unitsPerSecond, seconds);
	}

	@Override
	public PIDChannel getPIDChannel(int group) {
		return getImplementation().getPIDChannel(group);
	}

	@Override
	public boolean killAllPidGroups() {
		return getImplementation().killAllPidGroups();
	}

	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		getImplementation().onAsyncResponse(data);
	}
	
	protected void firePIDResetEvent(int group, int val) {
		getImplementation().firePIDResetEvent(group, val);
	}
	protected void firePIDEvent(PIDEvent pidEvent) {
		getImplementation().firePIDEvent(pidEvent);
	}
	public ArrayList<PIDChannel> getChannels() {
		return getImplementation().getChannels();
	}
	public void setChannels(ArrayList<PIDChannel> channels) {
		getImplementation().setChannels(channels);
	}
	public GenericPidNamespaceImp getImplementation() {
		

		if(implementation==null){
			if(this.getClass() == VirtualGenericPIDDevice.class){
				setImplementation(new LegacyPidNamespaceImp(this));
				return implementation;
			}else{
				if(hasNamespace("bcs.pid.*;0.3;;")){
					//Log.info("Using legacy PID namespace");
					setImplementation(new LegacyPidNamespaceImp(this));
				}
				else{
					//Log.info("Using new PID namespace");
					setImplementation(new PidNamespaceImp(this));
				}
			}
		}
		return implementation;
	}
	public void setImplementation(GenericPidNamespaceImp implementation) {
		this.implementation = implementation;
	}
	@Override
	public boolean runOutputHysteresisCalibration(int group) {
		try{
			return getImplementation().runOutputHysteresisCalibration(group);
		}catch(RuntimeException e){
			Log.error(e.getMessage());
			return false;
		}
		
	}
	
}
