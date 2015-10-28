package com.neuronrobotics.sdk.namespace.bcs.pid;


// TODO: Auto-generated Javadoc
/**
 * The Interface IExtendedPIDControl.
 */
public interface IExtendedPIDControl extends IPidControlNamespace{

	/**
	 * Run output hysteresis calibration.
	 *
	 * @param group the group
	 * @return true, if successful
	 */
	boolean runOutputHysteresisCalibration(int group);
}
