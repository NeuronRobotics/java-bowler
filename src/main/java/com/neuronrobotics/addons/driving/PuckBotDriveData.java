package com.neuronrobotics.addons.driving;

// TODO: Auto-generated Javadoc
/**
 * The Class PuckBotDriveData.
 */
public class PuckBotDriveData {
	
	/** The l. */
	private int l;
	
	/** The r. */
	private int r;
	
	/** The seconds. */
	private final double seconds;
	
	/**
	 * Instantiates a new puck bot drive data.
	 *
	 * @param leftEncoder the left encoder
	 * @param rightEncoder the right encoder
	 * @param seconds the seconds
	 */
	public PuckBotDriveData(int leftEncoder, int rightEncoder, double seconds){
		this.seconds = seconds;
		setL(leftEncoder);
		setR(rightEncoder);
	}
	
	/**
	 * Sets the l.
	 *
	 * @param l the new l
	 */
	private void setL(int l) {
		this.l = l;
	}
	
	/**
	 * Gets the left encoder data.
	 *
	 * @return the left encoder data
	 */
	public int getLeftEncoderData() {
		return l;
	}
	
	/**
	 * Sets the r.
	 *
	 * @param r the new r
	 */
	private void setR(int r) {
		this.r = r;
	}
	
	/**
	 * Gets the right encoder data.
	 *
	 * @return the right encoder data
	 */
	public int getRightEncoderData() {
		return r;
	}
	
	/**
	 * Gets the drive time in seconds.
	 *
	 * @return the drive time in seconds
	 */
	public double getDriveTimeInSeconds() {
		return seconds;
	}
}
