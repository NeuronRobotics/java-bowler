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
package com.neuronrobotics.sdk.dyio;

import java.util.ArrayList;
import java.util.Collection;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

import com.neuronrobotics.sdk.commands.bcs.io.AsyncMode;
import com.neuronrobotics.sdk.commands.bcs.io.AsyncThreshholdEdgeType;
import com.neuronrobotics.sdk.commands.bcs.io.ConfigAsyncCommand;
import com.neuronrobotics.sdk.commands.bcs.io.GetChannelModeCommand;
import com.neuronrobotics.sdk.commands.bcs.io.SetAllChannelValuesCommand;
import com.neuronrobotics.sdk.commands.bcs.io.setmode.SetChannelModeCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.DyPID.ConfigureDynamicPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.safe.SafeModeCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.dyio.InfoCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.dyio.InfoFirmwareRevisionCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.dyio.PowerCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerRuntimeException;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.common.ISendable;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.config.SDKBuildInfo;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.IPIDControl;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.util.ThreadUtil;

/**
 * The DyIO class is an encapsulation of all of the functionality of the DyIO module into one object. This 
 * object has one connection to one DyIO module and wraps all of the commands in an accessible API. 
 * @author Kevin Harrington, Robert Breznak
 */
public class DyIO extends BowlerAbstractDevice implements IPIDControl,IConnectionEventListener {

	private ArrayList<IDyIOEventListener> listeners = new ArrayList<IDyIOEventListener>();
	private ArrayList<DyIOChannel> channels = new ArrayList<DyIOChannel>();
	
	private byte [] firmware = {0, 0, 0};
	private String info = "Unknown";
	
	private DyIOPowerState bankAState;
	private DyIOPowerState bankBState;
	private double batteryVoltage = 0;
	
	private boolean cachedMode=false;
	private boolean muteResyncOnModeChange=false;
	private static boolean checkFirmware=true;
	private boolean resyncing = false;
	private boolean haveBeenSynced =false;
	
	private GenericPIDDevice pid = new GenericPIDDevice();
	/**
	 * Default Constructor.
	 * Builds a generic DyIO that has the default broadcast address and no default connection.
	 */
	public DyIO() {
		setAddress(new MACAddress(MACAddress.BROADCAST));
		pid.setAddress(new MACAddress(MACAddress.BROADCAST));
	}

	/**
	 * Builds a DyIO that has the given address and no default connection.
	 * @param address
	 */
	public DyIO(MACAddress address) {
		setAddress(address);
		pid.setAddress(address);
	}
	
	/**
	 * Builds a DyIO with the given connection and the broadcast address.
	 * @param connection
	 */
	public DyIO(BowlerAbstractConnection connection) {
		setAddress(new MACAddress(MACAddress.BROADCAST));
		setConnection(connection);
		pid.setConnection(connection);
		pid.setAddress(new MACAddress(MACAddress.BROADCAST));
	}

	/**
	 * Builds a DyIO with the given address and connection.
	 * @param address
	 * @param connection
	 */
	public DyIO(MACAddress address, BowlerAbstractConnection connection) {
		setAddress(address);
		setConnection(connection);
		pid.setConnection(connection);
		pid.setAddress(address);
	}

	/**
	 * Returns the DyIO channel associated with a channel number.
	 * 
	 * @param channel  integer representing the index of the channel
	 *            - a channel number
	 * @return
	 */
	public DyIOChannel getChannel(int channel) {
		validateChannel(channel);
		return getInternalChannels().get(channel);
	}
	
	/**
	 * This method sets the I/O mode of a specific channel. the DyIO will be sent a packet to update its mode in firmware
	 * If the mode is the same as the current mode, nothing happens
	 * If the mode is different from the previous mode, a mode change listeners will be fired
	 * Asynchronus state will be unaffected
	 * 
	 * @param channel integer representing the index of the channel
	 * @param mode    the DyIOChannelMode that this channel should be set to
	 * @return true for success
	 */
	public boolean setMode(int channel, DyIOChannelMode mode) {
		return getChannel(channel).setMode(mode);
	}

