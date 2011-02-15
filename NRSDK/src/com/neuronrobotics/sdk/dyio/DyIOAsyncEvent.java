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

import com.neuronrobotics.sdk.common.BowlerDatagram;
/**
 * An asyncrono event.
 * @author rbreznak
 *
 */
public class DyIOAsyncEvent implements IDyIOEvent {
	
	private BowlerDatagram datagram;
	
	/**
	 * Create a new asynchronous event with the given datagram.
	 * 
	 * @param data
	 *            The datagram
	 */
	public DyIOAsyncEvent(BowlerDatagram data) {
		datagram = data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return datagram.toString();
	}
}
