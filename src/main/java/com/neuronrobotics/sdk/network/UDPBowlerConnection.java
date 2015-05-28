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
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;

/**
 * 
 */
public class UDPBowlerConnection extends BowlerAbstractConnection{
	private int sleepTime = 5000;

	private int port = 1865;

	
	private InetAddress IPAddressSet=null;
	private ArrayList<InetAddress>  addrs=null;
	//private ByteList internalReceiveBuffer= new ByteList();
	private DatagramSocket udpSock = null;
	
	/**
	 * 
	 */
	public UDPBowlerConnection(){
		init();
	}
	public UDPBowlerConnection(InetAddress set){
		init();
		setAddress(set);
	}
	public UDPBowlerConnection(InetAddress set,int port){
		this.port=port;
		init();
		setAddress(set);
	}
	/**
	 * 
	 * 
	 * @param set
	 */
	public void setAddress(InetAddress set){
		IPAddressSet=set;
    }
	
	/**
	 * 
	 * 
	 * @param port
	 */
	public UDPBowlerConnection(int port){
		this.port=port;
		init();
	}
	
	
	/**
	 * Gets the data ins.
	 *
	 * @return the data ins
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
		//waitForConnectioToBeReady();
		setLastWrite(System.currentTimeMillis());
		
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddressSet, port);
		//Log.info("Sending UDP packet: "+sendPacket);
		udpSock.send(sendPacket);
		
	}
	
	@Override
	public BowlerDatagram loadPacketFromPhy(ByteList bytesToPacketBuffer) throws NullPointerException, IOException{
		byte[] receiveData=new byte[4096];
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		//Log.info("Waiting for UDP packet");
		udpSock.setSoTimeout(1);// Timeout the socket after 1 ms
		try{
			udpSock.receive(receivePacket);
		}catch(Exception ex){
			// disconnect called
			//Log. warning("Receive bailed out because of close");
			return null;
		}
		
		Log.info("Got UDP packet");
		if(addrs== null)
			addrs=new ArrayList<InetAddress>();
		getAllAddresses().add(receivePacket.getAddress());
		
		byte [] data = receivePacket.getData();
		
		for (int i=0;i<receivePacket.getLength();i++){
			bytesToPacketBuffer.add(data[i]);
		}
		
		BowlerDatagram bd =null;
	
		return BowlerDatagramFactory.build(bytesToPacketBuffer);
	}
	
	

	
	private void init(){
		setSynchronusPacketTimeoutTime(sleepTime);
		setChunkSize(5210);
		try {
			if(IPAddressSet == null)
				IPAddressSet=InetAddress.getByAddress(new byte[]{(byte) 255,(byte) 255,(byte) 255,(byte) 255});
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(connect()){
			
		}else{
			Log.error("Connection failed");
			throw new RuntimeException("UDP Connection failed");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	public void disconnect(){
		if(udpSock!=null){
			udpSock.close();
			udpSock=null;
		}
		setConnected(false);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		if(isConnected()){
			Log.info("already connected..");
			return true;
		}
		setConnected(false);
		try {
			udpSock =  new DatagramSocket();
			
			setConnected(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setConnected(false);
		}
		return isConnected();	
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
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
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	public ArrayList<InetAddress>  getAllAddresses(){
		if(addrs== null){
			addrs=new ArrayList<InetAddress>();
			try {
				
				//Generate a ping command
				BowlerDatagram ping = BowlerDatagramFactory.build(new MACAddress(), new PingCommand());
				ping.setUpstream(false);
				Log.info("Sending synchronization ping: \n"+ping);
				//send it to the UDP socket
				write(ping.getBytes());
				//wait for all devices to report back
				try {Thread.sleep(3000);} catch (InterruptedException e) {}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return addrs;
	}

	public void setAddress(String address) {
		for (InetAddress in: getAllAddresses()) {
			if(in.getHostAddress().contains(address)) {
				 setAddress(in);
				 return;
			}
				
		}
		throw new RuntimeException("Unknown address");
	}
}
