package com.neuronrobotics.sdk.addons.kinematics.time;

import com.neuronrobotics.sdk.util.ThreadUtil;

public class TimeKeeper {
	private static ITimeProvider mostRecent;
	
	private  ITimeProvider clock = new ITimeProvider() {};
	public  void setTimeProvider(ITimeProvider t) {
		if(t==null)
			t= new ITimeProvider() {};
		clock = t;
		setMostRecent(clock);
		ThreadUtil.wait(1);
	}
	public  ITimeProvider getTimeProvider() {
		return clock;
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
	public static void setMostRecent(ITimeProvider mostRecent) {
		TimeKeeper.mostRecent = mostRecent;
	}
	
}
