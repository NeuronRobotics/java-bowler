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
public class SetUARTBaudrateCommand extends BowlerAbstractCommand {
	
	/**
	 * 
	 * 
	 * @param channel
	 * @param baudrate
	 */
	public SetUARTBaudrateCommand(int channel, int baudrate) {
		setMethod(BowlerMethod.CRITICAL);
		setOpCode("cchn");
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(ByteList.convertTo32(baudrate));
	}
		
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#parseResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		
		if(!data.getRPC().equals("_rdy")) {
			throw new InvalidResponseException("Could not set the UART passthough baudrate.");
		}
		//System.out.println("Baudrate set return: \n"+data);
		return data;
	}
}
