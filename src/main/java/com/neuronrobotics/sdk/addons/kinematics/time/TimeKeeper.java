package com.neuronrobotics.sdk.addons.kinematics.time;

import java.util.ArrayList;


public class TimeKeeper {
	private static ITimeProvider mostRecent;
	private ArrayList<Runnable> timebaseChangeListener =new ArrayList<>();
	
	private  ITimeProvider clock = new ITimeProvider() {};
	public  void setTimeProvider(ITimeProvider t) {
		if(t==null)
			t= new ITimeProvider() {};
		clock = t;
		setMostRecent(clock);
		for(int i=0;i<timebaseChangeListener.size();i++) {
			try {
				timebaseChangeListener.get(i).run();
			}catch(Throwable tr) {
				tr.printStackTrace(System.out);
			}
			
		}
	}
	public  ITimeProvider getTimeProvider() {
		return clock;
	}
	
	public void addTimeBaseChangeListener(Runnable r) {
		if(timebaseChangeListener.contains(r))
			return;
		timebaseChangeListener.add(r);
	}
	public void removeTimeBaseChangeListener(Runnable r) {
		if(timebaseChangeListener.contains(r))
			timebaseChangeListener.remove(r);
	}
	
	public void cleaarTimeBaseChangeListener() {
		timebaseChangeListener.clear();
	}
	public  void sleep(long time) throws InterruptedException {
		getTimeProvider().sleep(time);
	}
	public  void sleep(long ms,int ns) throws InterruptedException {
		getTimeProvider().sleep(ms,ns);
	}
	
	/**
	 * Wait.
	 *
	 * @param time the time
	 */
	public void wait(int time) {
		try { sleep(time); } catch (InterruptedException e) { throw new RuntimeException(e); }
	}
	
	/**
	 * Wait.
	 *
	 * @param time0 the time0
	 * @param time1 the time1
	 */
	public void wait(int time0, int time1) {
		try { sleep(time0, time1); } catch (InterruptedException e) {throw new RuntimeException(e); }
	}
	
	public  long currentTimeMillis() {
		return getTimeProvider().currentTimeMillis();
	}
	/**
	 * @return the mostRecent
	 */
	public static ITimeProvider getMostRecent() {
		if(null==mostRecent) {
			mostRecent=new ITimeProvider() {};
		}
		return mostRecent;
	}
	/**
	 * @param mostRecent the mostRecent to set
	 */
	private static void setMostRecent(ITimeProvider mostRecent) {
		TimeKeeper.mostRecent = mostRecent;
	}
	
}
