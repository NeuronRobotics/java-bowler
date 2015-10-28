package com.neuronrobotics.sdk.bowlercam.device;

import java.awt.image.BufferedImage;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IWebcamImage events.
 * The class that is interested in processing a IWebcamImage
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIWebcamImageListener  method. When
 * the IWebcamImage event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IWebcamImageListener {
	
	/**
	 * On new image.
	 *
	 * @param camera the camera
	 * @param image the image
	 */
	public void onNewImage(int camera,BufferedImage image);
}
