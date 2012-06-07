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

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOPeripheralException;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

/**
 * 
 */
public class CreateArm {
	ServoChannel [] links;
	private static final double l1 = 6.0;
	private static final double l2 = 3.93;
	private static final double l3 = 4.75;
	//private static final double l3 = .0001;
	private static final double M_PI = Math.PI; 
	private double scale[]={1.55,1.50,-1.76};
	private double [] positions=new double[4];
	private double [] angles=new double[3];
	private double [] pose=new double[3];
	private double [] centers={134,136,128,48};
	private boolean blocking = false;
	
	private double xyThreshHold = .1;
	private double orentThreshHold = 1;
	
	/**
	 * 
	 * @param links this is an array of servo channel links.
	 * 0th element is shoulder
	 * 1th element is elbow
	 * 2th element is is wrist
	 * 3th element is the gripper
	 */
	public CreateArm(ServoChannel [] links){
		check(links);
		home();
		pose=getCartesianPose();
	}
	
	/**
	 * 
	 */
	public void home(){
		setAngles(90,-90,0);
		setCartesianPose(getCartesianPose());
		gripOpen();
	}
	
	/**
	 * 
	 */
	public void rest(){
		setAngles(110,-110,0);
		setCartesianPose(getCartesianPose());
		gripOpen();
	}
	
