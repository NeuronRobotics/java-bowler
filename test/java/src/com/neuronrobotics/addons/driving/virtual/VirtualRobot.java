package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;
import com.neuronrobotics.sdk.pid.PIDEvent;

public class VirtualRobot extends Thread{
	

	private AbstractDrivingRobot robot;

	private static final long threadTime=200;
	private int chan;
	private long ticks=0;
	private long lastTick=ticks;
	private long setPoint;
	private long duration;
	private long startTime;
	private long startPoint;
	private double maxTicksPerSecond;
	public  VirtualRobot(int channel,AbstractDrivingRobot r, double d) {
		robot=r;
		this.maxTicksPerSecond=d;
		setChan(channel);	
	}
	public synchronized  void ZeroEncoder() {
		ticks=0;
		lastTick=0;
		setPoint=0;
		duration=0;
		startTime=0;
		startPoint=0;
	}
	public void run() {
		while(true) {
			try {Thread.sleep(threadTime);} catch (InterruptedException e) {}
			interpolate();
			if(ticks!=lastTick) {
				lastTick=ticks;
				robot.onPIDEvent(new PIDEvent(getChan(), (int)ticks, System.currentTimeMillis(),0));
			}
		}
	}
	
	private void interpolate() {
		float back;
		float diffTime;
		if(duration > 0){
			diffTime = System.currentTimeMillis()-startTime;
			if((diffTime < duration) && (diffTime>0)){
				float elapsed = 1-((duration-diffTime)/duration);
				float tmp=((float)startPoint+(float)(setPoint-startPoint)*elapsed);
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
		ticks = (long) back;
	}

	public synchronized  void SetPIDSetPoint(int setpoint,double seconds){
		
		double TPS = (double)setpoint/seconds;
		//Models motor saturation
		if(TPS >  maxTicksPerSecond){
			//seconds = (double)setpoint/maxTicksPerSeconds;
			throw new RuntimeException("Saturated PID on channel: "+chan+" Attempted Ticks Per Second: "+TPS+", when max is"+maxTicksPerSecond+" set: "+setpoint+" sec: "+seconds);
		}
		duration = (long) (seconds*1000);
		startTime=System.currentTimeMillis();
		startPoint = ticks;
		setPoint=setpoint;
		System.out.println("Setting Setpoint Ticks to: "+setPoint);
	}
	
	private void setChan(int chan) {
		this.chan = chan;
	}
	public int getChan() {
		return chan;
	}
	
}
