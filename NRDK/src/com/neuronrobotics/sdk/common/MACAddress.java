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
package com.neuronrobotics.sdk.common;
// TODO: Auto-generated Javadoc
/**
 * A mac address object. This object represents a MAC Address.
 * @author rbreznak
 *
 */
public class MACAddress implements ISendable {
	
	/** The Constant BROADCAST. */
	public static final String BROADCAST = "00:00:00:00:00:00";
	
	/** The address. */
	private byte [] address = new byte[6];
	
	/**
	 * Instantiates a new mAC address.
	 */
	public MACAddress(){
		init(BROADCAST);
	}
	/**
	 * Construct a new MAC address object with a given MAC address represented as a string of 6 bytes in hex deliminated by semicolons.
	 * @param address The string representation.
	 */
	public MACAddress(String address) {
		init(address);
	}
	
	/**
	 * Inits the.
	 *
	 * @param address the address
	 */
	private void init(String address){
		address = address.toUpperCase().trim();
		address = address.replace("-", ":");
		
		if (address.matches("^([0-9A-Z]{2}:){5}[0-9A-Z]{2}$")) {
			String[] strs = address.split(":");
			for(int i=0; i<6; i++) {
				this.address[i] = Integer.decode("0x" + strs[i]).byteValue();
			}
		}
	}
	
	/**
	 * Create a mac address from an array of bytes.
	 * @param address The byte array.
	 */
	public MACAddress(byte[] address) {
		for(int i=0; i<6; i++) {
			this.address[i] = address[i];
		}
	}
	
	/**
	 * Compare two mac addresses.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean equals(Object o) {
		if(!(o instanceof MACAddress)) { return false; }
		
		MACAddress addr = (MACAddress) o;
		
		for(int i=0; i<6; i++) {
			if(addr.address[i] != address[i]) {
				return false;
			}
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String rtn = "";
		for(byte x : address){
			rtn += String.format("%02x:", x);
		}
		rtn = rtn.substring(0, rtn.length()-1);
		return rtn.toUpperCase();
	}
	
	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	 
	public byte[] getBytes() {
		return address;
	}
	
	public void increment(){
		if(address[5]<255) {
			address[5]++;
			return;
		}else {
			if(address[4]<255) {
				address[5]=0;
				address[4]++;
			}else {
				if(address[3]<255) {
					address[5]=0;
					address[4]=0;
					address[3]++;
				}else {
					throw new RuntimeException("MAC Address can not be incremented!");
				}
			}
		}
	}
}
