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
 * This DatagramFactory Builds a datagram. 
 * 
 * @author robert
 *
 */

public class BowlerDatagramFactory {
	
	/** The instance. */
	private static BowlerDatagramFactory instance;
	
	/** The pool. */
	private static BowlerDatagram pool [];
	
	/** The failed. */
	private static int failed=0;
	
	/** The last index. */
	private static int lastIndex = 0;
	
	/** The pool default size. */
	private static int poolDefaultSize = 1000;
	
	/** The packet timeout. */
	private static long packetTimeout = 5000;
	
	static{
		if(instance ==  null){
			instance = new BowlerDatagramFactory();
			pool =  new BowlerDatagram[getDefaultPoolSize()];
			for (int i=0;i<getDefaultPoolSize();i++){
				pool [i] = new BowlerDatagram(instance);
				freePacket(pool [i]);
			}
		}
	}
	
	/**
	 * Gets the current pool size.
	 *
	 * @return the current pool size
	 */
	public static int getCurrentPoolSize(){
		return pool.length;
	}
	
	/**
	 * Validate factory.
	 *
	 * @param test the test
	 * @return true, if successful
	 */
	public static boolean validateFactory(BowlerDatagramFactory test){
		if(test == instance){
			return true;
		}
		throw new RuntimeException("Invalid factory generation of packet. Use BowlerDatagramFactory.getNextPacket()");
	}
	
	/**
	 * Gets the next packet.
	 *
	 * @return the next packet
	 */
	private static BowlerDatagram getNextPacket(){
		BowlerDatagram ref = new BowlerDatagram(instance);
		
		//Find the most recent free packet from the pool
//		for(int i=lastIndex;(i<pool.length && ref==null);i++){
//			//Log.warning("Checking pool packet "+i);
//			if(pool[i]==null){
//				pool[i]=new BowlerDatagram(instance);
//			}
//			freePacket(pool[i]);
//			if(pool[i].isFree()){
//				lastIndex=i;
//				ref=pool[i];
//			}
//			if(i==pool.length-1 && ref==null){
//				//loop around since we started at the last index
//				i=0;
//			}
//			if(i==lastIndex-1 && ref==null){
//				//looped around, bail
//				i=pool.length;
//			}
//			if(ref!=null){
//				lastIndex=i++;
//			}
//		}
//		if(ref == null){
//			//The whole list was search and no free packets were found
//			//pool= new BowlerDatagram[(int) ((float)pool.length)];
//			//Log.warning("Resetting pool "+pool.length);
//			//pool[0]= new BowlerDatagram(instance);
//			ref=pool[0];
//		}
		//old pool data given to the GC
		ref.setFree(false,instance);
		return ref;
	}
	
	/**
	 * Free packet.
	 *
	 * @param bd the bd
	 */
	public static void freePacket(BowlerDatagram bd){
		bd.setFree(true, instance);
	}

	/**
	 *  The Constant REVISION.
	 *
	 * @param addr the addr
	 * @param cmd the cmd
	 * @return the bowler datagram
	 */
	//public static final byte REVISION = BowlerDatagram.REVISION;
	
	/**
	 * This builds a datagram with the given RPC and MAC address.
	 *
	 * @param addr The MAC address of the DyIO
	 * @param cmd 	The RPC
	 * @return 		A valid Bowler Datagram.
	 */
	
	public static BowlerDatagram build(MACAddress addr, BowlerAbstractCommand cmd) {
		BowlerDatagram bd = getNextPacket();
		long start = System.currentTimeMillis();

		if (addr== null)
			addr = new MACAddress();
		try{
			bd.setAddress(addr);
			bd.setMethod(cmd.getMethod()); // method id
			bd.setNamespaceResolutionID((byte) cmd.getNamespaceIndex());// Rpc Index id
			bd.setData(cmd.getBytes());
			bd.calcCRC();
		}catch(Exception e){
			e.printStackTrace();
			//if(System.currentTimeMillis()>(start+BowlerDatagramFactory.getPacketTimeout()))
			Log.error("Timeout detected, duration = "+(System.currentTimeMillis()-start)+", expected = "+BowlerDatagramFactory.getPacketTimeout());
		}
		bd.setFree(false,instance);
		return bd;
	}
	
