package com.neuronrobotics.sdk.namespace.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.DeviceConnectionException;
import com.neuronrobotics.sdk.pid.PDVelocityConfiguration;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;

public class PidNamespaceImp extends GenericPidNamespaceImp {
	private final String ns = "bcs.pid.*";
	public PidNamespaceImp(BowlerAbstractDevice device) {
		super(device);
	}

	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) throws DeviceConnectionException {
		Object[] args = new Object[]{group,valueToSetCurrentTo};
		getDevice().send(ns,BowlerMethod.POST,"rpid",args);
		return true;
	}

	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {		
		getDevice().send(ns,BowlerMethod.CRITICAL,"cpid",config.getArgs());
		return true;
	}

	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		
		return new PIDConfiguration(getDevice().send(ns,BowlerMethod.GET,"cpid",new Object[]{group}));
	}

	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		getDevice().send(ns,BowlerMethod.CRITICAL,"cpdv",config.getArgs());
		return true;
	}

	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		Object [] args = getDevice().send(ns,BowlerMethod.GET,"cpdv",new Object[]{group});
		return new PDVelocityConfiguration(args);
	}

	@Override
	public int getPIDChannelCount() {
		Object [] args = getDevice().send(ns,BowlerMethod.GET,"gpdc",new Object[]{});
		return (Integer)args[0];
	}

	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		getDevice().send(ns,BowlerMethod.POST,"_pid",new Object[]{group,setpoint,seconds});
		return true;
	}

	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		getDevice().send(ns,BowlerMethod.POST,"apid",new Object[]{seconds,setpoints});
		return false;
	}

	@Override
	public int GetPIDPosition(int group) {
		Object [] args = getDevice().send(ns,BowlerMethod.GET,"_pid",new Object[]{group});
		return (Integer)args[0];
	}

	@Override
	public int[] GetAllPIDPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)
			throws PIDCommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PIDChannel getPIDChannel(int group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean killAllPidGroups() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}
	
}
