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

	public PidNamespaceImp(BowlerAbstractDevice device) {
		super(device);
	}

	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) throws DeviceConnectionException {
		Object[] args = new Object[]{group,valueToSetCurrentTo};
		getDevice().send("bcs.pid.*",BowlerMethod.POST,"rpid",args);
		return true;
	}

	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {		
		getDevice().send("bcs.pid.*",BowlerMethod.CRITICAL,"cpid",config.getArgs());
		return true;
	}

	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		
		return new PIDConfiguration(getDevice().send("bcs.pid.*",BowlerMethod.GET,"cpid",new Object[]{group}));
	}

	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPIDChannelCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int GetPIDPosition(int group) {
		// TODO Auto-generated method stub
		return 0;
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
