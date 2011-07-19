package com.neuronrobotics.sdk.bowlercam.device;

import java.awt.image.BufferedImage;

public interface IWebcamImageListener {
	public void onNewImage(int camera,BufferedImage image);
}
