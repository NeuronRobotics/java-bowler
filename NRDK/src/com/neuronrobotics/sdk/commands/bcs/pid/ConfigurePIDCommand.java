package com.neuronrobotics.sdk.commands.bcs.pid;


import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

import com.neuronrobotics.sdk.pid.PIDConfiguration;

public class ConfigurePIDCommand extends BowlerAbstractCommand {
	
	public ConfigurePIDCommand(char group) {
		setOpCode("cpid");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(group);
	}
	
	public ConfigurePIDCommand(char group,boolean enabled,boolean inverted,boolean async,double KP,double KI,double KD) {
		setOpCode("cpid");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(group);
		getCallingDataStorage().add(((enabled)?1:0));
		getCallingDataStorage().add(((inverted)?1:0));
		getCallingDataStorage().add(((async)?1:0));
		getCallingDataStorage().addAs32((int) (KP*100));
		getCallingDataStorage().addAs32((int) (KI*100));
		getCallingDataStorage().addAs32((int) (KD*100));
	}
	
	public ConfigurePIDCommand(PIDConfiguration config) {
		setOpCode("cpid");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(config.getGroup());
		getCallingDataStorage().add(((config.isEnabled())?1:0));
		getCallingDataStorage().add(((config.isInverted())?1:0));
		getCallingDataStorage().add(((config.isAsync())?1:0));
		getCallingDataStorage().addAs32((int) (config.getKP()*100));
		getCallingDataStorage().addAs32((int) (config.getKI()*100));
		getCallingDataStorage().addAs32((int) (config.getKD()*100));
	}


}
