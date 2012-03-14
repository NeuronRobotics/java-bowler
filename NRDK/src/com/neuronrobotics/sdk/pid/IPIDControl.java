package com.neuronrobotics.sdk.pid;
public interface IPIDControl {
	/**
	 * This method calls a reset of the PID group. This will set the current value of the controllers input to the given value (if possible)
	 * and will set the setpoint of the PID group to the current value (stopping the output)
	 * @param group the index of the PID group 
	 * @param valueToSetCurrentTo the target value that the controller should be set to. 
	 * @return true if success
	 */
	public boolean ResetPIDChannel(int group,int valueToSetCurrentTo);
	/**
	 * This method sends a PID configuration object to the device. THe controller can be enabled/disabled with this method
	 * All PID parameters are stored in the PIDConfiguration ojbect prior to calling this method will be sent to the device. 
	 * @param config the configuration wrapper object
	 * @return true if success
	 */
	public boolean ConfigurePIDController(PIDConfiguration config);
	/**
	 * Gets the current state of the PID group. The PIDConfiguration object will contain the current configuration state of the requested 
	 * PID controller. 
	 * @param group  the index of the PID group 
	 * @return the configuration object
	 */
	public PIDConfiguration getPIDConfiguration(int group);
	/**
	 * This method sets the target setpoint for the specified PID controller group. 
	 * This method will set up a linear interpolation from current position to target position which will take the specified number of seconds to make that transition
	 * @param group the index of the PID group 
	 * @param setpoint the target position for the controller
	 * @param seconds units in Seconds, the time it takes to make the transition from current to target. Zero will tell the controller to go as fast as possible. 
	 * @return true if no errors occur 
	 */
	public boolean SetPIDSetPoint(int group,int setpoint,double seconds);
	/**
	 * Same as SetPIDSetPoint, but will set all setpoints at once. This can be used for co-ordinated motion of independant PID control groups.
	 * @param setpoints and array of setpoint values (must match the number of availible PID control groups)
	 * @param seconds units in Seconds, the time it takes to make the transition from current to target. Zero will tell the controllers to go as fast as possible. 
	 * @return true if no errors occur 
	 */
	public boolean SetAllPIDSetPoint(int []setpoints,double seconds);
	/**
	 * This method requests a single PID controller group value. The value returned represents the current state of the PID controller's input sensor in raw units
	 * @param group  the index of the PID group 
	 * @return The current value of the sensor input 
	 */
	public int GetPIDPosition(int group);
	/**
	 * This method requests all PID controllers to report back their current value of their input sensors. 
	 * This method is also used to determine dynamically how many PID control groups are availible on a device. 
	 * @return and array of values representing the current state of the given cntrollers input
	 */
	public int [] GetAllPIDPosition();
	/**
	 * Allows a user to attach a listener to the device to listen for PID events
	 * Events include: 
	 * PID reset, where the user is notified if the controllers input is reset from software 
	 * PID limit, if the device generates a Home, Upper limit, or Lower limit event from a hardware event
	 * PID position, if the current position of the PID controllers sensor input changes
	 * @param l
	 */
	public void addPIDEventListener(IPIDEventListener l);
	/**
	 * Removes a specific IPIDEventListener
	 * @param l
	 */
	public void removePIDEventListener(IPIDEventListener l);
	/**
	 * This method will read all of the cached or current setpoints for all PID controllers and calls SetAllPIDSetPoint with its internal data
	 * @param time
	 */
	public void flushPIDChannels(double time);
	/**
	 * This method will use the linear interpolation system to set an output velocity of the PID controller. This method can be bounded by the
	 * maximum value representable by the sensor and can fail if that value is out of range. 
	 * @param group the index of the PID group 
	 * @param unitsPerSecond a velocity in raw units per second
	 * @param seconds the amount of time to run at this velocity
	 * @return true if successful
	 * @throws PIDCommandException If the values are out of range with the given data
	 */
	public boolean SetPIDInterpolatedVelocity(int group,int unitsPerSecond,double seconds) throws PIDCommandException;
	/**
	 * This method will use the internal PD velocity controller to run a PID controller at a constant velocity. Since this is not using the linear interpolation, 
	 * it can run forever by giving Zero as the 'seconds' parameter. 
	 * @param group the index of the PID group 
	 * @param unitsPerSecond  a velocity in raw units per second
	 * @param seconds the amount of time to run at this velocity, or Zero to run forever
	 * @return
	 * @throws PIDCommandException If the values are out of range with the given data
	 */
	public boolean SetPDVelocity(int group,int unitsPerSecond,double seconds) throws PIDCommandException;
	/**
	 * Gets the PID channel wrapper for a specific channel. The channel wrappers can be used to cache values for use with the cache/flush system.
	 * This wrapper will encapsulate a specific PID channel.   
	 * @param group the index of the PID group
	 * @return a PIDChannel encapsulation object
	 */
	public PIDChannel getPIDChannel(int group);
	/**
	 * Sends a single packet to stop all PID groups at once.
	 * @return
	 */
	public boolean killAllPidGroups();
	/**
	 * Checks to see if the PID controller object is connected with its device.
	 * @return
	 */
	public boolean isAvailable();
	
}
