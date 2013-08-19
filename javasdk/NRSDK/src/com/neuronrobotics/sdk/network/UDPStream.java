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
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;

/**
 * 
 */
public class UDPStream {
	private int port = 1865;
	private MulticastSocket udpSock = null;
	
    byte[] sendData;
    ArrayList<UdpInterfaceData>  addrs = new ArrayList<UdpInterfaceData>();
    
    //InetAddress IPAddress=null;
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
		if(isServer){
			//Log.info("Starting UDP as server");
			try {
				udpSock = new MulticastSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//Log.info("Starting UDP as client");
			try {
				udpSock = new MulticastSocket();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(!udpSock.isBound()){
			ThreadUtil.wait(10);
		}
		if(udpSock == null)
			throw new RuntimeException();
		System.err.println("Socket Set up");
	}
    
	/**
	 * 
	 */
	public void start(){
		//Log.info("Starting the UDP Stream Manager...");
		setUdpAlive(true);
		INS.start();
		OUTS.start();
		
	}
	/**
	 * Private class for input streams
	 * @author hephaestus
	 *
	 */
	private class UDPins extends Thread {
		private ByteList inputData = new ByteList();
		
		
		private InputStream ins = new InputStream() {
			public int available(){
				if(inputData.size()>0) {
					return inputData.size();
				}
				return 0;
			}
			public final int read(byte[] b, int off,int len)throws IOException{
				////Log.info("Reading "+len+" bytes from UDP: "+inputData.size());
				int i=0;
				byte[] get;
				synchronized(inputData){
					get = inputData.popList(off,len);
				}
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
				synchronized(inputData){
					return read(rawBuffer,0,inputData.size());
				}
			}

			@Override
			public int read() throws IOException {
				synchronized(inputData){
					if(inputData.size()>0)
						return inputData.pop();
				}
				throw new IOException("Reading from empty buffer!");
			}
		};
		public void run(){
			byte[] receiveData=new byte[65500];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);;
			while(isUdpAlive()){
				try {Thread.sleep(10);} catch (InterruptedException e) {}
                try {
                	while(udpSock==null && isUdpAlive()) {
                		ThreadUtil.wait(100);
                	}
                	if(receivePacket !=null){
                		
						udpSock.receive(receivePacket);
						
						byte [] data = receivePacket.getData();
						byte [] tmp = new byte [receivePacket.getLength()];
						
						if(	receivePacket.getLength()>0){
							boolean packetOk=false;
							if(IPAddressSet!= null){
								for(int i=0;i<getAllIntetAddresses().size();i++){
									if(IPAddressSet.equals(getAllAddresses().get(i))){
										packetOk=true;
									}
								}
							}else{
								packetOk=true;
							}
							if(packetOk){
//								Log.warning("Got packet "+receivePacket.getAddress()+
//										"\nGot Port: "+receivePacket.getPort()+
//										"\nLH: "+InetAddress.getLocalHost()+
//										"\nlocal port: "+udpSock.getLocalPort()+
//										"\nInetAddress: "+udpSock.getInetAddress()+
//										"\ngetLocalSocketAddress(): "+udpSock.getLocalSocketAddress()+
//										"\nRemoteSocketAddress(): "+udpSock.getRemoteSocketAddress()+
//										"\nPort: "+udpSock.getPort()+
//										"\nLocalAddress: "+udpSock.getLocalAddress());
								for (int i=0;i<receivePacket.getLength();i++){
									tmp[i]=data[i];
								}
								
								add(tmp, receivePacket);
							}
						}else{
							if(receivePacket.getLength()<=0){
								Log.warning("Packet Filtered, from localhost "+receivePacket.getAddress());
							}
						}
						
                	}
				} catch (Exception e) {
					System.err.println("Fixing the socket");
					e.printStackTrace();
					if(udpSock != null){
						 udpSock.close();
					}
					try {
						setUDPSocket(port);
					} catch (SocketException e1) {
						System.err.println("FAILED to reconnect");
						e1.printStackTrace();
						setUdpAlive(false);
					}
				}

			}
			System.err.println("UDPins failed out here");
		}
		private void add(byte[] b, DatagramPacket receivePacket){
			//Use most recent address as output
			InetAddress IPAddress = receivePacket.getAddress();
			if(addrs.size() ==0){
				addrs.add(new UdpInterfaceData(receivePacket.getAddress(), receivePacket.getPort()));
				Log.warning("Adding IP address "+IPAddress.toString());
			}else{
				boolean adderInList=false;
				for(int i=0;i<addrs.size();i++){
					if(addrs.get(i).getAddr().toString().contains(IPAddress.toString())){
						adderInList=true;
						//Log.warning("Repeated IP address  "+IPAddress.toString());
					}
				}
				if(!adderInList){
					addrs.add(new UdpInterfaceData(receivePacket.getAddress(), receivePacket.getPort()));
					try {
						udpSock.joinGroup(receivePacket.getAddress());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.warning("Adding IP address "+IPAddress.toString());
				}
			}
			synchronized(inputData){
				inputData.add(b);
			}
		}
		public DataInputStream getStream(){
			return new DataInputStream(ins);
		}
	}
	
	
	/**
	 * Private class for output stream
	 * @author hephaestus
	 *
	 */
	private class UDPouts extends Thread{
		private ByteList outputData = new ByteList();
		//private InetAddress myAddr=null;
		private OutputStream outs = new OutputStream() {
			public void write(byte [] raw){
				synchronized(outputData){
					outputData.add(raw);
				}
			}
			public void flush(){
				while( outputData.size()>0);
			}
			@Override
			public void write(int arg) throws IOException {
				synchronized(outputData){
					outputData.add((byte)arg);
				}
			}
		};
		public void run(){
			while(isUdpAlive() ){
				ThreadUtil.wait(1);
				while(udpSock==null && isUdpAlive()){
            		ThreadUtil.wait(100);
            	}
				try {				
					if( outputData.size()>0){
						synchronized(outputData){
							sendData=outputData.popList(outputData.size());
						}
						if(sendData.length == 0 )
							return;
						
						lastSent=sendData;
						if(udpSock == null)
							throw new RuntimeException("Udp Socket is null!!");

						
						try {
							if(getAllAddresses().size()==0){
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, port);
								udpSock.send(sendPacket);
								Log.info("Sending message to broadcast");
							}else{
								for(int i=0;i< getAllAddresses().size();i++){
									DatagramPacket sendPacket = new DatagramPacket(	sendData, 
																					sendData.length, 
																					getAllAddresses().get(i).getAddr(),
																					getAllAddresses().get(i).getPort());
									udpSock.send(sendPacket);
								}
							}
							
						} catch (IOException e) {
							return;
						}	
					}
				} catch (Exception e) {}
			}
		}
		public DataOutputStream getStream(){
			return new DataOutputStream(outs);
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
		setUdpAlive(false);
		udpSock=null;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public ArrayList<UdpInterfaceData>  getAllAddresses(){
		return addrs;
	}

	public boolean isUdpAlive() {
		return isAlive;
	}

	public void setUdpAlive(boolean isAlive) {
		this.isAlive = isAlive;
		if(isAlive == false){
			try{
				throw new RuntimeException();
			}catch(Exception e){
				System.err.println("Stopping socket");
				e.printStackTrace();
			}
		}
	}
	private class UdpInterfaceData{
		private InetAddress addr;
		private int port2;

		public UdpInterfaceData(InetAddress addr, int port){
			this.setAddr(addr);
			setPort(port);
			
		}

		public InetAddress getAddr() {
			return addr;
		}

		public void setAddr(InetAddress addr) {
			this.addr = addr;
		}

		public int getPort() {
			return port2;
		}

		public void setPort(int port2) {
			this.port2 = port2;
		}
	}

	public ArrayList<InetAddress> getAllIntetAddresses() {
		ArrayList<InetAddress> back=new ArrayList<InetAddress>();
		for(int i=0;i<getAllAddresses().size();i++){
			 back.add(getAllAddresses().get(i).getAddr());
		}
		return back;
	}
	
}


