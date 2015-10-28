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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MissingNativeLibraryException;




// TODO: Auto-generated Javadoc
//import com.intel.bluetooth.test.SimpleClient.CancelThread;

/**
 * The Class BlueCoveManager.
 */
public class BlueCoveManager implements DiscoveryListener {
	
	/** The records. */
	ArrayList<ServiceRecord> records= new ArrayList<ServiceRecord>();
	
	/** The device list. */
	ArrayList<RemoteDevice> deviceList = new ArrayList<RemoteDevice>();
	
	/** The Constant uuid. */
	static final UUID uuid = com.intel.bluetooth.BluetoothConsts.RFCOMM_PROTOCOL_UUID;
	
	/** The selected. */
	private String selected = null;
	
	/** The ins. */
	private DataInputStream ins;
	
	/** The outs. */
	private DataOutputStream outs;
	
	/** The conn. */
	private StreamConnection conn;
	
	/** The search id. */
	private int searchId=0xffff;

	/**
	 * Instantiates a new blue cove manager.
	 */
	public BlueCoveManager(){
	}
	
	/**
	 * Find.
	 *
	 * @throws MissingNativeLibraryException the missing native library exception
	 */
	public synchronized void find() throws MissingNativeLibraryException {
		for (RemoteDevice d:deviceList) {
			if(d.getBluetoothAddress().equals(selected)){
				try {Thread.sleep(100);} catch (InterruptedException e) {}
				return;
			}
		}
		disconnect();
		Log.info("Finding Devices...");
		try {
			LocalDevice.getLocalDevice().getDiscoveryAgent().cancelInquiry(this);
			LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
			try {wait();} catch (InterruptedException e) {e.printStackTrace();}
			
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		}
		for (RemoteDevice d:deviceList) {
			try {
				Log.info("Device name: " + d.getFriendlyName(false)+" address: " + d.getBluetoothAddress());
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		if(deviceList.size()==0)
			Log.info("No Devices Found!");
		
	}
	
	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		deviceList.add(btDevice);
		//Log.info("deviceDiscovered " + btDevice.getBluetoothAddress() + " DeviceClass: " + ((Object)cod).toString());
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
	 */
	public synchronized void inquiryCompleted(int discType) {
		notifyAll();
	}
	
	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
	 */
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		//Log.info("###Record list added: "+servRecord.length);
		for ( int i=0; i< servRecord.length;i++)
			records.add(servRecord[i]);
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
	 */
	public synchronized void serviceSearchCompleted(int transID, int respCode) {
		notifyAll();
	}
	
	/**
	 * Gets the available serial devices.
	 *
	 * @param refresh the refresh
	 * @return the available serial devices
	 */
	public String [] getAvailableSerialDevices(boolean refresh) {
		if(refresh){
			find();
		}
		String [] s=new String[deviceList.size()];
		String tmp="";
		int i=0;
		for (RemoteDevice d: deviceList){
			try {
				tmp=d.getFriendlyName(false);
				if (tmp == "")
					tmp = "No Name";
				s[i]=tmp+"_"+d.getBluetoothAddress();
			} catch (Exception e) {
				s[i]="Failed Name"+"_"+d.getBluetoothAddress();
			}
			i++;
		}
		return s;
	}
	
	/**
	 * Gets the device.
	 *
	 * @param name the name
	 * @return the device
	 */
	public synchronized RemoteDevice getDevice(String name){
		String addr = name.substring(name.indexOf('_')+1);
		//System.out.println("Getting device with address: "+addr);
		String [] s=getAvailableSerialDevices(false);
		for (int i=0;i<s.length;i++){
			if(s[i].contains(addr)){
				try {
					RemoteDevice dev =  deviceList.get(i);
					//System.out.println("Found device: "+s[i]);
					return dev;
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else {
				//System.out.println("Non matching device: "+s[i]);
			}
		}
		return null;
	}
	
	/**
	 * Disconnect.
	 */
	public synchronized void disconnect() {
		Log.info("Disconnecting: "+selected);
		deviceList.clear();
		try {
			if(ins!= null)
				ins.close();
			if(outs!=null)
				outs.close();
			if (conn != null)
				conn.close();
			LocalDevice.getLocalDevice().getDiscoveryAgent().cancelInquiry(this);
			if(searchId != 0xffff)
				LocalDevice.getLocalDevice().getDiscoveryAgent().cancelServiceSearch(searchId);
		} catch (Exception e) {
			throw new MissingNativeLibraryException(e.getMessage());
		}
		Log.info("Disconnected");
		
	}
	/*
	public boolean reconnect(){
		find();
		if(!deviceList.contains(selected))
			return false;
		disconnect();
		return connect();
	}
	*/
	/**
	 * Connect.
	 *
	 * @param devAddress the dev address
	 */
	public synchronized void connect(String devAddress){
		RemoteDevice d = getDevice(devAddress);
		if(deviceList.contains(d))
			selected = d.getBluetoothAddress();
		else{
			find();
			if(!deviceList.contains(d))
				throw new RuntimeException("Divice no longer availiable");
		}
		connect();
	}
	
	/**
	 * Connect.
	 *
	 * @return true, if successful
	 */
	private synchronized boolean connect(){
		try{
			if(ins!= null)
				ins.close();
			if(outs!=null)
				outs.close();
			if(conn!=null)
				conn.close();
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		Log.info("Connecting : "+selected);
		synchronized (this) {
			records.clear();
			try {
				int[] attrSet = null;
				UUID[] id =new UUID[] { uuid };
				if(searchId != 0xffff)
					LocalDevice.getLocalDevice().getDiscoveryAgent().cancelServiceSearch(searchId);
				searchId=LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrSet,id, getDevice(selected), this);
				try {wait();} catch (InterruptedException e) {e.printStackTrace();}

			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
		}
		
		if (records.size()==0){
			System.err.println("No compatible records");
			return false;
		}
		//Log.info("Found SPP:RFCOMM services OK!");
		try {
			String url = records.get(0).getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
			try{
				StreamConnection c =(StreamConnection) Connector.open(url);
				conn = c;
			}catch (IOException ex){
				System.err.println("Failed to connect to "+url);
				if(!ex.getMessage().contains("Device or resource busy"))
					throw ex;
			}
			outs = conn.openDataOutputStream();
			ins = conn.openDataInputStream();
			outs.flush();
			ins.available();
			Log.info("Connection OK!");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("No connection");
			return false;
		}
	}
	
	/**
	 * Gets the data ins.
	 *
	 * @return the data ins
	 */
	public synchronized DataInputStream getDataIns() {
		if (selected == null || ins==null)
			throw new RuntimeException();
		return ins;
	}
	
	/**
	 * Gets the data outs.
	 *
	 * @return the data outs
	 */
	public synchronized DataOutputStream getDataOuts() {
		if (selected == null || outs==null )
			throw new RuntimeException();
		return outs;
	}
}
