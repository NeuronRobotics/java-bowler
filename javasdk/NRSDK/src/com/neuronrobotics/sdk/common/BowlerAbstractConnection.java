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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.config.SDKBuildInfo;
import com.neuronrobotics.sdk.util.ThreadUtil;




/**
 * Connections create a bridge between a device and the SDK. Each connection is encapsulated to allow maximum
 * reuse and system changes without the need to restart / reconfigure.
 *
 */
public abstract class BowlerAbstractConnection {
	
	//private boolean threadedUpstreamPackets=false;
	
	/** The sleep time. */
	private int sleepTime = 10;
	
	private int chunkSize = 64;
	
	/** The poll timeout time. */
	private int pollTimeoutTime = 1;
	
	/** The response. */
	private BowlerDatagram response = null;
	
	/** The listeners. */
	private ArrayList<IBowlerDatagramListener> listeners = new ArrayList<IBowlerDatagramListener>();
	ArrayList<IConnectionEventListener> disconnectListeners = new ArrayList<IConnectionEventListener> ();
	private ISynchronousDatagramListener syncListen = null;
	/** The queue. */
	private QueueManager syncQueue = null;
	//private QueueManager asyncQueue = null;
	
	/** The connected. */
	private boolean connected = false;
	
	/** The data ins. */
	private DataInputStream dataIns;
	
	/** The data outs. */
	private DataOutputStream dataOuts;
	
	private Updater updater = null;
	
	private ThreadedTimeout timeout=new ThreadedTimeout(getSleepTime());
	
	
	/**
	 * Attempt to establish a connection. Return if the attempt was successful.
	 *
	 * @return true, if successful
	 */
	abstract public boolean connect();
	
	/**
	 * Attempt to re-establish a connection. Return if the attempt was successful.
	 *
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	abstract public boolean reconnect() throws IOException;
	
	/**
	 * Attempt to re-establish a connection. Return if the attempt was successful.
	 *
	 * @return true, if successful
	 */
	abstract public boolean waitingForConnection();
	
	/**
	 * Tells the connection to use asynchronous packets as threads or not. 
	 * @param up
	 */
	public void setThreadedUpstreamPackets(boolean up){
		//threadedUpstreamPackets=up;
	}
	public boolean isThreadedUpstreamPackets() {
		//if (SDKBuildInfo.isLinux() && SDKBuildInfo.isARM())
			return false;
		//return threadedUpstreamPackets;
	}
	
