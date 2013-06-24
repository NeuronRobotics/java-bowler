package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.io.GetChannelModeCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ConfigurePDVelocityCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ConfigurePIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ControlAllPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ControlPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.GetPIDChannelCountCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.KillAllPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.PDVelocityCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ResetPIDCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.namespace.bcs.pid.GenericPidNamespaceImp;
import com.neuronrobotics.sdk.namespace.bcs.pid.LegacyPidNamespaceImp;
import com.neuronrobotics.sdk.namespace.bcs.pid.PidNamespaceImp;

/**
 * This class is a generic implementation of the PID system. This can be used as a template, superclass or internal object class for 
 * use with and device that implements the IPIDControl interface. 
 * @author hephaestus
 *
 */
public class GenericPIDDevice extends BowlerAbstractDevice implements IPIDControl {
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
		if(implementation == null){
			if(hasNamespace("bcs.pid.*;0.3;;")){
				implementation = new LegacyPidNamespaceImp(this);
			}
			else{
				implementation = new PidNamespaceImp(this);
			}
		}
		isInit = true;
	}

	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		return implementation.ResetPIDChannel(group, valueToSetCurrentTo);
	}

	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return implementation.ConfigurePIDController(config);
	}

	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		return implementation.getPIDConfiguration(group);
	}

	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		return implementation.ConfigurePDVelovityController(config);
	}

	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		return implementation.getPDVelocityConfiguration(group);
	}

	@Override
	public int getPIDChannelCount() {
		return implementation.getNumberOfChannels();
	}

	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		return implementation.SetPIDSetPoint(group, setpoint, seconds);
	}

	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		return implementation.SetAllPIDSetPoint(setpoints, seconds);
	}

	@Override
	public int GetPIDPosition(int group) {
		return implementation.GetPIDPosition(group);
	}

	@Override
	public int[] GetAllPIDPosition() {
		return implementation.GetAllPIDPosition();
	}

	@Override
	public void addPIDEventListener(IPIDEventListener l) {
		implementation.addPIDEventListener(l);
	}

	@Override
	public void removePIDEventListener(IPIDEventListener l) {
		implementation.removePIDEventListener(l);
	}

	@Override
	public void flushPIDChannels(double time) {
		implementation.flushPIDChannels(time);
	}

	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond,
			double seconds) throws PIDCommandException {
		return implementation.SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
	}

	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)
			throws PIDCommandException {
		return implementation.SetPDVelocity(group, unitsPerSecond, seconds);
	}

	@Override
	public PIDChannel getPIDChannel(int group) {
		return implementation.getPIDChannel(group);
	}

	@Override
	public boolean killAllPidGroups() {
		return implementation.killAllPidGroups();
	}

	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		implementation.onAsyncResponse(data);
	}
	
	protected void firePIDResetEvent(int group, int val) {
		implementation.firePIDResetEvent(group, val);
	}
	protected void firePIDEvent(PIDEvent pidEvent) {
		implementation.firePIDEvent(pidEvent);
	}
	public ArrayList<PIDChannel> getChannels() {
		return implementation.getChannels();
	}
	public void setChannels(ArrayList<PIDChannel> channels) {
		implementation.setChannels(channels);
	}
	
}
