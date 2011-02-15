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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.List;


import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;



/**
 * 
 */
public class BowlerTCPClient extends BowlerAbstractConnection{
	private int sleepTime = 5000;
	private int pollTimeoutTime = 5;
	
	private Socket tcpSock = null;
	private InetAddress tcpAddr=null;
	
	
	/**
	 * 
	 */
	public BowlerTCPClient(){
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
	}
	
	/**
	 * 
	 * 
	 * @param addr
	 * @param port
	 * @throws IOException 
	 */
	public BowlerTCPClient(String addr,int port) throws IOException{
		if(isConnected())
			return;
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
		Log.info("Bowler TCP connection on: "+addr+":"+port);
		try {
			setTCPSocket(new Socket(addr,port));
		} catch (UnknownHostException e) {
			System.err.println("No such host");
		} catch (IOException e) {
			System.err.println("Port un-availible");
			throw e;
		}
	}
	
	/**
	 * 
	 * 
	 * @param addr
	 * @param port
	 * @throws IOException 
	 */
	public BowlerTCPClient(InetAddress addr,int port) throws IOException {
		if(isConnected())
			return;
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
		try {
			setTCPSocket(new Socket(addr,port));
		} catch (IOException e) {
			System.err.println("Port un-availible");
			throw e;
		}
	}
	
	/**
	 * 
	 * 
	 * @param addr
	 */
	public void setTCPAddress(InetAddress addr){
		tcpAddr=addr;
	}
	
	/**
	 * 
	 * 
	 * @param port
	 * @throws IOException
	 */
	public void setTCPPort(int port) throws IOException{
		if(isConnected())
			return;
		if (tcpAddr==null&&(port>0)&&(port<0xffff))
			throw new IOException("Connection Info Invalid: "+":"+port);
		setTCPSocket(new Socket(tcpAddr, port));
	}
	
	/**
	 * 
	 * 
	 * @param sock
	 */
	public void setTCPSocket(Socket sock){
		if(isConnected())
			return;
		Log.info("Setting TCP socket");
		while(!sock.isBound());
		tcpSock = sock;
		connect();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		if (tcpSock == null)
			throw new RuntimeException("Can't connect before setting up the socket information");
		// TODO Auto-generated method stub
		try {
			setDataIns(new DataInputStream(tcpSock.getInputStream()));
			setDataOuts(new DataOutputStream(tcpSock.getOutputStream()));
			setConnected(true);
		} catch (IOException e) {
			e.printStackTrace();
			setConnected(false);
		}
		return isConnected();	
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		try {tcpSock.close();} catch (Exception e) {}
		tcpSock = null;
		setDataIns(null);
		setDataOuts(null);
	}
	
	/**
	 * This will broadcast out 1 packet on UDP socket 1865.
	 * It will wait for devices to respond and makes a list of 
	 * the availible TCP sockets
	 * @return list of devices that responded 
	 */
	public static List<AvailibleSocket> getAvailableSockets() {
        ArrayList<AvailibleSocket> available = new ArrayList<AvailibleSocket>();
        
        return available;
    }
	


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	 */
	@Override
	public boolean reconnect() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
	 */
	@Override
	public boolean waitingForConnection() {
		// TODO Auto-generated method stub
		return false;
	}

}