	/**
	 * 
	 * This method sets the I/O mode of a specific channel. the DyIO will be sent a packet to update its mode in firmware
	 * If the mode is the same as the current mode, nothing happens
	 * If the mode is different from the previous mode, a mode change listeners will be fired
	 * 
	 * @param channel integer representing the index of the channel
	 * @param mode    the DyIOChannelMode that this channel should be set to
	 * @param async	  Forces the mode into or out of async mode
	 * @return true for success
	 */
	public boolean setMode(int channel, DyIOChannelMode mode, boolean async) {
		return getChannel(channel).setMode(mode, async);
	}
	
	/**
	 * THis method returns the state of the given channels I/O mode
	 * 
	 * @param channel integer representing the index of the channel
	 * @return the current mode
	 */
	public DyIOChannelMode getMode(int channel) {
		return getChannel(channel).getMode();
	}
	
	/**
	 * This method is a simple value set for a DyIO channel. This method is unit-less and will clip data to fit the 
	 * channel modes requirements. 
	 * 
	 * @param channel integer representing the index of the channel
	 * @param value   Unit-less value to set to the DyIO's channel
	 * @return  true for success
	 */
	public boolean setValue(int channel, int value) {
		return getChannel(channel).setValue(value);
	}
	
	/**
	 * This method sets the value of a channel with a pre-packaged ByteList of the data to send. 
	 * Since the bytelist is pre-packaged, the data will be sent to the DyIO as it is packet into 
	 * the ByteList with NO VERIFICATION.
	 * 
	 * @param channel integer representing the index of the channel
	 * @param value	  ByteList of the data to send to the DyIO
	 * @return  true for success
	 */
	public boolean setValue(int channel, ByteList value) {
		return getChannel(channel).setValue(value);
	}

	/**
	 * This method is used to get the value of a given channel. The data units will be determined by 
	 * DyIO channel mode, and so should be treated by this method as unit-less.
	 * 
	 * @param channel integer representing the index of the channel
	 * @return		  The DyIO channels current value
	 */
	public int getValue(int channel) {
		return getChannel(channel).getValue();
	}
	
	/**
	 * Gets the DyIO's internally stored info string. This is a 16 byte string that can be stored on 
	 * the DYIO and used as a human-readable, user setable, identifier.
	 * 
	 * @return The current string
	 */
	public String getInfo(){
		BowlerDatagram response = send(new InfoCommand());
		if (response != null) {
			info = response.getData().asString();
		}
		return info;
	}
	
	/**
	 * Sets the DyIO's internally stored info string. This is a 16 byte string that can be stored on 
	 * the DYIO and used as a human-readable, user setable, identifier.
	 * 
	 * @param info The String identifier to be stored by the DyIO.
	 */
	public void setInfo(String info){
		if(send(new InfoCommand(info)) == null) {
			return;
		}
		this.info = info;
	}
	
	/**
	 * This method gets the 3 byte firmware revision code. THis code is used to determine compatibility between 
	 * the version of firmware on the DyIO and the version of the NRDK being used to communicate with it.
	 * 
	 * @return The firmware version data
	 */
	public byte [] getFirmwareRev(){
		return firmware;
	}
	
	/**
	 * This method turns the firmware version number into a formatted string for clear display
	 *  
	 * @return Firmware version string. 
	 */
	public String getFirmwareRevString(){
		String revFmt = "%02d.%02d.%03d";
		return "Firmware Revision: v" + String.format(revFmt, firmware[0], firmware[1], firmware[2]);
	}

	/**
	 * This method creates a new collection and populates it with the DyIO channel objects. 
	 * 
	 * @return a collection of DyIOChannel objects
	 */
	public Collection<DyIOChannel> getChannels() {
		ArrayList<DyIOChannel> c = new ArrayList<DyIOChannel>();
		for(DyIOChannel chan:getInternalChannels() ) {
			//System.out.println(this.getClass()+" Adding channel: "+chan.getChannelNumber()+" as mode: "+chan.getMode());
			c.add(chan);
		}
		return c;
	}
	
	/**
	 * This method synchronizes the DyIO channel mode from the DyIO module with this DyIO channel object
	 * This will actively query the DyIO for this information
	 * 
	 * @param channel  integer representing the index of the channel
	 */
	public void resync(int channel) {
		getChannel(channel).resync(false);
	}
	
	/**
	 * This static method can be called before connection a DyIO object to disable the firmware verification step
	 * This can be used to allow older versions of the DyIO firmware to be used with newer NRDK versions. 
	 */
	public static void disableFWCheck() {
		checkFirmware=false;
	}
	
