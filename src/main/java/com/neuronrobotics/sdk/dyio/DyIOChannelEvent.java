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

import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * a DyIO Channel Event.
 * 
 * @author rbreznak
 */
public class DyIOChannelEvent {
	
	/** The data. */
	private ByteList data;
	
	/** The channel. */
	private DyIOChannel channel;
	
	/** The integer. */
	private Integer integer;
	
	/**
	 * Instantiates a new dy io channel event.
	 *
	 * @param channel the channel
	 * @param data the data
	 */
	public DyIOChannelEvent(DyIOChannel channel, ByteList data) {
		this.channel = channel;
		this.data = data;
	}
	
	/**
	 * Instantiates a new dy io channel event.
	 *
	 * @param c the c
	 * @param integer the integer
	 */
	public DyIOChannelEvent(DyIOChannel c, Integer integer) {
		// TODO Auto-generated constructor stub
		this.channel =c;
		this.integer = integer;
		
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public DyIOChannel getChannel() {
		return channel;
	}
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public ByteList getData() {
		return data;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		if(integer!=null)
			return integer;
		int value;
		DyIOChannelMode mode = getChannel().getCurrentMode();
		if(channel.isStreamChannel())
			return 0;
		switch(mode){
		case COUNT_IN_DIR:
		case COUNT_IN_INT:
		case COUNT_OUT_DIR:
		case COUNT_OUT_INT:
			value = getSignedValue();
			break;
		default:
			value = getUnsignedValue();
			break;
		}
		return value;
	}
	
	/**
	 * Gets the unsigned value.
	 *
	 * @return the unsigned value
	 */
	public int getUnsignedValue() {
		if(integer!=null)
			return integer;
		return ByteList.convertToInt(getData().getBytes(),false);
	}
	
	/**
	 * Gets the signed value.
	 *
	 * @return the signed value
	 */
	public int getSignedValue() {
		if(integer!=null)
			return integer;
		int value =ByteList.convertToInt(getData().getBytes(),true);
		return value;
	}
}
