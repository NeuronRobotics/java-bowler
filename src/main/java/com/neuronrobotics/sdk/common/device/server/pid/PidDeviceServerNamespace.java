package com.neuronrobotics.sdk.common.device.server.pid;

import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.namespace.bcs.pid.IExtendedPIDControl;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PDVelocityConfiguration;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;


public class PidDeviceServerNamespace extends BowlerAbstractDeviceServerNamespace implements IExtendedPIDControl {

	private IExtendedPIDControl device;

	public PidDeviceServerNamespace(MACAddress addr,IExtendedPIDControl device ) {
		super(addr, "bcs.pid.*;1.0;;");
		this.device = device;
		
	}

	@Override
	public Object[] process(Object[] data, String rpc, BowlerMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		// TODO Auto-generated method stub
		return device.ResetPIDChannel(group, valueToSetCurrentTo);
	}

	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {
		// TODO Auto-generated method stub
		return device.ConfigurePIDController(config);
	}

	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		// TODO Auto-generated method stub
		return device.getPIDConfiguration(group);
	}

	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		// TODO Auto-generated method stub
		return device.ConfigurePDVelovityController(config);
	}

	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		// TODO Auto-generated method stub
		return device.getPDVelocityConfiguration(group);
	}

	@Override
	public int getPIDChannelCount() {
		// TODO Auto-generated method stub
		return device.getPIDChannelCount();
	}

	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		// TODO Auto-generated method stub
		return device.SetPIDSetPoint(group, setpoint, seconds);
	}

	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		// TODO Auto-generated method stub
		return device.SetAllPIDSetPoint(setpoints, seconds);
	}

	@Override
	public int GetPIDPosition(int group) {
		// TODO Auto-generated method stub
		return device.GetPIDPosition(group);
	}

	@Override
	public int[] GetAllPIDPosition() {
		// TODO Auto-generated method stub
		return device.GetAllPIDPosition();
	}

	@Override
	public void addPIDEventListener(IPIDEventListener l) {
		device.addPIDEventListener(l);
	}

	@Override
	public void removePIDEventListener(IPIDEventListener l) {
		device.removePIDEventListener(l);
	}

	@Override
	public void flushPIDChannels(double time) {
		device.flushPIDChannels(time);
	}

	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond,
			double seconds) throws PIDCommandException {
		// TODO Auto-generated method stub
		return device.SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
	}

	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)
			throws PIDCommandException {
		// TODO Auto-generated method stub
		return device.SetPDVelocity(group, unitsPerSecond, seconds);
	}

	@Override
	public PIDChannel getPIDChannel(int group) {
		// TODO Auto-generated method stub
		return device.getPIDChannel(group);
	}

	@Override
	public boolean killAllPidGroups() {
		// TODO Auto-generated method stub
		return device.killAllPidGroups();
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean runOutputHysteresisCalibration(int group) {
		// TODO Auto-generated method stub
		return device.runOutputHysteresisCalibration(group);
	}

}
