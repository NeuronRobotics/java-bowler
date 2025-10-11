package com.neuronrobotics.sdk.addons.kinematics.time;

public interface ITimeProvider {
	//default public final long timestamp = System.currentTimeMillis();
	default long currentTimeMillis() {
		return System.currentTimeMillis();
	}
	default void sleep(long time) throws InterruptedException {
		Thread.sleep(time);
	}
	default void sleep(long ms,int ns) throws InterruptedException {
		Thread.sleep(ms,ns);
	}
}
