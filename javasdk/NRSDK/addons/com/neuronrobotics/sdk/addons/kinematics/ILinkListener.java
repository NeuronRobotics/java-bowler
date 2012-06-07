package com.neuronrobotics.sdk.addons.kinematics;

public interface ILinkListener {
	public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue);
}
