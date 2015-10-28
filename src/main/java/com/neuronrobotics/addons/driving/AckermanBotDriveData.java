package com.neuronrobotics.addons.driving;

// TODO: Auto-generated Javadoc
/**
 * The Class AckermanBotDriveData.
 */
public class AckermanBotDriveData {
	
	/** The steer angle. */
	private final double steerAngle;
	
	/** The ticks to travil. */
	private final int ticksToTravil;
	
	/** The seconds to travil. */
	private final double secondsToTravil;

	/**
	 * Instantiates a new ackerman bot drive data.
	 *
	 * @param steerAngle the steer angle
	 * @param ticksToTravil the ticks to travil
	 * @param secondsToTravil the seconds to travil
	 */
	public AckermanBotDriveData(double steerAngle,int ticksToTravil,double secondsToTravil) {
		this.steerAngle = steerAngle;
		this.ticksToTravil = ticksToTravil;
		this.secondsToTravil = secondsToTravil;
		
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
	 * Gets the ticks to travil.
	 *
	 * @return the ticks to travil
	 */
	public int getTicksToTravil() {
		return ticksToTravil;
	}

	/**
	 * Gets the seconds to travil.
	 *
	 * @return the seconds to travil
	 */
	public double getSecondsToTravil() {
		return secondsToTravil;
	}
}
