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

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.neuronrobotics.sdk.config.SDKBuildInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class ByteList.
 */
public class ByteList implements ISendable,Iterable<Byte> {
	
	/** The Constant useStaticBuffer. */
	private static final boolean useStaticBuffer = true;
	
	/** The static buffer size. */
	private int staticBufferSize = BowlerDatagram.MAX_PACKET_SIZE;
	
	/** The static buffer. */
	private byte [] staticBuffer = new byte[staticBufferSize];
	
	/** The static buffer read pointer. */
	private int staticBufferReadPointer = 0;
	
	/** The static buffer write pointer. */
	private int staticBufferWritePointer = 0;
	
	/** The store. */
	private List<Byte> store = new ArrayList<Byte>();

	/**
	 * Default constructor.
	 * Constructs a ByteList that has no data.
	 */
	public ByteList() {
//		if (SDKBuildInfo.isLinux() && SDKBuildInfo.isARM())
//			useStaticBuffer = true;
		if(isUseStaticBuffer()){
			//Log.debug("Starting Static ByteList");
			setStaticBufferSize(staticBufferSize);
		}else{
			staticBuffer=null;
		}
	}
	
	/**
	 * Initial value constructor.
	 * Constructs a ByteList and populates it with the byte 
	 * @param data the initial data to load into the bytelist after construction
	 */
	public ByteList(Byte data) {
		add(data);
	}
	
	/**
	 * Initial value constructor.
	 * Constructs a ByteList and populates it with the given byte array 
	 * @param data the initial data to load into the bytelist after construction
	 */
	public ByteList(byte[] data) {
		
		add(data);
	}

	/**
	 * Initial value constructor.
	 * Constructs a ByteList and populates it with the given string 
	 * @param data the initial data to load into the bytelist after construction
	 */
	public ByteList(String data) {
		add(data);
	}

	/**
	 * Initial value constructor.
	 * Constructs a ByteList and populates it with the given int
	 * @param data the initial data to load into the bytelist after construction
	 */
	public ByteList(int data) {
		add(data);
	}

	/**
	 * Instantiates a new byte list.
	 *
	 * @param data the data
	 */
	public ByteList(int[] data) {
		for(int i=0;i<data.length;i++){
			add(data[i]);
		}
	}
	
	/**
	 * Instantiates a new byte list.
	 *
	 * @param arguments the arguments
	 */
	public ByteList(BowlerDataType[] arguments) {
		for(int i=0;i<arguments.length;i++){
			add(arguments[i].getValue());
		}
	}

	/**
	 * Gets the static buffer byte count.
	 *
	 * @return the static buffer byte count
	 */
	private int getStaticBufferByteCount(){
		int w =staticBufferWritePointer;
		int r = staticBufferReadPointer;
		if(w>r){
			return w-r;
		}else if(w==r){
			return  0;
		}else{
			return (w+staticBuffer.length)-r;
		}
	}

	/**
	 * Adds a single byte to the bytelist and return the status of the additon.
	 *
	 * @param data the data
	 * @return if the addition was successful
	 */
	public synchronized boolean add(byte data) {
		if(isUseStaticBuffer()){
			if(staticBuffer == null){
				setStaticBufferSize(staticBufferSize);
			}
			if(getStaticBufferByteCount()>=(staticBuffer.length-1)){
				int newSize = staticBufferSize*2;
				Log.info("Bytelist static buffer overflow, resizing to "+newSize);
				byte tmpBuff[] = getBytes();
				// Double the buffer size
				setStaticBufferSize(newSize);
				//load the old data into newly resized buffer
				for(int i=0;i<tmpBuff.length;i++){
					staticBuffer[staticBufferWritePointer++] = tmpBuff[i];
					if(staticBufferWritePointer == (staticBuffer.length)){
						staticBufferWritePointer=0;
					}
				}
			}
			staticBuffer[staticBufferWritePointer++] = data;
			if(staticBufferWritePointer == (staticBuffer.length)){
				staticBufferWritePointer=0;
			}
			return true;
		}
		return store.add(data);
	}
	
