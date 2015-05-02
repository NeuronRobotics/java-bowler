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

/**
 * a DyIO Channel Event.
 * 
 * @author rbreznak
 */
public class DyIOChannelEvent {
	private ByteList data;
	private DyIOChannel channel;
	private Integer integer;
	
	/**
	 * 
	 * 
	 * @param channel
	 * @param data
	 */
	public DyIOChannelEvent(DyIOChannel channel, ByteList data) {
		this.channel = channel;
		this.data = data;
	}
	
	public DyIOChannelEvent(DyIOChannel c, Integer integer) {
		// TODO Auto-generated constructor stub
		this.channel =c;
		this.integer = integer;
		
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public DyIOChannel getChannel() {
		return channel;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public ByteList getData() {
		return data;
	}

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
	
	public int getUnsignedValue() {
		if(integer!=null)
			return integer;
		return ByteList.convertToInt(getData().getBytes(),false);
	}
	
	public int getSignedValue() {
		if(integer!=null)
			return integer;
		int value =ByteList.convertToInt(getData().getBytes(),true);
		return value;
	}
}