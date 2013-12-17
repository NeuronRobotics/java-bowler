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
 * @author rbreznak, Kevin Harrington
 *
 */
public class BowlerDatagram implements ISendable,IthreadedTimoutListener {
	
	/** The Constant REVISION. */
	public static final byte REVISION = 3;
	
	/** The Constant HEADER_SIZE. */
	public static final int HEADER_SIZE = 11;
	
	/** The Constant CRC_INDEX. */
	public static final int CRC_INDEX = 10;

	public static final int MAX_PACKET_SIZE = HEADER_SIZE+255;
	
	/** The address. */
	private MACAddress address = new MACAddress();
	
	/** The method. */
	private BowlerMethod method;
	
	/** The transaction id. */
	private byte namespaceResolutionID;
	
	/** The upstream. */
	private boolean upstream;
	
	/** The crc. */
	private byte crc;
	
	/** The data. */
	private ByteList data = new ByteList();
	
	/** time of instantiation **/
	
	//private long timestamp;
	
	private boolean isPackedAvailibleForLoading = true;
	
	private ThreadedTimeout timeout=new ThreadedTimeout();
	
	
	/**
	 * Default constructor.
	 */
	public BowlerDatagram(BowlerDatagramFactory factory) {
		validate(factory);
		setFree(true,factory);
	}
	

	/**
	 * Constructs a BowlerDatagram that will parse the given data into a packet.
	 * Data must be at least the size of a standard Bowler packet header (11 bytes long)
	 *
	 * @param data the data
	 */
	public BowlerDatagram(ByteList data,BowlerDatagramFactory factory) {
		validate(factory);
		parse(data);
	}
	
	private void validate(BowlerDatagramFactory factory){
		if(!isFree()){
			throw new RuntimeException("Packet is in use, be sure to use the factory");
		}
		timeout.initialize(BowlerDatagramFactory.getPacketTimeout(), this);
	}
	
	/**
	 * Breaks a chunk of data into the proper Bowler datagram fields. 
	 * 
	 * @param raw the chunk of data
	 */
	public void parse(ByteList raw) {	
		checkValidPacket();
		// Every valid Bowler packet has 11 characters from the header.
		if(raw.size() < HEADER_SIZE) {
			throw new MalformattedDatagram("Datagram does not have a valid Bowler header size.");
		}
		
		// Make sure that the revisions match
		if(raw.getByte(0) != REVISION) {
			throw new MalformattedDatagram("Datagram is revision " + raw.getByte(0) + ". Must be of revision " + REVISION);
		}

		setAddress(new MACAddress(raw.getBytes(1,6)));
		setMethod(BowlerMethod.get(raw.getByte(7)));
		if(getMethod() == null){
			setMethod(BowlerMethod.STATUS);
			System.err.println("Method was invalid!! Value="+raw.getByte(7));
			Log.error("Method was invalid!! Value="+raw.getByte(7));
		}
			
		setNamespaceResolutionID((byte) (raw.getByte(8)&0x7f));
		setUpstream((raw.getByte(8)<0));
		// Make sure that the size of the data payload is the stated length
		if(raw.getByte(9) != raw.getBytes(11).length) {
			//throw new MalformattedDatagram("Datagram payload length is mismatched");
		}		

		// Validate the CRC
		if(!CheckCRC(raw)) {
			System.err.println("CRC failed check");
			throw new MalformattedDatagram("CRC does not match");
		}else{
			setCrc(raw.getCRC());
		}
		
		// Put the remaining data into the data payload 
		setData(raw.getBytes(HEADER_SIZE));
		setFree(false);
	}
	
	public void setData(byte[] bs){
		checkValidPacket();
		data.clear();
		data.add(bs);
	}
	
	/**
	 * Get the payload including the RPC.
	 *
	 * @return byte list of the paload
	 */
	public ByteList getPayload(){
		checkValidPacket();
		return new ByteList(data.getBytes());
	}
	
	/**
	 * Get the datagram's current address.
	 *
	 * @return the current address
	 */
	public MACAddress getAddress() {
		checkValidPacket();
		return address;
	}

	/**
	 * Get the datagram's current revision.
	 *
	 * @return the current revision
	 */
	public byte getRevision() {
		checkValidPacket();
		return REVISION;
	}

	/**
	 * Get the datagram's current method.
	 *
	 * @return the current method
	 */
	public BowlerMethod getMethod() {
		checkValidPacket();
		return method;
	}
	
	/**
	 * Get the datagram's current transaction id.
	 *
	 * @return the current transaction id
	 */
	public byte getNamespaceResolutionID() {
		checkValidPacket();
		return (byte) namespaceResolutionID;
	}

	/**
	 * Determines if the datagram is being sent synchronously or not.
	 *
	 * @return true if Synchronous
	 */
	public boolean isSyncronous() {
		checkValidPacket();
//		if(namespaceResolutionID != 0 && method == BowlerMethod.ASYNCHRONOUS){
//			Log.error("Device firmware out of date, should be using BowlerMethod.ASYNCHRONOUS rather than transactionID != 0" + this);
//		}
		return getMethod() != BowlerMethod.ASYNCHRONOUS;
	}
	
