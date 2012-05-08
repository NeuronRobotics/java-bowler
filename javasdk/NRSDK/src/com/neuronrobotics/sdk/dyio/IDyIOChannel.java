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
import com.neuronrobotics.sdk.common.ISendable;

/**
 * 
 */
public interface IDyIOChannel {
	
	/**
	 * This method returns the channel object when requested. 
	 * 
	 * @return the DyIOChannel object
	 */
	public DyIOChannel getChannel();
	
	/**
	 * Gets the channel's current mode.
	 * 
	 * @return the mode
	 */
	public DyIOChannelMode getMode();
	
	/**
	 * Get the current value of a channel.
	 * 
	 * @return the current value
	 */
	public int getValue();
	
	/**
	 * Set the mode of a channel.
	 * 
	 * @param mode the mode to set the channel to 
	 * @param async if it should be async or not
	 * @return if the action was successful
	 * @throws Exception if there is a communication error
	 */
	public boolean setMode(DyIOChannelMode mode, boolean async);
	
	/**
	 * Set the value of a channel. Channels may not be able to be set to certain
	 * or potentially any values depending on the mode that a channel is in.
	 * 
	 * @param value the value to set
	 * @return if the action was successful
	 */
	public boolean setValue(int value);
	
	/**
	 * Set the value of a channel. Channels may not be able to be set to certain or potentially any values
	 * depending on the mode that a channel is in. 
	 * 
	 * @param value
	 * @return true if successful
	 */
	public boolean setValue(ByteList value);
}
