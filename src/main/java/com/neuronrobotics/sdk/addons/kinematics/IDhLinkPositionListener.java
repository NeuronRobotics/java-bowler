package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IDhLinkPosition events.
 * The class that is interested in processing a IDhLinkPosition
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIDhLinkPositionListener  method. When
 * the IDhLinkPosition event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IDhLinkPositionListener {
	
	/**
	 * On link global position change.
	 *
	 * @param newPose the new pose
	 */
	public void onLinkGlobalPositionChange(TransformNR newPose);
}
