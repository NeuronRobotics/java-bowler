package com.neuronrobotics.sdk.commands.neuronrobotics.bootloader;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.InvalidResponseException;

// TODO: Auto-generated Javadoc
/**
 * The Class ProgramSectionCommand.
 */
public class ProgramSectionCommand extends BowlerAbstractCommand {
	
	/**
	 * Hex.
	 *
	 * @param n the n
	 * @return the string
	 */
	public static String hex(long n) {
	    // call toUpperCase() if that's required
	    return String.format("0x%8s", Long.toHexString(n)).replace(' ', '0');
	}
	
	/**
	 * Instantiates a new program section command.
	 *
	 * @param channel the channel
	 * @param address the address
	 * @param byteData the byte data
	 */
	public ProgramSectionCommand(int channel, int address, ByteList byteData) {
		setOpCode("prog");
		System.out.println("Sending to address "+hex(address)+" size = "+byteData.size());
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().addAs32(address);
		for (byte b:byteData){
			getCallingDataStorage().add(b);
		}
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
