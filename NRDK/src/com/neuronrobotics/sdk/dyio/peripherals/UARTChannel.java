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

import com.neuronrobotics.sdk.commands.bcs.io.SetUARTBaudrateCommand;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.ISendable;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIOInputStream;
import com.neuronrobotics.sdk.dyio.DyIOOutputStream;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.dyio.InvalidChannelOperationException;


/**
 * 
 */
public class UARTChannel implements ISendable {
	private ArrayList<IUARTStreamListener> listeners = new ArrayList<IUARTStreamListener>();
	public static final int UART_IN = 17;
	public static final int UART_OUT = 16;
	DyIO device;
	UARTTxChannel tx;
	UARTRxChannel rx;
	
	/**
	 * 
	 * 
	 * @param d
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
	 * 
	 * 
	 * @param size
	 * @return
	 */
	public byte[] getBytes(int size) {
		return rx.getBytes(size);
	}
	
	
	/**
	 * 
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public boolean sendBytes(ByteList stream) throws IOException{
		return tx.putStream(stream);
	}
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	public int getInStreamSize(){
		return rx.getInStreamSize();
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean inStreamDataReady(){
		return (getInStreamSize()>0);
	}
	
	/**
	 * 
	 * 
	 * @param baudrate
	 * @return
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
		device.send(new SetUARTBaudrateCommand(UART_IN, baudrate));
		return true;

	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public DyIOOutputStream getOutStream(){
		return tx.getOutStream();
	}
	
	/**
	 * 
	 * 
	 * @return
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
	 * 
	 * 
	 * @param e
	 */
	protected void fireChannelEvent(DyIOChannelEvent e) {
		for(IUARTStreamListener l : listeners) {
			l.onChannelEvent(e);
		}
	}
	
	private class UARTTxChannel extends DyIOAbstractPeripheral {
		DyIOOutputStream out;
		public UARTTxChannel(DyIOChannel channel) {
			super(channel,DyIOChannelMode.USART_TX);
			if(!setMode()) {
				throw new DyIOPeripheralException("Could not set channel " + channel + " to " + DyIOChannelMode.USART_TX +  " mode");
			}
			out = new DyIOOutputStream(channel);
		}
		public boolean putStream(ByteList stream) throws IOException {
			out.write(stream);
			return false;
		}
		public DyIOOutputStream getOutStream(){
			return out;
		}
		
		public boolean hasAsync() {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
	
	private class UARTRxChannel extends DyIOAbstractPeripheral implements IChannelEventListener {
		DyIOInputStream in;
		public UARTRxChannel(DyIOChannel channel) {
			super(channel,DyIOChannelMode.USART_RX);
			channel.addChannelEventListener(this);
			if(!setMode()) {
				throw new DyIOPeripheralException("Could not set channel " + channel + " to " + DyIOChannelMode.USART_RX +  " mode");
			}
			in = new DyIOInputStream(channel);
		}

		
		public void onChannelEvent(DyIOChannelEvent e) {
			in.write(e.getData());
			fireChannelEvent(e);
		}
		public int getInStreamSize(){
			return in.available();
		}

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
		public DyIOInputStream getInputStream(){
			return in;
		}

		
		public boolean hasAsync() {
			// TODO Auto-generated method stub
			return true;
		}

	}
}
