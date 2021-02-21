package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public interface ICalcLimbHomeProvider {
	/**
	 * Calculate the home position for the limb in the walking state. 
	 * @param limb
	 * @return
	 */
	public TransformNR calcHome(DHParameterKinematics limb);
}
