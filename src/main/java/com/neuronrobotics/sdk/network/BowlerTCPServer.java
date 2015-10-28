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


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;



// TODO: Auto-generated Javadoc
/**
 * The Class BowlerTCPServer.
 */
public class BowlerTCPServer extends BowlerAbstractConnection{
	
	/** The sleep time. */
	private int sleepTime = 5000;

	/** The out. */
	private PrintWriter out;

	/** The socket. */
	private Socket socket;

	
	/**
	 * Instantiates a new bowler tcp server.
	 *
	 * @param socket the socket
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public BowlerTCPServer(Socket socket) throws IOException{
		this.socket = socket;
		try {
			socket.setSoTimeout(1000);
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setSynchronusPacketTimeoutTime(sleepTime);
		setChunkSize(5210);
		setDataIns(new DataInputStream(socket.getInputStream()));
		setDataOuts(new DataOutputStream(socket.getOutputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		setConnected(true);
		connect();
	}
	
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		
		if(isConnected())
			return true;
		Log.warning("Connecting..");

		return isConnected();	
	}
	
	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	@Override
	public boolean isConnected() {
		
		return super.isConnected();
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		if(!isConnected())
			return;
		Log.warning("Disconnecting Tcp Server..");
		super.disconnect();
		try {
			
			if(!socket.isClosed()){
				socket.shutdownOutput(); // Sends the 'FIN' on the network
			    while (getDataIns().read() >= 0) ; // "read()" returns '-1' when the 'FIN' is reached
			    socket.close(); // Now we can close the Socket	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	


//	/* (non-Javadoc)
//	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
//	 */
//	@Override
//	public boolean reconnect() {
//		Log.warning("TCP Server Reconnect, just disconnecting");
//		disconnect();
//		return false;
//	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
	 */
	@Override
	public boolean waitingForConnection() {
		return false;
	}

	/**
	 * Checks if is client connected.
	 *
	 * @return true, if is client connected
	 */
	public boolean isClientConnected() {
		if(out==null)
			return true;
		return !out.checkError();
	}



}
