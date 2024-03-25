package com.neuronrobotics.sdk.addons.kinematics.imu;

import com.neuronrobotics.sdk.addons.kinematics.time.TimeKeeper;

/**
 * This is a state object for the IMU
 * any function that returns null has no new data availible.
 * @author hephaestus
 *
 */
public class IMUUpdate {
	private static boolean notice=true;
	private final Double xAcceleration;
	private final  Double yAcceleration;
	private final  Double zAcceleration;
	private final  Double rotxAcceleration;
	private final  Double rotyAcceleration;
	private final  Double rotzAcceleration;
	private  long timestamp;
	/**
	 * Values represent current state of accelerations
	 * Null values means there is no update for this value
	 * @param xAcceleration (meters / second^2)
	 * @param yAcceleration (meters / second^2)
	 * @param zAcceleration (meters / second^2)
	 * @param rotxAcceleration (radian / second^2)
	 * @param rotyAcceleration (radian / second^2)
	 * @param rotzAcceleration (radian / second^2)
	 */
	public IMUUpdate(Double xAcceleration,Double yAcceleration,Double zAcceleration, 
			Double rotxAcceleration,Double rotyAcceleration,Double rotzAcceleration , long timestamp
			){
				this.xAcceleration = xAcceleration;
				this.yAcceleration = yAcceleration;
				this.zAcceleration = zAcceleration;
				this.rotxAcceleration = rotxAcceleration;
				this.rotyAcceleration = rotyAcceleration;
				this.rotzAcceleration = rotzAcceleration;
				this.setTimestamp(timestamp);
		
	}

	/**
	 * Values represent current state of accelerations
	 * Null values means there is no update for this value
	 * @param xAcceleration (meters / second^2)
	 * @param yAcceleration (meters / second^2)
	 * @param zAcceleration (meters / second^2)
	 * @param rotxAcceleration (radian / second^2)
	 * @param rotyAcceleration (radian / second^2)
	 * @param rotzAcceleration (radian / second^2)
	 */
	@Deprecated
	public IMUUpdate(Double xAcceleration,Double yAcceleration,Double zAcceleration, 
			Double rotxAcceleration,Double rotyAcceleration,Double rotzAcceleration 
			){
				this.xAcceleration = xAcceleration;
				this.yAcceleration = yAcceleration;
				this.zAcceleration = zAcceleration;
				this.rotxAcceleration = rotxAcceleration;
				this.rotyAcceleration = rotyAcceleration;
				this.rotzAcceleration = rotzAcceleration;
				if(notice) {
					notice=false;
				new RuntimeException("This constructor is depricated, please provide a timestamp").printStackTrace(System.out);
				}
				this.setTimestamp(TimeKeeper.getMostRecent().currentTimeMillis());
		
	}

	public Double getxAcceleration() {
		return xAcceleration;
	}

	public Double getyAcceleration() {
		return yAcceleration;
	}

	public Double getzAcceleration() {
		return zAcceleration;
	}

	public Double getRotxAcceleration() {
		return rotxAcceleration;
	}

	public Double getRotyAcceleration() {
		return rotyAcceleration;
	}

	public Double getRotzAcceleration() {
		return rotzAcceleration;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
