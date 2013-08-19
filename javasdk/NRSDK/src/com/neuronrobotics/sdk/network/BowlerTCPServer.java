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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;



/**
 * 
 */
public class BowlerTCPServer extends BowlerAbstractConnection{
	private int sleepTime = 5000;
	
	private ServerSocket tcpSock = null;
	private Socket connectionSocket=null;

	private TCPListener tcp = null;

	private int port = 1866;

	private PrintWriter out;
	
	/**
	 * 
	 */
	public BowlerTCPServer(){
		setSynchronusPacketTimeoutTime(sleepTime);
		setChunkSize(5210);
		try {
			setTCPSocket(port);
		} catch (UnknownHostException e) {
			Log.error("No such host");
		} catch (IOException e) {
			Log.error("Port un-availible");
		}
	}
	
	/**
	 * 
	 * 
	 * @param port
	 */
	public BowlerTCPServer(int port){
		setSynchronusPacketTimeoutTime(sleepTime);
		setChunkSize(5210);
		this.port=port;
		try {
			setTCPSocket(port);
		} catch (UnknownHostException e) {
			Log.error("No such host");
		} catch (IOException e) {
			Log.error("Port un-availible");
		}
	}
	
	private void setTCPSocket(int port) throws IOException{
		if(tcpSock != null)
			tcpSock.close();
		tcpSock = new ServerSocket(port);
		while(!tcpSock.isBound()){
			ThreadUtil.wait(10);
		}
		connect();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		if (tcpSock == null){
			try {
				setTCPSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		if(tcp == null){
			tcp = new TCPListener();
			tcp.start();
			setConnected(true);
			Log.warning("Connecting...OK");
		}
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
		Log.warning("Disconnecting..");
		try {
			tcpSock.close();
			tcp=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.disconnect();
	}
	
	
	private class TCPListener extends Thread {
		public void run(){
			//Log.info("Starting the TCP listener...");
			
			while(isConnected()){
				try {
					//Log.info("Waiting for next connection...");
					connectionSocket = tcpSock.accept();
					Log.info("\nGot connection..");
					setDataIns(new DataInputStream(connectionSocket.getInputStream()));
					setDataOuts(new DataOutputStream(connectionSocket.getOutputStream()));
					out = new PrintWriter(connectionSocket.getOutputStream(), true);
					setConnected(true);
				} catch (Exception e1) {
					setConnected(false);
					throw new RuntimeException(e1);
				}
				//Log.info("..OK!\n");
			}
		}
	}



	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	 */
	@Override
	public boolean reconnect() {
		disconnect();
		connect();
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
	 */
	@Override
	public boolean waitingForConnection() {
		return false;
	}

	public boolean isClientConnected() {
		if(out==null)
			return false;
		return !out.checkError();
	}


}
