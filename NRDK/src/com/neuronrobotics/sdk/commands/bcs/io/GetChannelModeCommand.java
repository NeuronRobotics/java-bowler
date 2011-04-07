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

import com.neuronrobotics.sdk.common.InvalidResponseException;

/**
 * 
 */
public class GetChannelModeCommand extends BowlerAbstractCommand {
	/**
	 * 
	 */
	public GetChannelModeCommand() {
		setOpCode("gacm");
		setMethod(BowlerMethod.GET);
	}
	
	/**
	 * 
	 * 
	 * @param channel
	 */
	public GetChannelModeCommand(int channel) {
		setOpCode("gchm");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(channel);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#parseResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		
		if(!data.getRPC().equals(getOpCode())) {
			throw new InvalidResponseException("Get Channel Mode did not return with '" + getOpCode() + "'.");
		}
		
		if(getOpCode().equals("gacm") && data.getData().size() != 24) {
			throw new InvalidResponseException("Get All Channel Mode did not return with 24 values.");
		}
		
		if(getOpCode().equals("gchm") && data.getData().size() != 1) {
			//throw new InvalidResponseException("Get Channel Mode did not return with 1 values.");
		}
		
		return data;
	}
}
