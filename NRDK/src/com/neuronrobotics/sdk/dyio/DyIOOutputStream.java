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
package com.neuronrobotics.sdk.dyio;

import java.io.IOException;
import java.io.OutputStream;

import com.neuronrobotics.sdk.common.ByteList;

/**
 * 
 */
public class DyIOOutputStream extends OutputStream {
	DyIOChannel chan;
	
	/**
	 * 
	 * 
	 * @param channel
	 */
	public DyIOOutputStream(DyIOChannel channel) {
		chan = channel;
	}
	
	/**
	 * Writes <code>bl</code> to this output stream as a single packet verses
	 * individual writes for each byte.
	 * 
	 * @param bl
	 *            - the data
	 * @throws IOException
	 *             - if an I/O error occurs.
	 */
	public void write(ByteList bl) throws IOException {
		if(chan.getMode() != DyIOChannelMode.USART_TX) {
			throw new IOException("The DyIO is not configured with a UART Tx mode");
		}
		//Bypassing the channel on transmit to prevent re-transmits
		//chan.getDevice().send(new SetChannelValueCommand(chan.getNumber(),bl));
		//System.out.println("Sending ByteList: "+bl.asString());
		while(bl.size()>0){
			ByteList b;
			if(bl.size()>20){
				b = new ByteList(bl.popList(20));
			}else{
				b = new ByteList(bl.popList(bl.size()));
			}
			//System.out.println("Sending ByteList: "+b.asString());
			chan.setValue(b);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		write(new ByteList(b));
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		write(new ByteList(b));
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		write(new ByteList(b).getBytes(off, len));
	}
}
