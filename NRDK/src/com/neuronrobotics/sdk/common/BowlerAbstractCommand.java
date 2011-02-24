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
 * This class encapsulates the generation of a Bowler RPC. 
 * Each command should represent a unique RPC.
 * 
 * @author rbreznak
 *
 */
public abstract class BowlerAbstractCommand implements ISendable {
	
	/** The op code. */
	private String opCode;
	
	/** The method. */
	private BowlerMethod method;
	private ByteList data = new ByteList();
	
	/**
	 * Get the data that will be used to call the RPC (i.e. parameters for the RPC).
	 * Each instance of a BAC should determine the correct method for generating this data.
	 * 
	 * @return the parameters for the RPC
	 */
	public byte[] getCallingData() {
		return data.getBytes();
	}
	
	/**
	 * Set the operation code string. To conform to the Bowler standard, the string should be 4 characters. 
	 * 
	 * @param opCode the RPC operation code to be implemented.
	 */
	public void setOpCode(String opCode) {
		if(opCode.length() != 4) {
			throw new InvalidDataLengthException();
		}
		
		this.opCode = opCode;
	}
	
	/**
	 * Returns the operation code.
	 * 
	 * @return the RPC operation code
	 */
	public String getOpCode() {
		return opCode;
	}

	/**
	 * Set the method that the command will use for execution.
	 *
	 * @param method the new method
	 */
	public void setMethod(BowlerMethod method) {
		this.method = method;
	}

	/**
	 * Get the method that the command will use for execution.
	 *
	 * @return the method used
	 */
	public BowlerMethod getMethod() {
		return method;
	}
	
	/**
	 * Get the total size of the command including opcode and calling data. 
	 * 
	 * @return the full size of the command
	 */
	public byte getLength() {
		return (byte) (opCode.length() + getCallingData().length);
	}
	
	/**
	 * Determine if the return response was successful; throw an InvalidResponseExpection otherwise. Commands
	 * with more complicated validation should override this method and provide more specific checking.
	 *
	 * @param data the data
	 * @return the incoming response
	 * @throws InvalidResponseException the invalid response exception
	 */
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		if (data==null){
			// TODO: Correct this with JSDK-8
			 throw new InvalidResponseException("No response from device");
		}
		// throws 
		if( data.getRPC().equals("_err")) {
			Integer zone=Integer.valueOf(data.getData().getByte(0));
			Integer section=Integer.valueOf(data.getData().getByte(1));
			System.err.println("Failed!!\n"+data);
			switch(zone) {
			default:
				throw new InvalidResponseException("Unknown error. (" + zone + " " + section + ")");
			case 0:
				switch(section) {
				default:
					throw new InvalidResponseException("Unknow error in the communications stack. (" + zone + " " + section + ")");
				case 0x7f:
					throw new InvalidResponseException("The method provided is invalid.");
				case 0:
					throw new InvalidResponseException("The packet was not sent syncronously.");
				case 1:
					throw new InvalidResponseException("The RPC sent in undefined with GET method.");
				case 2:
					throw new InvalidResponseException("The RPC sent in undefined with POST method.");
				case 3:
					throw new InvalidResponseException("The RPC sent in undefined with CRITICAL method.");
				}
			case 85:
				switch(section) {
				default:
					throw new InvalidResponseException("Unknown co-processor error. (" + zone + " " + section + ")");
				case 1:
				case 2:
					throw new InvalidResponseException("The co-processor did not respond.");
				}
			case 1:
				switch(section) {
				default:
					throw new InvalidResponseException("Unknow error in the GET parser. (" + zone + " " + section + ")");
				case 0:
					throw new InvalidResponseException("Error with GET parsing, mostlikely the channel mode does not have a GET functionality.");
				}
			case 2:
				switch(section) {
				default:
					throw new InvalidResponseException("Unknow error in the POST parser. (" + zone + " " + section + ")");
				case 0:
					throw new InvalidResponseException("Failed to properly set the value to the channel.");
				case 1:
					throw new InvalidResponseException("Failed to properly set the mode / the given mode type is unknown.");
				case 2:
					throw new InvalidResponseException("Failed to set the input channel value.");
				}
			case 3:
			case 6:
				switch(section) {
				default:
					throw new InvalidResponseException("Unknow error in the CRITICAL parser. (" + zone + " " + section + ")");
				case 0:
					throw new InvalidResponseException("Failed to configure channel.");
				case 1:
					throw new InvalidResponseException("Failed to configure PID channel.");
				case 3:
					throw new InvalidResponseException("Invalid name string, either too short or too long");
				}
			}
		}
		
		return data;
	}
	
	public static BowlerAbstractCommand parse(BowlerDatagram data) {
		return new BowlerAbstractCommand() {};
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		ByteList buffer = new ByteList();
		buffer.add(opCode.getBytes());
		buffer.add(getCallingData());
		return buffer.getBytes();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(getBytes().length == 0) {
			return "";
		}
		String rtn = "";
		for(byte x : getBytes()){
			rtn += String.format("%02x ", x);
		}
		rtn = rtn.substring(0, rtn.length()-1);
		return rtn.toUpperCase();
	}

	public void setData(ByteList data) {
		this.data = data;
	}

	public ByteList getCallingDataStorage() {
		return data;
	}

}
