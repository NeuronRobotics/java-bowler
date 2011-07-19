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

import com.neuronrobotics.sdk.common.ByteList;

/**
 * 
 */
public class CreateSensors {
	public boolean rightBump=false;
	public boolean leftBump=false;
	public boolean rightDrop=false;
	public boolean leftDrop=false;
	public boolean centerDrop=false;
	public boolean wall=false;
	public boolean clifFrontLeft=false;
	public boolean clifFrontRight=false;
	public boolean clifLeft=false;
	public boolean clifRight=false;
	public boolean vitrualWall=false;
	
	public short distance=0;
	public short angle=0;
	public int tempreture=0;
	public int charge=0;
	public int capacity=0;
	public float voltage=0;
	byte [] data;
	
	/**
	 * 
	 * 
	 * @param packet
	 */
	public CreateSensors(byte [] packet){
		data=packet;
		bumps(data[0]);
		clifLeft = data[1]==1;
		clifFrontLeft = data[2]==1;
		clifFrontRight = data[3]==1;
		clifRight = data[4]==1;
		vitrualWall = data[5]==1;
		distance = (short) ((((short)data[12])<<8)+data[13]);
		angle = (short) ((((short)data[14])<<8)+data[15]);
		voltage = (float) ((((int)data[17])<<8)+data[18])/1000;
		tempreture=(char) data[21];
		charge = (int) ((((int)data[22])<<8)+data[23]);
		capacity = (int) ((((int)data[24])<<8)+data[25]);
	}
	private void bumps(byte data){
		rightBump=(data&0x01)>0;
		leftBump=(data&0x02)>0;
		rightDrop=(data&0x04)>0;
		leftDrop=(data&0x08)>0;
		centerDrop=(data&0x10)>0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s="Raw: "+new ByteList(data);
		s+="\nDistance: "+distance;
		s+="\nAngle: "+angle;
		s+="\nTempreture: "+tempreture;
		s+="\nCharge: "+charge;
		s+="\nCapacity: "+capacity;
		s+="\nVoltage: "+voltage;
		s+="\nBump right: "+rightBump;
		s+="\nBump left: "+leftBump;
		
		s+="\nDrop right: "+rightDrop;
		s+="\nDrop left: "+leftDrop;
		s+="\nDrop center: "+centerDrop;

		
		return s;
	}
}
