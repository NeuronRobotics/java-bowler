package com.neuronrobotics.addons.driving;

// TODO: Auto-generated Javadoc
/**
 * The Class AckermanBotVelocityData.
 */
public class AckermanBotVelocityData {
	
	/** The ticks per second. */
	private final double ticksPerSecond;
	
	/** The steer angle. */
	private final double steerAngle;

	/**
	 * Instantiates a new ackerman bot velocity data.
	 *
	 * @param steerAngle the steer angle
	 * @param ticksPerSecond the ticks per second
	 */
	public  AckermanBotVelocityData (double steerAngle,double ticksPerSecond) {
		this.steerAngle = steerAngle;
		this.ticksPerSecond = ticksPerSecond;
		
	}

	/**
	 * Gets the steer angle.
	 *
	 * @return the steer angle
	 */
	public double getSteerAngle() {
		return steerAngle;
	}

	/**
	 * Gets the ticks per second.
	 *
	 * @return the ticks per second
	 */
	public double getTicksPerSecond() {
		return ticksPerSecond;
	}
}
