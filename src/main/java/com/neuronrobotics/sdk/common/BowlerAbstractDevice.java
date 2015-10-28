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
/**
 *
 * Copyright 2009 Neuron Robotics, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.dyio.InfoFirmwareRevisionCommand;

// TODO: Auto-generated Javadoc
/**
 * AbstractDevices are used to model devices that are connected to the Bowler network. AbstractDevice
 * implementations should encapsulate command generation and provide higher-level actions to users.  
 * 
 * @author rbreznak
 *
 */
public abstract class BowlerAbstractDevice implements IBowlerDatagramListener {
	
	/** The keep alive. */
	private boolean keepAlive = true;
	
	/** The disconnecting. */
	private boolean disconnecting = false;
	
	/** The last packet time. */
	private long lastPacketTime=0;
	
	/** The connection. */
	private BowlerAbstractConnection connection=null;
	/** The address. */
	private MACAddress address = new MACAddress(MACAddress.BROADCAST);
	
	/** The disconnect listeners. */
	private static ArrayList<IDeviceConnectionEventListener> disconnectListeners = new ArrayList<IDeviceConnectionEventListener> ();
	
	/** The scripting name. */
	private String scriptingName = "device";
	
	
	/**
	 * Determines if the device is available.
	 *
	 * @return true if the device is avaiable, false if it is not
	 * @throws InvalidConnectionException the invalid connection exception
	 */
	public boolean isAvailable() throws InvalidConnectionException{
		return getConnection().isConnected()&&disconnecting==false;
	}

	
	/**
	 * Set the connection to use when communicating commands with a device.
	 */
	protected void fireDisconnectEvent() {
		for(IDeviceConnectionEventListener l:getDisconnectListeners()) {
			l.onDisconnect(getDevice());
		}
	}
	
	/**
	 * Fire connect event.
	 */
	protected void fireConnectEvent() {
		for(IDeviceConnectionEventListener l:getDisconnectListeners()) {
			l.onConnect(getDevice());
		}
	}
	
	/**
	 * Adds the connection event listener.
	 *
	 * @param l the l
	 */
	public void addConnectionEventListener(final IDeviceConnectionEventListener l ) {
		if(!getDisconnectListeners().contains(l)) {
			getDisconnectListeners().add(l);
		}
		if(connection !=null)
		connection.addConnectionEventListener(new IConnectionEventListener() {
			
			@Override
			public void onDisconnect(BowlerAbstractConnection source) {
				
				l.onDisconnect(getDevice());
			}
			
			@Override
			public void onConnect(BowlerAbstractConnection source) {
				// TODO Auto-generated method stub
				l.onConnect(getDevice());
			}
		});
	}
	
	/**
	 * Removes the connection event listener.
	 *
	 * @param l the l
	 */
	public void removeConnectionEventListener(IDeviceConnectionEventListener l ) {
		if(getDisconnectListeners().contains(l)) {
			getDisconnectListeners().remove(l);
		}

	}
	
	/**
	 * Gets the device.
	 *
	 * @return the device
	 */
	private BowlerAbstractDevice getDevice(){
		return this;
	}
	
	/**
	 * Sets the connection.
	 *
	 * @param connection the new connection
	 */
	public void setConnection(BowlerAbstractConnection connection) {
		setThreadedUpstreamPackets(false);
		if(connection == null) {
			throw new NullPointerException("Can not use a NULL connection.");
		}
		for(int i=0;i<getDisconnectListeners().size();i++) {
			final int index = i;
			connection.addConnectionEventListener(new IConnectionEventListener() {
				
				@Override
				public void onDisconnect(BowlerAbstractConnection source) {
					getDisconnectListeners().get(index).onDisconnect(getDevice());
				}
				
				@Override
				public void onConnect(BowlerAbstractConnection source) {
					// TODO Auto-generated method stub
					getDisconnectListeners().get(index).onConnect(getDevice());
				}
			});
		}
		this.connection = connection;
		connection.addDatagramListener(this);
	}
	
	/**
	 * This method tells the connection object to start and connects the up and down streams pipes. 
	 * Once this method is called and returns without exception, the device is ready to communicate with
	 *
	 * @return true, if successful
	 * @throws InvalidConnectionException the invalid connection exception
	 */
	public boolean connect() throws InvalidConnectionException {
		
		if (connection == null) {
			throw new InvalidConnectionException("Null Connection");
		}
		if(!connection.isConnected()) {
			if(!connection.connect()) {
				return false;
			}else {
				//startHeartBeat();
			}
		}
		
		return this.isAvailable();
	}
	
