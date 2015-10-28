package com.neuronrobotics.sdk.commands.bcs.safe;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class SafeModeCommand.
 */
public class SafeModeCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new safe mode command.
	 */
	public SafeModeCommand(){
		setOpCode("safe");
		setMethod(BowlerMethod.GET);
	}
	
	/**
	 * Instantiates a new safe mode command.
	 *
	 * @param enable the enable
	 * @param msTime the ms time
	 */
	public SafeModeCommand(boolean enable,int msTime){
		setOpCode("safe");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(enable?1:0);
		getCallingDataStorage().addAs16(msTime);
	}
}
