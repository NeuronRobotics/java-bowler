package com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class ImageURLCommand extends BowlerAbstractCommand {
	public ImageURLCommand(int camera){
		setMethod(BowlerMethod.GET);
		setOpCode("imsv");
		getCallingDataStorage().add(camera);
	}
	public ImageURLCommand(String url){
		setMethod(BowlerMethod.POST);
		setOpCode("imsv");
		getCallingDataStorage().add(url);
	}
}
