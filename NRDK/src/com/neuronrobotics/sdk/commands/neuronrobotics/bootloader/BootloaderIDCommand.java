package com.neuronrobotics.sdk.commands.neuronrobotics.bootloader;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.InvalidResponseException;

public class BootloaderIDCommand extends BowlerAbstractCommand {
	private ByteList data = new ByteList();
	
	public  BootloaderIDCommand() {
		setOpCode("blid");
		setMethod(BowlerMethod.GET);
	}

	@Override
	public byte[] getCallingData() {
		return data.getBytes();
	}
	
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		
		if(!data.getRPC().equals("blid")) {
			throw new InvalidResponseException("Program Command did not return '_rdy'.");
		}
		
		return data;
	}
}
