package com.neuronrobotics.sdk.commands.bcs.pid.DyPID;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;

public class ConfigureDynamicPIDCommand extends BowlerAbstractCommand {
	public ConfigureDynamicPIDCommand(int group){
		setOpCode("dpid");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(group);
	}
	public ConfigureDynamicPIDCommand(char group,int inputChannel,DyIOChannelMode inputMode,int outputChannel,DyIOChannelMode outputMode) {
		setOpCode("dpid");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(group);
		getCallingDataStorage().add(inputChannel);
		getCallingDataStorage().add(inputMode.getValue());
		getCallingDataStorage().add(outputChannel);
		getCallingDataStorage().add(outputMode.getValue());
		
	}
	public ConfigureDynamicPIDCommand(DyPIDConfiguration config) {
		setOpCode("dpid");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(config.getGroup());
		getCallingDataStorage().add(config.getInputChannel());
		getCallingDataStorage().add(config.getInputMode().getValue());
		getCallingDataStorage().add(config.getOutputChannel());
		getCallingDataStorage().add(config.getOutputMode().getValue());
	}
}
