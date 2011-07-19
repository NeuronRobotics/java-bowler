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
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;

/**
 * 
 */
public class BowlerUDPClient extends BowlerAbstractConnection{
	private int sleepTime = 5000;
	private int pollTimeoutTime = 5;
	
	
	private UDPStream udp = null;

	private int port = 1865;
	
	
	/**
	 * 
	 */
	public BowlerUDPClient(){
		init();
	}
	public BowlerUDPClient(InetAddress set){
		init();
		setAddress(set);
	}
	public BowlerUDPClient(InetAddress set,int port){
		this.port=port;
		init();
		setAddress(set);
	}
	
	/**
	 * 
	 * 
	 * @param port
	 */
	public BowlerUDPClient(int port){
		this.port=port;
		init();
	}
	
	/**
	 * 
	 * 
	 * @param set
	 */
	public void setAddress(InetAddress set){
    	udp.setAddress(set);
    }
	
	private void init(){
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
		setChunkSize(5210);
		if(connect()){
			sendAsync(BowlerDatagramFactory.build(new MACAddress(), new PingCommand(),0));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	public void disconnect(){
		if (udp!=null)
			udp.disconnect();
		udp=null;
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
			udp = new UDPStream(port,false);
			udp.start();
			setDataIns(udp.getDataInputStream());
			setDataOuts(udp.getDataOutptStream());
			setConnected(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setConnected(false);
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
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * 
	 * 
	 * @return
	 */
	public ArrayList<InetAddress>  getAllAddresses(){
		if (udp!= null)
			return udp.getAllAddresses();
		return new ArrayList<InetAddress>();
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