	/**
	 * This method tells the connection object to disconnect its pipes and close out the connection. Once this is called, it is safe to remove your device.
	 */
	public void disconnect() {
		disconnecting=true;
		if (connection != null) {
			if(connection.isConnected()){
				Log.info("Disconnecting Bowler Device");
				connection.disconnect();
			}
		}
	}
	
	/**
	 * Get the connection that is used for communicating with the device.
	 *
	 * @return the device's connection
	 */
	public BowlerAbstractConnection getConnection() {
		return connection;
	}
	
	/**
	 * Set the MAC Address of the device. Every device should have a unique MAC address.
	 *
	 * @param address the new address
	 */
	public void setAddress(MACAddress address) {
		if(!address.isValid()) {
			throw new InvalidMACAddressException();
		}
		this.address = address;
	}
	
	/**
	 * Get the device's mac address.
	 *
	 * @return  the device's address
	 */
	public MACAddress getAddress() {
		//System.out.println();
		return address;
	}
	
//	/**
//	 * Send a sendable to the connection.
//	 *
//	 * @param sendable the sendable
//	 * @return the syncronous response
//	 */
//	public BowlerDatagram send(ISendable sendable) {
//		setLastPacketTime(System.currentTimeMillis());
//		return connection.send(sendable, getAddress());
//	}
	

	/**
	 * Send a command to the connection.
	 *
	 * @param command the command
	 * @return the syncronous response
	 * @throws NoConnectionAvailableException the no connection available exception
	 * @throws InvalidResponseException the invalid response exception
	 */
	public BowlerDatagram send(BowlerAbstractCommand command) throws NoConnectionAvailableException, InvalidResponseException {	
		return send(command,1);
	}
	
	/**
	 * Send a command to the connection.
	 *
	 * @param command the command
	 * @param retry the retry
	 * @return the syncronous response
	 * @throws NoConnectionAvailableException the no connection available exception
	 * @throws InvalidResponseException the invalid response exception
	 */
	public BowlerDatagram send(BowlerAbstractCommand command, int retry) throws NoConnectionAvailableException, InvalidResponseException {
		
		return connection.send(command,getAddress(), retry);
	}
	
	/**
	 * THis is the scripting interface to Bowler devices. THis allows a user to describe a namespace, rpc, and array or 
	 * arguments to be paced into the packet based on the data types of the argument. The response in likewise unpacked 
	 * into an array of objects.
	 *
	 * @param namespace The string of the desired namespace
	 * @param method the method
	 * @param rpcString The string of the desired RPC
	 * @param arguments An array of objects corresponding to the data to be stuffed into the packet.
	 * @param retry the retry
	 * @return The return arguments parsed and packet into an array of arguments
	 * @throws DeviceConnectionException If the desired RPC's are not available then this will be thrown
	 */
	public Object [] send(String namespace,BowlerMethod method, String rpcString, Object[] arguments, int retry) throws DeviceConnectionException{
		return connection.send(getAddress(),namespace, method, rpcString, arguments, retry);
	}
	
	/**
	 * THis is the scripting interface to Bowler devices. THis allows a user to describe a namespace, rpc, and array or 
	 * arguments to be paced into the packet based on the data types of the argument. The response in likewise unpacked 
	 * into an array of objects.
	 *
	 * @param namespace The string of the desired namespace
	 * @param method the method
	 * @param rpcString The string of the desired RPC
	 * @param arguments An array of objects corresponding to the data to be stuffed into the packet.
	 * @return The return arguments parsed and packet into an array of arguments
	 * @throws DeviceConnectionException If the desired RPC's are not available then this will be thrown
	 */
	public Object [] send(String namespace,BowlerMethod method, String rpcString, Object[] arguments) throws DeviceConnectionException{
		return send(namespace, method, rpcString, arguments, 5);
	}
		
	/**
	 * Implementation of the Bowler ping ("_png") command
	 * Sends a ping to the device returns the device's MAC address.
	 *
	 * @return the device's address
	 */
	public boolean ping() {
		return ping(false);
	}
	
