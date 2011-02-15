package com.neuronrobotics.sdk.dyio.peripherals;

import com.neuronrobotics.sdk.commands.bcs.io.SetChannelValueCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;

public class SPIChannel {
	DyIO dyio;
	public SPIChannel (DyIO d) {
		d.setMode(0, DyIOChannelMode.SPI_CLOCK);
		dyio = d;
	}
	public BowlerDatagram sendSPIStream(int channel,int ss, byte [] stream) {
		ByteList b = new ByteList();
		b.add(ss);
		b.add(stream);
		return dyio.send(new  SetChannelValueCommand(channel, b));
	}
	public byte [] read(int ss, int numBytes) {
		byte [] stream = new byte[numBytes];
		for(int i = 0; i < numBytes; i++) {
			stream[i]=(byte) 0xff;
		}
		return write(ss,stream);
	}
	public byte [] write(int ss, byte [] stream) {
		BowlerDatagram  b= sendSPIStream(0,ss,stream);
		if(b==null)
			return new byte[0];
		return b.getData().getBytes(2);
	}
}
