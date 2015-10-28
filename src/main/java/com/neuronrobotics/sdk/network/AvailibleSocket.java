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
package com.neuronrobotics.sdk.network;

import java.net.InetAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class AvailibleSocket.
 */
public class AvailibleSocket {
	
	/** The tcp addr. */
	private InetAddress tcpAddr=null;
	
	/** The port. */
	private int port=0;
	
	/**
	 * Sets the tcp addr.
	 *
	 * @param tcpAddr the new tcp addr
	 */
	public void setTcpAddr(InetAddress tcpAddr) {
		this.tcpAddr = tcpAddr;
	}
	
	/**
	 * Gets the tcp addr.
	 *
	 * @return the tcp addr
	 */
	public InetAddress getTcpAddr() {
		return tcpAddr;
	}
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
}
