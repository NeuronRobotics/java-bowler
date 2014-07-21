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
import java.io.InputStream;
import java.io.OutputStream;
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
import com.neuronrobotics.sdk.util.ThreadUtil;

/**
 * 
 */
public class UDPStream {
	private int port = 1865;
	private DatagramSocket udpSock = null;
	byte[] receiveData=new byte[4096];
    byte[] sendData;
    ArrayList<InetAddress>  addrs = new ArrayList<InetAddress>();
    InetAddress IPAddress=null;
    private int incomingPort = port;
    byte [] bcast = {(byte) 255,(byte) 255,(byte) 255,(byte) 255};
    InetAddress broadcast = null;
    InetAddress localHost = null;
    InetAddress IPAddressSet=null;
    UDPins INS=new  UDPins();
    UDPouts OUTS=new UDPouts(); 

    private boolean isServer;
    private boolean isAlive = true;
    
    byte [] lastSent=new byte[1];
    
    /**
	 * 
	 * 
	 * @param port
	 * @param isServer
	 * @throws SocketException
	 */
    public UDPStream(int port, boolean isServer) throws SocketException{
    	this.port=port;
    	this.isServer=isServer;
    	setUDPSocket(port);
    }
    
    /**
	 * 
	 * 
	 * @param port
	 * @param set
	 * @param isServer
	 * @throws SocketException
	 */
    public UDPStream(int port,InetAddress set,boolean isServer) throws SocketException{
    	this.port=port;
    	this.isServer=isServer;
    	setAddress(set);
    	setUDPSocket(port);
    }
    
    /**
	 * 
	 * 
	 * @param set
	 */
    public void setAddress(InetAddress set){
    	IPAddressSet = set;
    }
    
    private void setUDPSocket(int port) throws SocketException {
    	////Log.enableDebugPrint(true);
    	////Log.enableSystemPrint(true);
    	try {
    		broadcast = InetAddress.getByAddress(bcast);
    	} catch (UnknownHostException e) {}
		if(udpSock != null)
			udpSock.close();
		DatagramSocket serverSocket;
		if(isServer){
			//Log.info("Starting UDP as server");
			serverSocket = new DatagramSocket(port);
		}else{
			//Log.info("Starting UDP as client");
			serverSocket = new DatagramSocket();
		}
		while(!serverSocket.isBound()){
			ThreadUtil.wait(10);
		}
		udpSock = serverSocket;
		if(udpSock == null)
			throw new RuntimeException();
		
	}
    
	/**
	 * 
	 */
	public void start(){
		//Log.info("Starting the UDP Stream Manager...");
		isAlive = true;
		//INS.start();
		//OUTS.start();
	}
	
