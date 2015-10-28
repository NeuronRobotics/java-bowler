package com.neuronrobotics.addons.driving;

// TODO: Auto-generated Javadoc
/**
 * The Class PuckBotVelocityData.
 */
public class PuckBotVelocityData {
	
	/** The left ticks per second. */
	private final double leftTicksPerSecond;
	
	/** The right ticks per second. */
	private final double rightTicksPerSecond;

	/**
	 * Instantiates a new puck bot velocity data.
	 *
	 * @param leftTicksPerSecond the left ticks per second
	 * @param rightTicksPerSecond the right ticks per second
	 */
	public PuckBotVelocityData(double leftTicksPerSecond, double rightTicksPerSecond){
		this.leftTicksPerSecond = leftTicksPerSecond;
		this.rightTicksPerSecond = rightTicksPerSecond;
		
	}

	/**
	 * Gets the left ticks per second.
	 *
	 * @return the left ticks per second
	 */
	public double getLeftTicksPerSecond() {
		return leftTicksPerSecond;
	}

	/**
	 * Gets the right ticks per second.
	 *
	 * @return the right ticks per second
	 */
	public double getRightTicksPerSecond() {
		return rightTicksPerSecond;
	}
}
