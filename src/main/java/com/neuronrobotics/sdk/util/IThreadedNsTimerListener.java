package com.neuronrobotics.sdk.util;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IThreadedNsTimer events.
 * The class that is interested in processing a IThreadedNsTimer
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIThreadedNsTimerListener  method. When
 * the IThreadedNsTimer event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IThreadedNsTimerListener {
	
	/**
	 * On timer interval.
	 *
	 * @param index the index
	 */
	public void onTimerInterval(long index);
}
