package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;

public class PausableTime {
	private static long timePaused = 0;
	private static long durationPaused = 0;
	private static boolean paused =false;
	private static ArrayList<IPauseTimeListener> listeners = new ArrayList<IPauseTimeListener>();
	
	public static long currentTimeMillis() {
		if(!paused)
			return System.currentTimeMillis()-durationPaused;
		return timePaused;
	}
	
	public static void pause(boolean val) {
		if(val)
			timePaused=System.currentTimeMillis();
		else 
			durationPaused+=(System.currentTimeMillis()-timePaused);
		
		paused=val;
		for(int i=0;i<listeners.size();i++)
			listeners.get(i).pause(val);
	}
	
	
	public static void step(long ms) {
		new Thread(()->{
			boolean start = paused;
			pause(false);
			sleep(ms);
			pause(start);
		}).start();
	}
	
	public static void sleep(long durationMS) {
		try {
			Thread.sleep(durationMS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		while(paused) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void addIPauseTimeListener(IPauseTimeListener l) {
		if(listeners.contains(l))
			return;
		listeners.add(l);
	}
	public static void removeIPauseTimeListener(IPauseTimeListener l) {
		if(listeners.contains(l))
			listeners.remove(l);
	}
}
