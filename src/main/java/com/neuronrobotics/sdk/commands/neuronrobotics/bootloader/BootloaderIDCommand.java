package com.neuronrobotics.sdk.commands.neuronrobotics.bootloader;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.InvalidResponseException;

// TODO: Auto-generated Javadoc
/**
 * The Class BootloaderIDCommand.
 */
public class BootloaderIDCommand extends BowlerAbstractCommand {
	
	/**
	 * Instantiates a new bootloader id command.
	 */
	public  BootloaderIDCommand() {
		setOpCode("blid");
		setMethod(BowlerMethod.GET);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#validate(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		
		if(!data.getRPC().equals("blid")) {
			throw new InvalidResponseException("Program Command did not return '_rdy'.");
		}
		
		return data;
	}
}
