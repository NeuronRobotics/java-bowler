package com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam;

import java.awt.Color;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class BlobCommand extends BowlerAbstractCommand {
	public BlobCommand(Color c, int threshhold,boolean within,int minBlobSize,int maxBlobSize){
		setMethod(BowlerMethod.POST);
		setOpCode("blob");
		getCallingDataStorage().add(c.getRed());
		getCallingDataStorage().add(c.getGreen());
		getCallingDataStorage().add(c.getBlue());
		getCallingDataStorage().add(threshhold);
		getCallingDataStorage().add(within?1:0);
		
		getCallingDataStorage().addAs32(minBlobSize);
		getCallingDataStorage().addAs32(maxBlobSize);
	}
	public BlobCommand(int x,int y,int radius){
		setMethod(BowlerMethod.POST);
		setOpCode("blob");
		getCallingDataStorage().addAs32(x);
		getCallingDataStorage().addAs32(y);
		getCallingDataStorage().addAs32(radius);
	}
	public BlobCommand(){
		setMethod(BowlerMethod.GET);
		setOpCode("blob");
	}
}
