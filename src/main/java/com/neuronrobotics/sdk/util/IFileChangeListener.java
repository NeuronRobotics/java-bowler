package com.neuronrobotics.sdk.util;

import java.io.File;
import java.nio.file.WatchEvent;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IFileChange events.
 * The class that is interested in processing a IFileChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIFileChangeListener  method. When
 * the IFileChange event occurs, that object's appropriate
 * method is invoked.
 *
 * @see WatchEvent
 */
public interface IFileChangeListener {
	
	/**
	 * On file change.
	 *
	 * @param fileThatChanged the file that changed
	 * @param event the event
	 */
	public void onFileChange(File fileThatChanged,WatchEvent event);

}
