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
package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.InvalidResponseException;

/**
 * 
 */
public class GetValueCommand extends BowlerAbstractCommand {
	private int channel;
	
	/**
	 * 
	 * 
	 * @param channel
	 */
	public GetValueCommand(int channel) {
		this.channel = channel;
		setOpCode("gchv");
		setMethod(BowlerMethod.GET);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#getCallingData()
	 */
	@Override
	public byte[] getCallingData() {
		return ByteList.wrap(channel);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#parseResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		if (data == null){
			System.err.println("No response to Get Value Command\n"+data);
			throw new InvalidResponseException("Get Channel Value did not respond.");
		}
		if(!data.getRPC().equals(getOpCode())) {
			System.err.println("Wrong response to Get Value Command, expected:"+getOpCode()+", got:\n"+data);
			throw new InvalidResponseException("Get Channel Value did not return with 'gchv'.\n"+data);
		}
		
		return data;
	}
}
