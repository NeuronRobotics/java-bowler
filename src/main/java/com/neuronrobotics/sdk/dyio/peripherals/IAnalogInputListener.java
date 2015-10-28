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
package com.neuronrobotics.sdk.dyio.peripherals;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IAnalogInput events.
 * The class that is interested in processing a IAnalogInput
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIAnalogInputListener  method. When
 * the IAnalogInput event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AnalogInputChannel
 */
public interface IAnalogInputListener {
	
	/**
	 * IAnalogInputListener.
	 *
	 * @param chan the chan
	 * @param value            The value of the analog channel is sent to listeners
	 */
	public void onAnalogValueChange(AnalogInputChannel chan,double value);
}
