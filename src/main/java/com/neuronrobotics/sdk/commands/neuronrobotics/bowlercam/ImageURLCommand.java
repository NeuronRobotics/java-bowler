package com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageURLCommand.
 */
public class ImageURLCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new image url command.
	 *
	 * @param camera the camera
	 */
	public ImageURLCommand(int camera){
		setMethod(BowlerMethod.GET);
		setOpCode("imsv");
		getCallingDataStorage().add(camera);
	}
	
	/**
	 * Instantiates a new image url command.
	 *
	 * @param url the url
	 */
	public ImageURLCommand(String url){
		setMethod(BowlerMethod.POST);
		setOpCode("imsv");
		getCallingDataStorage().add(url);
	}
}
