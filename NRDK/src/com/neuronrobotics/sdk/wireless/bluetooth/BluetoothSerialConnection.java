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
package com.neuronrobotics.sdk.wireless.bluetooth;

import javax.bluetooth.RemoteDevice;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MissingNativeLibraryException;

/**
 * 
 */
public class BluetoothSerialConnection extends BowlerAbstractConnection{
		private int sleepTime = 800;
		private int pollTimeoutTime = 5;
		
		
		private String bluetoothAddress=null;
		private final int baud = 115200;
		
		private BlueCoveManager blue = null;
		private boolean connecting=false;
		private boolean reconOk = false;
		
		/**
		 * Default Constructor.
		 * <p>
		 * Using this constructor will require that at least the port be set
		 * later on.
		 * 
		 * The baudrate will default to 115200bps.
		 * 
		 * @param blue
		 * @param deviceAddress
		 */
		public BluetoothSerialConnection(BlueCoveManager blue,String deviceAddress) {
			this.blue = blue;
			this.bluetoothAddress=deviceAddress;
			RemoteDevice device=blue.getDevice(bluetoothAddress);
			if(device== null){
				throw new RuntimeException();
			}else{
				try {
					String d = device.getBluetoothAddress();
					Log.info("Device selected: "+d);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			init();
		}
		
		private void init(){
			setPollTimeoutTime(pollTimeoutTime);
			setSleepTime(sleepTime);
			//
		}
		
		/**
		 * 
		 * 
		 * @return
		 */
		public String getPort() {
			return bluetoothAddress;
		}

		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
		 */
		@Override
		public synchronized boolean connect() {
			if(isConnected()) {
				Log.error(bluetoothAddress + " is already connected.");
				return true;
			}
			try 
			{	
				setConnected(true);
				connecting=true;
				blue.connect(bluetoothAddress);
				setDataIns(blue.getDataIns());
				setDataOuts(blue.getDataOuts());
				connecting=false;
			}catch(UnsatisfiedLinkError e){
				setConnected(false);
				throw new MissingNativeLibraryException(e.getMessage());
	        }catch (Exception e) {
	        	setConnected(false);
	        	System.err.println("Failed to connect on port:"+bluetoothAddress+" exception: ");
	        	e.printStackTrace();
				return false;
			}
	        
			return isConnected();	
		}
		
		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
		 */
		@Override
		public synchronized void disconnect() {
			
			try{
				super.disconnect();
				if(getBlueManager() != null){
					try{getBlueManager().disconnect();}catch(Exception e){}
				}
			}catch( UnsatisfiedLinkError e){
				throw new MissingNativeLibraryException(e.getMessage());
	        }
			setConnected(false);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return bluetoothAddress;
		}


		/**
		 * 
		 * 
		 * @return
		 */
		public int getBaud() {
			return this.baud;
		}

		/**
		 * 
		 * 
		 * @return
		 */
		public BlueCoveManager getBlueManager() {
			return blue;
		}

		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
		 */
		@Override
		public  boolean reconnect() {
			//return true;
			reconOk = false;
			return recon();
		}
		
		private synchronized boolean recon() {
			if(!isConnected()||reconOk==true)
				return reconOk;
			Log.info("Attempting to re-connect");
			blue.find();
			setConnected(false);
			connect();
			//This prevents multible resyncs from building up and running
			reconOk = true;
			return isConnected();
		}

		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
		 */
		@Override
		public boolean waitingForConnection() {
			return connecting;
		}
		
	

}
