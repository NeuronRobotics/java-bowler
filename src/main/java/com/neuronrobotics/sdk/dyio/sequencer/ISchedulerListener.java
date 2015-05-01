package com.neuronrobotics.sdk.dyio.sequencer;

public interface ISchedulerListener {
	/**
	 * This is called by the scheduler on regular intervals 
	 * @param ms the current time of the running scheduler
	 */
	public void onTimeUpdate(double ms);
	/**
	 * This method is to configure the listeners timing. This passes in the time interval that the scheduler will run at
	 * @param msInterval time interval that the scheduler will run at
	 * @param msInterval the total time for the loop
	 */
	public void setIntervalTime(int msInterval, int totalTime);
	
	/**
	 * This function is called when the seceduler is stopped
	 */
	public void onReset();
	
	public void onPlay();
	
	public void onPause();
}
