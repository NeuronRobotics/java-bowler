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

	private PrintWriter out;

	private Socket socket;

	
	/**
	 * @throws IOException 
	 * 
	 */
	public BowlerTCPServer(Socket socket) throws IOException{
		this.socket = socket;
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
		Log.warning("Disconnecting..");
		super.disconnect();
		try {
			socket.close();			
			Log.warning("\n\nWaiting for sockets to shut down...\n\n");
			ThreadUtil.wait(1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
			throw new RuntimeException();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
//	private class TCPListener extends Thread {
//		private Socket connectionSocket=null;
//		public void run(){
//			//Log.info("Starting the TCP listener...");
//			setConnected(true);
//			try{
//				while(isConnected()){
//					Log.warning("Waiting for old connection to close...");
////					while(true){
////						try{
////							getDataIns();
////							getDataOuts();
////							ThreadUtil.wait(100);
////						}catch(Exception ex){
////							break;
////						}
////					}
//					Log.warning("\n\nWaiting for next connection...");
//					connectionSocket = getTcpSock().accept();
//					Log.warning("\n\nGot connection..");
//					setDataIns(new DataInputStream(connectionSocket.getInputStream()));
//					setDataOuts(new DataOutputStream(connectionSocket.getOutputStream()));
//					out = new PrintWriter(connectionSocket.getOutputStream(), true);
//					setConnected(true);
//				}
//			}catch(Exception ex){
//				// catch the loop break
//			}
//			try {
//				if(connectionSocket!=null)
//					connectionSocket.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Log.error("TCP server loop exit");
//		}
//	}



	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	 */
	@Override
	public boolean reconnect() {
		Log.warning("TCP Reconnect");
		try {			
			disconnect();
			if(!isConnected()){
				connect();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			return true;
		return !out.checkError();
	}

//	public ServerSocket getTcpSock() {
//		return tcpSock;
//	}
//
//	public void setTcpSock(ServerSocket tcpSock) {
//		if(this.tcpSock!= null){
//			try {
//				this.tcpSock.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		this.tcpSock = tcpSock;
//	}
//
//	public TCPListener getTcpServerThread() {
//		return tcpServerThread;
//	}
//
//	public void setTcpServerThread(TCPListener tcpServerThread) {
//		this.tcpServerThread = tcpServerThread;
//	}


}
