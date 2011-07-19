package com.neuronrobotics.sdk.commands.neuronrobotics.bootloader;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.InvalidResponseException;

public class ProgramSectionCommand extends BowlerAbstractCommand {
	
	public ProgramSectionCommand(int channel, int address, ByteList byteData) {
		setOpCode("prog");
		setMethod(BowlerMethod.CRITICAL);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().addAs32(address);
		for (byte b:byteData){
			getCallingDataStorage().add(b);
		}
	}
	
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		
		if(!data.getRPC().equals("_rdy")) {
			throw new InvalidResponseException("Program Command did not return '_rdy'.");
		}
		
		return data;
	}
}