	/**
	 * Builds the.
	 *
	 * @param buffer the buffer
	 * @return the bowler datagram
	 */
	public static BowlerDatagram build(ByteList buffer ){
		BowlerDatagram buff= getNextPacket();
		buff.setFree(false,instance);
		BowlerDatagram ret = build(buffer, buff );
		if(ret == null)
			freePacket(buff);
		return ret;
	}
	
	/**
	 * Builds the.
	 *
	 * @param buffer the buffer
	 * @param staticMemory the static memory
	 * @return the bowler datagram
	 */
	private static BowlerDatagram build(ByteList buffer, BowlerDatagram staticMemory){
		if((buffer.size()==0))
			return null;
		byte fb;
		try{
			fb = buffer.get(0);
		}catch (Exception e){
			Log.warning("Datagram builder first byte warning: "+e.getMessage());
			return null;
		}
		
		while(fb!=BowlerDatagram.REVISION) {
			//Log.error("First Byte Fail, Junk byte: "+String.format("%02x ", buffer.pop()));
			failed++;
			try{
				if(buffer.size()==0){
					if(failed>0){
						//Log.error("Failed out "+failed+" bytes");
					}
					return null;
				}
				fb = buffer.get(0);
			}catch (Exception e){
				Log.warning("Datagram builder warning: "+e.getMessage());
				if(failed>0){
					//Log.error("Failed out "+failed+" bytes");
				}
				return null;
			}
		}
		
		
		if(buffer.size()<BowlerDatagram.HEADER_SIZE) 
			return null;
		boolean check = false;
		while(check==false) {
			try{
				if( (buffer.get(0) != BowlerDatagram.REVISION)
						|| (!BowlerDatagram.CheckCRC(buffer,false))){
					if(buffer.get(0) != BowlerDatagram.REVISION)
						Log.error("First Byte Fail (second attempt) Junk byte: "+String.format("%02x ", buffer.pop()));
					else
						Log.error("CRC check Fail (second attempt) Junk byte: "+String.format("%02x ", buffer.pop()));
					failed++;
				}else{
					check=true;
				}
			}catch (Exception e){
				//Log.error("Datagram builder error: "+e.getMessage());
				if(failed>0){
					//Log.error("Failed out "+failed+" bytes");
				}
				e.printStackTrace();
				return null;
			}
			if (buffer.size()<BowlerDatagram.HEADER_SIZE) {
				if(failed>0){
					//Log.error("Failed out "+failed+" bytes");
				}
				return null;//Not enough bytes to even be a header, try back later
			}
		}
		int len =buffer.getUnsigned(9);
	
		if(len<4){
			Log.error("#*#*Warning, packet has no RPC, size: "+len);	
		}
		int totalLen = len+BowlerDatagram.HEADER_SIZE;	
		
		if(BowlerDatagram.isUseBowlerV4())
			totalLen+=1;
		staticMemory.setFree(false,instance);
		// See if all the data has arrived for this packet
		if (buffer.size()>=(totalLen)){
			failed=0;
			ByteList rawContent = new ByteList(buffer.popList(totalLen));	
			try{
				staticMemory.parse(rawContent);
				return staticMemory;
			}catch(MalformattedDatagram m){
				Log.error("Data CRC check Fail  "+staticMemory);
				failed = rawContent.size();
				throw m;
			}		
		}
//		if(failed>0)
//			Log.error("Failed out "+failed+" bytes");
		return null;
	}

	/**
	 * Gets the default pool size.
	 *
	 * @return the default pool size
	 */
	public static int getDefaultPoolSize() {
		return poolDefaultSize;
	}

	/**
	 * Sets the pool size.
	 *
	 * @param poolSize the new pool size
	 */
	public static void setPoolSize(int poolSize) {
		BowlerDatagramFactory.poolDefaultSize = poolSize;
	}

	/**
	 * Gets the packet timeout.
	 *
	 * @return the packet timeout
	 */
	public static long getPacketTimeout() {
		return packetTimeout;
	}

	/**
	 * Sets the packet timeout.
	 *
	 * @param packetTimeout the new packet timeout
	 */
	public static void setPacketTimeout(int packetTimeout) {
		BowlerDatagramFactory.packetTimeout = packetTimeout;
	}

}