	/**
	 * Sync the state cache with the live device. 
	 * This method will query the device for its firmware revision and its info string. 
	 * The default opperation will be to throw a DyIOFirmwareOutOfDateException is the firmware version does not match
	 * the NRDK build version. This can be overridden if DyIO.disableFWCheck() is called BEFORE connection.
	 */
	public void checkFirmwareRev()throws DyIOFirmwareOutOfDateException{
		if(checkFirmware) {
			int[] sdkRev = SDKBuildInfo.getBuildInfo(); 
			
			for(int i=0;i<3;i++){
				if(firmware[i] != sdkRev[i]){
					DyIOFirmwareOutOfDateException e = new DyIOFirmwareOutOfDateException( 	"\nNRDK version = "+new ByteList(sdkRev)+
																"\nDyIO version = "+ new ByteList(firmware)+
																"\nTry updating your firmware using the firmware update instructions from http://neuronrobotics.com/");
					//e.printStackTrace();
					throw e;
				}
			}
		}else{
			Log.debug("Not checking firmware version for DyIO");
		}
	}

	
	/**
	 * This method re-synchronizes the entire state of the DyIO object with the DyIO module. 
	 * The Firmware version is checked
	 * The info string is checked
	 * All channel modes are updated
	 * 
	 * @return true if success
	 */
	public boolean resync() {
		if(getConnection() == null) {
			return false;
		}
		if(!getConnection().isConnected()) {
			return false;
		}
		if(isResyncing())
			return true;
		
		setResyncing(true);
		setMuteResyncOnModeChange(true);
		Log.info("Re-syncing...");
		BowlerDatagram response;
		try{
			if (!haveFirmware()){
				getBatteryVoltage(true);
				firmware = getRevisions().get(0).getBytes();
			}
			checkFirmwareRev();
			if(info.contains("Unknown")){
				response = send(new InfoCommand());
				if (response != null) {
					info = response.getData().asString();
				}
			}
			
		}catch (InvalidResponseException e){
			checkFirmwareRev();
		}
		
		try {
			response = send(new GetChannelModeCommand());
		} catch (Exception e) {
			if (getInternalChannels().size()==0){
				Log.error("Initilization failed once, retrying");
				try{
					response = send(new GetChannelModeCommand());
				}catch(Exception e2){
					e2.printStackTrace();
					throw new DyIOCommunicationException("DyIO failed to report during initialization. Could not determine DyIO configuration: "+e2.getMessage());
				}
			}
			else
				return false;
		}
		if(response == null)
			checkFirmwareRev();
		//if(getAddress().equals(new MACAddress(MACAddress.BROADCAST))) {
			setAddress(response.getAddress());
		//}
		
		if (response.getData().size()<24)
			throw new DyIOCommunicationException("Not enough channels, not a valid DyIO"+response.toString());
		for (int i = 0; i < response.getData().size(); i++){
			DyIOChannelMode cm = DyIOChannelMode.get(response.getData().getByte(i));
			boolean editable = true;
			if(cm == null) {
				cm = DyIOChannelMode.DIGITAL_IN;
				editable = false;
				try {
					Log.error("Mode get failed, setting to default");
					send(new SetChannelModeCommand(i, cm));
				} catch(Exception e) {
					throw new DyIOCommunicationException("Setting a pin to Digital In failed");
				}
			}
			try{
				getInternalChannels().get(i).update(this, i, cm, editable);
				//System.out.println("Updating channel: "+i);
			}catch(IndexOutOfBoundsException e){
				//System.out.println("New channel "+i);
				getInternalChannels().add(new DyIOChannel(this, i, cm, editable));
				DyIOChannel dc =getInternalChannels().get(i);
				dc.fireModeChangeEvent(dc.getCurrentMode());
			}
		}
//		for (int i = 0; i < response.getData().size(); i++){
//			DyIOChannel dc =getInternalChannels().get(i);
//			dc.fireModeChangeEvent(dc.getCurrentMode());
//		}
		if (getInternalChannels().size()==0)
			throw new DyIOCommunicationException("DyIO failed to report during initialization");
		setMuteResyncOnModeChange(false);
		haveBeenSynced =true;
		setResyncing(false);
		return true;
	}
	
	/**
	 * Check to see if the firmware has been checked yet
	 * @return true if already checked
	 */
	private boolean haveFirmware() {
		if (firmware[0]==0 && firmware[1]==0 && firmware[2]==0)
			return false;
		else 
			return true;
	}

