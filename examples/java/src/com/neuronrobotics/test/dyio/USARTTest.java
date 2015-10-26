package com.neuronrobotics.test.dyio;

import java.io.IOException;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.UARTChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class USARTTest{
	
	public static void main(String [] args) throws InterruptedException {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		//Instantiate the UARTPassThroughChannel
		UARTChannel uart = new UARTChannel(dyio);
		//Configure the DyIO's output UART to use 115200 baud
		uart.setUARTBaudrate(115200);
		//Create a test string and send it to the serial port
		String s = new String("abcdefghijklmnopqrstuvwxyz");
		ByteList stream = new ByteList(s.getBytes());
		try{
			System.out.println("Sending: "+stream.asString());
			uart.sendBytes(stream);
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		Thread.sleep(1000);
		//Wait for data to arrive
		while (!uart.inStreamDataReady()){
			Thread.sleep(10);
		}
		//Print out the data we got back
		try {
			System.out.println("Got input: " + new ByteList(uart.getBytes()).asString());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		//Cleanup and exit
		dyio.disconnect();
		System.exit(0);
	}
}
