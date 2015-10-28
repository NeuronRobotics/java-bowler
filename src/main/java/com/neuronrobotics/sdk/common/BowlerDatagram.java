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

// TODO: Auto-generated Javadoc
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

	/** The Constant MAX_PACKET_SIZE. */
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
	
	/** The Data crc. */
	private byte dataCrc;
	
	/** The data. */
	private ByteList data = new ByteList();
	
	/**  time of instantiation *. */
	
	//private long timestamp;
	
	private boolean isPackedAvailibleForLoading = true;
	
	/** The timeout. */
	private ThreadedTimeout timeout=new ThreadedTimeout();
	
	/** The use bowler v4. */
	private static boolean useBowlerV4 =true;
	
	
	/**
	 * Default constructor.
	 *
	 * @param factory the factory
	 */
	public BowlerDatagram(BowlerDatagramFactory factory) {
		validate(factory);
		setFree(true,factory);
	}
	
	/**
	 * Generate a CRC of a section of bytes,.
	 *
	 * @param index Start o the section
	 * @param len LEngth of the section
	 * @param data the data
	 * @return The Calculated CRC
	 */
	private  static byte genCRC(int index, int len, ByteList data) {
		int check = 0;
		try{
			for(int i = index; i < len+index; i++) {
				check += data.getByte(i);
			}
		}catch(IndexOutOfBoundsException e){
			throw new IndexOutOfBoundsException("Attempting from: "+index+ " to: "+len+" in: "+data.size()+" data: "+data);
		}
		return (byte)(check&0x000000ff);
	}
	
	/**
	 * Assumes that the packet starts at byte 0.
	 *
	 * @param data the data
	 * @return the byte
	 */
	private static byte genCrc(ByteList data){
		return genCRC(0, (BowlerDatagram.HEADER_SIZE-1), data);
	}
	
	/**
	 * Assumes the packet starts at byte 0.
	 *
	 * @param data the data
	 * @return the byte holding the header crc
	 */
	private  static byte getCRC(ByteList data){
		return data.getByte(BowlerDatagram.CRC_INDEX);
	}
	
	/**
	 * Gets the crc.
	 *
	 * @return the crc
	 */
	private byte getCrc() {
		checkValidPacket();
		return crc;
	}


	/**
	 * Sets the crc.
	 *
	 * @param crc the new crc
	 */
	private void setCrc(byte crc) {
		checkValidPacket();
		this.crc = crc;
	}
	

	/**
	 * Assumes that the packet starts at byte 0.
	 *
	 * @param data the data
	 * @return the byte
	 */
	public  static byte genDataCrc(ByteList data){
		return genCRC(BowlerDatagram.HEADER_SIZE, data.getUnsigned(9), data);
	}
	
	/**
	 * Assumes the packet starts at byte 0.
	 *
	 * @param data the data
	 * @return the byte holding the header crc
	 */
	private  static byte getDataCrc(ByteList data){
		return data.getByte((BowlerDatagram.HEADER_SIZE)+data.getUnsigned(9));
	}
	
	/**
	 * Gets the data crc.
	 *
	 * @return the data crc
	 */
	private byte getDataCrc() {
		checkValidPacket();
		return dataCrc;
	}


	/**
	 * Sets the data crc.
	 *
	 * @param crc the new data crc
	 */
	private void setDataCrc(byte crc) {
		checkValidPacket();
		this.dataCrc = crc;
	}

	/**
	 * Constructs a BowlerDatagram that will parse the given data into a packet.
	 * Data must be at least the size of a standard Bowler packet header (11 bytes long)
	 *
	 * @param data the data
	 * @param factory the factory
	 */
	public BowlerDatagram(ByteList data,BowlerDatagramFactory factory) {
		validate(factory);
		parse(data);
	}
	
	/**
	 * Validate.
	 *
	 * @param factory the factory
	 */
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
			System.err.println("Method was invalid!! Value="+raw.getUnsigned(7));
			Log.error("Method was invalid!! Value="+raw.getUnsigned(7));
		}
			
		setNamespaceResolutionID((byte) (raw.getUnsigned(8)&0x7f));
		setUpstream((raw.getByte(8)<0));
		// Make sure that the size of the data payload is the stated length
		int dataLength = raw.getUnsigned(9);
		//Either legacy parser or the v4 parser
		if((dataLength != raw.getBytes(11).length-1) && (dataLength != raw.getBytes(11).length) ) {
			throw new MalformattedDatagram("Datagram payload length is mismatched expected "+dataLength+" got "+raw.getBytes(11).length);
		}	
		// Put the remaining data into the data payload  
		setData(raw.getBytes(HEADER_SIZE,dataLength));

		// Validate the CRC
		if(!CheckCRC(raw,true) ) {
				throw new MalformattedDatagram("CRC does not match: "+raw);
		}else{
			setCrc(getCRC(raw));
			setDataCrc(raw.getByte(raw.size()-1));
		}
		setFree(false);
	}
	
	/**
	 * Sets the data.
	 *
	 * @param bs the new data
	 */
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
			//e.printStackTrace();
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
		setCrc(genCrc(bl));
		
		
		bl.add(getCRC());
		bl.add(data);
		if(isUseBowlerV4()){
			//Log.warning("parsing v4 ");
			setDataCrc(genDataCrc(bl));
			bl.add(getDataCrc());
		}
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
		if(!Log.isPrinting()){
			return str;
		}
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
		str +=  String.format("%02x ", getCrc());
		if(isUseBowlerV4()){
			str += "\n\tD-CRC: \t\t";
			str += String.format("%02x ", getDataCrc());
		}
		
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
	 * @param checkData the check data
	 * @return true, if successful
	 */
	static boolean CheckCRC(ByteList buffer, boolean checkData) {
		try{
			byte generated,inPacket;
			generated = genCrc(buffer);
			inPacket  = getCRC(buffer);
			if(generated != inPacket){
				Log.error("CRC of packet is: "+generated+" Expected: "+inPacket);
				return false;
			}
			
			if(checkData && isUseBowlerV4()){
				generated = genDataCrc(buffer);
				inPacket  = getDataCrc(buffer);
				if(generated != inPacket){
					Log.error("Data CRC of packet is: "+generated+" Expected: "+inPacket);
					return false;
				}
			}
		}catch(Exception ex){
			if(InterruptedException.class.isInstance(ex))throw new RuntimeException(ex);
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public long getTimestamp() {
		checkValidPacket();
		return timeout.getStartTime();
	}

	/**
	 * Sets the as async.
	 *
	 * @param id the new as async
	 */
	public void setAsAsync(int id) {
		setNamespaceResolutionID((byte) id);
		setMethod(BowlerMethod.ASYNCHRONOUS);
		checkValidPacket();
	}


	/**
	 * Clear.
	 */
	public void clear() {
		data.clear();
	}


	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(MACAddress address) {
		checkValidPacket();
		this.address = address;
	}


	/**
	 * Sets the method.
	 *
	 * @param method the new method
	 */
	public void setMethod(BowlerMethod method) {
		checkValidPacket();
		this.method = method;
	}


	/**
	 * Sets the namespace resolution id.
	 *
	 * @param namespaceResolutionID the new namespace resolution id
	 */
	public void setNamespaceResolutionID(byte namespaceResolutionID) {
		checkValidPacket();
		this.namespaceResolutionID = namespaceResolutionID;
	}


	/**
	 * Sets the upstream.
	 *
	 * @param upstream the new upstream
	 */
	public void setUpstream(boolean upstream) {
		checkValidPacket();
		this.upstream = upstream;
		calcCRC();
	}



	
	/**
	 * Check valid packet.
	 */
	private void checkValidPacket(){
		if(isFree() && timeout.isTimedOut()){
			throw new RuntimeException("This packet has timed out and the data has been cleared marked="+isFree());
		}else{
			setFree(false);
		}
	}


	/**
	 * Checks if is free.
	 *
	 * @return true, if is free
	 */
	public boolean isFree() {
		return isPackedAvailibleForLoading;
	}


	/**
	 * Sets the free.
	 *
	 * @param isFree the is free
	 * @param factory the factory
	 */
	public void setFree(boolean isFree, BowlerDatagramFactory factory) {
		BowlerDatagramFactory.validateFactory(factory);
		setFree(isFree);
	}

	/**
	 * Sets the not free.
	 */
	private void setNotFree(){
		clear();
		timeout.stop();
	}
	
	/**
	 * Sets the to free.
	 */
	private void setToFree(){
		timeout.initialize(BowlerDatagramFactory.getPacketTimeout(), this);
	}
	
	/**
	 * Sets the free.
	 *
	 * @param isFree the new free
	 */
	void setFree(boolean isFree) {
		if(isFree== true){
			setNotFree();
		}else{
			setToFree();
		}
		this.isPackedAvailibleForLoading = isFree;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IthreadedTimoutListener#onTimeout(java.lang.String)
	 */
	@Override
	public void onTimeout(String message) {
		if(!isFree() ){
			long timeoutTime= System.currentTimeMillis()-timeout.getStartTime();
			if(timeout.getAmountOfTimeForTimerToRun() < timeoutTime){
				setFree(true);
			}else{
				Log.error("Packet fucked up. Expected "+timeout.getAmountOfTimeForTimerToRun()+" ms, took "+timeoutTime+" ms");
				timeout.initialize(BowlerDatagramFactory.getPacketTimeout(), this);
			}
		}
	}


	/**
	 * Calc crc.
	 */
	public void calcCRC() {
		checkValidPacket();
		getBytes();
	}


	/**
	 * Sets the rpc.
	 *
	 * @param opCode the new rpc
	 */
	public void setRpc(String opCode) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Checks if is use bowler v4.
	 *
	 * @return true, if is use bowler v4
	 */
	public static boolean isUseBowlerV4() {
		return useBowlerV4;
	}

	/**
	 * Sets the use bowler v4.
	 *
	 * @param useBowlerV4 the new use bowler v4
	 */
	public static void setUseBowlerV4(boolean useBowlerV4) {
		Log.warning("Setting V4 mode = "+useBowlerV4);
		//new Exception().printStackTrace();
		BowlerDatagram.useBowlerV4 = useBowlerV4;
	}
}
