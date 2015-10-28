package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NoConnectionAvailableException;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualGenericPIDDevice.
 */
public class VirtualGenericPIDDevice extends GenericPIDDevice{
	
	/** The Constant threadTime. */
	private static final long threadTime=10;
	
	/** The drive threads. */
	private ArrayList<LinearInterpolationEngine>  driveThreads = new  ArrayList<LinearInterpolationEngine>();
	
	/** The configs. */
	private ArrayList<PIDConfiguration>  configs = new  ArrayList<PIDConfiguration>();
	
	/** The P dconfigs. */
	private ArrayList<PDVelocityConfiguration>  PDconfigs = new  ArrayList<PDVelocityConfiguration>();
	
	/** The sync. */
	SyncThread sync = new SyncThread ();
	
	/** The max ticks per second. */
	private double maxTicksPerSecond;
	
	/** The num channels. */
	private int numChannels = 24;
	
	/**
	 * Instantiates a new virtual generic pid device.
	 */
	public  VirtualGenericPIDDevice( ) {
		this(1000000);
	}
	
	/**
	 * Instantiates a new virtual generic pid device.
	 *
	 * @param maxTicksPerSecond the max ticks per second
	 */
	public  VirtualGenericPIDDevice( double maxTicksPerSecond) {
		this.setMaxTicksPerSecond(maxTicksPerSecond);
		getImplementation().setChannelCount(new Integer(numChannels));
		GetAllPIDPosition();
		for(int i=0; i<numChannels;i++){
			configs.add(new PIDConfiguration());
			PDconfigs.add(new PDVelocityConfiguration());
		}
			
		sync.start();
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#ConfigurePDVelovityController(com.neuronrobotics.sdk.pid.PDVelocityConfiguration)
	 */
	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		PDconfigs.set(config.getGroup(), config);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#getPDVelocityConfiguration(int)
	 */
	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		return PDconfigs.get(group);
	}
	
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#ConfigurePIDController(com.neuronrobotics.sdk.pid.PIDConfiguration)
	 */
	public boolean ConfigurePIDController(PIDConfiguration config) { 
		configs.set(config.getGroup(), config);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#getPIDConfiguration(int)
	 */
	public PIDConfiguration getPIDConfiguration(int group) {
		return configs.get(group);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#getNamespaces()
	 */
	@Override 
	public ArrayList<String> getNamespaces(){
		ArrayList<String> s = new ArrayList<String>();
		s.add("bcs.pid.*");
		return s;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#killAllPidGroups()
	 */
	@Override
	public boolean killAllPidGroups() {
		for(PIDConfiguration c:configs)
			c.setEnabled(false);
		return true;
	}
	

	/**
	 * since there is no connection, this is an easy to nip off com functionality.
	 *
	 * @param command the command
	 * @return the bowler datagram
	 * @throws NoConnectionAvailableException the no connection available exception
	 * @throws InvalidResponseException the invalid response exception
	 */
	@Override
	public BowlerDatagram send(BowlerAbstractCommand command) throws NoConnectionAvailableException, InvalidResponseException {	
		RuntimeException r = new RuntimeException("This method is never supposed to be called in the virtual PID");
		r.printStackTrace();
		throw r;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#ResetPIDChannel(int, int)
	 */
	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		driveThreads.get(group).ResetEncoder(valueToSetCurrentTo);
		int val = GetPIDPosition(group);
		firePIDResetEvent(group,val);
		return true;
	}

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#SetPIDSetPoint(int, int, double)
	 */
	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		Log.info("Virtual setpoint, group="+group+" setpoint="+setpoint);
		driveThreads.get(group).SetPIDSetPoint(setpoint, seconds);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#SetPDVelocity(int, int, double)
	 */
	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)throws PIDCommandException {
		if(unitsPerSecond>getMaxTicksPerSecond())
			throw new RuntimeException("Saturated PID on channel: "+group+" Attempted Ticks Per Second: "+unitsPerSecond+", when max is"+getMaxTicksPerSecond()+" set: "+getMaxTicksPerSecond()+" sec: "+seconds);
		if(unitsPerSecond<-getMaxTicksPerSecond())
			throw new RuntimeException("Saturated PID on channel: "+group+" Attempted Ticks Per Second: "+unitsPerSecond+", when max is"+getMaxTicksPerSecond()+" set: "+getMaxTicksPerSecond()+" sec: "+seconds);
		if(seconds<0.1 && seconds>-0.1){
			//System.out.println("Setting virtual velocity="+unitsPerSecond);
			driveThreads.get(group).SetVelocity(unitsPerSecond);
		}
		else{
			SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#flushPIDChannels
	 */
	@Override
	public void flushPIDChannels(double time) {
		int [] data = new int[getChannels().size()];
		for(int i=0;i<data.length;i++){
			data[i]=getPIDChannel(i).getCachedTargetValue();
		}
		Log.info("Flushing in "+time+"ms");
		SetAllPIDSetPoint(data, time);
	}
	
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#SetAllPIDSetPoint(int[], double)
	 */
	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		sync.setPause(true);
		for(int i=0;i<setpoints.length;i++){
			SetPIDSetPoint(i,  setpoints[i], seconds);
		}
		sync.setPause(false);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#GetPIDPosition(int)
	 */
	@Override
	public int GetPIDPosition(int group) {
		// TODO Auto-generated method stub
		return driveThreads.get(group).getPosition();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#isAvailable()
	 */
	@Override
	public boolean isAvailable(){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#GetAllPIDPosition()
	 */
	@Override
	public int[] GetAllPIDPosition() {
		//This is the trigger to populate the number of PID channels
		int [] back = new int[numChannels];

		setChannels(  new ArrayList<PIDChannel>());
		//lastPacketTime =  new long[back.length];
		for(int i=0;i<back.length;i++){
			back[i]=0;
			PIDChannel c =new PIDChannel(this,i);
			c.setCachedTargetValue(back[i]);
			getChannels().add(c);
			PIDConfiguration conf =new PIDConfiguration();
			LinearInterpolationEngine d = new LinearInterpolationEngine(i,conf);
			driveThreads.add(d);
			configs.add(conf);
		}
		return back;
	}
	
	
	/**
	 * Sets the max ticks per second.
	 *
	 * @param maxTicksPerSecond the new max ticks per second
	 */
	public void setMaxTicksPerSecond(double maxTicksPerSecond) {
		this.maxTicksPerSecond = maxTicksPerSecond;
	}
	
	/**
	 * Gets the max ticks per second.
	 *
	 * @return the max ticks per second
	 */
	public double getMaxTicksPerSecond() {
		return maxTicksPerSecond;
	}



	/**
	 * This class is designed to simulate a wheel driveing with a perfect controller.
	 *
	 * @author hephaestus
	 */
	private class SyncThread extends Thread{
		
		/** The pause. */
		private boolean pause =false;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			setName("Bowler Platform Virtual PID sync thread");
			while(true) {
				try {Thread.sleep(threadTime);} catch (InterruptedException e) {}
				while(isPause()){
					ThreadUtil.wait(10);
				}
				long time = System.currentTimeMillis();
				for(LinearInterpolationEngine dr : driveThreads){
					if(dr.update()){
						try{
							firePIDEvent(new PIDEvent(dr.getChan(), (int)dr.getTicks(), time,0));
						}catch (NullPointerException ex){
							//initialization issue, let it work itself out
						}catch (Exception ex){
							ex.printStackTrace();
						}
					}
				}
			}
		}
	
		/**
		 * Checks if is pause.
		 *
		 * @return true, if is pause
		 */
		public boolean isPause() {
			return pause;
		}
		
		/**
		 * Sets the pause.
		 *
		 * @param pause the new pause
		 */
		public void setPause(boolean pause) {
			if(pause)
				try {Thread.sleep(threadTime*2);} catch (InterruptedException e) {}
			this.pause = pause;
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#connect()
	 */
	@Override
	public boolean connect(){
		fireConnectEvent();
		return true;
	}
	
	/**
	 * This method tells the connection object to disconnect its pipes and close out the connection. Once this is called, it is safe to remove your device.
	 */
	@Override
	public void disconnect(){
		fireDisconnectEvent();
		
	}
	
}
