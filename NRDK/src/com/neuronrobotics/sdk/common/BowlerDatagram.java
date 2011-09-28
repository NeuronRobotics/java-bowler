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
/**
 * Formats data into a Bowler packet. 
 * 
 * @author rbreznak
 *
 */
public class BowlerDatagram implements ISendable {
	
	/** The Constant REVISION. */
	public static final byte REVISION = 3;
	
	/** The Constant HEADER_SIZE. */
	public static final int HEADER_SIZE = 11;
	
	/** The Constant CRC_INDEX. */
	public static final int CRC_INDEX = 10;
	
	/** The address. */
	private MACAddress address;
	
	/** The method. */
	private BowlerMethod method;
	
	/** The transaction id. */
	private byte transactionID;
	
	/** The upstream. */
	private boolean upstream;
	
	/** The crc. */
	private byte crc;
	
	/** The data. */
	private ByteList data = new ByteList();
	
	/** time of instantiation **/
	
	private long timestamp;
	
	/**
	 * Default constructor.
	 */
	public BowlerDatagram() {
		// left empty to allow default construction
		timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Constructs a BowlerDatagram that will parse the given data into a packet.
	 * Data must be at least the size of a standard Bowler packet header (11 bytes long)
	 *
	 * @param data the data
	 */
	public BowlerDatagram(ByteList data) {
		parse(data);
		timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Breaks a chunk of data into the proper Bowler datagram fields. 
	 * 
	 * @param raw the chunk of data
	 */
	private void parse(ByteList raw) {		
		// Every valid Bowler packet has 11 characters from the header.
		if(raw.size() < HEADER_SIZE) {
			throw new MalformattedDatagram("Datagram does not have a valid Bowler header size.");
		}
		
		// Make sure that the revisions match
		if(raw.getByte(0) != REVISION) {
			throw new MalformattedDatagram("Datagram is revision " + raw.getByte(0) + ". Must be of revision " + REVISION);
		}

		address = new MACAddress(raw.getBytes(1,6));
		method = BowlerMethod.get(raw.getByte(7));
		transactionID = (byte) (raw.getByte(8)&0x7f);
		upstream=(raw.getByte(8)<0);
		// Make sure that the size of the data payload is the stated length
		if(raw.getByte(9) != raw.getBytes(11).length) {
			//throw new MalformattedDatagram("Datagram payload length is mismatched");
		}		

		// Validate the CRC
		if(!CheckCRC(raw)) {
			System.err.println("CRC failed check");
			throw new MalformattedDatagram("CRC does not match");
		}else{
			crc = raw.getCRC();
		}
		
		// Put the remaining data into the data payload 
		data = new ByteList(raw.getBytes(HEADER_SIZE));
	}
	
	/**
	 * Get the payload including the RPC.
	 *
	 * @return byte list of the paload
	 */
	public ByteList getPayload(){
		return new ByteList(data.getBytes());
	}
	
	/**
	 * Get the datagram's current address.
	 *
	 * @return the current address
	 */
	public MACAddress getAddress() {
		return new MACAddress(address.getBytes());
	}

	/**
	 * Get the datagram's current revision.
	 *
	 * @return the current revision
	 */
	public byte getRevision() {
		return REVISION;
	}

	/**
	 * Get the datagram's current method.
	 *
	 * @return the current method
	 */
	public BowlerMethod getMethod() {
		return method;
	}
	
	/**
	 * Get the datagram's current transaction id.
	 *
	 * @return the current transaction id
	 */
	public byte getTransactionID() {
		return transactionID;
	}

	/**
	 * Determines if the datagram is being sent syncronously or not.
	 *
	 * @return true if Syncronous
	 */
	public boolean isSyncronous() {
		return transactionID == 0;
	}
	
	/**
	 * Checks if is upstream.
	 *
	 * @return true, if is upstream
	 */
	public boolean isUpstream(){
		return upstream;
	}
	
	/**
	 * Gets the transaction upstream.
	 *
	 * @return the transaction upstream
	 */
	public byte getTransactionUpstream(){
		byte back=(byte) (getTransactionID()|(isUpstream()?0x80:0));
		return back;
	}

	/**
	 * Generates the valid CRC of the datagram.
	 *
	 * @return The datagram's CRC
	 */
	public byte getCRC() {
		return crc;
	}
	/**
	 * Gets the String representation of the datagram's RPC.
	 * @return A string representation of the datagram's RPC
	 */
	public String getRPC() {
		return new String(data.getBytes(0, 4));
	}
	
	/**
	 * Gets the Datagram's Session ID.
	 *
	 * @return The Session ID
	 */
	public int getSessionID() {
		return transactionID >= 0 ? (int) transactionID:(int) transactionID+256;
	}
	
	/**
	 * Get the datagram's current data payload after the RPC.
	 * @Deprecated use getData() instead
	 * @return the current data payload
	 */
	@Deprecated
	public ByteList getRPCData() {
		return getData();
	}
	/**
	 * Get the datagram's current data payload after the RPC.
	 *
	 * @return the current data payload
	 */
	public ByteList getData() {
		return new ByteList(data.getBytes(4));
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	public byte[] getBytes() {
		ByteList bl = new ByteList();
		bl.add(REVISION);
		bl.add(address);
		bl.add(method.getValue());
		bl.add(getTransactionUpstream());
		bl.add(data.size());
		bl.add(getCRC());
		bl.add(data);
		return bl.getBytes();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String str="";
		
		str += "\tRaw Packet:\t";
		for (byte x : getBytes()){
			// This writes out the hex values of all the data Bytes
			str += String.format("%02x ", x);
		}		
		str += "\n";
		str += "\tRevision: \t" + (int) REVISION + '\n';
		str += "\tMAC address: \t" + address + '\n';
		str += "\tMethod: \t" + method + '\n';
		str += "\tDirection: \t";
		str += "(" + (int) transactionID + ") ";
		str += isSyncronous() ? "Syncronous\n" : "Asyncronous\n";
		str += "\tSession ID: \t" + getSessionID();
		str += "\n\tData Size: \t" + (int) data.size() + '\n';
		str += "\tCRC: \t\t";
		str += ( getCRC()>=0) ? (int) getCRC():(int) getCRC()+256 ;
		str += "\n\tRPC: \t\t";
		str += getRPC();// This extracts the opcode as ascii
		str += "\n\tData: \t\t";
		for (byte x : getData().getBytes()){
			// This writes out the hex values of all the data Bytes
			str += String.format("%02x ", x);
		}
		str += '\n';
		return str;
	}

	/**
	 * Check crc.
	 *
	 * @param buffer the buffer
	 * @return true, if successful
	 */
	public static boolean CheckCRC(ByteList buffer) {
		byte generated;
		try{
			generated = buffer.genCRC();
		}catch (IndexOutOfBoundsException e){
			return false;
		}
		byte inPacket  = buffer.getCRC();
		if(generated != inPacket){
			//Log.info("CRC of packet is:"+generated+" CRC in packet is:"+inPacket);
			return false;
		}
		return true;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setAsAsync(int id) {
		transactionID=(byte) id;
	}
}
