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
	
	private static BowlerDatagramFactory instance;
	
	private static BowlerDatagram pool [];
	private static int failed=0;
	private static int poolDefaultSize = 40;
	private static int packetTimeout = 2000;
	
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
	
	public static int getCurrentPoolSize(){
		return pool.length;
	}
	
	public static boolean validateFactory(BowlerDatagramFactory test){
		if(test == instance){
			return true;
		}
		throw new RuntimeException("Invalid factory generation of packet. Use BowlerDatagramFactory.getNextPacket()");
	}
	
	private static synchronized BowlerDatagram getNextPacket(){
		BowlerDatagram ref = null;
		
		//Find the most recent free packet from the pool
		for(int i=0;(i<pool.length && ref==null);i++){
			//Log.warning("Checking pool packet "+i);
			if(pool[i].isFree()){
				ref=pool[i];
			}
		}
		if(ref == null){
			//The whole list was search and no free packets were found
			BowlerDatagram newPool [ ] = new BowlerDatagram[pool.length+getDefaultPoolSize()];
			Log.warning("Increasing BowlerDatagram pool size to "+newPool.length);
			for(int i=0;i<pool.length;i++){
				newPool[i] = pool[i];
			}
			//Adding the new packets
			for(int i=pool.length;i<newPool.length;i++){
				newPool[i] = new BowlerDatagram(instance);
				freePacket(newPool[i]);
				if(ref==null)
					ref=newPool[i];
			}
			pool = newPool;
		}
		//old pool data given to the GC
		ref.setFree(false,instance);
		return ref;
	}
	
	public static void freePacket(BowlerDatagram bd){
		bd.setFree(true, instance);
	}

	/** The Constant REVISION. */
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
	
	public static BowlerDatagram build(ByteList buffer ){
		BowlerDatagram buff= getNextPacket();
		buff.setFree(false,instance);
		BowlerDatagram ret = build(buffer, buff );
		if(ret == null)
			freePacket(buff);
		return ret;
	}
	
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
			Log.error("First Byte Fail, Junk byte: "+String.format("%02x ", buffer.pop()));
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
						|| (!BowlerDatagram.CheckCRC(buffer))){
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
		int len =(int) buffer.getByte(9);
		if(len<0){
			len+=256;
		}
		if(len<4){
			Log.error("#*#*Warning, packet has no RPC, size: "+len);
			
		}
		int totalLen = len+BowlerDatagram.HEADER_SIZE;
		// See if all the data has arrived for this packet
		if (buffer.size()>=(totalLen) ){
			failed=0;
			ByteList rawContent = new ByteList(buffer.popList(totalLen));
			staticMemory.parse(rawContent);
			staticMemory.setFree(false,instance);
			return  staticMemory;
		}
//		if(failed>0)
//			Log.error("Failed out "+failed+" bytes");
		return null;
	}

	public static int getDefaultPoolSize() {
		return poolDefaultSize;
	}

	public static void setPoolSize(int poolSize) {
		BowlerDatagramFactory.poolDefaultSize = poolSize;
	}

	public static int getPacketTimeout() {
		return packetTimeout;
	}

	public static void setPacketTimeout(int packetTimeout) {
		BowlerDatagramFactory.packetTimeout = packetTimeout;
	}

}
