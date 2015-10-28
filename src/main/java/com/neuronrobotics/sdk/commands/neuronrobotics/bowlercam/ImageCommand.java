package com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageCommand.
 */
public class ImageCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new image command.
	 *
	 * @param chan the chan
	 * @param scale the scale
	 */
	public ImageCommand(int chan, double scale){
		setMethod(BowlerMethod.GET);
		setOpCode("_img");
		getCallingDataStorage().add(chan);
		getCallingDataStorage().addAs32((int) (scale*1000));
	}
	
	/**
	 * Instantiates a new image command.
	 *
	 * @param camera the camera
	 * @param chunk the chunk
	 * @param totalChunks the total chunks
	 * @param imgData the img data
	 */
	public ImageCommand(int camera,int chunk,int totalChunks, byte [] imgData){
		setMethod(BowlerMethod.POST);
		setOpCode("_img");
		getCallingDataStorage().addAs16(camera);
		getCallingDataStorage().addAs16(chunk);
		getCallingDataStorage().addAs16(totalChunks);
		getCallingDataStorage().add(imgData);
		//Log.info("Sending data of size: "+imgData.length+", "+getCallingDataStorage().size());
		if(getCallingDataStorage().size()>255)
			throw new RuntimeException("Image data too big!");
	}
}
