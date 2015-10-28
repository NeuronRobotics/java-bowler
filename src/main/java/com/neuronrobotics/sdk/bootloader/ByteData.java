package com.neuronrobotics.sdk.bootloader;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ByteData.
 */
public class ByteData {
	
	/** The address. */
	private long address;
	
	/** The data bytes. */
	private ArrayList<Byte> dataBytes = new ArrayList<Byte>();
	
	/**
	 * Instantiates a new byte data.
	 *
	 * @param address the address
	 */
	public ByteData(long address){
		this.setAddress(address);
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	private void setAddress(long address) {
		this.address = address;
	}

	/**
	 * Gets the start address.
	 *
	 * @return the start address
	 */
	public long getStartAddress() {
		return address;
	}
	
	/**
	 * Gets the end address.
	 *
	 * @return the end address
	 */
	public long getEndAddress() {
		return address+dataBytes.size();
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(byte data) {
		dataBytes.add(new Byte(data));
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public byte [] getData() {
		byte [] b = new byte[dataBytes.size()];
		int i=0;
		for (Byte bld:dataBytes){
			b[i++]=bld.byteValue();
		}
		return b;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "Address: "+address+" Number of bytes:" + dataBytes.size()+" Data: "+dataBytes;
	}
}
