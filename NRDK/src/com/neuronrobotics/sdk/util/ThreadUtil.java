package com.neuronrobotics.sdk.util;

public class ThreadUtil {
	public static void wait(int time) {
		try { Thread.sleep(time); } catch (InterruptedException e) { }
	}
	
	public static void wait(int time0, int time1) {
		try { Thread.sleep(time0, time1); } catch (InterruptedException e) { }
	}
}
