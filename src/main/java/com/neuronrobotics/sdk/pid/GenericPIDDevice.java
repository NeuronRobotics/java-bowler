package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.namespace.bcs.pid.AbstractPidNamespaceImp;
import com.neuronrobotics.sdk.namespace.bcs.pid.IExtendedPIDControl;
import com.neuronrobotics.sdk.namespace.bcs.pid.LegacyPidNamespaceImp;
import com.neuronrobotics.sdk.namespace.bcs.pid.PidNamespaceImp;

// TODO: Auto-generated Javadoc
/**
 * This class is a generic implementation of the PID system. This can be used as a template, superclass or internal object class for 
 * use with and device that implements the IPIDControl interface. 
 * @author hephaestus
 *
 */
public class GenericPIDDevice extends BowlerAbstractDevice implements IExtendedPIDControl {
	
	/** The is init. */
	private boolean isInit=false;
	
	/** The implementation. */
	private AbstractPidNamespaceImp implementation;
	
	/**
	 * Instantiates a new generic pid device.
	 */
	public GenericPIDDevice() {
		setAddress(new MACAddress(MACAddress.BROADCAST));
	}
	
	/**
	 * Instantiates a new generic pid device.
	 *
	 * @param connection the connection
	 */
	public GenericPIDDevice(BowlerAbstractConnection connection) {
		setAddress(new MACAddress(MACAddress.BROADCAST));
		setConnection(connection);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#setConnection(com.neuronrobotics.sdk.common.BowlerAbstractConnection)
	 */
	@Override
	public void setConnection(BowlerAbstractConnection connection) {
		super.setConnection(connection);
		if(connection.isConnected())
			init();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#connect()
	 */
	@Override
	public boolean connect(){
		if(super.connect()){
			init();
			return true;
		}
		return false;
	}
	
	/**
	 * Inits the.
	 */
	private void init(){
		if(isInit){
			return;
		}
		if(getImplementation() == null){
			
		}
		isInit = true;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ResetPIDChannel(int, int)
	 */
	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		return getImplementation().ResetPIDChannel(group, valueToSetCurrentTo);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ConfigurePIDController(com.neuronrobotics.sdk.pid.PIDConfiguration)
	 */
	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return getImplementation().ConfigurePIDController(config);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDConfiguration(int)
	 */
	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		return getImplementation().getPIDConfiguration(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ConfigurePDVelovityController(com.neuronrobotics.sdk.pid.PDVelocityConfiguration)
	 */
	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		return getImplementation().ConfigurePDVelovityController(config);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPDVelocityConfiguration(int)
	 */
	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		return getImplementation().getPDVelocityConfiguration(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDChannelCount()
	 */
	@Override
	public int getPIDChannelCount() {
		return getImplementation().getNumberOfChannels();
	}
	
	/**
	 * Gets the number of channels.
	 *
	 * @return the number of channels
	 */
	//This is added for backward compatibility
	public int getNumberOfChannels(){
		return getPIDChannelCount();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPIDSetPoint(int, int, double)
	 */
	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		return getImplementation().SetPIDSetPoint(group, setpoint, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetAllPIDSetPoint(int[], double)
	 */
	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		return getImplementation().SetAllPIDSetPoint(setpoints, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#GetPIDPosition(int)
	 */
	@Override
	public int GetPIDPosition(int group) {
		return getImplementation().GetPIDPosition(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#GetAllPIDPosition()
	 */
	@Override
	public int[] GetAllPIDPosition() {
		return getImplementation().GetAllPIDPosition();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#addPIDEventListener(com.neuronrobotics.sdk.pid.IPIDEventListener)
	 */
	@Override
	public void addPIDEventListener(IPIDEventListener l) {
		getImplementation().addPIDEventListener(l);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#removePIDEventListener(com.neuronrobotics.sdk.pid.IPIDEventListener)
	 */
	@Override
	public void removePIDEventListener(IPIDEventListener l) {
		getImplementation().removePIDEventListener(l);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#flushPIDChannels(double)
	 */
	@Override
	public void flushPIDChannels(double time) {
		getImplementation().flushPIDChannels(time);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPIDInterpolatedVelocity(int, int, double)
	 */
	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond,
			double seconds) throws PIDCommandException {
		return getImplementation().SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPDVelocity(int, int, double)
	 */
	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)
			throws PIDCommandException {
		return getImplementation().SetPDVelocity(group, unitsPerSecond, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDChannel(int)
	 */
	@Override
	public PIDChannel getPIDChannel(int group) {
		return getImplementation().getPIDChannel(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#killAllPidGroups()
	 */
	@Override
	public boolean killAllPidGroups() {
		return getImplementation().killAllPidGroups();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		getImplementation().onAsyncResponse(data);
	}
	
	/**
	 * Fire pid reset event.
	 *
	 * @param group the group
	 * @param val the val
	 */
	protected void firePIDResetEvent(int group, int val) {
		getImplementation().firePIDResetEvent(group, val);
	}
	
	/**
	 * Fire pid event.
	 *
	 * @param pidEvent the pid event
	 */
	protected void firePIDEvent(PIDEvent pidEvent) {
		getImplementation().firePIDEvent(pidEvent);
	}
	
	/**
	 * Gets the channels.
	 *
	 * @return the channels
	 */
	public ArrayList<PIDChannel> getChannels() {
		return getImplementation().getChannels();
	}
	
	/**
	 * Sets the channels.
	 *
	 * @param channels the new channels
	 */
	public void setChannels(ArrayList<PIDChannel> channels) {
		getImplementation().setChannels(channels);
	}
	
	/**
	 * Gets the implementation.
	 *
	 * @return the implementation
	 */
	public AbstractPidNamespaceImp getImplementation() {
		

		if(implementation==null){
			if(this instanceof VirtualGenericPIDDevice){
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
	
	/**
	 * Sets the implementation.
	 *
	 * @param implementation the new implementation
	 */
	public void setImplementation(AbstractPidNamespaceImp implementation) {
		this.implementation = implementation;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IExtendedPIDControl#runOutputHysteresisCalibration(int)
	 */
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
