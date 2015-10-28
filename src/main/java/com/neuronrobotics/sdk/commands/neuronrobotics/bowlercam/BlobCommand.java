package com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam;

import java.awt.Color;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class BlobCommand.
 */
public class BlobCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new blob command.
	 *
	 * @param c the c
	 * @param threshhold the threshhold
	 * @param within the within
	 * @param minBlobSize the min blob size
	 * @param maxBlobSize the max blob size
	 */
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
	
	/**
	 * Instantiates a new blob command.
	 *
	 * @param x the x
	 * @param y the y
	 * @param radius the radius
	 */
	public BlobCommand(int x,int y,int radius){
		setMethod(BowlerMethod.POST);
		setOpCode("blob");
		getCallingDataStorage().addAs32(x);
		getCallingDataStorage().addAs32(y);
		getCallingDataStorage().addAs32(radius);
	}
	
	/**
	 * Instantiates a new blob command.
	 */
	public BlobCommand(){
		setMethod(BowlerMethod.GET);
		setOpCode("blob");
	}
}
