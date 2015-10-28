package com.neuronrobotics.sdk.dyio.sequencer;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IScheduler events.
 * The class that is interested in processing a IScheduler
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addISchedulerListener  method. When
 * the IScheduler event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface ISchedulerListener {
	
	/**
	 * This is called by the scheduler on regular intervals .
	 *
	 * @param ms the current time of the running scheduler
	 */
	public void onTimeUpdate(double ms);
	
	/**
	 * This method is to configure the listeners timing. This passes in the time interval that the scheduler will run at
	 *
	 * @param msInterval the total time for the loop
	 * @param totalTime the total time
	 */
	public void setIntervalTime(int msInterval, int totalTime);
	
	/**
	 * This function is called when the seceduler is stopped.
	 */
	public void onReset();
	
	/**
	 * On play.
	 */
	public void onPlay();
	
	/**
	 * On pause.
	 */
	public void onPause();
}
