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
package com.neuronrobotics.sdk.addons.irobot;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOPeripheralException;
import com.neuronrobotics.sdk.dyio.peripherals.IUARTStreamListener;
import com.neuronrobotics.sdk.dyio.peripherals.UARTChannel;
/**
 * 
 */
public class Create implements IUARTStreamListener{
	
	private short myAngle;
	private short myDistance;
	//private boolean packetRecieved;
	
	private short previousRad=0;
	private short previousVel=0;
	
	private UARTChannel channel;
	private byte []ledState={(byte) 139,0,0,0};
	private byte []sensor  = new byte[26];
	private CreateSensorRequest senReq=CreateSensorRequest.NONE;
	private ArrayList<ICreateSensorListener> listeners = new ArrayList<ICreateSensorListener>();
	
	/**
	 * 
	 * 
	 * @param chan
	 */
	public Create(UARTChannel chan){
		channel= chan;
		channel.setUARTBaudrate(57600);
		channel.addUARTStreamListener(this);
		byte [] init = {(byte) 128,(byte) 131};
		try {
			send(init);
		} catch (Exception e) {
			throw new DyIOPeripheralException("Failed to initialize iRobot Create");
		}
		requestSensors();
	}
	
	/**
	 * 
	 */
	public void setFullMode(){
		byte [] init = {(byte) 128,(byte) 132};
		try {
			send(init);
		} catch (Exception e) {
			throw new DyIOPeripheralException("Failed to initialize iRobot Create in Full Mode");
		}
	}
	
	/**
	 * 
	 */
	public void InitCreate(){
		byte [] init = {(byte) 128,(byte) 131};
		try {
			send(init);
		} catch (Exception e) {
			throw new DyIOPeripheralException("Failed to initialize iRobot Create in Full Mode");
		}
	}
	
	/**
	 * 
	 * 
	 * @param timeout
	 */
	public void InitCreateBlocking(int timeout){
		byte [] init = {(byte) 128,(byte) 131};
		while(true){
			try {
				Thread.sleep(500);
				Log.info("Initializing Create..");
				send(init);
				break;
			} catch (Exception e) {
				throw new DyIOPeripheralException("Failed to initialize iRobot Create in Full Mode");
			}
		}
	}
	
	/**
	 * wrapper for the drive command
	 * NOTE, this is not a positional, only velocity. It will run forever until you tell it to stop
	 *	  Special cases for radius param: 
	 	  straight = 32768 = hex 8000
		  Turn in place clockwise = -1
		  Turn in place counter-clockwise = 1

	 * @param velocity  mm/s 32768 to -32768
	 * @param radius	mm	 32768 to -32768
	 */
	public void move(short velocity,short radius){
		if((velocity == previousVel) &&(radius == previousRad) )
			return;
		previousRad = radius;
		previousVel = velocity; 
		byte[] drv = {(byte) 137,(byte) (velocity>>8),(byte) velocity,(byte) (radius>>8),(byte) radius};
		try {
			send(drv);
		} catch (Exception e) {
			throw new DyIOPeripheralException("Failed to send drive command");
		}
	}
	/**
	 * 
	 * @param distance mm Distance from current location to drive
	 */
	public void driveStraight(short distance){
		driveStraight((short)0x7fff,distance);
	}
	/**
	 * Driving macro. This will drive for a distance and stop.
	 * @param velocity mm/s
	 * @param distance mm
	 */
	public void driveStraight(short velocity,short distance){
		if((distance > 0 && velocity < 0) || (distance < 0 && velocity > 0)){
			velocity *= -1;
		}
		byte[] drv = {(byte) 152,13,
					  (byte) 137,(byte) (velocity>>8),(byte) velocity,(byte) (128),0,
					  (byte) 156,(byte) (distance>>8),(byte) (distance),
					  (byte) 137,0,0,0,0,
					  (byte) 153};
		try {
			send(drv);
		} catch (Exception e) {
			throw new DyIOPeripheralException("Failed to send drive command");
			
		}
	}
	/**
	 * @param timeout
	 * @param velocity mm/s
	 * @param distance mm
	 * @return 
	 * @throws InterruptedException 
	 */
	public boolean driveStraightBlocking(int timeout,short velocity,short distance) throws InterruptedException{
		int tries=0;
		Log.info("Driving...");
		try {
			driveStraight(velocity,distance);
		} catch (Exception e) {
			Log.error(e.toString());
		}
		myDistance=0;
		while (myDistance==0) {
			tries++;
			// try to get a good sensor reading. break on sucsess
			while(true){
				try{
					Log.info("Trying to get a good sensor reading");
					Thread.sleep(1000);
					this.requestSensors();
					break;
				} catch (Exception e) {
					Log.error(e.toString());
				}
			}
			Log.info("Driving Attempt "+Integer.toBinaryString(tries));
			if (tries==10){
				Log.info("Re attempting to send command");
				tries=0;
				try{
					driveStraight(velocity,distance);
				} catch (Exception e) {
					Log.error(e.toString());
				}
				Thread.sleep(1000);
			}
		}
		
		return false;
		
	}
	
	/**
	 * 
	 * @param angle degrees Distance from current location to drive
	 */
	public void turn(short angle){
		turn((short)0x7fff,angle);
	}
	/**
	 * Driving macro. This will drive for a distance and stop.
	 * @param velocity mm/s
	 * @param angle degrees
	 */
	public void turn(short velocity,short angle){
		if(velocity<0){
			velocity *= -1;
		}
		short turn = (short) ((angle>0)? 1:-1);
		byte[] drv = {(byte) 152,13,
					  (byte) 137,(byte) (velocity>>8),(byte) velocity,(byte) (turn>>8),(byte) (turn),
					  (byte) 157,(byte) (angle>>8),(byte) (angle),
					  (byte) 137,0,0,0,0,
					  (byte) 153};
		//Log.info("Turning: "+new ByteList(drv));
		try {
			send(drv);
		} catch (Exception e) {
			throw new DyIOPeripheralException("Failed to send drive command");
		}
	}
	
