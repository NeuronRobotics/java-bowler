package com.neuronrobotics.sdk.addons.kinematics.imu;

public interface IMUUpdateListener {
	/**
	 * When the IM state updates this function is called by the IMU
	 * @param newState
	 */
	public void onIMUUpdate(IMUUpdate newState);
}
