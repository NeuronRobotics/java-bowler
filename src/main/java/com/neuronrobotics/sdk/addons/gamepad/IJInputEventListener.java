package com.neuronrobotics.sdk.addons.gamepad;

import net.java.games.input.Component;
import net.java.games.input.Event;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IJInputEvent events.
 * The class that is interested in processing a IJInputEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIJInputEventListener  method. When
 * the IJInputEvent event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IJInputEventListener {

	/**
	 * On event.
	 *
	 * @param comp the comp
	 * @param event the event
	 * @param value the value
	 * @param eventString the event string
	 */
	public void onEvent(Component comp,Event event,float value,String eventString);
}
