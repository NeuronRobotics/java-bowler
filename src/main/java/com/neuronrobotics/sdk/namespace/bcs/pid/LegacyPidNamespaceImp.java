package com.neuronrobotics.sdk.namespace.bcs.pid;

import com.neuronrobotics.sdk.commands.bcs.pid.ConfigurePDVelocityCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ConfigurePIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ControlAllPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ControlPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.GetPIDChannelCountCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.KillAllPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.PDVelocityCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ResetPIDCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.PDVelocityConfiguration;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class LegacyPidNamespaceImp.
 */
public class LegacyPidNamespaceImp extends AbstractPidNamespaceImp {

	
	/**
	 * Instantiates a new legacy pid namespace imp.
	 *
	 * @param device the device
	 */
	public LegacyPidNamespaceImp(BowlerAbstractDevice device) {
		super(device);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.AbstractPidNamespaceImp#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
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

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ResetPIDChannel
	 */
	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		BowlerDatagram rst = getDevice().send(new  ResetPIDCommand((char) group,valueToSetCurrentTo));
		if(rst==null)
			return false;
		int val = GetPIDPosition(group);
		firePIDResetEvent(group,val);
		return true;
	}

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ConfigurePIDController
	 */
	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return getDevice().send(new  ConfigurePIDCommand(config))!=null;
	}

	@Override
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDConfiguration
	 */
	public PIDConfiguration getPIDConfiguration(int group) {
		BowlerDatagram conf = getDevice().send(new ConfigurePIDCommand( (char) group) );
		PIDConfiguration back=new PIDConfiguration (conf);
		return back;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ConfigurePDVelovityController(com.neuronrobotics.sdk.pid.PDVelocityConfiguration)
	 */
	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		return getDevice().send(new ConfigurePDVelocityCommand(config))!=null;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPDVelocityConfiguration(int)
	 */
	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		// TODO Auto-generated method stub
		return new PDVelocityConfiguration(getDevice().send(new ConfigurePDVelocityCommand(group)));
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDChannelCount()
	 */
	@Override
	public int getPIDChannelCount() {
		if(getChannelCount()==null){
			BowlerDatagram dg = getDevice().send (new GetPIDChannelCountCommand());
			setChannelCount(ByteList.convertToInt(dg.getData().getBytes(0, 4)));
		}
		return getChannelCount();
	}

	@Override
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPIDSetPoint
	 */
	public boolean SetPIDSetPoint(int group,int setpoint,double seconds){
		getPIDChannel(group).setCachedTargetValue(setpoint);
		Log.info("Setting PID position group="+group+", setpoint="+setpoint+" ticks, time="+seconds+" sec.");
		return getDevice().send(new  ControlPIDCommand((char) group,setpoint, seconds))!=null;
	}

	@Override
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetAllPIDSetPoint
	 */
	public boolean SetAllPIDSetPoint(int []setpoints,double seconds){
		int[] sp;
		if(setpoints.length<getNumberOfChannels()) {
			sp = new int[getNumberOfChannels()];
			for(int i=0;i<sp.length;i++) {
				sp[i]=getPIDChannel(i).getCachedTargetValue();
			}
			for(int i=0;i<setpoints.length;i++) {
				sp[i]=setpoints[i];
			}
		}else {
			sp=setpoints;
		}
		for(int i=0;i<getNumberOfChannels();i++){
			getPIDChannel(i).setCachedTargetValue(sp[i]);
		}
		return getDevice().send(new  ControlAllPIDCommand(sp, seconds))!=null;
	}

	@Override
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#GetPIDPosition
	 */
	public int GetPIDPosition(int group) {
		BowlerDatagram b = getDevice().send(new  ControlPIDCommand((char) group));
		return ByteList.convertToInt(b.getData().getBytes(	1,//Starting index
															4),//number of bytes
															true);//True for signed data
	}
	@Override
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#GetAllPIDPosition
	 */
	public int [] GetAllPIDPosition() {
		Log.debug("Getting All PID Positions");
		BowlerDatagram b = getDevice().send(new ControlAllPIDCommand());
		ByteList data = b.getData();
		int [] back = new int[data.size()/4];
		for(int i=0;i<back.length;i++) {
			int start = i*4;
			byte [] tmp = data.getBytes(start, 4);
			back[i] = ByteList.convertToInt( tmp,true);
		}
		if(back.length != getNumberOfChannels()){
			lastPacketTime =  new long[back.length];
			for(int i=0;i<back.length;i++){
				PIDChannel c =new PIDChannel(this,i);
				c.setCachedTargetValue(back[i]);
				getChannels().add(c);
			}
		}
		return back;
	}


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPDVelocity
	 */
	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)throws PIDCommandException {
		try{
			Log.debug("Setting hardware velocity control");
			return  getDevice().send(new PDVelocityCommand(group, unitsPerSecond, seconds))!=null;
		}catch (Exception ex){
			Log.error("Failed! Setting interpolated velocity control..");
			return SetPIDInterpolatedVelocity( group, unitsPerSecond,  seconds);
		}
	}


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#illAllPidGroups
	 */
	@Override
	public boolean killAllPidGroups() {
		getDevice().getConnection().setSynchronusPacketTimeoutTime(10000);
		return getDevice().send(new KillAllPIDCommand())==null;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IExtendedPIDControl#runOutputHysteresisCalibration(int)
	 */
	@Override
	public boolean runOutputHysteresisCalibration(int group) {
		throw new RuntimeException("This method is not implemented in this version of the namespace");
	}


	
}
