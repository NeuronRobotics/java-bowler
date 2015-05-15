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
import java.io.InputStream;

import com.neuronrobotics.sdk.common.ByteList;

/**
 * 
 */
public class DyIOInputStream extends InputStream {

	private ByteList buffer = new ByteList();
	DyIOChannel chan;
	
	/**
	 * 
	 * 
	 * @param channel
	 */
	public DyIOInputStream(DyIOChannel channel) {
		chan = channel;
	}
	
	/**
	 * 
	 * 
	 * @param data
	 */
	public void write(ByteList data) {
		synchronized (buffer) {
			buffer.add(data);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		while(chan.getMode() == DyIOChannelMode.USART_RX) {
			if(available() > 0) {
				// cast the poped byte as an int to make sure its positive
				Byte val = buffer.pop();
				
				if(val == null) {
					continue;
				}
				
				return (int) val;
			}
		}
		
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() {
		return buffer.size();
	}
}
