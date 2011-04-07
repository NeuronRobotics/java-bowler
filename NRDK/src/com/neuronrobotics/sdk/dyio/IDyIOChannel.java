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

import com.neuronrobotics.sdk.common.ISendable;

/**
 * 
 */
public interface IDyIOChannel {
	
	/**
	 * 
	 * 
	 * @return
	 */
	public DyIOChannel getChannel();
	
	/**
	 * Gets the channel's current mode.
	 * 
	 * @return
	 */
	public DyIOChannelMode getMode();
	
	/**
	 * Get the current value of a channel.
	 * 
	 * @return
	 */
	public int getValue();
	
	/**
	 * Set the mode of a channel.
	 * 
	 * @param mode
	 * @param async
	 * @return if the action was successful
	 * @throws Exception 
	 */
	public boolean setMode(DyIOChannelMode mode, boolean async);
	
	/**
	 * Set the value of a channel. Channels may not be able to be set to certain
	 * or potentially any values depending on the mode that a channel is in.
	 * 
	 * @param value
	 * @return if the action was successful
	 */
	public boolean setValue(int value);
	
	/**
	 * Set the value of a channel. Channels may not be able to be set to certain or potentially any values
	 * depending on the mode that a channel is in. 
	 * 
	 * @param value
	 * @return
	 */
	public boolean setValue(ISendable value);
}