	/**
	 * Checks if is upstream.
	 *
	 * @return true, if is upstream
	 */
	public boolean isUpstream(){
		checkValidPacket();
		return upstream;
	}
	
	/**
	 * Gets the transaction upstream.
	 *
	 * @return the transaction upstream
	 */
	public byte getTransactionUpstream(){
		checkValidPacket();
		byte back=(byte) (getNamespaceResolutionID()|(isUpstream()?0x80:0));
		return back;
	}

	/**
	 * Generates the valid CRC of the datagram.
	 *
	 * @return The datagram's CRC
	 */
	public byte getCRC() {
		checkValidPacket();
		return getCrc();
	}
	/**
	 * Gets the String representation of the datagram's RPC.
	 * @return A string representation of the datagram's RPC
	 */
	public String getRPC() {
		checkValidPacket();
		try{
			return new String(data.getBytes(0, 4));
		}catch(Exception e){
			e.printStackTrace();
			return "****";
		}
	}
	
	/**
	 * Gets the Datagram's Session ID.
	 *
	 * @return The Session ID
	 */
	private int getSessionID() {
		checkValidPacket();
		return getNamespaceResolutionID() >= 0 ? (int) getNamespaceResolutionID():(int) getNamespaceResolutionID()+256;
	}
	
	/**
	 * Get the datagram's current data payload after the RPC.
	 * @Deprecated use getData() instead
	 * @return the current data payload
	 */
	@Deprecated
	public ByteList getRPCData() {
		checkValidPacket();
		return getData();
	}
	/**
	 * Get the datagram's current data payload after the RPC.
	 *
	 * @return the current data payload
	 */
	public ByteList getData() {
		checkValidPacket();
		return new ByteList(data.getBytes(4));
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	public byte[] getBytes() {
		checkValidPacket();
		ByteList bl = new ByteList();
		bl.add(REVISION);
		bl.add(getAddress());
		bl.add(getMethod().getValue());
		bl.add(getTransactionUpstream());
		bl.add(data.size());
		//calculate the CRC
		setCrc(bl.genCRC());
		
		bl.add(getCRC());
		bl.add(data);
		return bl.getBytes();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(isFree())
			return "Empty Packet";
		String str="";
		
		str += "\tRaw Packet:\t";
		for (byte x : getBytes()){
			// This writes out the hex values of all the data Bytes
			str += String.format("%02x ", x);
		}		
		str += "\n";
		str += "\tRevision: \t" + (int) REVISION + '\n';
		str += "\tMAC address: \t" + getAddress() + '\n';
		str += "\tMethod: \t" + getMethod() + '\n';
		str += "\tDirection: \t";
		str += isUpstream() ? "Upstream\n" : "Downstream\n";
		str += "\tRPC Namespace Index: \t" + getSessionID();
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
			Log.error("CRC of packet is: "+generated+" Expected: "+inPacket);
			return false;
		}
		return true;
	}

	public long getTimestamp() {
		checkValidPacket();
		return timeout.getStartTime();
	}

	public void setAsAsync(int id) {
		setNamespaceResolutionID((byte) id);
		setMethod(BowlerMethod.ASYNCHRONOUS);
		checkValidPacket();
	}


	public void clear() {
		data.clear();
	}


	public void setAddress(MACAddress address) {
		checkValidPacket();
		this.address = address;
	}


	public void setMethod(BowlerMethod method) {
		checkValidPacket();
		this.method = method;
	}


	public void setNamespaceResolutionID(byte namespaceResolutionID) {
		checkValidPacket();
		this.namespaceResolutionID = namespaceResolutionID;
	}


	public void setUpstream(boolean upstream) {
		checkValidPacket();
		this.upstream = upstream;
	}


	private byte getCrc() {
		checkValidPacket();
		return crc;
	}


	public void setCrc(byte crc) {
		checkValidPacket();
		this.crc = crc;
	}
	
	private void checkValidPacket(){
		if(isFree() && timeout.isTimedOut()){
			throw new RuntimeException("This packet has timed out and the data has been cleared marked="+isFree());
		}else{
			setFree(false);
		}
	}


	public boolean isFree() {
		return isPackedAvailibleForLoading;
	}


	public void setFree(boolean isFree, BowlerDatagramFactory factory) {
		BowlerDatagramFactory.validateFactory(factory);
		setFree(isFree);
	}

	void setFree(boolean isFree) {
		if(isFree== true){
			clear();
			timeout.stop();
		}else{
			timeout.initialize(BowlerDatagramFactory.getPacketTimeout(), this);
		}
		this.isPackedAvailibleForLoading = isFree;
	}

	@Override
	public void onTimeout(String message) {
		if(!isFree() ){
			if((BowlerDatagramFactory.getPacketTimeout()+timeout.getStartTime())<System.currentTimeMillis()){
				setFree(true);
			}else{
				Log.error("Packet fucked up "+ ((BowlerDatagramFactory.getPacketTimeout()+timeout.getStartTime())-System.currentTimeMillis()));
				timeout.initialize(BowlerDatagramFactory.getPacketTimeout(), this);
				throw new RuntimeException();
			}
		}
	}


	public void calcCRC() {
		checkValidPacket();
		getBytes();
	}


	public void setRpc(String opCode) {
		// TODO Auto-generated method stub
		
	}
}
