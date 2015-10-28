package com.neuronrobotics.sdk.dyio.peripherals;

import com.neuronrobotics.sdk.commands.bcs.io.SetChannelValueCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.util.ThreadUtil;
// TODO: Auto-generated Javadoc

/**
 * This class wraps ports 0,1, and 2 as an SPI interface.
 *
 * @author Kevin Harrington
 */
public class SPIChannel implements IChannelEventListener{
	
	/** The dyio. */
	DyIO dyio;
	
	/** The rx. */
	byte [] rx=null;
	
	/**
	 * Default constructor, assumes the DyIO regestry is being used.
	 */
	public SPIChannel () {
		this(((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)));
	}
	
	/**
	 * Constructor for an SPI channel.
	 *
	 * @param d the d
	 */
	public SPIChannel (DyIO d) {
		d.setMode(0, DyIOChannelMode.SPI_CLOCK);
		dyio = d;
		dyio.getChannel(0).addChannelEventListener(this);
	}
	/**
	 * THis method sends a byte array our the SPI peripheral. It uses another DyIO channel as its slave select pin. 
	 * @param ss the index of the DyIO channel to use as a slave select pin for the SPI
	 * @param stream the Bytes to be sent out
	 * @return true if success
	 */
	@Deprecated
	private BowlerDatagram sendSPIStream(int ss, byte [] stream) {
		
		ByteList b = new ByteList();
		b.add(ss);
		b.add(stream);
		return dyio.send(new  SetChannelValueCommand(0, b));

	}
	/**
	 * This performs a dumb read. The data sent out by the host is junk data.
	 * @param ss the index of the DyIO channel to use as a slave select pin for the SPI
	 * @param numBytes the number of bytes to read
	 * @return the data received
	 */
	public byte [] read(int ss, int numBytes) {
		byte [] stream = new byte[numBytes];
		for(int i = 0; i < numBytes; i++) {
			stream[i]=(byte) 0xff;
		}
		return write(ss,stream);
	}
	/**
	 * This performs a full read/write transaction. The data is sent down, and the corosponding data is read back in. 
	 * @param ss the index of the DyIO channel to use as a slave select pin for the SPI
	 * @param stream the Bytes to be sent out
	 * @return the data received
	 */
	public byte [] write(int ss, byte [] stream) {
		if(dyio.isLegacyParser()){
			BowlerDatagram  b= sendSPIStream(ss,stream);
			if(b==null)
				return new byte[0];
			return b.getData().getBytes(2);
		}else{
			ByteList data  = new ByteList(stream);
			data.insert(0, (byte) ss);
			dyio.send("bcs.io.*;0.3;;",
					BowlerMethod.POST,
					"strm",
					new Object[]{0,data});
			int timeout = 1000;
			rx=null;
			while(timeout-->0){
				ThreadUtil.wait(1);
				if(rx!=null){
					return rx;
				}
			}
			return new byte [0];
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.IChannelEventListener#onChannelEvent(com.neuronrobotics.sdk.dyio.DyIOChannelEvent)
	 */
	@Override
	public void onChannelEvent(DyIOChannelEvent e) {
		//Log.error("SPI"+e);
		rx = e.getData().getBytes();
	}
}
