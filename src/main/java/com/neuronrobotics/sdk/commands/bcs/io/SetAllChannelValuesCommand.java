package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;


// TODO: Auto-generated Javadoc
/**
 * The Class SetAllChannelValuesCommand.
 */
public class SetAllChannelValuesCommand extends BowlerAbstractCommand {

/**
 * Instantiates a new sets the all channel values command.
 *
 * @param time in seconds
 * @param values the values
 */
	public SetAllChannelValuesCommand(double time, int [] values){
		setMethod(BowlerMethod.POST);
		setOpCode("sacv");
		getCallingDataStorage().addAs32((int)(time*1000));
		for(int i=0;i<values.length;i++) {
			getCallingDataStorage().addAs32(values[i]);
		}
	}
	
	/**
	 * Instantiates a new sets the all channel values command.
	 *
	 * @param time in seconds
	 * @param values the values
	 */
		public SetAllChannelValuesCommand(double time, Integer [] values){
			setMethod(BowlerMethod.POST);
			setOpCode("sacv");
			getCallingDataStorage().addAs32((int)(time*1000));
			for(int i=0;i<values.length;i++) {
				getCallingDataStorage().addAs32(values[i]);
			}
		}
}
