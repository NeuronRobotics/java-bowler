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


import java.io.IOException;
import java.net.SocketException;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;


/**
 * 
 */
public class BowlerUDPServer extends BowlerAbstractConnection {
	private int sleepTime = 1000;
	private int pollTimeoutTime = 5;
	
	
	private UDPStream udp = null;

	private int port = 1865;
	
	/**
	 * 
	 */
	public BowlerUDPServer(){
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
		setChunkSize(5210);
	}
	
	/**
	 * 
	 * 
	 * @param port
	 */
	public BowlerUDPServer(int port){
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
		setChunkSize(5210);
		this.port=port;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	public void disconnect(){
		if(udp != null)
			udp.disconnect();
		udp=null;
		setConnected(false);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		if(isConnected())
			return true;
		setConnected(false);
		try {
			udp = new UDPStream(port,true);
			udp.start();
			setDataIns(udp.getDataInputStream());
			setDataOuts(udp.getDataOutptStream());
			setConnected(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isConnected();	
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	 */
	@Override
	public boolean reconnect() throws IOException {
		disconnect();
		connect();
		return true;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
	 */
	@Override
	public boolean waitingForConnection() {
		return false;
	}

}
