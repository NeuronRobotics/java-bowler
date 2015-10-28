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

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IDyIOStateChange events.
 * The class that is interested in processing a IDyIOStateChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIDyIOStateChangeListener  method. When
 * the IDyIOStateChange event occurs, that object's appropriate
 * method is invoked.
 *
 * @see IDyIOEvent
 */
public interface IDyIOStateChangeListener {
	
	/**
	 * On state change.
	 *
	 * @param channel the channel
	 * @param mode the mode
	 */
	public void onStateChange(int channel, DyIOChannelMode mode);
}