	private class UDPins {
		private ByteList inputData = new ByteList();
		DatagramPacket receivePacket;
		private InputStream ins = new InputStream() {
			
			private void update(){
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                	while(udpSock==null) {
                		ThreadUtil.wait(100);
                	}
					udpSock.receive(receivePacket);
					
					byte [] data = receivePacket.getData();
					byte [] tmp = new byte [receivePacket.getLength()];
					
					for (int i=0;i<receivePacket.getLength();i++){
						tmp[i]=data[i];
					}
					
					if(IPAddressSet != null){
						if(IPAddressSet.equals(IPAddress) ){
							////Log.info("Got data from MY address");
							add(tmp);
						}else{
							//Log.error("Data not from My host");
							IPAddress = null;
							return;
						}
					}else{
						////Log.info("Got data from .. someone");
						add(tmp);
					}
				} catch (IOException e) {
					e.printStackTrace();
					disconnect();
					try {
						setUDPSocket(port);
					} catch (SocketException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
			
			public int available(){
				if(inputData.size()>0) {
					return inputData.size();
				}
				return 0;
			}
			public final int read(byte[] b, int off,int len)throws IOException{
				update();
				////Log.info("Reading "+len+" bytes from UDP: "+inputData.size());
				int i=0;
				byte[] get;

				get = inputData.popList(off,len);
				
				for(i=0;i<len;i++) {
					if(i==b.length) {
						throw new IOException("Buffer too small to hold data");
					}
					b[i]=get[i];
				}
				////Log.info("Read: "+i+" Bytes, "+inputData.size()+" left");
				return i;
			}
			@Override
			public final int read( byte[] rawBuffer) throws IOException {
				update();

				return read(rawBuffer,0,inputData.size());
				
			}

			@Override
			public int read() throws IOException {
				update();

				if(inputData.size()>0)
					return inputData.pop();
				
				throw new IOException("Reading from empty buffer!");
			}
		};
		
		private void add(byte[] b){
			//Use most recent address as output
			IPAddress = receivePacket.getAddress();
			incomingPort = receivePacket.getPort();
			if(!addrs.contains(IPAddress))
				addrs.add(IPAddress);
			inputData.add(b);
		}
		DataInputStream dataInputStream=null;
		
		public DataInputStream getStream(){
			if(dataInputStream == null)
				dataInputStream = new DataInputStream(ins);
			return dataInputStream;
		}
	}
	private class UDPouts{
		private ByteList outputData = new ByteList();
		private InetAddress myAddr=null;
		private int myPort;
		private OutputStream outs = new OutputStream() {
			public void write(byte [] raw){
				outputData.add(raw);
				update();
				
			}
			public void flush(){
				
				while( outputData.size()>0);
			}
			@Override
			public void write(int arg) throws IOException {	
				outputData.add((byte)arg);
				update();
			}
		};
		public void update(){
	
				while(udpSock==null && isAlive){
            		ThreadUtil.wait(100);
            	}
				try {				
					if( outputData.size()>0){
						
						sendData=outputData.popList(outputData.size());
						
						if(sendData.length == 0 )
							return;
						
						lastSent=sendData;
						if(udpSock == null)
							throw new RuntimeException("Udp Socket is null!!");
						if(IPAddress == null){
							//Log.info("Sending as broadcast");
							myAddr = broadcast;
							myPort = port;
						}else {
							try {
								if (!IPAddress.equals(InetAddress.getLocalHost())){
									myAddr = IPAddress;
									myPort = incomingPort;
								}
								else{
									myAddr = broadcast;
									myPort = port;
								}
							} catch (UnknownHostException e) {
								myAddr = broadcast;
								myPort = port;
							}
						}
						try {
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, myAddr, myPort);
							Log.info("Sending UDP packet: "+sendPacket);
							udpSock.send(sendPacket);
						} catch (IOException e) {
							return;
						}	
					}
				} catch (Exception e) {}
			}
		
		
		DataOutputStream dataOutputStream=null;
				
		public DataOutputStream getStream(){
			if(dataOutputStream == null)
				dataOutputStream=new DataOutputStream(outs);
			return dataOutputStream;
		}
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public DataInputStream getDataInputStream() {
		return INS.getStream();
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public DataOutputStream getDataOutptStream() {
		return OUTS.getStream();
	}

	/**
	 * 
	 */
	public void disconnect() {
		if(udpSock != null)
			 udpSock.close();
		isAlive = false;
		udpSock=null;
	}
	
	public void updateAvailibleAddresses(){
		if(!isAlive)
			start();
        ArrayList<InetAddress> available = new  ArrayList<InetAddress> ();
        try {
			try {
				//Generate a ping command
				BowlerDatagram ping = BowlerDatagramFactory.build(new MACAddress(), new PingCommand());
				//send it to the UDP socket
				getDataOutptStream().write(ping.getBytes());
				//wait for all devices to report back
				try {Thread.sleep(3000);} catch (InterruptedException e) {}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        addrs = available;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public ArrayList<InetAddress>  getAllAddresses(){

		return addrs;
	}
}


