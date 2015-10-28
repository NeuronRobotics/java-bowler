package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.pid.PIDLimitEvent;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ILink events.
 * The class that is interested in processing a ILink
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addILinkListener  method. When
 * the ILink event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AbstractLink
 */
public interface ILinkListener {
	
	/**
	 * On link position update.
	 *
	 * @param source the source
	 * @param engineeringUnitsValue the engineering units value
	 */
	public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue);
	
	/**
	 * On the event of a limit, this is called.
	 *
	 * @param source the source
	 * @param event the event
	 */
	public void onLinkLimit(AbstractLink source,PIDLimitEvent event);

}
