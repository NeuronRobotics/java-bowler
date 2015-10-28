/**
 * 
 */
package com.neuronrobotics.sdk.bootloader;

import java.io.IOException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class IntelHexParser.
 *
 * @author hephaestus
 */
public class IntelHexParser {
	
	/** The high address. */
	private long highAddress=0;
	
	/** The packet list. */
	ArrayList<ByteData> packetList = new ArrayList<ByteData>();
	
	/** The data index. */
	private long dataIndex=0; 
	
	/** The base. */
	private long base = 0x1D00A000L;
	
	/** The head. */
	private long head =  0x1D01FFFFL;
	
	/**
	 * Hex.
	 *
	 * @param n the n
	 * @return the string
	 */
	public static String hex(long n) {
	    // call toUpperCase() if that's required
	    return String.format("0x%8s", Long.toHexString(n)).replace(' ', '0');
	}
	
	/**
	 * Check address validity.
	 *
	 * @param currentAddress the current address
	 * @param type the type
	 */
	private void checkAddressValidity(long currentAddress, NRBootCoreType type){
		if(type==NRBootCoreType.PIC32){
			if(currentAddress>head ){
				throw new RuntimeException("Address "+hex(currentAddress)+" is larger than "+hex(head));
			}
			if(currentAddress<base){
				throw new RuntimeException("Address "+hex(currentAddress)+" is less than "+hex(base));
			}	
		}
		
	}
	
	/**
	 * Instantiates a new intel hex parser.
	 *
	 * @param lines the lines
	 * @param type the type
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public IntelHexParser(ArrayList<hexLine> lines, NRBootCoreType type) throws IOException{
	     ByteData tmp=null;
	     hexLine  previousLine=null;
	     for (hexLine l : lines){
	    	 long currentAddress;
	    	 long endOfLastAddress = 0;
	    	 
	    	 if (l.getRecordType()==4){
	    		 byte[] haddr=l.getDataBytes();
	    		 highAddress = ByteList.convertToInt(haddr, false)*65536;
	    		 ////System.out.println("High Address :" + highAddress);
	    	 } if (l.getRecordType()==0){
	    		 
	    		 l.setHighAddress(highAddress);
	    		 ////System.out.println(l);
	    		 
	    		 currentAddress=l.getStartAddress();
	    		 checkAddressValidity(currentAddress,type);
	    		 if (previousLine != null){
	    			 endOfLastAddress = previousLine.getEndAddress();
	    		 }
	    		 boolean isSequential = (currentAddress==(endOfLastAddress));
	    		 if(tmp==null){
		    		 tmp =  new ByteData(currentAddress);
		    	 }
	    		 if(type==NRBootCoreType.PIC32){
	    			 if(!isSequential||(tmp.getData().length > 50)){
			    		 packetList.add(tmp);
			    		 tmp =  new ByteData(currentAddress);
			    	 } 
	    		 }
	    		 for (byte b : l.getDataBytes()){
	    			 if(type == NRBootCoreType.AVRxx4p){
	    				 if(!isSequential||(tmp.getData().length == 128)){
				    		 packetList.add(tmp);
				    		 tmp =  new ByteData(currentAddress);
				    	 }
	    			 }
	    			 tmp.setData(b);
	    			 
	    			 currentAddress++;

		    		 checkAddressValidity(currentAddress,type);
	    		 }
	    		 previousLine=l;
	    	 }
	    	 
	    
	     }
	     if (tmp.getData().length!=0){
	    	 packetList.add(tmp); 
	     }
	    
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size(){
		return packetList.size();
	}
	
	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	public ByteData getNext(){
		if (dataIndex < packetList.size()){
			return packetList.get((int) dataIndex++);
		}
		return null;
	}
	
}