	/**
	 * Adds an int to the bytelist cast as a single byte.
	 *
	 * @param data the data
	 * @return if the addition was successful
	 */
	public boolean add(int data) {
		return add((byte) data);
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#add(java.lang.Object)
	 */
	 
	/**
	 * Adds the.
	 *
	 * @param b the b
	 * @return true, if successful
	 */
	public boolean add(Byte b) {
		return add(b.byteValue());
	}
	
	/**
	 * Gets the bytes from a sendable and adds them to the bytelist.
	 *
	 * @param sendable the sendable
	 * @return true, if successful
	 */
	public boolean add(ISendable sendable) {
		return add(sendable.getBytes());
	}
	
	/**
	 * Adds each member of a given array to the ByteList. If the addition failed, then any added bytes are removed.
	 *
	 * @param data the data
	 * @return if the addition was successful
	 */
	public boolean add(String data) {
		return add(data.getBytes());
	}
	
	/**
	 * Adds each value of a given array to the ByteList. If the addition failed, then any added bytes are removed.
	 *
	 * @param data the data
	 * @return if the addition was successful
	 */
	public boolean add(byte [] data) {
		int index = 0;
		for(byte b : data) {
			if(!add(b)) {
				
				// remove all the added bytes if there was an error adding a byte
				for(int i = 0; i < index; i++) {
					remove(size());
				}
			}
			index++;
		}
		
		return true;
	}
	
	/**
	 * Adds each value of a given array to the ByteList. If the addition failed, then any added bytes are removed.
	 *
	 * @param data the data
	 * @return if the addition was successful
	 */
	public boolean add(int [] data) {
		int index = 0;
		for(int d : data) {
			if(!add(d)) {
				
				// remove all the added bytes if there was an error adding a byte
				for(int i = 0; i < index; i++) {
					remove(size());
				}
			}
			index++;
		}
		
		return true;
	}

	/**
	 * Adds each value of a given array to the ByteList. If the addition failed, then any added bytes are removed.
	 *
	 * @param bl the bl
	 * @return true, if successful
	 */
	public boolean add(Byte [] bl) {
		int index = 0;
		for(Byte b : bl) {
			if(!add(b)) {
				
				// remove all the added bytes if there was an error adding a byte
				for(int i = 0; i < index; i++) {
					remove(size());
				}
			}
			index++;
		}
		
		return true;
	}
	
	/**
	 * Splits a long into the given number of peices then adds them to the bytelist.
	 * This method should be avoided as it will be removed with 1.0 and add16 or add32 should be used instead.
	 *
	 * @param data the data
	 * @param split the split
	 * @return true, if successful
	 */
	@Deprecated
	public boolean add(long data, int split) {
		return add(split(data, split));
	}
	
	
	
	/**
	 * Adds len bytes from data starting at the offset.
	 *
	 * @param data the data
	 * @param len the len
	 * @param offset the offset
	 * @return true, if successful
	 */
	public boolean add(byte[] data, int len, int offset) {
		byte[] out = new byte[len];
		System.arraycopy(data, offset, out, 0, len);
		return add(out);
	}
	
	/**
	 * Add an integer as a 16 bit value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean addAs16(int value) {
		return add(convertTo16(value));
	}
	
	/**
	 * Add an integer as a 32 bit value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean addAs32(int value) {
		return add(convertTo32(value));
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	 
	/**
	 * Adds the all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean addAll(Collection<? extends Byte> c) {
		Byte b[] = new Byte[c.size()];
		return add(c.toArray(b));
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	 
	/**
	 * Adds the all.
	 *
	 * @param index the index
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean addAll(int index, Collection<? extends Byte> c) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Check if the bytelist is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		if(isUseStaticBuffer()){
			return getStaticBufferByteCount() == 0;
		}
		return store.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	 
	public byte[] getBytes() {
		return getBytes(0, 	size());
	}
	
	/**
	 * Get a specific amount of bytes.
	 *
	 * @param start Start byte
	 * @param len Desired Length
	 * @return 	The desired Bytes.
	 */
	public  synchronized  byte[] getBytes(int start, int len) {
		int sizeLocal =size();
		int requestedLen=len;
		// starting offset that is less than 0
		if(start < 0) {
			start = 0;
		}
		
		// length that is less than 0
		if(len < 0) {
			len = 0;
		}

		// starting offset is further then the last element
		if(start > sizeLocal) {
			return new byte [0];
		}
				
		// the ending position is 
		if(start+len > sizeLocal) {
			len = sizeLocal- start - 1;
		}
		if(len < 0) {
			throw new RuntimeException("Requesting more bytes then in the list, size="+sizeLocal+" start="+start+" len="+len+" requested="+ requestedLen);
		}

		byte out[] = new byte[len];
		
		if(isUseStaticBuffer()){
			int tmpRead = staticBufferReadPointer;
			//Allign the start pointer
			if(start != 0){
				for(int i=0;i<start;i++){
					tmpRead++;
					if(tmpRead== staticBuffer.length)
						tmpRead=0;
				}
			}
			//do the peek read
			for(int i=0;i<len;i++) {
				out[i] = staticBuffer[tmpRead++];
				if(tmpRead== staticBuffer.length)
					tmpRead=0;
			}
		}else{
			List<Byte> iter = store.subList(start, start+len);
			for(int i=0;i<len;i++) {
				out[i] = iter.get(i);
			}
		}
		return out;
	}
	
	/**
	 * Get Bytes after an index.
	 *
	 * @param index The index
	 * @return the bytes
	 */
	public byte[] getBytes(int index) {
		return getBytes(index, size()-index);
	}

	/**
	 * Get a specific byte.
	 *
	 * @param index The index of the byte
	 * @return the byte
	 */
	public byte getByte(int index) {
		int size = size();
		if(index < 0 || index >  size-1) {
			throw new IndexOutOfBoundsException("Requested : "+index+" have : "+size());
		}
		
		return getBytes(index,1)[0];
		
	}
	
	/**
	 * Get the size of the bytelist.
	 *
	 * @return the int
	 */
	public int size() {
		if(isUseStaticBuffer()){
			return getStaticBufferByteCount();
		}
		return store.size();
	}
	
	/**
	 * Removes bytes from the start of the buffer upto and including the given index. If the index is greater
	 * than the size of the buffer, null is returned otherwise the byte at the given index is returned.
	 *
	 * @param index the index
	 * @return the byte
	 */
	public Byte pop(int index) {
		if(index < 0) {
			throw new RuntimeException("Can not pop a list of length "+index);
		}
		
		if(index > size()) {
			clear();
			return null;
		}
		for(int i=0;i<index;i++)
			pop();
		
		return pop();
	}
	
	/**
	 * Removes the first byte from the buffer and returns it. If the buffer is empty, null is returned
	 *
	 * @return the byte
	 */
	public Byte pop() {	
		Byte b=null;;
		try {
			b = remove(0);
		} catch (Exception e) {
			b = null;
		}
		return b;
	}
	
	/**
	 * Pop a number of bytes off of the list.
	 * @param index	The last byte you want to pop
	 * @return	an array of bytes up to the index
	 */
	public byte[] popList(int index) {
		byte[] rtn;
		
		if(index < size()) {
			//The first param is inclusive, the second is exclusive
			rtn = getBytes(0, index);
			if(isUseStaticBuffer()){
				for(int i=0;i<index;i++){
					remove(0);
				}
			}else{
				//The first param is inclusive, the second is exclusive
				store = store.subList(index, store.size());
			}
		} else {
			rtn = getBytes();
			clear();
		}
		return rtn;
	}

	/**
	 * Utility method used to quickly return a single int as a byte array.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	public static byte[] wrap(int value) {
		byte [] b = {(byte) value};
		
		return b;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	 
	/**
	 * Clear.
	 */
	public void clear() {
		if(isUseStaticBuffer()){
			setStaticBufferSize(staticBufferSize);
			return;
		}
		store.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	 
	/**
	 * To array.
	 *
	 * @param <T> the generic type
	 * @param a the a
	 * @return the t[]
	 */
	public <T> T[] toArray(T[] a) {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.toArray(a);
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	 
	/**
	 * Contains.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean contains(Object o) {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	 
	/**
	 * Contains all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean containsAll(Collection<?> c) {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	 
	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the byte
	 */
	public Byte get(int index) {
		if(size()>0)	
			return getByte(index);
		Log.error("Requesting data out of an empty ByteList");
		throw new RuntimeException("Requesting data out of an empty ByteList");
	}
	
	/**
	 * Gets the unsigned.
	 *
	 * @param index the index
	 * @return the unsigned
	 */
	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public int getUnsigned(int index) {
		if(size()>0){
			int val =get(index);
			if(val<0)
				val+=256;
			return val;
		}
		Log.error("Requesting data out of an empty ByteList");
		throw new RuntimeException("Requesting data out of an empty ByteList");
	}
	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	 
	/**
	 * Index of.
	 *
	 * @param o the o
	 * @return the int
	 */
	public int indexOf(Object o) {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.indexOf(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	 
	public Iterator<Byte> iterator() {
		if(isUseStaticBuffer()){
			return new Iterator<Byte>() {
				int size = getStaticBufferByteCount();
				int readIndex=0;
				byte [] data = getBytes();
				@Override
				public boolean hasNext() {
					return readIndex != size;
				}

				@Override
				public Byte next() {
					// TODO Auto-generated method stub
					return data[readIndex++];
				}

				@Override
				public void remove() {
					readIndex++;
				}
			};
		}
		return store.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	 
	/**
	 * Last index of.
	 *
	 * @param o the o
	 * @return the int
	 */
	public int lastIndexOf(Object o) {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.lastIndexOf(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	 
	/**
	 * List iterator.
	 *
	 * @return the list iterator
	 */
	public ListIterator<Byte> listIterator() {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.listIterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	 
	/**
	 * List iterator.
	 *
	 * @param index the index
	 * @return the list iterator
	 */
	public ListIterator<Byte> listIterator(int index) {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.listIterator(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	 
	/**
	 * Removes the.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean remove(Object o) {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	 
	/**
	 * Removes the.
	 *
	 * @param index the index
	 * @return the byte
	 */
	public synchronized Byte remove(int index) {
		if(isUseStaticBuffer()){
			Byte b = staticBuffer[staticBufferReadPointer++];
			if(staticBufferReadPointer == staticBuffer.length){
				staticBufferReadPointer=0;
			}
			return b;
		}
		
		return store.remove(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	 
	/**
	 * Sub list.
	 *
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the list
	 */
	public List<Byte> subList(int fromIndex, int toIndex) {
		if(isUseStaticBuffer()){
			byte [] content = getBytes(fromIndex, toIndex-fromIndex);
			ArrayList<Byte> back = new ArrayList<Byte>();
			for(int i=0;i<content.length;i++){
				back.add(content[i]);
			}
			return back;
		}
		return store.subList(fromIndex, toIndex);
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	 
	/**
	 * To array.
	 *
	 * @return the object[]
	 */
	public Object[] toArray() {
		if(isUseStaticBuffer()){
			throw new UnsupportedOperationException();
		}
		return store.toArray();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(size() == 0) {
			return "";
		}
		String rtn = "";
		for(byte x : getBytes()){
			rtn += String.format("%02x ", x);
		}
		rtn = rtn.substring(0, rtn.length()-1);
		return rtn.toUpperCase();
	}
	
	/**
	 * As string.
	 *
	 * @return the string
	 */
	public String asString() {
		byte [] data = getBytes();
		if(data.length == 0) {
			return "";
		}
		String s="";
		
		int i=0;
		while(i<data.length ){
			if(data[i] != 0)
				s+=(char)data[i++];
			else
				return s;
		}
		return s;
	}

	/**
	 * Split.
	 *
	 * @param data the data
	 * @param split the split
	 * @return the byte[]
	 */
	public static byte[] split(long data, int split) {
		if(split < 1) {
			byte[] b = {};
			return b;
		}
		
		if(split > 8) {
			split = 8;
		}
		
        byte[] bArray = new byte[8];
        byte[] rtn = new byte[split];
        ByteBuffer bBuffer = ByteBuffer.wrap(bArray);
        LongBuffer lBuffer = bBuffer.asLongBuffer();
        lBuffer.put(0, data);
        
        System.arraycopy(bArray, 8-split, rtn, 0, split);
        
        return rtn;
	}
	
	/**
	 * Converts a 4 byte array of unsigned bytes to an long.
	 *
	 * @param b an array of 4 unsigned bytes
	 * @return a long representing the unsigned int
	 */
	public static final int convertToInt(byte[] b) 
	{
		int i=0;
		i=(int) convertToInt(b,false);
	    return i;
	}
	
	/**
	 * toInt
	 *  Takes a ByteList and turns it into the int that the stream represents.
	 *  Assumes the 0th element is the most significant byte
	 *  Assumes the entire stream is one number
	 * @param b The byte array with the byte data in it
	 * @param Signed If the stream should be treated as a signed integer
	 * @return a long representing the value in the stream
	 */

	public static int convertToInt(byte[] b,boolean Signed){
		
		long bytes = b.length;
		if(bytes>4)
			throw new RuntimeException("Ints can only have 2 or 4 bytes");
		long out = 0;
		long tmp = 0;
		for (int i=0;i<bytes;i++){
			tmp = rawByteToInt(b[i]);
			int power =(int) ((bytes-i-1)*8);
			long val = (long) (tmp * Math.pow(2, power   ));
			out+=val;
		}
		if (Signed){
			// This converts to a signed long
			long power = bytes*8;
			long max_pos_val =(long) Math.pow(2,power-1);
			long sub_val = (long) Math.pow(2,power);
			long abs_val=0;
            if (out>max_pos_val){
            	abs_val=(sub_val-out);
                out=(int) (-1*abs_val);
            }
		}
		int ret = (int) out;
		return ret;
	}

	/**
	 * Convert to16.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	public static byte[] convertTo16(int value) {
		byte b [] = new byte[2];
		
		b[0] = (byte)(value >> 8);
		b[1] = (byte)(value);
		
		return b;
	}
	
	/**
	 * Convert to32.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	public static byte[] convertTo32(int value) {
		byte b [] = new byte[4];
		
		b[0] = (byte)(value >> 24);
		b[1] = (byte)(value >> 16);
		b[2] = (byte)(value >> 8);
		b[3] = (byte)(value);
		
		return b;
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	 
	/**
	 * Adds the.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 */
	public void add(int arg0, Byte arg1) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	 
	/**
	 * Removes the all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	 
	/**
	 * Retain all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	 
	/**
	 * Sets the.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 * @return the byte
	 */
	public Byte set(int arg0, Byte arg1) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Insert.
	 *
	 * @param index the index
	 * @param val the val
	 */
	public void insert(int index,byte val){
		if(isUseStaticBuffer()){
			byte [] current = getBytes();
			clear();
			for(int i=0;i<current.length;i++){
				if(i==index){
					add(val);
				}
				add(current[i]);
			}
		}else{
			store.add(index, val);
		}
	}
	
	/**
	 * Raw byte to int.
	 *
	 * @param b the b
	 * @return the int
	 */
	public static int rawByteToInt(byte b){
		int tmp =(int)b;
		if (tmp < 0){
			// This solves the Java signedness problem of "bytes"
			tmp +=256;
		}
		return tmp;
	}
	
	/**
	 * Pop list.
	 *
	 * @param off the offset from which to start
	 * @param len the number of bytes to pop
	 * @return the byte[]
	 */
	public byte[] popList(int off, int len) {
		if (size() >= off+len) {
			byte [] ret = new byte[len];
			for(int i=0;i<len;i++) {
				ret[i]=remove(off);
			}
			return ret;
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * Checks if is use static buffer.
	 *
	 * @return true, if is use static buffer
	 */
	public static boolean isUseStaticBuffer() {
		return useStaticBuffer;
	}

	/**
	 * Sets the use static buffer.
	 *
	 * @param useStaticBuffer the new use static buffer
	 */
	public static void setUseStaticBuffer(boolean useStaticBuffer) {
		//ByteList.useStaticBuffer =true;
	}

	/**
	 * Gets the static buffer size.
	 *
	 * @return the static buffer size
	 */
	public int getStaticBufferSize() {
		return staticBufferSize;
	}

	/**
	 * Sets the static buffer size.
	 *
	 * @param staticBufferSize the new static buffer size
	 */
	public void setStaticBufferSize(int staticBufferSize) {
		this.staticBufferSize = staticBufferSize;
		staticBuffer = new byte[getStaticBufferSize()];
		staticBufferReadPointer = 0;
		staticBufferWritePointer = 0;	
		store = null;
	}
}
