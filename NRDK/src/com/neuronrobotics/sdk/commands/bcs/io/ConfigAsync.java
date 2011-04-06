package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

public class ConfigAsync extends BowlerAbstractCommand {
	public ConfigAsync(int channel,int msTime, AsyncMode mode) {
		if(mode != AsyncMode.AUTOSAMP && mode != AsyncMode.NOTEQUAL)
			throw new  RuntimeException("Missing configuration data for async configuration");
		setOpCode("asyn");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(mode.getValue());
		getCallingDataStorage().addAs32(msTime);
	}
	public ConfigAsync(int channel,int msTime, int deadbandValue) {
		setOpCode("asyn");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(AsyncMode.DEADBAND.getValue());
		getCallingDataStorage().addAs32(msTime);
		getCallingDataStorage().addAs32(deadbandValue);
	}
	public ConfigAsync(int channel,int msTime, int threshholdValue,AsyncThreshholdEdgeType edge) {
		setOpCode("asyn");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(AsyncMode.THRESHHOLD.getValue());
		getCallingDataStorage().addAs32(msTime);
		getCallingDataStorage().addAs32(threshholdValue);
		getCallingDataStorage().add(edge.getValue());
	}
}
