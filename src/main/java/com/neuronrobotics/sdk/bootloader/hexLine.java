package com.neuronrobotics.sdk.bootloader;
//import java.util.LinkedList;
//import java.util.Queue;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class hexLine.
 */
public class hexLine {
	
	/** The data bytes. */
	private ArrayList<Byte> dataBytes = new ArrayList<Byte>();
	
	/** The address. */
	private int address;
	
	/** The byte count. */
	private int byteCount;
	
	/** The record type. */
	private int recordType;
	
	/** The check sum. */
	private int checkSum;
	
	/** The has set high address. */
	private boolean hasSetHighAddress=false;
	
	/**
	 * Gets the check sum.
	 *
	 * @return the check sum
	 */
	public int getCheckSum() {
		return checkSum;
	}

	/**
	 * Instantiates a new hex line.
	 *
	 * @param s the s
	 * @throws Exception the exception
	 */
	public hexLine(String s) throws Exception{
		char data[] = s.toCharArray();
		if ((data.length<11)||data[0]!=':')
			throw new Exception("This line is not a hex line");
		
		char[] bc={data[1],data[2]};
		byteCount = Integer.parseInt(new String(bc), 16); 
		
		char[] ad={data[3],data[4],data[5],data[6]};
		address = Integer.parseInt(new String(ad), 16); 
		
		char[] rt={data[7],data[8]};
		recordType = Integer.parseInt(new String(rt), 16); 
		
		char[] cs={data[data.length-2],data[data.length-1]};
		checkSum = Integer.parseInt(new String(cs), 16); 
		
		for (int i=0;i<byteCount;i++){
			char[] d={data[9+(i*2)],data[9+1+(i*2)]};
			Byte b =new Byte((byte) Integer.parseInt(new String(d), 16));
			dataBytes.add(b);
		}
		
	}

	/**
	 * Gets the start address.
	 *
	 * @return the start address
	 */
	public int getStartAddress() {
		return address;
	}
	
	/**
	 * Gets the end address.
	 *
	 * @return the end address
	 */
	public int getEndAddress() {
		return address+dataBytes.size();
	}
	
	/**
	 * Sets the high address.
	 *
	 * @param highAddress the new high address
	 */
	public void setHighAddress(long highAddress){
		if(!hasSetHighAddress){
			hasSetHighAddress=true;
			address+=highAddress;
		}
	}

	/**
	 * Gets the byte count.
	 *
	 * @return the byte count
	 */
	public int getByteCount() {
		return byteCount;
	}

	/**
	 * Gets the record type.
	 *
	 * @return the record type
	 */
	public int getRecordType() {
		return recordType;
	}
	
	/**
	 * Gets the data bytes.
	 *
	 * @return the data bytes
	 */
	public byte[] getDataBytes() { 
		if (dataBytes.size()==0) 
			return null;
		return dataToArray(dataBytes);
	}
	
	/**
	 * Checks for data.
	 *
	 * @return the boolean
	 */
	public Boolean hasData(){
		if (dataBytes==null) return false;
		return true;
	}
	
	/**
	 * Data to array.
	 *
	 * @param bl the bl
	 * @return the byte[]
	 */
	private byte [] dataToArray( ArrayList<Byte> bl){
		byte [] b = new byte[bl.size()];
		int i=0;
		for (Byte bld:bl){
			b[i++]=bld.byteValue();
		}
		return b;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s="";
		if (getRecordType()==0){
			s+="Address = "+getStartAddress();
			s+=" Data: [";
			for (byte b: getDataBytes()){
				s+=b+",";
			}
			s+="]";
		}else if (getRecordType()==4){
			s="High Address Set: ";
		}
		return s;
	}
	
}
