package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigAsyncCommand.
 */
public class ConfigAsyncCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new config async command.
	 *
	 * @param channel the channel
	 * @param msTime the ms time
	 * @param mode the mode
	 */
	public ConfigAsyncCommand(int channel,int msTime, AsyncMode mode) {
		if(mode != AsyncMode.AUTOSAMP && mode != AsyncMode.NOTEQUAL)
			throw new  RuntimeException("Missing configuration data for async configuration");
		setOpCode("asyn");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(mode.getValue());
		getCallingDataStorage().addAs32(msTime);
	}
	
	/**
	 * Instantiates a new config async command.
	 *
	 * @param channel the channel
	 * @param msTime the ms time
	 * @param deadbandValue the deadband value
	 */
	public ConfigAsyncCommand(int channel,int msTime, int deadbandValue) {
		setOpCode("asyn");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(AsyncMode.DEADBAND.getValue());
		getCallingDataStorage().addAs32(msTime);
		getCallingDataStorage().addAs32(deadbandValue);
	}
	
	/**
	 * Instantiates a new config async command.
	 *
	 * @param channel the channel
	 * @param msTime the ms time
	 * @param threshholdValue the threshhold value
	 * @param edge the edge
	 */
	public ConfigAsyncCommand(int channel,int msTime, int threshholdValue,AsyncThreshholdEdgeType edge) {
		setOpCode("asyn");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(AsyncMode.THRESHHOLD.getValue());
		getCallingDataStorage().addAs32(msTime);
		getCallingDataStorage().addAs32(threshholdValue);
		getCallingDataStorage().add(edge.getValue());
	}
}