	/**
	 * Sends any "universal" data to the connection and returns either the syncronous response or null in the
	 * event that the connection has determined a timeout. Before sending, use clearLastSyncronousResponse()
	 * and use getLastSyncronousResponse() to get the last response since clearing.
	 *
	 * @param sendable the sendable
	 * @return the bowler datagram
	 */
	public synchronized BowlerDatagram send(ISendable sendable){
		if(!isConnected()) {
			Log.error("Can not send message because the engine is not connected.");
			return null;
		}
		clearLastSyncronousResponse();
		long start = System.currentTimeMillis();
		if((!getSyncQueue().isEmpty() ||!getAsyncQueue().isEmpty())){
			//Log.debug("Waiting for byte and packet buffers to clear...");
			//Log.info("Synchronus queue size: " + getSyncQueue().size());
			//Log.info("Asynchronus queue size: " + getAsyncQueue().size());
			//Log.info("Byte Buffer: " + builder.size());
		}
		while ((!getSyncQueue().isEmpty() ||!getAsyncQueue().isEmpty())) {
			ThreadUtil.wait(1);
		}
		long diff = System.currentTimeMillis()-start;
		if(diff>2){
			//Log.debug("Buffers cleared in : "+diff+"ms");
		}
		try {
			//long send = System.currentTimeMillis();
			write(sendable.getBytes());
			//Log.info("Transmit took: "+(System.currentTimeMillis()-send)+" ms");
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		long rcv = System.currentTimeMillis();
		timeout.initialize(getSleepTime());
		while ((!timeout.isTimedOut())  && (getLastSyncronousResponse() == null)){
			ThreadUtil.wait(getPollTimeoutTime());
		}
		Log.info("Receive took: "+(System.currentTimeMillis()-rcv)+" ms");
		BowlerDatagram b =getLastSyncronousResponse();
		if (b== null){
			try {
				new RuntimeException().printStackTrace();
				Log.error("No response from device...");
				reconnect();
			} catch (IOException e) {
				clearLastSyncronousResponse();
				throw new RuntimeException(e);
			}
		}
		clearLastSyncronousResponse();
		return b;
	}
	
	/**
	 * Sends any "universal" data to the connection and returns either the syncronous response or null in the
	 * event that the connection has determined a timeout. Before sending, use clearLastSyncronousResponse()
	 * and use getLastSyncronousResponse() to get the last response since clearing.
	 *
	 * @param sendable the sendable
	 */
	public void sendAsync(BowlerDatagram sendable){
		if(!isConnected()) {
			//Log.error("Can not send message because the engine is not connected.");
			return;
		}
		try {
			write(sendable.getBytes());
		} catch (IOException e1) {
			Log.error("No response from device...");
			try {
				reconnect();
				write(sendable.getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e1);
			}
		}
	}
	
	/**
	 * Disconnect and deactive the current connection.
	 */
	public void disconnect(){
		if(!isConnected()) {
			return;
		}
		Log.info("Disconnecting Bowler Connection");
		setConnected(false);

	}

	/**
	 * Sets the poll timeout time.
	 *
	 * @param pollTimeoutTime the new poll timeout time
	 */
	private void setPollTimeoutTime(int pollTimeoutTime) {
		this.pollTimeoutTime = pollTimeoutTime;
	}

	/**
	 * Gets the poll timeout time.
	 *
	 * @return the poll timeout time
	 */
	private int getPollTimeoutTime() {
		return pollTimeoutTime;
	}

	/**
	 * Sets the sleep time.
	 *
	 * @param sleepTime the new sleep time
	 */
	public void setSynchronusPacketTimeoutTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	
	/**
	 * Sets the sleep time.
	 *
	 * @param sleepTime the new sleep time
	 */
	@Deprecated 
	public void setSleepTime(int sleepTime) {
		setSynchronusPacketTimeoutTime(sleepTime);
	}
	
	/**
	 * Gets the sleep time.
	 *
	 * @return the sleep time
	 */
	public int getSleepTime() {
		return sleepTime;
	}
	private long lastWrite = 0;
	public long msSinceLastSend() {
		return System.currentTimeMillis() - lastWrite ;
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
		lastWrite = System.currentTimeMillis();
		if(dataOuts != null){
			try{
				//Log.info("Writing: "+data.length+" bytes");
				
				//while(outgoing.size()>0){
					//byte[] b =outgoing.popList(getChunkSize());
					getDataOuts().write(data);
					getDataOuts().flush();
				//}
			}catch (IOException e){
				Log.error("Write failed. "+e.getMessage());
				reconnect();
			}
		}else{
			Log.warning("No data sent, stream closed");
		}
		
	}
	
	/**
	 * Sets the connected.
	 *
	 * @param connected the new connected
	 */
	public void setConnected(boolean c) {
		if(connected == c)
			return;
		connected = c;
		if(connected){
			updater = new Updater();
			updater.start();
			setSyncQueue(new QueueManager());
			getSyncQueue().start();
			fireConnectEvent();
			if (SDKBuildInfo.isLinux() && SDKBuildInfo.isARM())
				Log.info("Is arm, no packet threads");
		}else{
			try {
				getDataIns().close();
				setDataIns(null);
			} catch (Exception e) {
				//return;
			}
			try {
				getDataOuts().close();
				setDataOuts(null);
			} catch (Exception e) {
				//return;
			}
			stopQueue();
			fireDisconnectEvent();
		}
	}
	
	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * This should be done before sending. 
	 */
	public void clearLastSyncronousResponse() {
		response = null;
	}
	
	/**
	 * Return the syncronous response buffer.
	 *
	 * @return the last syncronous response
	 */
	public BowlerDatagram getLastSyncronousResponse() {
		return response;
	}
	
	/**
	 * Whenever a connection has received a full datagram from its "physical" connection, it should then call
	 * <code>onDataReceived</code> with the datagram. This will set it to the last received data if it is
	 * synchronous and will add it to the appropriate queues to be picked up and send to the listeners.
	 *
	 * @param data the data
	 */
	protected void onDataReceived(BowlerDatagram data) {
		if(data.isSyncronous()) {
			response = data;
		}
		if(isThreadedUpstreamPackets()){
			if(data.isSyncronous()) {
				getSyncQueue().addDatagram(data);
			}else {
				getAsyncQueue().addDatagram(data);
			}
		}else{
			pushUp(data);
		}
	}
	
	/**
	 * Fire On Response.
	 *
	 * @param datagram the datagram
	 */

	protected BowlerDatagram  fireSyncOnReceive(BowlerDatagram datagram) {
		if(datagram.isSyncronous()){
			if (syncListen!=null){
				return syncListen.onSyncReceive(datagram);
			}
		}
		return null;
	}
	
	protected void fireAsyncOnResponse(BowlerDatagram datagram) {
		if(!datagram.isSyncronous()){
			//Log.debug("\nASYNC<<"+datagram);
			if(isThreadedUpstreamPackets()){
				//synchronized(listeners){
					for(IBowlerDatagramListener l : listeners) {
						new AsyncSender(l,datagram).start();
					}
				//}
			}else{
				for(IBowlerDatagramListener l : listeners) {
					l.onAsyncResponse(datagram);
				}
			
			}
		}
		
	}
//	private class SyncSender extends Thread{
//		IBowlerDatagramListener l;
//		BowlerDatagram datagram;
//		public SyncSender(IBowlerDatagramListener l,BowlerDatagram datagram){
//			 this.l=l;
//			 this.datagram=datagram;
//		}
//		public void run(){
//			l.onAllResponse(datagram);
//		}
//	}
	private class AsyncSender extends Thread{
		IBowlerDatagramListener l;
		BowlerDatagram datagram;
		public AsyncSender(IBowlerDatagramListener l,BowlerDatagram datagram){
			 this.l=l;
			 this.datagram=datagram;
		}
		public void run(){
			l.onAsyncResponse(datagram);
		}
	}

	/**
	 * Add a listener that will be notified whenever an asyncronous packet arrives.
	 *
	 * @param listener the listener
	 */
	public void addDatagramListener(IBowlerDatagramListener listener) {
		if(listeners.contains(listener)) {
			return;
		}
		//synchronized(listeners){
			listeners.add(listener);
		//}
	}
	
	/**
	 * Remove a listener from new packet notifications.
	 *
	 * @param listener the listener
	 */
	public void removeDatagramListener(IBowlerDatagramListener listener) {
		if(!listeners.contains(listener)) {
			return;
		}
		
		listeners.remove(listener);
	}
	
	/**
	 * Kills the Queue.
	 */
	protected void stopQueue() {
		if(getSyncQueue() != null) {
			getSyncQueue().kill();
			setSyncQueue(null);
		}
		if(getAsyncQueue() != null) {
			getAsyncQueue().kill();
		}
	}
	
	/**
	 * Start builder.
	 */

	
	/**
	 * Sets the data ins.
	 *
	 * @param dataIns the new data ins
	 */
	public void setDataIns(DataInputStream dataIns) {
		this.dataIns = dataIns;
	}

	/**
	 * Gets the data ins.
	 *
	 * @return the data ins
	 */
	public DataInputStream getDataIns() throws NullPointerException{
		if(dataIns==null)
			throw new NullPointerException();
		return dataIns;
	}

	/**
	 * Sets the data outs.
	 *
	 * @param dataOuts the new data outs
	 */
	public void setDataOuts(DataOutputStream dataOuts) {
		
		this.dataOuts = dataOuts;
	}

	/**
	 * Gets the data outs.
	 *
	 * @return the data outs
	 */
	public DataOutputStream getDataOuts() throws NullPointerException{
		if(dataOuts==null)
			throw new NullPointerException();
		return dataOuts;
	}
	
	/**
	 * Wait for connectio to be ready.
	 */
	private void waitForConnectioToBeReady(){
		if(!waitingForConnection()) {
			return;
		}
		
		Log.info("Waiting for connection...");
		long start = System.currentTimeMillis() ;
		while(true){
			if(System.currentTimeMillis()> (start + 20000)){
				break;
			}
			
			if(!waitingForConnection()) {
				break;
			}
			
			ThreadUtil.wait(10);
		}
		Log.info("Connection ready");
	}
	
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getChunkSize() {
		return chunkSize;
	}
	
	public void setSyncQueue(QueueManager syncQueue) {
		this.syncQueue = syncQueue;
	}
	public QueueManager getAsyncQueue() {
		return syncQueue;
	}
	public QueueManager getSyncQueue() {
		return syncQueue;
	}
	
	private class Updater extends Thread{
		private ByteList buffer = new ByteList();
		private BowlerDatagram pool [] = new   BowlerDatagram [100];
		private int readPointer = 0;
		public void run() {
			for(int i=0;i<pool.length;i++){
				pool[i]=new BowlerDatagram();
			}
			//wait for the data stream to stabilize
			while(dataIns == null){
				ThreadUtil.wait(100);
			}
			while(isConnected()) {
				try {
					if(dataIns!=null){
						if(getDataIns().available()>0){
							//updateBuffer();
							buffer.add(getDataIns().read());
							BowlerDatagram bd = BowlerDatagramFactory.build(buffer,pool[readPointer]);
							if (bd!=null) {
								readPointer++;
								if(readPointer == pool.length){
									readPointer=0;
									for(int i=0;i<pool.length;i++){
										pool[i]=new BowlerDatagram();
									}
								}
								//Log.info("Got :\n"+bd);
								onDataReceived(bd);
								buffer.clear();
							}
							//Log.info("buffer: "+buffer);
						}else{
							// prevents the thread from locking
							ThreadUtil.wait(1);
						}
					}else{
						// prevents the thread from locking
						ThreadUtil.wait(10);
					}
				} catch (Exception e) {
					Log.error("Data read failed "+e.getMessage());
					try {
						reconnect();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Thread safe queue manager.
	 * @author rbreznak
	 *
	 */
	private class QueueManager extends Thread {
		// stack extends vector and gives thread safety
		/** The queue buffer. */
		private ArrayList<BowlerDatagram> queueBuffer = new ArrayList<BowlerDatagram>();
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while(isConnected()) {
				ThreadUtil.wait(0,1);
				
				while(!queueBuffer.isEmpty()) {
					//Log.info("Poping latest packet and sending to listeners");
					// pop is thread safe.
					
					synchronized(queueBuffer){
						int len = queueBuffer.size();
						for(int i=0;i<len;i++){
							try{
								if(queueBuffer.get(i).isSyncronous()){
									pushUp(queueBuffer.remove(i));
								}
							}catch(Exception e){}
							
						}
						if(!queueBuffer.isEmpty()){
							try{
								//send(queueBuffer.remove(queueBuffer.size()-1)	);
								pushUp(queueBuffer.remove(0)	);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							ThreadUtil.wait(1);
						}
						int index = 0;
						int max = 500;
						while(queueBuffer.size()>max){
							if(!queueBuffer.get(index).isSyncronous() && queueBuffer.get(index).getMethod() != BowlerMethod.CRITICAL){
								Log.enableDebugPrint(true);
								Log.error("Removing packet from overflow: "+queueBuffer.remove(index));
							}else{
								index++;
							}
							if(index >= max){
								break;
							}
						}
					}
				}
			}
		}
		

		
		
		/**
		 * check the buffer state
		 */
		public boolean isEmpty(){
			return queueBuffer.isEmpty();	
		}
		
		/**
		 * Adds the datagram.
		 *
		 * @param dg the dg
		 */
		private void addDatagram(BowlerDatagram dg) {
			synchronized(queueBuffer){
				queueBuffer.add(dg);
			}
		}
		
		/**
		 * Kill.
		 */
		public void kill() {
			if(isConnected())
				disconnect();
		}
	}
	
	private void pushUp(BowlerDatagram b){
		if(b.isSyncronous()){
			BowlerDatagram ret = fireSyncOnReceive(b);
			if(ret !=null){
				// Sending response to server
				sendAsync(ret);
			}
		}else
			fireAsyncOnResponse(b);

	}
	
	
	
	public void addConnectionEventListener(IConnectionEventListener l ) {
		if(!disconnectListeners.contains(l)) {
			disconnectListeners.add(l);
		}
	}
	public void removeConnectionEventListener(IConnectionEventListener l ) {
		if(disconnectListeners.contains(l)) {
			disconnectListeners.remove(l);
		}
	} 
	private void fireDisconnectEvent() {
		for(IConnectionEventListener l:disconnectListeners) {
			l.onDisconnect(this);
		}
	}
	private void fireConnectEvent() {
		for(IConnectionEventListener l:disconnectListeners) {
			l.onConnect(this);
		}
	}
	
	public void setSynchronousDatagramListener(ISynchronousDatagramListener l ) {
		if (syncListen == null){
			syncListen = l;
		}else{
			if(syncListen == l)
				return;
			throw new RuntimeException("There is already a listener "+syncListen);
		}
	}
	public void removeSynchronousDatagramListener(ISynchronousDatagramListener l ) {
		if(syncListen!= null){
			if(syncListen!= l){
				throw new RuntimeException("There is a different listener "+syncListen);
			}
		}
		syncListen=null;
	} 



}
