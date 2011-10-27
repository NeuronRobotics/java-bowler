package com.neuronrobotics.addons.driving.virtual;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.pid.PDVelocityCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NoConnectionAvailableException;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class VirtualGenericPIDDevice extends GenericPIDDevice{
	private ArrayList<DriveThread>  driveThreads = new  ArrayList<DriveThread>();

	private double maxTicksPerSecond;
	
	private int numChannels = 10;
	
	
	public  VirtualGenericPIDDevice( double maxTicksPerSecond) {
		this.maxTicksPerSecond=maxTicksPerSecond;
		
	}
	/**
	 * since there is no connection, this is an easy to nip off com functionality
	 *
	 */
	@Override
	public BowlerDatagram send(BowlerAbstractCommand command) throws NoConnectionAvailableException, InvalidResponseException {	
		RuntimeException r = new RuntimeException("This method is never supposed to be called in the virtual PID");
		r.printStackTrace();
		throw r;
	}
	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		driveThreads.get(group).ResetEncoder(valueToSetCurrentTo);
		int val = GetPIDPosition(group);
		firePIDResetEvent(group,val);
		return true;
	}

	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		
		driveThreads.get(group).SetPIDSetPoint(setpoint, seconds);
		return true;
	}
	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)throws PIDCommandException {
		if(unitsPerSecond>maxTicksPerSecond)
			throw new RuntimeException("Saturated PID on channel: "+group+" Attempted Ticks Per Second: "+unitsPerSecond+", when max is"+maxTicksPerSecond+" set: "+maxTicksPerSecond+" sec: "+seconds);
		if(unitsPerSecond<-maxTicksPerSecond)
			throw new RuntimeException("Saturated PID on channel: "+group+" Attempted Ticks Per Second: "+unitsPerSecond+", when max is"+maxTicksPerSecond+" set: "+maxTicksPerSecond+" sec: "+seconds);
		if(seconds<0.1 && seconds>-0.1){
			//System.out.println("Setting virtual velocity="+unitsPerSecond);
			driveThreads.get(group).SetVelocity(unitsPerSecond);
		}
		else{
			SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
		}
		return true;
	}
	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		for(int i=0;i<setpoints.length;i++){
			 SetPIDSetPoint(i,  setpoints[i], seconds);
		}
		return true;
	}
	@Override
	public int GetPIDPosition(int group) {
		// TODO Auto-generated method stub
		return driveThreads.get(group).getPosition();
	}
	@Override
	public boolean isAvailable(){
		return true;
	}
	@Override
	public int[] GetAllPIDPosition() {
		//This is the trigger to populate the number of PID channels
		int [] back = new int[numChannels];
		if(back.length != channels.size()){
			channels =  new ArrayList<PIDChannel>();
			lastPacketTime =  new long[back.length];
			for(int i=0;i<back.length;i++){
				back[i]=0;
				PIDChannel c =new PIDChannel(this,i);
				c.setCachedTargetValue(back[i]);
				channels.add(c);
				DriveThread d = new DriveThread(i);
				d.start();
				driveThreads.add(d);
			}
		}
		return back;
	}

	
	
	/**
	 * This class is designed to simulate a wheel driveing with a perfect controller
	 * @author hephaestus
	 *
	 */
	
	
	private class DriveThread extends Thread{

		private static final long threadTime=200;
		
		private long ticks=0;
		private long lastTick=ticks;
		private long lastInterpolationTime;
		private long setPoint;
		private long duration;
		private long startTime;
		private long startPoint;
		boolean pause = false;
		private boolean velocityRun=false;
		private double unitsPerMs;
		private int chan;
		public DriveThread(int index){
			setChan(index);
		}
		public void SetVelocity(double unitsPerSecond) {
			//System.out.println("Setting velocity to "+unitsPerSecond+"ticks/second");
			this.unitsPerMs=unitsPerSecond/1000;
			lastInterpolationTime=System.currentTimeMillis();
			velocityRun=true;
		}
		public int getPosition() {
			return (int) ticks;
		}
		public synchronized  void SetPIDSetPoint(int setpoint,double seconds){
			velocityRun=false;
			pause=true;
			ThreadUtil.wait((int)(threadTime*2));
			double TPS = (double)setpoint/seconds;
			//Models motor saturation
			if(TPS >  maxTicksPerSecond){
				//seconds = (double)setpoint/maxTicksPerSeconds;
				throw new RuntimeException("Saturated PID on channel: "+chan+" Attempted Ticks Per Second: "+TPS+", when max is"+maxTicksPerSecond+" set: "+setpoint+" sec: "+seconds);
			}
			duration = (long) (seconds*1000);
			startTime=System.currentTimeMillis();
			setPoint=setpoint;
			startPoint = ticks;
			
			pause=false;
			//System.out.println("Setting Setpoint Ticks to: "+setPoint);
		}
		public void run() {
			while(true) {
				while(pause){
					ThreadUtil.wait(10);
				}
				try {Thread.sleep(threadTime);} catch (InterruptedException e) {}
				interpolate();
				if((ticks!=lastTick) && !pause) {
					lastTick=ticks;
					firePIDEvent(new PIDEvent(getChan(), (int)ticks, System.currentTimeMillis(),0));
				}
			}
		}
		public synchronized  void ResetEncoder(int value) {
			System.out.println("Resetting channel "+getChan());
			velocityRun=false;
			pause=true;
			ThreadUtil.wait((int)(threadTime*2));
			ticks=value;
			lastTick=value;
			setPoint=value;
			duration=0;
			startTime=System.currentTimeMillis();
			startPoint=value;
			pause=false;	
		}
		private void setChan(int chan) {
			this.chan = chan;
		}
		public int getChan() {
			return chan;
		}
		private void interpolate() {
			double back;
			double diffTime;
			if(duration > 0 ){
				diffTime = System.currentTimeMillis()-startTime;
				if((diffTime < duration) && (diffTime>0) ){
					double elapsed = 1-((duration-diffTime)/duration);
					double tmp=((float)startPoint+(float)(setPoint-startPoint)*elapsed);
					if(setPoint>startPoint){
						if((tmp>setPoint)||(tmp<startPoint))
							tmp=setPoint;
					}else{
						if((tmp<setPoint) || (tmp>startPoint))
							tmp=setPoint;
					}
					back=tmp;
				}else{
					// Fixes the overflow case and the timeout case
					duration=0;
					back=setPoint;
				}
			}else{
				back=setPoint;
				duration = 0;
			}
			if(velocityRun){
				long ms = System.currentTimeMillis()-lastInterpolationTime;
				//System.out.println("Time Diff="+ms+" tick difference="+unitsPerMs*ms+" ticksPerMs="+unitsPerMs);
				back=(ticks+unitsPerMs*ms);
			}
			ticks = (long) back;
			lastInterpolationTime=System.currentTimeMillis();
		}
	}
	
}