	/**
	 * Implementation of the Bowler ping ("_png") command
	 * Sends a ping to the device returns the device's MAC address.
	 *
	 * @param switchParser the switch parser
	 * @return the device's address
	 */
	public boolean ping(boolean switchParser) {
		return connection.ping(getAddress(),switchParser);
	}
	/**
	 * Gets the revisions.
	 *
	 * @return the revisions
	 */
	public ArrayList<ByteList> getRevisions(){
		ArrayList<ByteList> list = new ArrayList<ByteList>();
		try {
			BowlerDatagram b = send(new InfoFirmwareRevisionCommand(),5);
			Log.debug("FW info:\n"+b);
			for(int i=0;i<(b.getData().size()/3);i++){
				list.add(new ByteList(b.getData().getBytes((i*3),3)));
			}
		} catch (InvalidResponseException e) {
			Log.error("Invalid response from Firmware rev request");
			
		} catch (NoConnectionAvailableException e) {
			Log.error("No connection is available.");
		}
		return list;
	}
	
	/**
	 * Get all the namespaces.
	 *
	 * @return the namespaces
	 */
	public ArrayList<String>  getNamespaces(){
		return connection.getNamespaces(getAddress());	
	}
	
	/**
	 * Check the device to see if it has the requested namespace.
	 *
	 * @param string the string
	 * @return true, if successful
	 */
	public boolean hasNamespace(String string) {
		return connection.hasNamespace(string,getAddress());
	}
	
	/**
	 * Start heart beat.
	 */
	public void startHeartBeat(){
		getConnection().startHeartBeat();
	}
	
	/**
	 * Start heart beat.
	 *
	 * @param msHeartBeatTime the ms heart beat time
	 */
	public void startHeartBeat(long msHeartBeatTime){
		getConnection().startHeartBeat(msHeartBeatTime);
	}
	
	/**
	 * Stop heart beat.
	 */
	public void stopHeartBeat(){
		getConnection().stopHeartBeat();
	}

	
	/**
	 * Tells the connection to use asynchronous packets as threads or not. 
	 *
	 * @param up the new threaded upstream packets
	 */
	public void setThreadedUpstreamPackets(boolean up){
		if(connection != null){
			connection.setThreadedUpstreamPackets(up);
		}
	}
	
	/**
	 * Requests all of the RPC's from a namespace.
	 *
	 * @param namespace the namespace
	 * @return the rpc list
	 */
	public ArrayList<RpcEncapsulation> getRpcList(String namespace) {
		return connection.getRpcList(namespace,getAddress());
	}
	
	/**
	 * Loads all the Requests for the RPC's from all namespaces.
	 */
	public void loadRpcList() {
		 ArrayList<String> names = getNamespaces();
		 
		 for (String s:names){
			 System.out.println(getRpcList(s));
		 }
		 
	}
	/**
	 * On all response.
	 *
	 * @param data the data
	 */
	@Deprecated
	public void onAllResponse(BowlerDatagram data){
		// this is here to prevent the breaking of an interface, 
	}
	
	/**
	 * Checks if is keep alive.
	 *
	 * @return true, if is keep alive
	 */
	public boolean isKeepAlive() {
		return keepAlive;
	}
	
	/**
	 * Sets the keep alive.
	 *
	 * @param keepAlive the new keep alive
	 */
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}


	/**
	 * Gets the last packet time.
	 *
	 * @return the last packet time
	 */
	public long getLastPacketTime() {
		return lastPacketTime;
	}


	/**
	 * Sets the last packet time.
	 *
	 * @param lastPacketTime the new last packet time
	 */
	public void setLastPacketTime(long lastPacketTime) {
		this.lastPacketTime = lastPacketTime;
	}


	/**
	 * Gets the scripting name.
	 *
	 * @return the scripting name
	 */
	public String getScriptingName() {
		return scriptingName;
	}


	/**
	 * Sets the scripting name.
	 *
	 * @param scriptingName the new scripting name
	 */
	public void setScriptingName(String scriptingName) {
		this.scriptingName = scriptingName;
	}


	/**
	 * Gets the disconnect listeners.
	 *
	 * @return the disconnect listeners
	 */
	public static ArrayList<IDeviceConnectionEventListener> getDisconnectListeners() {
		return disconnectListeners;
	}


	/**
	 * Sets the disconnect listeners.
	 *
	 * @param disconnectListeners the new disconnect listeners
	 */
	public static void setDisconnectListeners(ArrayList<IDeviceConnectionEventListener> disconnectListeners) {
		BowlerAbstractDevice.disconnectListeners = disconnectListeners;
	}
	
}
