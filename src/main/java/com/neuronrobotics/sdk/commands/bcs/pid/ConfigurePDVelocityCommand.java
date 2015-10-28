package com.neuronrobotics.sdk.commands.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.pid.PDVelocityConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigurePDVelocityCommand.
 */
public class ConfigurePDVelocityCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new configure pd velocity command.
	 *
	 * @param group the group
	 */
	public ConfigurePDVelocityCommand(int group){
		setOpCode("cpdv");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(group);
	}
	
	/**
	 * Instantiates a new configure pd velocity command.
	 *
	 * @param config the config
	 */
	public ConfigurePDVelocityCommand(PDVelocityConfiguration config){
		setOpCode("cpdv");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(config.getGroup());
		getCallingDataStorage().addAs32((int) (config.getKP()*100));
		getCallingDataStorage().addAs32((int) (config.getKD()*100));
	}

}
