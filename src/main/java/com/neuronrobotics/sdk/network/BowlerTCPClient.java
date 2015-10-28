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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;



// TODO: Auto-generated Javadoc
/**
 * The Class BowlerTCPClient.
 */
public class BowlerTCPClient extends BowlerAbstractConnection{
	
	/** The sleep time. */
	private int sleepTime = 5000;
	
	/** The reconnect retry. */
	private int reconnectRetry = 5;
	
	/** The tcp sock. */
	private Socket tcpSock = null;
	
	/** The tcp addr. */
	private InetAddress tcpAddr=null;


	/** The port. */
	private int port;
	
	
	/**
	 * Instantiates a new bowler tcp client.
	 */
	public BowlerTCPClient(){
		setSynchronusPacketTimeoutTime(sleepTime);
	}
	
	/**
	 * Instantiates a new bowler tcp client.
	 *
	 * @param addr the addr
	 * @param port the port
	 * @throws Exception the exception
	 */
	public BowlerTCPClient(String addr,int port) throws Exception{
		this.port = port;
		if(isConnected())
			return;
		setSynchronusPacketTimeoutTime(sleepTime);
		Log.info("Bowler TCP connection on: "+addr+":"+port);
		try {
			InetAddress address = InetAddress.getByName(addr);
			setTCPAddress(address);
			Socket clientSocket =new Socket(address,port);
			
			setTCPSocket(clientSocket);
		} catch (UnknownHostException e) {
			Log.error("No such host");
			throw e;
		} catch (Exception e) {
			Log.error("Port un-availible");
			throw e;
		}
	}
	
	/**
	 * Instantiates a new bowler tcp client.
	 *
	 * @param addr the addr
	 * @param port the port
	 * @throws Exception the exception
	 */
	public BowlerTCPClient(InetAddress addr,int port) throws Exception {
		this(addr.getHostName(),port);
	}
	
	/**
	 * Sets the TCP address.
	 *
	 * @param addr the new TCP address
	 */
	public void setTCPAddress(InetAddress addr){
		tcpAddr=addr;
	}
	
	/**
	 * Sets the TCP port.
	 *
	 * @param port the new TCP port
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void setTCPPort(int port) throws IOException{
		if(isConnected())
			return;
		if (tcpAddr==null&&(port>0)&&(port<0xffff))
			throw new IOException("Connection Info Invalid: "+":"+port);
		setTCPSocket(new Socket(tcpAddr, port));
	}
	
	/**
	 * Sets the TCP socket.
	 *
	 * @param sock the new TCP socket
	 */
	public void setTCPSocket(Socket sock){
		if(isConnected())
			return;
		Log.info("Setting TCP socket");
		while(!sock.isBound());
		tcpSock = sock;
		try {
			tcpSock.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connect();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		if (tcpSock == null)
			throw new RuntimeException("Can't connect before setting up the socket information");
		if(!isConnected()){
			try {
				setDataIns(new DataInputStream(tcpSock.getInputStream()));
				setDataOuts(new DataOutputStream(tcpSock.getOutputStream()));
				setConnected(true);
			} catch (IOException e) {
				e.printStackTrace();
				setConnected(false);
			}
		}
		return isConnected();	
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	@Override
	public void disconnect() {

		Log.warning("Disconnecting Tcp Client..");
		super.disconnect();
		try {
			if(!tcpSock.isClosed()){
				tcpSock.shutdownOutput(); // Sends the 'FIN' on the network
			    while (getDataIns().read() >= 0) ; // "read()" returns '-1' when the 'FIN' is reached
			    tcpSock.close(); // Now we can close the Socket
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public static ArrayList<InetAddress> getAvailableSockets() {
        ArrayList<InetAddress> available = new  ArrayList<InetAddress> ();
        UDPBowlerConnection udp;
        try {
			udp = new UDPBowlerConnection();
			available= udp.getAllAddresses();
	        udp.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return available;
    }
	


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	 */
	/**
	 * Reconnect.
	 *
	 * @return true, if successful
	 */
	//@Override
	public boolean reconnect() {
		Log.warning("Reconnecting TCP Socket..");
		disconnect();
		ThreadUtil.wait(getSleepTime());
		for(int i=0;i<getReconnectRetry();i++){
			try {
				setTCPSocket(new Socket(tcpAddr,port));
				connect();
				if(isConnected())
					return true;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			disconnect();
			ThreadUtil.wait(i*10*getSleepTime());
			Log.error("Reconnect failed, retry: "+i);
		}
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

	/**
	 * Gets the reconnect retry.
	 *
	 * @return the reconnect retry
	 */
	public int getReconnectRetry() {
		return reconnectRetry;
	}

	/**
	 * Sets the reconnect retry.
	 *
	 * @param reconnectRetry the new reconnect retry
	 */
	public void setReconnectRetry(int reconnectRetry) {
		this.reconnectRetry = reconnectRetry;
	}
	
}
