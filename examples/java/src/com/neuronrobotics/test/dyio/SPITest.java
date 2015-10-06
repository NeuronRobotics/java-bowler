package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.SPIChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class SPITest{
	
	public static void main(String [] args) throws InterruptedException {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		//Instantiate the SPI
		SPIChannel spi = new SPIChannel(dyio);
		
		ByteList dataStream = new ByteList(new byte[] {33,55,66,77,88,99});
		
		int slaveSelectChannelNumber = 3;
		
		//Send data to the SPI channel using the given DYIO channel as the slave select. 
		byte [] back = spi.write(slaveSelectChannelNumber, dataStream.getBytes());
		
		
		//Cleanup and exit
		dyio.disconnect();
		System.exit(0);
	}
}
