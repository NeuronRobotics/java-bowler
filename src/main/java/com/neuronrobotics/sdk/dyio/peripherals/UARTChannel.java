/*******************************************************************************
 * Copyright 2010 Neuron Robotics, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.neuronrobotics.sdk.dyio.peripherals;

import java.io.IOException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.io.SetChannelValueCommand;
import com.neuronrobotics.sdk.commands.bcs.io.SetUARTBaudrateCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.ISendable;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIOInputStream;
import com.neuronrobotics.sdk.dyio.DyIOOutputStream;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.dyio.InvalidChannelOperationException;


// TODO: Auto-generated Javadoc
/**
 * The Class UARTChannel.
 */
public class UARTChannel implements ISendable {
	
	/** The listeners. */
	private ArrayList<IUARTStreamListener> listeners = new ArrayList<IUARTStreamListener>();
	
	/** The Constant UART_IN. */
	public static final int UART_IN = 17;
	
	/** The Constant UART_OUT. */
	public static final int UART_OUT = 16;
	
	/** The device. */
	DyIO device;
	
	/** The tx. */
	UARTTxChannel tx;
	
	/** The rx. */
	UARTRxChannel rx;
	
	/**
	 * Instantiates a new UART channel.
	 */
	public UARTChannel() {
		this(((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)));
	}
	
	/**
	 * Instantiates a new UART channel.
	 *
	 * @param d the d
	 */
	public UARTChannel(DyIO d){
		device = d;
		tx=new UARTTxChannel(d.getChannel(16));
		rx=new UARTRxChannel(d.getChannel(17));
	}
	
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	public byte[] getBytes() {
		return getBytes(rx.getInStreamSize());
	}
	
	/**
	 * Gets the bytes.
	 *
	 * @param size the size
	 * @return the bytes
	 */
	public byte[] getBytes(int size) {
		return rx.getBytes(size);
	}
	
	
	/**
	 * Send bytes.
	 *
	 * @param stream the stream
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean sendBytes(ByteList stream) throws IOException{
		return tx.putStream(stream);
	}
	
	
	/**
	 * Gets the in stream size.
	 *
	 * @return the in stream size
	 */
	public int getInStreamSize(){
		return rx.getInStreamSize();
	}
	
	/**
	 * In stream data ready.
	 *
	 * @return true, if successful
	 */
	public boolean inStreamDataReady(){
		return (getInStreamSize()>0);
	}
	
	/**
	 * Sets the uart baudrate.
	 *
	 * @param baudrate the baudrate
	 * @return true, if successful
	 */
	public boolean setUARTBaudrate(int baudrate) {
		switch(baudrate){
		case   2400:
		case   4800:
		case   9600:
		case  14400:
		case  19200:
		case  28800:
		case  38400:
		case  57600:
		case  76800:
		case 115200:
		case 230400:
			break;
		default:
			throw new InvalidChannelOperationException("This channel can not be set to that baudrate");
		}
		if(device.isLegacyParser()){
			device.send(new SetUARTBaudrateCommand(UART_IN, baudrate));
		}else{
			
			device.send("bcs.io.*;0.3;;",
											BowlerMethod.CRITICAL,
											"cchn",
											new Object[]{	17,
															true,
															new Integer[]{baudrate}
														});
		}
		
		return true;

	}
	
	/**
	 * Gets the out stream.
	 *
	 * @return the out stream
	 */
	public DyIOOutputStream getOutStream(){
		return tx.getOutStream();
	}
	
	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
	 */
	public DyIOInputStream getInputStream(){
		return rx.getInputStream();
	}
	
	/**
	 * Clear list of objects that have subscribed to channel updates.
	 */
	public void removeAllUARTStreamListener() {
		listeners.clear();
	}
	
	/**
	 * Remove a particular subscription.
	 * 
	 * @param l
	 *            The object that has subscribed to updates
	 */
	public void removeUARTStreamListener(IUARTStreamListener l) {
		if(!listeners.contains(l)) {
			return;
		}
		listeners.remove(l);
	}
	
	/**
	 * Add an object that wishes to receive channel updates.
	 * 
	 * @param l
	 *            The object that wishes to receive updates.
	 */
	public void addUARTStreamListener(IUARTStreamListener l) {
		if(listeners.contains(l)) {
			return;
		}
		listeners.add(l);
	}
	
	/**
	 * Fire channel event.
	 *
	 * @param e the e
	 */
	protected void fireChannelEvent(DyIOChannelEvent e) {
		for(IUARTStreamListener l : listeners) {
			l.onChannelEvent(e);
		}
	}
	
	/**
	 * The Class UARTTxChannel.
	 */
	private class UARTTxChannel extends DyIOAbstractPeripheral {
		
		/** The out. */
		DyIOOutputStream out;
		
		/**
		 * Instantiates a new UART tx channel.
		 *
		 * @param channel the channel
		 */
		public UARTTxChannel(DyIOChannel channel) {
			super(channel,DyIOChannelMode.USART_TX,true);
			if(!setMode()) {
				throw new DyIOPeripheralException("Could not set channel " + channel + " to " + DyIOChannelMode.USART_TX +  " mode");
			}
			out = new DyIOOutputStream(channel);
		}
		
		/**
		 * Put stream.
		 *
		 * @param stream the stream
		 * @return true, if successful
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public boolean putStream(ByteList stream) throws IOException {
			out.write(stream);
			return false;
		}
		
		/**
		 * Gets the out stream.
		 *
		 * @return the out stream
		 */
		public DyIOOutputStream getOutStream(){
			return out;
		}
		
		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral#hasAsync()
		 */
		public boolean hasAsync() {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
	
	/**
	 * The Class UARTRxChannel.
	 */
	private class UARTRxChannel extends DyIOAbstractPeripheral implements IChannelEventListener {
		
		/** The in. */
		DyIOInputStream in;
		
		/**
		 * Instantiates a new UART rx channel.
		 *
		 * @param channel the channel
		 */
		public UARTRxChannel(DyIOChannel channel) {
			super(channel,DyIOChannelMode.USART_RX,true);
			channel.addChannelEventListener(this);
			if(!setMode()) {
				throw new DyIOPeripheralException("Could not set channel " + channel + " to " + DyIOChannelMode.USART_RX +  " mode");
			}
			in = new DyIOInputStream(channel);
		}

		
		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.dyio.IChannelEventListener#onChannelEvent(com.neuronrobotics.sdk.dyio.DyIOChannelEvent)
		 */
		public void onChannelEvent(DyIOChannelEvent e) {
			in.write(e.getData());
			fireChannelEvent(e);
		}
		
		/**
		 * Gets the in stream size.
		 *
		 * @return the in stream size
		 */
		public int getInStreamSize(){
			return in.available();
		}

		/**
		 * Gets the bytes.
		 *
		 * @param inStreamSize the in stream size
		 * @return the bytes
		 */
		public byte[] getBytes(int inStreamSize) {
			if (inStreamSize > getInStreamSize())
				throw new IndexOutOfBoundsException();
			byte [] b = new byte [inStreamSize];

			for (int i=0;i<inStreamSize;i++){
				try {
					b[i]=(byte) in.read();
				} catch (IOException e) {
					
				}
			}
			
			return b;
		}
		
		/**
		 * Gets the input stream.
		 *
		 * @return the input stream
		 */
		public DyIOInputStream getInputStream(){
			return in;
		}

		
		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral#hasAsync()
		 */
		public boolean hasAsync() {
			// TODO Auto-generated method stub
			return true;
		}

	}
}
