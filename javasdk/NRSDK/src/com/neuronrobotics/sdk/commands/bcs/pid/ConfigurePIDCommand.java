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
	
	public ConfigurePIDCommand(char group,boolean enabled,boolean inverted,boolean async,double KP,double KI,double KD, double latchValue, boolean use, boolean stop) {
		this(new PIDConfiguration(group, enabled,inverted,async,KP,KI,KD, latchValue,use, stop));
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
		getCallingDataStorage().addAs32((int) (config.getIndexLatch()));
		getCallingDataStorage().add(((config.isUseLatch())?1:0));
		getCallingDataStorage().add(((config.isStopOnIndex())?1:0));
	}


}
