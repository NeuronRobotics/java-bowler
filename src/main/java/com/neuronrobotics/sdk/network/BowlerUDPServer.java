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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;


// TODO: Auto-generated Javadoc
/**
 * The Class BowlerUDPServer.
 */
public class BowlerUDPServer extends BowlerAbstractConnection {
	
	/** The sleep time. */
	private int sleepTime = 1000;
	
	/** The IP address set. */
	private InetAddress IPAddressSet=null;
	
	/** The internal receive buffer. */
	private ByteList internalReceiveBuffer= new ByteList();
	
	/** The udp sock. */
	private DatagramSocket udpSock = null;
	
	//private UDPStream udp = null;

	/** The port. */
	private int port = 1865;
	
	/** The destination port. */
	private int destinationPort=port;
	
	/**
	 * Instantiates a new bowler udp server.
	 */
	public BowlerUDPServer(){
		setSynchronusPacketTimeoutTime(sleepTime);
		setChunkSize(5210);
	}
	
	/**
	 * Instantiates a new bowler udp server.
	 *
	 * @param port the port
	 */
	public BowlerUDPServer(int port){
		setSynchronusPacketTimeoutTime(sleepTime);
		setChunkSize(5210);
		this.port=port;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	public void disconnect(){
		if(udpSock != null)
			udpSock.close();
		udpSock=null;
		setConnected(false);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		if(isConnected())
			return true;
		try {
			udpSock = new DatagramSocket(port);
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
	/**
	 * Reconnect.
	 *
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//@Override
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
	
	
	
	/**
	 * Gets the data ins.
	 *
	 * @return the data ins
	 * @throws NullPointerException the null pointer exception
	 */
	@Override
	public DataInputStream getDataIns() throws NullPointerException{
		new RuntimeException("This method should not be called").printStackTrace();
		while(true);
	}

	/**
	 * Gets the data outs.
	 *
	 * @return the data outs
	 * @throws NullPointerException the null pointer exception
	 */
	@Override
	public DataOutputStream getDataOuts() throws NullPointerException{
		new RuntimeException("This method should not be called").printStackTrace();
		while(true);
	}
	
	/**
	 * Write.
	 *
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//private ByteList outgoing = new ByteList();
	public void write(byte[] data) throws IOException {
		waitForConnectioToBeReady();
		setLastWrite(System.currentTimeMillis());
		
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddressSet, destinationPort);
		Log.info("Sending UDP packet: "+sendPacket);
		udpSock.send(sendPacket);
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#loadPacketFromPhy(com.neuronrobotics.sdk.common.ByteList)
	 */
	@Override
	public BowlerDatagram loadPacketFromPhy(ByteList bytesToPacketBuffer) throws NullPointerException, IOException{
		byte[] receiveData=new byte[4096];
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		Log.info("Waiting for UDP packet");
		
		try{
			udpSock.receive(receivePacket);
		}catch(SocketException ex){
			// disconnect called
			Log. warning("Receive bailed out because of close");
			return null;
		}
		
		IPAddressSet=(receivePacket.getAddress());
		destinationPort = receivePacket.getPort();
		
		Log.info("Got UDP packet from "+IPAddressSet+" : "+destinationPort);
		
		byte [] data = receivePacket.getData();
		
		for (int i=0;i<receivePacket.getLength();i++){
			internalReceiveBuffer.add(data[i]);
		}
		BowlerDatagram bd =null;
		
		while(internalReceiveBuffer.size()>0){
			bytesToPacketBuffer.add(internalReceiveBuffer.pop());
			if (bd==null) {
				bd = BowlerDatagramFactory.build(bytesToPacketBuffer);
			}
		}
	
		return bd;
	}
	

}