	/**
	 * 
	 */
	public void gripClose(){
		links[3].SetPosition((int) centers[3]+75,(float).5);
		if (isBlocking()){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// ignore
			}
		}
	}
	
	/**
	 * 
	 */
	public void gripOpen(){
		links[3].SetPosition((int) centers[3],(float) .5);
		if (isBlocking()){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// ignore
			}
		}
	}
	private void check(ServoChannel [] links){
		if(links.length != 4){
			throw new DyIOPeripheralException("This perpheral needs 4 links.");
		}
		for(int i=0;i<4;i++){
			if(links[i]==null){
				throw new DyIOPeripheralException("All links must already be instantiated.");
			}
			links[i].SetPosition((int) getCenters()[i],2);
			positions[i]=(double)getCenters()[i];
		}
		this.links = links;
	}
	/**
	 * 
	 * @param angle set just the shoulder angle
	 */
	public void setShouler(double angle){
		setAngles(angle,angles[1],angles[2]);
	}
	
	/**
	 * 
	 * @param angle set just the elbow angle
	 */
	public void setElbow(double angle){
		setAngles(angles[0],angle,angles[2]);
	}
	
	/**
	 * 
	 * @param angle set just the wrist angle
	 */
	public void setWrist(double angle){
		setAngles(angles[0],angles[1],angle);
	}
	
	/**
	 * This takes angles in degrees and converts them to servo positions; Note:
	 * All zeros would be the arm completely horizontal, all links pointing out.
	 * 
	 * @param shoulder
	 * @param elbow
	 * @param wrist
	 */
	public void setAngles(double shoulder, double elbow, double wrist){
		setAngles(shoulder, elbow, wrist,(float) 1.0);
	}
	
	/**
	 * This takes angles in degrees and converts them to servo positions; Note:
	 * All zeros would be the arm completely horizontal, all links pointing out.
	 * 
	 * @param shoulder
	 * @param elbow
	 * @param wrist
	 * @param time
	 *            the time it should take for the transition to take
	 */
	public void setAngles(double shoulder, double elbow, double wrist,float time) {
		angles[0]=shoulder;
		angles[1]=elbow;
		angles[2]=wrist;
		double s,e,w;
		s =shoulder-90;
		s *=getScale()[0];
		positions[0]=getCenters()[0]+s;
		
		e =(elbow+90);
		e *=getScale()[1];
		positions[1]=getCenters()[1]+e;
		double interference = centers[0]-45;
		double limit = 255 - (1.1*(positions[0]-interference));
		if(positions[0]>interference){
			Log.info("In interference zone: "+interference+" with limit: "+limit );
			if (positions[1]>limit){
				System.err.print("\nAttempting to set angle that interferes, fixing. Was: "+positions[0]+","+positions[1]);
				positions[1]=limit;
				System.err.print(" Is: "+positions[0]+" , "+positions[1]+"\n");
			}
			Log.info("\n");
		}
		
		if(positions[1]<(centers[1]-83)){
			positions[1]=(centers[1]-83);
		}
		
		double [] an= getAngles();
		w =(int) ((pose[2]-an[0]-an[1])*getScale()[2]);
		positions[2]=getCenters()[2]+w;
		
		
		for (int i=0;i<3;i++){
			if (positions[i]>255){
				positions[i]=255;
			}
			if (positions[i]<0){
				positions[i]=0;
			}
			links[i].SetPosition((int) positions[i],time);
		}
		Log.info("Set positions Shoulder: "+positions[0]+" Elbow: "+positions[1]+" Wrist: "+positions[2]);
		if (isBlocking()){
			try {
				Thread.sleep((long) (time*1000));
			} catch (InterruptedException e1) {
				// ignore
			}
		}
		
	}
	/**
	 * 
	 * @return an array of angles in degrees. This should correspond to pose of the arm.
	 */
	public double [] getAngles(){
		double [] a=new double [3];
		for (int i=0;i<3;i++){
			a[i]=(positions[i]-getCenters()[i])/getScale()[i];
		}
		a[0]+=90;
		a[1]-=90;
		return a;
	}
	/**
	 * 
	 * @return pose vector, X,Y,Orentation
	 */
	public double [] getCartesianPose(){
		double [] angles =getAngles();
		pose[2] = GetOrentation();
		
		pose[0]=(l1* cos(ToRadians(angles[0]))+l2* cos(ToRadians(angles[0])+ToRadians(angles[1]))+(l3* cos(ToRadians(GetOrentation()))));
		pose[1]=(l1* sin(ToRadians(angles[0]))+l2* sin(ToRadians(angles[0])+ToRadians(angles[1]))+(l3* sin(ToRadians(GetOrentation()))));
		double [] p = new double [3];
		for ( int i = 0; i<3; i++){
			p[i]=pose[i];
		}
		return p;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getCartesianPoseString(){
		getCartesianPose();
		String s="[";
		for(int i=0;i <pose.length;i++){
			s+=pose[i];
			if(i<pose.length-1)
				s+=",";
		}
		return s+"]";
	}
	
	/**
	 * 
	 * 
	 * @param p
	 */
	public void setCartesianPose(double [] p){
		setCartesianPose(p,(float) 1.0);
	}
	
	/**
	 * 
	 * 
	 * @param p
	 * @param time
	 */
	public void setCartesianPose(double [] p, float time){
		setCartesianPose(p[0],p[1], p[2],time);
	}
	
	/**
	 * 
	 * 
	 * @param x
	 * @param y
	 * @param orentation
	 */
	public void setCartesianPose(double x, double y, double orentation){
		setCartesianPose(x,y, orentation,(float).2);
	}
	
	/**
	 * 
	 * 
	 * @param x
	 * @param y
	 * @param orentation
	 * @param time
	 */
	public void setCartesianPose(double x, double y, double orentation, float time){
		if(orentation<-35)
			orentation=-35;
		if(orentation>35)
			orentation=35;
		if (!updateCartesian(x,y,orentation)){
			return;
		}
		
		pose[0]=x;
		pose[1]=y;
		pose[2]=orentation;
		
		Log.info("Setting Pose X: "+x+" Y: "+y+" Orentation: "+orentation );
		
		x -= (l3*cos(orentation*M_PI/180));
		y -= (l3*sin(orentation*M_PI/180));
		if (sqrt(x*x+y*y) > l1+l2) {
			System.err.println("Hypotenus too long"+x+" "+y+"\r\n");
			return;
		}
		double elbow = 0;
		elbow =(-1*acos(((x*x+y*y)-(l1*l1+l2*l2))/(2*l1*l2)));
		elbow *=(180.0/M_PI);

		double shoulder =0;
		shoulder =(atan2(y,x)+acos((x*x+y*y+l1*l1-l2*l2)/(2*l1*sqrt(x*x+y*y))));
		shoulder *=(180.0/M_PI);
		
		double wrist = orentation-elbow-shoulder;
		setAngles(shoulder,elbow,wrist,time);
		
		
	}
	
	private boolean updateCartesian(double x, double y, double orentation) {
		if(((x>(pose[0]+xyThreshHold))) || (x<(pose[0]-xyThreshHold))){
			Log.info("X changed");
			return true;
		}else{
			Log.info("X set: "+x+" was: "+pose[0]);
		}
		if(((y>(pose[1]+xyThreshHold))) || (y<(pose[1]-xyThreshHold))){
			Log.info("Y changed");
			return true;
		}else{
			Log.info("Y set: "+y+" was: "+pose[1]);
		}
		if((orentation>pose[2]+orentThreshHold) || (orentation<pose[2]-orentThreshHold)){
			Log.info("Orentation changed");
			return true;
		}
		Log.info("No signifigant change");
		return false;
	}
	
	/**
	 * 
	 * 
	 * @param x
	 */
	public void setCartesianX(double x){
		setCartesianPose(x, pose[1], pose[2]);
	}
	
	/**
	 * 
	 * 
	 * @param y
	 */
	public void setCartesianY(double y){
		setCartesianPose(pose[0], y, pose[2]);
	}
	
	/**
	 * 
	 * 
	 * @param o
	 */
	public void setCartesianOrentation(double o){
		setCartesianPose(pose[0], pose[1], o);
	}
	
	/*
	 * Math wrappers for direct compatibility with C code
	 */
	private double sqrt(double d) {
		return Math.sqrt(d);
	}
	private double atan2(double y, double x) {
		return Math.atan2(y, x);
	}
	private double acos(double d) {
		return Math.acos(d);
	}
	private double sin(double angle) {
		return Math.sin(angle);
	}
	private double cos(double angle) {
		return Math.cos(angle);
	}
	private double ToRadians(double degrees){
		return degrees*M_PI/180.0;
	}
	
	/**
	 * 
	 * @return the current approach orientation of the wrist
	 */
	public double GetOrentation() {
		double [] angles =getAngles();
		return angles[0]+angles[1]+angles[2];
	}
	
	/**
	 * 
	 * 
	 * @param centers
	 */
	public void setCenters(double [] centers) {
		this.centers = centers;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public double [] getCenters() {
		return centers;
	}
	
	/**
	 * 
	 * 
	 * @param blocking
	 */
	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isBlocking() {
		return blocking;
	}
	
	/**
	 * 
	 * 
	 * @param scale
	 */
	public void setScale(double scale[]) {
		this.scale = scale;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public double[] getScale() {
		return scale;
	}
	
}