	/**
	 * Add an IDyIOEventListener that will be contacted with an IDyIOEvent on
	 * each incoming data event.
	 * 
	 * DyIO event listeners are used to get information from all DyIO events. 
	 * This is how to access the Power events:
	 * DyIO power switch change events
	 * DyIO external power voltage change events
	 * 
	 * 
	 * @param l
	 */
	public void addDyIOEventListener(IDyIOEventListener l) {
		if(listeners.contains(l)) {
			return;
		}
		
		listeners.add(l);
	}
	
	/**
	 * Removes an IDyIOEventListener from being contacted on each new
	 * IDyIOEvent.
	 * 
	 * @param l
	 */
	public void removeDyIOEventListener(IDyIOEventListener l) {
		if(!listeners.contains(l)) {
			return;
		}
		
		listeners.remove(l);
	}
	
	/**
	 * Clears out all current IDyIOEventListeners.
	 */
	public void removeAllDyIOEventListeners() {
		listeners.clear();
	}
	
	/**
	 * Contact all of the listeners with the given event.
	 * 
	 * @param e
	 *            - the event to fire to all listeners
	 */
	public void fireDyIOEvent(IDyIOEvent e) {
		//System.out.println("DyIO Event: "+e);
		for(IDyIOEventListener l : listeners) {
			l.onDyIOEvent(e);
		}
	}
	
