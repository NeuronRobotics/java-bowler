package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public interface IDhLinkPositionListener {
	public void onLinkGlobalPositionChange(TransformNR newPose);
}
