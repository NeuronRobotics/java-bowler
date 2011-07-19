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
package com.neuronrobotics.sdk.common;
// TODO: Auto-generated Javadoc
/**
 * This exception is thrown when an invalid response is recieved from a device.
 * @author rbreznak
 *
 */
public class InvalidResponseException extends RuntimeException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The message. */
	private String message;
	
	/**
	 * Instantiates a new invalid response exception.
	 */
	public InvalidResponseException() {
		message = "Invalid Response";
		Log.warning(getMessage());
	}
	
	/**
	 * Instantiates a new invalid response exception.
	 *
	 * @param msg the msg
	 */
	public InvalidResponseException(String msg) {
		message = msg;
		Log.warning(getMessage());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}
}
