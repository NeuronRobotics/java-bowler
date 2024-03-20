package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.time.ITimeProvider;
import com.neuronrobotics.sdk.addons.kinematics.time.TimeKeeper;

public class PausableTime extends TimeKeeper {
	private static long timePaused = 0;
	private static long durationPaused = 0;
	private static boolean paused =false;
	private static ArrayList<IPauseTimeListener> listeners = new ArrayList<IPauseTimeListener>();
	public PausableTime(ITimeProvider t) {
		setTimeProvider(t);
	}
	public  long currentTimeMillis() {
		if(!paused)
			return super.currentTimeMillis()-durationPaused;
		return timePaused;
	}
	
	public  void pause(boolean val) {
		if(val)
			timePaused=super.currentTimeMillis();
		else 
			durationPaused+=(super.currentTimeMillis()-timePaused);
		
		paused=val;
		for(int i=0;i<listeners.size();i++)
			listeners.get(i).pause(val);
	}
	
	
	public  void step(long ms) {
		new Thread(()->{
			boolean start = paused;
			pause(false);
			sleep(ms);
			pause(start);
		}).start();
	}
	
	public void sleep(long durationMS) {
		try {
			super.sleep(durationMS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		while(paused) {
			try {
				super.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void addIPauseTimeListener(IPauseTimeListener l) {
		if(listeners.contains(l))
			return;
		listeners.add(l);
	}
	public void removeIPauseTimeListener(IPauseTimeListener l) {
		if(listeners.contains(l))
			listeners.remove(l);
	}
}
