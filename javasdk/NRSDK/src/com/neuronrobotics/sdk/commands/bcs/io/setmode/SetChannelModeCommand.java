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
package com.neuronrobotics.sdk.commands.bcs.io.setmode;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;

import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;

/**
 * 
 */
public class SetChannelModeCommand extends BowlerAbstractCommand {

	
	/**
	 * 
	 * 
	 * @param channel
	 * @param mode
	 */
	public SetChannelModeCommand(int channel, DyIOChannelMode mode) {
		setOpCode("schm");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(mode);
		getCallingDataStorage().add(0);
	}
	
	/**
	 * 
	 * 
	 * @param channel
	 * @param mode
	 * @param isAsync
	 */
	public SetChannelModeCommand(int channel, DyIOChannelMode mode,boolean isAsync) {
		setOpCode("schm");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(mode);
		getCallingDataStorage().add((isAsync?1:0));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#parseResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		if(!(data.getRPC().equals("_rdy") || data.getRPC().equals("schm"))) {
			throw new InvalidResponseException("Set Channel Mode did not return '_rdy' or 'schm'.");
		}
		
		return data;
	}
}