	/**
	 * 
	 * 
	 * @param timeout
	 * @param velocity
	 * @param angle
	 * @return
	 * @throws InterruptedException
	 */
	public boolean turnBlocking(int timeout,short velocity,short angle) throws InterruptedException{
		int tries=0;
		Log.info("Driving...");
		try {
			turn(velocity,angle);
		} catch (Exception e) {
			Log.error(e.toString());
		}
		myAngle=0;
		while (myAngle==0) {
			tries++;
			// try to get a good sensor reading. break on sucsess
			while(true){
				try{
					Log.info("Trying to get a good sensor reading");
					Thread.sleep(1000);
					this.requestSensors();
					break;
				} catch (Exception e) {
					Log.error(e.toString());
				}
				Thread.sleep(1000);
			}
			Log.info("Driving Attempt "+Integer.toBinaryString(tries));
			if (tries==10){
				Log.info("Re attempting to send command");
				tries=0;
				try{
					turn(velocity,angle);
				} catch (Exception e) {
					Log.error(e.toString());
				}
				Thread.sleep(1000);
			}
		}
		
		return false;
	}
	/**
	 * 
	 * @param max sets the state of the "max" led
	 * @param spot sets the state of the "spot" led
	 */
	public void setLed(boolean max,boolean spot){
		int led=0;
		led+=max?			(1<<1):0;
		led+=spot?			(1<<3):0;
		ledState[1]=(byte)led;
		setLed();
	}
	/**
	 * 
	 * @param color Power Color (0 – 255), 0 = green, 255 = red
	 * @param intensity Power Intensity (0 – 255), 0 = off, 255 = full intensity
	 */
	public void setStatusLed(int color,int intensity){
		ledState[2]=(byte)color;
		ledState[3]=(byte)intensity;
		setLed();
	}
	
	/**
	 * 
	 */
	public void requestSensors(){
		requestSensors(CreateSensorRequest.ALL);
	}
	
	/**
	 * 
	 * 
	 * @param req
	 */
	public void requestSensors(CreateSensorRequest req){
		senReq=req;
		byte []all={(byte)142,req.getValue()};
		try {
			send(all);
		} catch (Exception e) {
			throw new DyIOPeripheralException("Failed to send sensor request");
		}
	}
	
	private void setLed(){
		try {
			send(ledState);
		} catch (Exception e) {
			// ignore
		}
	}
	
	private void send(byte[]b) throws Exception{
		channel.sendBytes(new ByteList(b));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.IChannelEventListener#onChannelEvent(com.neuronrobotics.sdk.dyio.DyIOChannelEvent)
	 */
	public void onChannelEvent(DyIOChannelEvent e) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// ignore
		}
		byte [] in = channel.getBytes();
		switch(senReq){
		case ALL:
			if(in.length==26){
				//Log.info("Got ALL packet from Create");
				for (int i=0;i<26;i++){
					sensor[i]=in[i];
				}
			}else{
				Log.error("malformed ALL packet from Create"+new ByteList(in));
				return;
			}
			break;
		case IO:
			if(in.length==10){
				//Log.info("Got IO packet from Create");
				for (int i=0;i<10;i++){
					sensor[i]=in[i];
				}
			}else{
				Log.error("malformed IO packet from Create"+new ByteList(in));
				return;
			}
			
			break;
		case DRIVE:
			if(in.length==6){
				//Log.info("Got DRIVE packet from Create");
				for (int i=0;i<6;i++){
					sensor[i+10]=in[i];
				}
			}else{
				Log.error("malformed DRIVE packet from Create"+new ByteList(in));
				return;
			}
			break;
		case BATTERY:
			if(in.length==10){
				//Log.info("Got BATTERY packet from Create");
				for (int i=0;i<10;i++){
					sensor[i+16]=in[i];
				}
			}else{
				Log.error("malformed BATTERY packet from Create"+new ByteList(in));
				return;
			}
			break;
		case NONE:
			Log.error("Create sent packet upstream unexpectedally: "+new ByteList(in));
		}
		fireCreatePacket(new CreateSensors(sensor));
		senReq=CreateSensorRequest.NONE;
	}
	
	/**
	 * removeAllCreateSensorListeners clears the list of async packet listeners.
	 */
	public void removeAllCreateSensorListeners() {
		listeners.clear();
	}
	
	/**
	 * removeCreateSensorListener.
	 * 
	 * @param l
	 *            remove the specified listener
	 */
	public void removeCreateSensorListener(ICreateSensorListener l) {
		if(!listeners.contains(l)) {
			return;
		}
		
		listeners.remove(l);
	}
	
	/**
	 * addCreateSensorListener.
	 * 
	 * @param l
	 *            add the specified listener
	 */
	public void addCreateSensorListener(ICreateSensorListener l) {
		if(listeners.contains(l)) {
			return;
		}
		listeners.add(l);
	}
	
	private void fireCreatePacket(CreateSensors packet) {
		// for the blocking drive funcs
		myAngle=packet.angle;
		myDistance = packet.distance;
		//packetRecieved = true;
		
		for(ICreateSensorListener l : listeners) {
			l.onCreateSensor(packet);
		}
	}
	
}
