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
package com.neuronrobotics.sdk.commands.bcs.core;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;

import com.neuronrobotics.sdk.common.InvalidResponseException;

/**
 * 
 */
public class NamespaceCommand extends BowlerAbstractCommand {
	
	/**
	 * 
	 */
	public NamespaceCommand() {
		setOpCode("_nms");
		setMethod(BowlerMethod.GET);
	}
	
	/**
	 * 
	 * 
	 * @param name
	 */
	public NamespaceCommand(int name) {
		setOpCode("_nms");
		setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(name);
	}
	/**
	 * 
	 * Jan 7, 2011
	 * @param name the number of namespaces
	 * @param upstream
	 */
	public NamespaceCommand(int name,boolean upstream) {
		setOpCode("_nms");
		if(upstream)
			setMethod(BowlerMethod.POST);
		else
			setMethod(BowlerMethod.GET);
		getCallingDataStorage().add(name);
	}
	
	public NamespaceCommand(String name) {
		setOpCode("_nms");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(name);
	}
	/**
	 * Determine if the return response was successful; throw an InvalidResponseExpection otherwise. Commands
	 * with more complicated validation should override this method and provide more specific checking. 
	 * 
	 * @param data
	 * @return the incoming response
	 * @throws InvalidResponseException
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		if (data==null){
			// TODO: Correct this with JSDK-8
			 throw new InvalidResponseException("No response from device");
		}
		// throws 
		if( data.getRPC().equals("_err")) {
			Integer zone=Integer.valueOf(data.getData().getByte(0));
			Integer section=Integer.valueOf(data.getData().getByte(1));
			
			switch(zone) {
			default:
				throw new InvalidResponseException("Unknown error. (" + zone + " " + section + ")");
			case 0:
				switch(section) {
				default:
					throw new InvalidResponseException("Unknow error in the communications stack. (" + zone + " " + section + ")");
				case 0x7f:
					throw new InvalidResponseException("The method provided is invalid.");
				case 0:
					throw new InvalidResponseException("The packet was not sent syncronously.");
				case 1:
					throw new InvalidResponseException("The RPC sent in undefined with GET method.");
				case 2:
					throw new InvalidResponseException("The RPC sent in undefined with POST method.");
				case 3:
					throw new InvalidResponseException("The RPC sent in undefined with CRITICAL method.");
				case 4:
					throw new InvalidResponseException("Invalid namespace request");
				case 5:
					throw new InvalidResponseException("Invalid namespace index requested");
				}
			}
		}
		
		return data;
	}
}
