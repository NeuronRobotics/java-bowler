package com.neuronrobotics.sdk.commands.bcs.pid.DyPID;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigureDynamicPIDCommand.
 */
public class ConfigureDynamicPIDCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new configure dynamic pid command.
	 *
	 * @param group the group
	 */
	public ConfigureDynamicPIDCommand(int group){
		setOpCode("dpid");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(group);
	}
	
	/**
	 * Instantiates a new configure dynamic pid command.
	 *
	 * @param group the group
	 * @param inputChannel the input channel
	 * @param inputMode the input mode
	 * @param outputChannel the output channel
	 * @param outputMode the output mode
	 */
	public ConfigureDynamicPIDCommand(char group,int inputChannel,DyIOChannelMode inputMode,int outputChannel,DyIOChannelMode outputMode) {
		setOpCode("dpid");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(group);
		getCallingDataStorage().add(inputChannel);
		getCallingDataStorage().add(inputMode.getValue());
		getCallingDataStorage().add(outputChannel);
		getCallingDataStorage().add(outputMode.getValue());
		
	}
	
	/**
	 * Instantiates a new configure dynamic pid command.
	 *
	 * @param config the config
	 */
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