	/**
	 * Validates a that a given channel is in the proper range.
	 * 
	 * @param channel  integer representing the index of the channel
	 */
	protected void validateChannel(int channel) {
		int syncs=0;
//		while(isResyncing()){
//			ThreadUtil.wait(100);
//		}
		if(!haveBeenSynced)
			return;
		while(getInternalChannels().size() == 0){
			Log.error("Valadate will fail, no channels, resyncing");
			resync();
			syncs++;
			if(syncs>5){
				throw new DyIOCommunicationException("DyIO failed to report number of channels");
			}
			ThreadUtil.wait(200);
			
		}
		if (channel < 0 || channel > getInternalChannels().size()) {
			throw new IndexOutOfBoundsException("DyIO channels must be between 0 and " + getInternalChannels().size()+" requested:"+channel);
		}
	}
	

	
	/**
	 * This method returns the current state of the DyIO objects cache/flush system for the DyIO channel values.
	 * @return true if cache/flush mode is enabled
	 */
	public boolean getCachedMode() {
		return cachedMode;
	}
	/**
	 * This method enables the DyIO cache/flush system. When enabled, the system will interrupt all set value method calls and prevent them 
	 * from sending packets to the DyIO. The channels will then need to either be flushed individually, or as a group. When the entire group 
	 * is flushed, all channel values are set with a single packet allowing for co-ordinated motion. 
	 * All channels will read the current state and set it as the cache value on the event of enabling the cache/flush mode
	 * @param mode true to enable cache/flush mode, false to disable.
	 */
	public void setCachedMode(boolean mode) {
		cachedMode=mode;
		for(DyIOChannel d:getInternalChannels()) {
			d.setCachedMode(mode);
			if(mode)
				try {
					d.setCachedValue(d.getValue());
				}catch(Exception e){
					d.setCachedValue(128);
				}
		}
	}
	/**
	 * This method will flush the DyIO cache for all channels. All channel values as stored by setting the value from code, or the value 
	 * stored at the time that the cache/flush mode was enabled. THis method will flush all 24 channel values in one packet allowing for 
	 * co-ordinated motion.
	 * @param time in seconds
	 */
	public void flushCache(double seconds) {
		//System.out.println("Updating all channels");
		int [] values = new int[getInternalChannels().size()];
		int i=0;
		for(DyIOChannel d:getInternalChannels()) {
			values[i++]=d.getCachedValue();
			//d.flush();
		}
		for(int i1=0;i1<5;i1++) {
			try {
				send(new SetAllChannelValuesCommand(seconds,values));
				return;
			}catch (InvalidResponseException e1) {
				System.err.println("Failed to update all, retrying");
			}
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#isAvailable()
	 */
	public boolean isAvailable() throws InvalidConnectionException {
		if(getConnection() == null)
			return false;
		return getConnection().isConnected();
	
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAllResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAllResponse(BowlerDatagram data) {

		pid.onAllResponse(data);
	}
	
	/**
	 * This method returns the bank switch state of bank A (0-11)
	 * This state is updated asynchronously by the DyIOEventListener
	 * @return the current state
	 */
	public DyIOPowerState getBankAState() {
		return bankAState;
	}
	/**
	 * This method returns the bank switch state of bank B (12-23)
	 * This state is updated asynchronously by the DyIOEventListener
	 * @return the current state
	 */
	public DyIOPowerState getBankBState() {
		return bankBState;
	}
	
	/**
	 * THis method will return the current voltage on the battery connected to the DyIO external power connector. 
	 * @param refresh true if you want to query the device, false to just get the cached value from the last async. 
	 * @return the voltage of the battery in Volts
	 */
	public double getBatteryVoltage(boolean refresh){
		if(refresh) {
			BowlerDatagram data = send(new PowerCommand());
			powerEvent(data);
		}
		return batteryVoltage;
	}
	/**
	 * Parses a datagram into the power event data
	 * @param data
	 */
	private void powerEvent(BowlerDatagram data) {
		//System.out.println("Updating Power state");
		ByteList bl = data.getData();
		if(bl.size() != 4) {
			return;
		}
		batteryVoltage = ((double)(ByteList.convertToInt(bl.getBytes(2, 2),false)))/1000.0;
		bankAState = DyIOPowerState.valueOf(bl.get(0),batteryVoltage);
		bankBState = DyIOPowerState.valueOf(bl.get(1),batteryVoltage);
		
		fireDyIOEvent(new DyIOPowerEvent(bankAState, bankBState, batteryVoltage));
		return;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse
	 */
	public void onAsyncResponse(BowlerDatagram data) {
		if(!haveBeenSynced){
			return;
		}
		Log.info("<< Async\n"+data.toString());
		if(data.getRPC().equals("_pwr")) {
			powerEvent(data);
		}
		if(data.getRPC().equals("gchv")) {
			ByteList bl = data.getData();
			
			Byte b = bl.pop();
			if(b == null) {
				return;
			}
			DyIOChannel c = getChannel(b);
			c.fireChannelEvent(new DyIOChannelEvent(c, bl));
			return;
		}else{
			IDyIOEvent e = new DyIOAsyncEvent(data);
			fireDyIOEvent(e);
		}
		

		pid.onAsyncResponse(data);
	}

	
	/**
	 * This method sends a packet to the DyIO module to set up the linking between a DyIO input channel and a DyIO output channel to a PID controller
	 * Inputs are read as the input to the PID calculation
	 * Outputs are set as a result of the PID calculation
	 * @param config the configuration data object
	 * @return true if success
	 */
	public boolean ConfigureDynamicPIDChannels(DyPIDConfiguration config){
		return send(new ConfigureDynamicPIDCommand(config))!=null;
	}
	/**
	 * This method gets the current state of the DyIO channel configuration of a given PID group
	 * @param group the index of the PID group to get information about
	 * @return
	 */
	public DyPIDConfiguration getDyPIDConfiguration(int group){
		BowlerDatagram conf = send(new ConfigureDynamicPIDCommand( group) );
		DyPIDConfiguration back=new DyPIDConfiguration(conf);
		return back;
	}
	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#ResetPIDChannel
	 */
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		return pid.ResetPIDChannel(group, valueToSetCurrentTo);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#ResetPIDChannel
	 */
	public boolean ResetPIDChannel(int group) {
		return pid.ResetPIDChannel(group);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#SetPIDSetPoint
	 */
	public boolean SetPIDSetPoint(int group,int setpoint, double seconds){
		return pid.SetPIDSetPoint(group, setpoint,seconds);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#SetAllPIDSetPoint
	 */
	public boolean SetAllPIDSetPoint(int []setpoints, double seconds){
		return pid.SetAllPIDSetPoint(setpoints,seconds);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#GetPIDPosition
	 */
	public int GetPIDPosition(int group) {
		return pid.GetPIDPosition(group);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#GetAllPIDPosition
	 */
	public int [] GetAllPIDPosition() {
		return pid.GetAllPIDPosition();
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#ConfigurePIDController
	 */
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return pid.ConfigurePIDController(config);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#getPIDConfiguration
	 */
	public PIDConfiguration getPIDConfiguration(int group) {
		return pid.getPIDConfiguration(group);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#addPIDEventListener
	 */
	public void addPIDEventListener(IPIDEventListener l) {
		pid.addPIDEventListener(l);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#removePIDEventListener
	 */
	public void removePIDEventListener(IPIDEventListener l) {
		pid.removePIDEventListener(l);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#flushPIDChannels
	 */
	@Override
	public void flushPIDChannels(double time) {
		pid.flushPIDChannels(time);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#getPIDChannel
	 */
	@Override
	public PIDChannel getPIDChannel(int group) {
		return pid.getPIDChannel(group);
		
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#SetPIDInterpolatedVelocity
	 */
	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond, double seconds) throws PIDCommandException {
		return pid.SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#SetPDVelocity
	 */
	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)throws PIDCommandException {
		return pid.SetPDVelocity(group, unitsPerSecond, seconds);
	}
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDControl#killAllPidGroups
	 */
	@Override
	public boolean killAllPidGroups() {
		return pid.killAllPidGroups();
	}
	
	/*
	 * Advanced Async Configuration. THese methods configure what value changes trigger an asynchronous event as well as 
	 * how often those event states are checked
	 */
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are not equal.
	 * This sets the sample time to 100 ms
	 * @param pin the DyIO channel to configure
	 * @return true is success
	 */
	public boolean configAdvancedAsyncNotEqual(int pin){
		return configAdvancedAsyncNotEqual(pin,100);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are outside a deadband
	 * This sets the sample time to 100 ms
	 * @param pin the DyIO channel to configure
	 * @param deadbandSize the size in sensor units of the deadband
	 * @return true if success
	 */
	public boolean configAdvancedAsyncDeadBand(int pin,int deadbandSize){
		return  configAdvancedAsyncDeadBand(pin,100,deadbandSize);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value crosses a threshhold
	 * This sets the sample time to 100 ms
	 * @param pin the DyIO channel to configure
	 * @param threshholdValue a value setpoint that triggers an even when it is crossed
	 * @param edgeType Rising, Falling, or both
	 * @return true if success
	 */
	public boolean configAdvancedAsyncTreshhold(int pin,int threshholdValue, AsyncThreshholdEdgeType edgeType){
		return  configAdvancedAsyncTreshhold(pin,100, threshholdValue, edgeType);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value is sampled on a real-time 
	 * This sets the sample time to 100 ms
	 * clock and sent as async regardless of value change
	 * @param pin  the DyIO channel to configure
	 * @return true if success
	 */
	public boolean configAdvancedAsyncAutoSample(int pin){
		return  configAdvancedAsyncAutoSample(pin,100);
	}
	
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are not equal.
	 * @param pin the DyIO channel to configure
	 * @param time the sample time in MiliSeconds
	 * @return true if success
	 */
	public boolean configAdvancedAsyncNotEqual(int pin,int time){
		return send(new ConfigAsyncCommand(pin,time,AsyncMode.NOTEQUAL)) == null;
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are outside a deadband
	 * @param pin the DyIO channel to configure
	 * @param time the sample time in MiliSeconds
	 * @param deadbandSize the size in sensor units of the deadband
	 * @return true if success
	 */
	public boolean configAdvancedAsyncDeadBand(int pin,int time,int deadbandSize){
		return send(new ConfigAsyncCommand(pin,time,deadbandSize)) == null;
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value crosses a threshhold
	 * @param pin the DyIO channel to configure
	 * @param time the sample time in MiliSeconds
	 * @param threshholdValue a value setpoint that triggers an even when it is crossed
	 * @param edgeType Rising, Falling, or both
	 * @return true if success
	 */
	public boolean configAdvancedAsyncTreshhold(int pin,int time,int threshholdValue, AsyncThreshholdEdgeType edgeType){
		return send(new ConfigAsyncCommand(pin,time,threshholdValue,edgeType)) == null;
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value is sampled on a real-time 
	 * clock and sent as async regardless of value change
	 * @param pin  the DyIO channel to configure
	 * @param time the sample time in MiliSeconds
	 * @return true if success
	 */
	public boolean configAdvancedAsyncAutoSample(int pin,int time){
		return send(new ConfigAsyncCommand(pin,time,AsyncMode.AUTOSAMP)) == null;
	}
	
	
	 
	public boolean connect(){
		if(getConnection()!=null) {
			getConnection().addConnectionEventListener(this);
		}
		if(super.connect()) {
			pid.setConnection(getConnection());
			pid.setAddress(getAddress());
			pid.connect();
			send( new PowerCommand());
			startHeartBeat(3000);
			resync();
			return true;
		}
		return false;
	}

	/**
	 * This method enables the heart beat. It tells the device how often to expect synchronous packet, and if 
	 * communication fails to send in the given time, then the device should go into its "safe state". 
	 * This will also set up a thread to ping the device periodically if it has been too long since the last 
	 * user generated synchronous packet.
	 */
	public void startHeartBeat(long msHeartBeatTime){
		super.startHeartBeat(msHeartBeatTime);
		try{
			BowlerDatagram b = send(new SafeModeCommand(true, (int) msHeartBeatTime));
			if(b== null)
				checkFirmwareRev();
		}catch(Exception e){
			System.err.println("DyIO is out of date");
			checkFirmwareRev();
		}
	}
	/**
	 * This method stops the heart beat and tells the device to stop expecting a heart beat. This will DISABLE the safe mode detect.
	 */
	public void stopHeartBeat(){
		
		super.stopHeartBeat();
		try{
			BowlerDatagram b = send(new SafeModeCommand(false, 0));
			if(b== null)
				checkFirmwareRev();
		}catch(Exception e){
			System.err.println("DyIO is out of date");
			checkFirmwareRev();
		}	
	}
	
	private boolean enableBrownOut=true;
	/**
	 * This method allows you to disable the brown out detect for the servo subsystem. If true is passed 
	 * @param enable true to enable the borwnout, false to disable
	 * @return True is success
	 */
	public boolean enableBrownOutDetect(boolean enable) {
		enableBrownOut=enable;
		return send(new PowerCommand(!enableBrownOut))!=null;
	}
	/**
	 * Tells the application whether or not to use the brownout detect
	 * @return
	 */
	public boolean isBrownOutDetect() {
		return enableBrownOut;
	}
	
	/**
	 * Getter for channels
	 * @return
	 */
	private ArrayList<DyIOChannel> getInternalChannels() {
		return channels;
	}

	/**
	 * This will enable a state where the DyIO will surpress DyIO mode update events. 
	 * @param muteResyncOnModeChange true to enable the muted mode
	 */
	public void setMuteResyncOnModeChange(boolean muteResyncOnModeChange) {
		this.muteResyncOnModeChange = muteResyncOnModeChange;
	}
	/**
	 * This will check if the device is in the muted mode change mode
	 * @return true if in muted mode
	 */
	public boolean isMuteResyncOnModeChange() {
		return muteResyncOnModeChange;
	}

	@Override
	public void onDisconnect() {
		firmware[0]=0;
		firmware[1]=0;
		firmware[2]=0;
	}

	@Override
	public void onConnect() {

	}
	/**
	 * Sets the flag to represent if the DyIO is currently re-suncing itself with the device
	 * @param resyncing
	 */
	public void setResyncing(boolean resyncing) {
		this.resyncing = resyncing;
	}
	/**
	 * Checks to see if the DyIO is currently re-syncing its internal staate. 
	 * @param resyncing
	 */
	public boolean isResyncing() {
		return resyncing;
	}

	
	/**
	 * This method enables the DyIO log printing. 
	 * 
	 * @param printing enable the system printing
	 * @param debug    enable the debug log level
	 */
	public void SetPrintModes(boolean printing,boolean debug){
		Log.enableSystemPrint(printing);
		Log.enableDebugPrint(debug);
	}
	
	/**
	 * This method enables the DyIO log printing. 
	 */
	public void enableDebug() {
		SetPrintModes(true,true);
	}
	
	/**
	 * This method disables the DyIO log printing. 
	 */
	public void disableDebug() {
		SetPrintModes(false,false);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	 
	public String toString() {
		
		String chFmt = "%02d - %-20s   %02d - %-20s\n";
		
		String s = getFirmwareRevString()+ " \nMAC: " + getAddress() + "\n";
		for(int i = 0; i < getInternalChannels().size()/2; i++) {
			s += String.format(chFmt, 
					           getInternalChannels().size() - 1 - i, 
					           getInternalChannels().get(getInternalChannels().size()-1-i).getMode().toSlug(), 
					           i, 
					           getInternalChannels().get(i).getMode().toSlug());
		}
		
		return s;
	}




}
