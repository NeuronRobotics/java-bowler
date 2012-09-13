package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public interface ILinkListener {
	public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue);
	/**
	 * On the event of a limit, this is called
	 * @param source
	 * @param event
	 */
	public void onLinkLimit(AbstractLink source,PIDLimitEvent event);

}
