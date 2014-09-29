package com.neuronrobotics.sdk.namespace.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.DeviceConnectionException;
import com.neuronrobotics.sdk.pid.PDVelocityConfiguration;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class PidNamespaceImp extends GenericPidNamespaceImp implements IExtendedPIDControl  {
	private final String ns = "bcs.pid.*";
	
	
	
	public PidNamespaceImp(BowlerAbstractDevice device) {
		super(device);
	}
	
	private Object[] send(BowlerMethod method, String rpcString, Object[] arguments){
		return getDevice().send(ns,method,rpcString,arguments,2);
	}

	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) throws DeviceConnectionException {
		Object[] args = new Object[]{group,valueToSetCurrentTo};
		send(BowlerMethod.POST,
				"rpid",
				args);
		return true;
	}

	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {		
		send(BowlerMethod.CRITICAL,
				"cpid",
				config.getArgs());
		return true;
	}

	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		
		return new PIDConfiguration(send(BowlerMethod.GET,
				"cpid",
				new Object[]{group}));
	}

	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		send(BowlerMethod.CRITICAL,
				"cpdv",
				config.getArgs());
		return true;
	}

	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		Object [] args = send(BowlerMethod.GET,
				"cpdv",
				new Object[]{group});
		return new PDVelocityConfiguration(args);
	}

	private Integer channelCount =null;
	@Override
	public int getPIDChannelCount() {
		if(channelCount == null){
			Object [] args = send(BowlerMethod.GET,
					"gpdc",
					new Object[]{});
			channelCount = (Integer)args[0];
		}
		return channelCount.intValue();
	}

	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		send(BowlerMethod.POST,
				"_pid",
				new Object[]{	group,
								setpoint,
								(int)(seconds*1000)});
		return true;
	}

	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		send(BowlerMethod.POST,
				"apid",
				new Object[]{	new Integer((int) (seconds*1000)),
								setpoints});
		return true;
	}

	@Override
	public int GetPIDPosition(int group) {
		Object [] args = send(BowlerMethod.GET,
				"_pid",
				new Object[]{group});
		if((Integer)args[0] != group){
			throw new RuntimeException("Channel ID did not match");
		}
		return (Integer)args[1];
	}

	@Override
	public int[] GetAllPIDPosition() {
		Object [] args = send(BowlerMethod.GET,
				"apid",
				new Object[]{});
		int[] data=new int[((Integer[])args[0]).length];
		for(int i=0;i<data.length;i++){
			data[i] = ((Integer[])args[0])[i];
		}
		
		return data;
	}

	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)throws PIDCommandException {
		send(BowlerMethod.POST,
				"_vpd",
				new Object[]{	group,
								unitsPerSecond,
								(int)(seconds*1000)});
		return true;
	}

	@Override
	public boolean killAllPidGroups() {
		send(BowlerMethod.CRITICAL,
				"kpid",
				new Object[]{});
		return true;
	}

	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		//Log.debug("\nPID ASYNC<<"+data);
		if(data.getRPC().contains("_pid")){
			
			PIDEvent e =new PIDEvent(data);
	
			firePIDEvent(e);
		}
		if(data.getRPC().contains("apid")){
			int [] pos = new int[getNumberOfChannels()];
			for(int i=0;i<getNumberOfChannels();i++) {
				pos[i] = ByteList.convertToInt( data.getData().getBytes(i*4, 4),true);
				PIDEvent e =new PIDEvent(i,pos[i],System.currentTimeMillis(),0);
				firePIDEvent(e);
			}	
		}
		if(data.getRPC().contains("pidl")){
			firePIDLimitEvent(new PIDLimitEvent(data));
		}
	}

	@Override
	public boolean runOutputHysteresisCalibration(int group) {
		send(BowlerMethod.POST,
				"hist",
				new Object[]{group});
		return true;
	}
	
}
