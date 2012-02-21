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
 * 
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
	 * @param channel
	 *            - a channel number
	 * @return
	 */
	public DyIOChannel getChannel(int channel) {
		validateChannel(channel);
		return getInternalChannels().get(channel);
	}
	
	/**
	 * 
	 * 
	 * @param channel
	 * @param mode
	 * @return
	 */
	public boolean setMode(int channel, DyIOChannelMode mode) {
		return getChannel(channel).setMode(mode);
	}

	/**
	 * 
	 * 
	 * @param channel
	 * @param mode
	 * @param async
	 * @return
	 */
	public boolean setMode(int channel, DyIOChannelMode mode, boolean async) {
		return getChannel(channel).setMode(mode, async);
	}
	
	/**
	 * 
	 * 
	 * @param channel
	 * @return
	 */
	public DyIOChannelMode getMode(int channel) {
		return getChannel(channel).getMode();
	}
	
	/**
	 * 
	 * 
	 * @param channel
	 * @param value
	 * @return
	 */
	public boolean setValue(int channel, int value) {
		return getChannel(channel).setValue(value);
	}
	
	/**
	 * 
	 * 
	 * @param channel
	 * @param value
	 * @return
	 */
	public boolean setValue(int channel, ByteList value) {
		return getChannel(channel).setValue(value);
	}

	/**
	 * 
	 * 
	 * @param channel
	 * @return
	 */
	public int getValue(int channel) {
		return getChannel(channel).getValue();
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getInfo(){
		return info;
	}
	
	/**
	 * 
	 * 
	 * @param info
	 */
	public void setInfo(String info){
		if(send(new InfoCommand(info)) == null) {
			return;
		}
		this.info = info;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public byte [] getFirmwareRev(){
		return firmware;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getFirmwareRevString(){
		String revFmt = "%02d.%02d.%03d";
		return "Firmware Revision: v" + String.format(revFmt, firmware[0], firmware[1], firmware[2]);
	}

	/**
	 * 
	 * 
	 * @return
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
	 * 
	 * 
	 * @param channel
	 */
	public void resync(int channel) {
		getChannel(channel).resync(false);
	}
	
	private static boolean checkFirmware=true;
	public static void disableFWCheck() {
		checkFirmware=false;
	}
	
	/**
	 * Sync the state cache with the live device. 
	 */
	public void checkFirmwareRev()throws DyIOFirmwareOutOfDateException{
		if(checkFirmware) {
			int[] sdkRev = SDKBuildInfo.getBuildInfo(); 
			
			for(int i=0;i<3;i++){
				if(firmware[i] < sdkRev[i]){
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
	private boolean resyncing = false;
	private boolean haveBeenSynced =false;
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
	 * @param channel
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
	 * 
	 * 
	 * @param printing
	 * @param debug
	 */
	public void SetPrintModes(boolean printing,boolean debug){
		//BOB, do not remove, i need this in here. 
		Log.enableSystemPrint(printing);
		Log.enableDebugPrint(debug);
	}
	
	/**
	 * 
	 */
	public void enableDebug() {
		SetPrintModes(true,true);
	}
	
	/**
	 * 
	 */
	public void disableDebug() {
		SetPrintModes(false,false);
	}
	
	public DyIOPowerState getBankAState() {
		return bankAState;
	}

	public DyIOPowerState getBankBState() {
		return bankBState;
	}
	
	public boolean getCachedMode() {
		return cachedMode;
	}
	
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
	 * 
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
				//System.err.println("Failed to update all, retrying");
			}
		}
		
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
	public double getBatteryVoltage(boolean refresh){
		if(refresh) {
			BowlerDatagram data = send(new PowerCommand());
			powerEvent(data);
		}
		return batteryVoltage;
	}
	public void onAllResponse(BowlerDatagram data) {

		pid.onAllResponse(data);
	}
	
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
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
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
			for(IDyIOEventListener l : listeners) {
				l.onDyIOEvent(e);
			}
		}
		

		pid.onAsyncResponse(data);
	}

	/**
	 * PID controller (new as of 0.3.6)
	 */
	
	
	public boolean ConfigureDynamicPIDChannels(DyPIDConfiguration config){
		return send(new ConfigureDynamicPIDCommand(config))!=null;
	}
	public DyPIDConfiguration getDyPIDConfiguration(int group){
		BowlerDatagram conf = send(new ConfigureDynamicPIDCommand( group) );
		DyPIDConfiguration back=new DyPIDConfiguration(conf);
		return back;
	}
	
	public boolean ResetPIDChannel(int group) {
		return pid.ResetPIDChannel(group);
	}
	 
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		return pid.ResetPIDChannel(group, valueToSetCurrentTo);
	}
	public boolean SetPIDSetPoint(int group,int setpoint, double seconds){
		return pid.SetPIDSetPoint(group, setpoint,seconds);
	}
	public boolean SetAllPIDSetPoint(int []setpoints, double seconds){
		return pid.SetAllPIDSetPoint(setpoints,seconds);
	}
	public int GetPIDPosition(int group) {
		return pid.GetPIDPosition(group);
	}
	public int [] GetAllPIDPosition() {
		return pid.GetAllPIDPosition();
	}
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return pid.ConfigurePIDController(config);
	}
	public PIDConfiguration getPIDConfiguration(int group) {
		return pid.getPIDConfiguration(group);
	}
	public void addPIDEventListener(IPIDEventListener l) {
		pid.addPIDEventListener(l);
	}
	public void removePIDEventListener(IPIDEventListener l) {
		pid.removePIDEventListener(l);
	}
	@Override
	public void flushPIDChannels(double time) {
		pid.flushPIDChannels(time);
	}
	@Override
	public PIDChannel getPIDChannel(int group) {
		return pid.getPIDChannel(group);
	}
	
	/**
	 * Advanced Async Configuration
	 */
	public boolean configAdvancedAsyncNotEqual(int pin){
		return configAdvancedAsyncNotEqual(pin,100);
	}
	public boolean configAdvancedAsyncDeadBand(int pin,int deadbandSize){
		return  configAdvancedAsyncDeadBand(pin,100,deadbandSize);
	}
	public boolean configAdvancedAsyncTreshhold(int pin,int threshholdValue, AsyncThreshholdEdgeType edgeType){
		return  configAdvancedAsyncTreshhold(pin,100, threshholdValue, edgeType);
	}
	public boolean configAdvancedAsyncAutoSample(int pin){
		return  configAdvancedAsyncAutoSample(pin,100);
	}
	
	
	public boolean configAdvancedAsyncNotEqual(int pin,int time){
		return send(new ConfigAsyncCommand(pin,time,AsyncMode.NOTEQUAL)) == null;
	}
	public boolean configAdvancedAsyncDeadBand(int pin,int time,int deadbandSize){
		return send(new ConfigAsyncCommand(pin,time,deadbandSize)) == null;
	}
	public boolean configAdvancedAsyncTreshhold(int pin,int time,int threshholdValue, AsyncThreshholdEdgeType edgeType){
		return send(new ConfigAsyncCommand(pin,time,threshholdValue,edgeType)) == null;
	}
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

	public void setInternalChannels(ArrayList<DyIOChannel> channels) {
		this.channels = channels;
	}

	public ArrayList<DyIOChannel> getInternalChannels() {
		return channels;
	}

	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond, double seconds) throws PIDCommandException {
		return pid.SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
	}

	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)throws PIDCommandException {
		return pid.SetPDVelocity(group, unitsPerSecond, seconds);
	}

	public void setMuteResyncOnModeChange(boolean muteResyncOnModeChange) {
		this.muteResyncOnModeChange = muteResyncOnModeChange;
	}

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

	@Override
	public boolean killAllPidGroups() {
		return pid.killAllPidGroups();
	}

	public void setResyncing(boolean resyncing) {
		this.resyncing = resyncing;
	}

	public boolean isResyncing() {
		return resyncing;
	}


}
