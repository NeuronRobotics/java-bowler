package com.neuronrobotics.sdk.commands.neuronrobotics.bootloader;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.InvalidResponseException;

// TODO: Auto-generated Javadoc
/**
 * The Class EraseFlashCommand.
 */
public class EraseFlashCommand extends BowlerAbstractCommand {

	
	/**
	 * Instantiates a new erase flash command.
	 *
	 * @param channel the channel
	 */
	public EraseFlashCommand(int channel) {
		setOpCode("erfl");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#validate(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		
		if(!data.getRPC().equals("_rdy")) {
			throw new InvalidResponseException("Program Command did not return '_rdy'.");
		}
		
		return data;
	}
}
