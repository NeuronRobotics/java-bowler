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
package com.neuronrobotics.sdk.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NRSerialPort;
import gnu.io.PortInUseException;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MissingNativeLibraryException;
import com.neuronrobotics.sdk.common.SDKInfo;

/**
 * SerialConnection manages a connection to a serial port on the host system. This class is responsible for
 * abstracting all of the aspects of a serial connection including:
 * <ul>
 * <li>opening and closing a connection</li>
 * <li>setting the baudrate</li>
 * <li>sending data and reading data both syncronously and asyncronously</li>
 * </ul>
 * <p>
 * SerialConnection extends SerialPortEventListener to use the RXTX framework for receiving serial 
 * communications efficiently. Remember to disconnect whenever reading and writing to the connection is not
 * necessary as a this class will continue to run a thread to wait for incoming data.
 * <p>
 *  
 */
public class SerialConnection extends BowlerAbstractConnection {
	private int sleepTime = 5000;
	private int pollTimeoutTime = 5;
	
	
	private String port=null;
	private int baud = 115200;
	
	private NRSerialPort serial;
	
	/**
	 * Default Constructor.
	 * <p>
	 * Using this constructor will require that at least the port be set later on. 
	 * 
	 * The baudrate will default to 115200bps.
	 */
	public SerialConnection() {
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
	}
	
	/**
	 * Class Constructor for a SerialConnection with a given port.
	 * 
	 * The baudrate will default to 115200bps.
	 * 
	 * @param port the port to connect to (i.e. COM6 or /dev/ttyUSB0)
	 */
	public SerialConnection(String port) {
		setPort(port);	
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
	}
	
	/**
	 * Class Constructor for a SerialConnection with a given port and baudrate.
	 * 
	 * @param port the port to connect to (i.e. COM6 or /dev/ttyUSB0)
	 * @param baud the baudrate to use (i.e. 9600 or 115200)
	 */
	public SerialConnection(String port, int baud) {
		setPort(port);
		setBaud(baud);
		setPollTimeoutTime(pollTimeoutTime);
		setSleepTime(sleepTime);
	}
	
	/**
	 * Set the port to use (i.e. COM6 or /dev/ttyUSB0)
	 * 
	 * @param port the serial port to use
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * Get the port to use (i.e. COM6 or /dev/ttyUSB0)
	 * 
	 * @return
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Set the baudrate for communications with the serial port. Standard baudrates should be used typically
	 * unless otherwise specififed by the device. The default system baudrate is 115200
	 * 
	 * Typical baudrates
	 * <ul>
	 * <li>110</li>
	 * <li>300</li>
	 * <li>600</li>
	 * <li>1200</li>
	 * <li>2400</li>
	 * <li>4800</li>
	 * <li>9600</li>
	 * <li>14400</li>
	 * <li>19200</li>
	 * <li>28800</li>
	 * <li>38400</li>
	 * <li>56000</li>
	 * <li>57600</li>
	 * <li>115200</li>
	 * </ul>
	 * 
	 * @param baud 
	 */
	public void setBaud(int baud) {
		this.baud = baud;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		if(isConnected()) {
			Log.error(port + " is already connected.");
			return true;
		}
		
		try 
		{
			serial = new NRSerialPort(getPort(), baud);
			serial.connect();	
			setDataIns(new DataInputStream(serial.getInputStream()));
			setDataOuts(new DataOutputStream(serial.getOutputStream()));
			setConnected(true);
		}catch(UnsatisfiedLinkError e){
			throw new MissingNativeLibraryException(e.getMessage());
        }catch (Exception e) {
			Log.error("Failed to connect on port: "+port+" exception: ");
			e.printStackTrace();
			setConnected(false);
		}
		
		if(isConnected()) {
			serial.notifyOnDataAvailable(true);
		}
		return isConnected();	
	}

	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		if(isConnected())
			Log.info("Disconnecting Serial Connection");
		try{
			super.disconnect();
			try{
				serial.disconnect();
			}catch(Exception e){
				//e.printStackTrace();
				//throw new RuntimeException(e);
			}
			serial = null;
			setConnected(false);
		} catch(UnsatisfiedLinkError e) {
			throw new MissingNativeLibraryException(e.getMessage());
        }
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return port;
	}
	
	public static List<String> getAvailableSerialPorts() {
		ArrayList<String> back = new  ArrayList<String>();
		for(String s:NRSerialPort.getAvailableSerialPorts()){
			back.add(s);
		}
        return back;
    }

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	 */
	@Override
	public boolean reconnect() {
		if(!isConnected())
			return false;
		else
			return true;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
	 */
	@Override
	public boolean waitingForConnection() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
