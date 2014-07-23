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
import java.net.SocketException;
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
public class BowlerUDPClient extends BowlerAbstractConnection{
	private int sleepTime = 5000;
	
	
	//private UDPStream udp = null;

	private int port = 1865;
	
	private InetAddress IPAddressSet=null;
	private  ArrayList<InetAddress>  addrs = new ArrayList<InetAddress>();
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
		waitForConnectioToBeReady();
		setLastWrite(System.currentTimeMillis());
//		if(dataOuts != null){
//			try{
//				//Log.info("Writing: "+data.length+" bytes");
//				
//				//while(outgoing.size()>0){
//					//byte[] b =outgoing.popList(getChunkSize());
//				//System.out.println("Writing "+new ByteList(data));
//				getDataOuts().write(data);
//				getDataOuts().flush();
//				//}
//			}catch (Exception e){
//				//e.printStackTrace();
//				Log.error("Write failed. "+e.getMessage());
//				reconnect();
//			}
//		}else{
//			Log.error("No data sent, stream closed");
//		}
		
	}
	
	public boolean loadPacketFromPhy(ByteList bytesToPacketBuffer) throws NullPointerException, IOException{
			while(getDataIns().available()>0){
				//we want to run this until the buffer is clear or a packet is found
				int b = getDataIns().read();
				bytesToPacketBuffer.add(b);
				BowlerDatagram bd = BowlerDatagramFactory.build(bytesToPacketBuffer);
				if (bd!=null) {
					Log.info("\nR<<"+bd);
					onDataReceived(bd);

					return true;
				}
				//Log.info("buffer: "+buffer);
			}
	
		return false;
	}
	
	
	/**
	 * 
	 * 
	 * @param set
	 */
	public void setAddress(InetAddress set){
		IPAddressSet=set;
    }
	
	private void init(){
		setSynchronusPacketTimeoutTime(sleepTime);
		setChunkSize(5210);
		if(connect()){
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
		}else{
			Log.error("Connection failed");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	public void disconnect(){
//		if (udp!=null)
//			udp.disconnect();
//		udp=null;
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
//			udp = new UDPStream(port,false);
//			udp.start();
//			setDataIns(udp.getDataInputStream());
//			setDataOuts(udp.getDataOutptStream());
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
