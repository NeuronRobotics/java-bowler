package com.neuronrobotics.sdk.dyio;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IDyIOChannelModeChange events.
 * The class that is interested in processing a IDyIOChannelModeChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIDyIOChannelModeChangeListener  method. When
 * the IDyIOChannelModeChange event occurs, that object's appropriate
 * method is invoked.
 *
 * @see IDyIOStateChangeListener
 */
public interface IDyIOChannelModeChangeListener {
	
	/**
	 * On mode change.
	 *
	 * @param newMode the new mode
	 */
	public void onModeChange(DyIOChannelMode newMode);
}
