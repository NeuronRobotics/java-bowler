package com.neuronrobotics.sdk.common;

public interface IClosedLoopController {
	/**
	 * This is an interface for a single axis generic closed loop controller
	 * Linear PID controllers are the simplest form, but any other form of 
	 * control could be implemented that opperates on this interface
	 * @param currentState the value that reperesents the curent state of the linear system
	 * @param target the desired target  state of the control system
	 * @param seconds the amount of time this computation should calculate the velocity for.
	 * @return the velocity set of the controller
	 */
	public double compute(double currentState, double target, double seconds);
}
