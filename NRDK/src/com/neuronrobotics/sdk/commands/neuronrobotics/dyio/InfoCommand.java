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
package com.neuronrobotics.sdk.commands.neuronrobotics.dyio;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;

import com.neuronrobotics.sdk.common.BowlerMethod;



/**
 * 
 */
public class InfoCommand extends BowlerAbstractCommand {
	
	/**
	 * 
	 * 
	 * @param name
	 */
	public InfoCommand(String name) {
		setOpCode("info");
		setMethod(BowlerMethod.CRITICAL);
		int len = name.length();
		if (len>16)
			len=16;
		getCallingDataStorage().add(name.getBytes());
	}

	/**
	 * 
	 */
	public InfoCommand() {
		setOpCode("info");
		setMethod(BowlerMethod.GET);
	}

}
