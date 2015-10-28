package com.neuronrobotics.sdk.commands.bcs.pid;


import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.pid.PIDConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigurePIDCommand.
 */
public class ConfigurePIDCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new configure pid command.
	 *
	 * @param group the group
	 */
	public ConfigurePIDCommand(char group) {
		setOpCode("cpid");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(group);
	}
	
	/**
	 * Instantiates a new configure pid command.
	 *
	 * @param group the group
	 * @param enabled the enabled
	 * @param inverted the inverted
	 * @param async the async
	 * @param KP the kp
	 * @param KI the ki
	 * @param KD the kd
	 * @param latchValue the latch value
	 * @param use the use
	 * @param stop the stop
	 * @param up the up
	 * @param low the low
	 * @param hStop the h stop
	 */
	public ConfigurePIDCommand(char group,boolean enabled,boolean inverted,boolean async,double KP,double KI,double KD, double latchValue, boolean use, boolean stop, double up, double low,double hStop) {
		this(new PIDConfiguration(group, enabled,inverted,async,KP,KI,KD, latchValue,use, stop, up,low,hStop));
	}
	
	/**
	 * Instantiates a new configure pid command.
	 *
	 * @param config the config
	 */
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
