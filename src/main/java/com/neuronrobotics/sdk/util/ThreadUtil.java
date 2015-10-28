package com.neuronrobotics.sdk.util;

// TODO: Auto-generated Javadoc
/**
 * The Class ThreadUtil.
 */
public class ThreadUtil {
	
	/**
	 * Wait.
	 *
	 * @param time the time
	 */
	public static void wait(int time) {
		try { Thread.sleep(time); } catch (InterruptedException e) { throw new RuntimeException(e); }
	}
	
	/**
	 * Wait.
	 *
	 * @param time0 the time0
	 * @param time1 the time1
	 */
	public static void wait(int time0, int time1) {
		try { Thread.sleep(time0, time1); } catch (InterruptedException e) {throw new RuntimeException(e); }
	}
}
