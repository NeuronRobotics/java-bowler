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

// TODO: Auto-generated Javadoc
/**
 * The Class CreateArm.
 */
public class CreateArm {
	
	/** The links. */
	ServoChannel [] links;
	
	/** The Constant l1. */
	private static final double l1 = 6.0;
	
	/** The Constant l2. */
	private static final double l2 = 3.93;
	
	/** The Constant l3. */
	private static final double l3 = 4.75;
	
	/** The Constant M_PI. */
	//private static final double l3 = .0001;
	private static final double M_PI = Math.PI; 
	
	/** The scale. */
	private double scale[]={1.55,1.50,-1.76};
	
	/** The positions. */
	private double [] positions=new double[4];
	
	/** The angles. */
	private double [] angles=new double[3];
	
	/** The pose. */
	private double [] pose=new double[3];
	
	/** The centers. */
	private double [] centers={134,136,128,48};
	
	/** The blocking. */
	private boolean blocking = false;
	
	/** The xy thresh hold. */
	private double xyThreshHold = .1;
	
	/** The orent thresh hold. */
	private double orentThreshHold = 1;
	
	/**
	 * Instantiates a new creates the arm.
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
	 * Home.
	 */
	public void home(){
		setAngles(90,-90,0);
		setCartesianPose(getCartesianPose());
		gripOpen();
	}
	
	/**
	 * Rest.
	 */
	public void rest(){
		setAngles(110,-110,0);
		setCartesianPose(getCartesianPose());
		gripOpen();
	}
	
	/**
	 * Grip close.
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
	 * Grip open.
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
	
	/**
	 * Check.
	 *
	 * @param links the links
	 */
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
	 * Sets the shouler.
	 *
	 * @param angle set just the shoulder angle
	 */
	public void setShouler(double angle){
		setAngles(angle,angles[1],angles[2]);
	}
	
	/**
	 * Sets the elbow.
	 *
	 * @param angle set just the elbow angle
	 */
	public void setElbow(double angle){
		setAngles(angles[0],angle,angles[2]);
	}
	
	/**
	 * Sets the wrist.
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
	 * @param shoulder the shoulder
	 * @param elbow the elbow
	 * @param wrist the wrist
	 */
	public void setAngles(double shoulder, double elbow, double wrist){
		setAngles(shoulder, elbow, wrist,(float) 1.0);
	}
	
	/**
	 * This takes angles in degrees and converts them to servo positions; Note:
	 * All zeros would be the arm completely horizontal, all links pointing out.
	 *
	 * @param shoulder the shoulder
	 * @param elbow the elbow
	 * @param wrist the wrist
	 * @param time            the time it should take for the transition to take
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
	 * Gets the angles.
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
	 * Gets the cartesian pose.
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
	 * Gets the cartesian pose string.
	 *
	 * @return the cartesian pose string
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
	 * Sets the cartesian pose.
	 *
	 * @param p the new cartesian pose
	 */
	public void setCartesianPose(double [] p){
		setCartesianPose(p,(float) 1.0);
	}
	
	/**
	 * Sets the cartesian pose.
	 *
	 * @param p the p
	 * @param time the time
	 */
	public void setCartesianPose(double [] p, float time){
		setCartesianPose(p[0],p[1], p[2],time);
	}
	
	/**
	 * Sets the cartesian pose.
	 *
	 * @param x the x
	 * @param y the y
	 * @param orentation the orentation
	 */
	public void setCartesianPose(double x, double y, double orentation){
		setCartesianPose(x,y, orentation,(float).2);
	}
	
	/**
	 * Sets the cartesian pose.
	 *
	 * @param x the x
	 * @param y the y
	 * @param orentation the orentation
	 * @param time the time
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
	
	/**
	 * Update cartesian.
	 *
	 * @param x the x
	 * @param y the y
	 * @param orentation the orentation
	 * @return true, if successful
	 */
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
	 * Sets the cartesian x.
	 *
	 * @param x the new cartesian x
	 */
	public void setCartesianX(double x){
		setCartesianPose(x, pose[1], pose[2]);
	}
	
	/**
	 * Sets the cartesian y.
	 *
	 * @param y the new cartesian y
	 */
	public void setCartesianY(double y){
		setCartesianPose(pose[0], y, pose[2]);
	}
	
	/**
	 * Sets the cartesian orentation.
	 *
	 * @param o the new cartesian orentation
	 */
	public void setCartesianOrentation(double o){
		setCartesianPose(pose[0], pose[1], o);
	}
	
	/**
	 * Sqrt.
	 *
	 * @param d the d
	 * @return the double
	 */
	/*
	 * Math wrappers for direct compatibility with C code
	 */
	private double sqrt(double d) {
		return Math.sqrt(d);
	}
	
	/**
	 * Atan2.
	 *
	 * @param y the y
	 * @param x the x
	 * @return the double
	 */
	private double atan2(double y, double x) {
		return Math.atan2(y, x);
	}
	
	/**
	 * Acos.
	 *
	 * @param d the d
	 * @return the double
	 */
	private double acos(double d) {
		return Math.acos(d);
	}
	
	/**
	 * Sin.
	 *
	 * @param angle the angle
	 * @return the double
	 */
	private double sin(double angle) {
		return Math.sin(angle);
	}
	
	/**
	 * Cos.
	 *
	 * @param angle the angle
	 * @return the double
	 */
	private double cos(double angle) {
		return Math.cos(angle);
	}
	
	/**
	 * To radians.
	 *
	 * @param degrees the degrees
	 * @return the double
	 */
	private double ToRadians(double degrees){
		return degrees*M_PI/180.0;
	}
	
	/**
	 * Gets the orentation.
	 *
	 * @return the current approach orientation of the wrist
	 */
	public double GetOrentation() {
		double [] angles =getAngles();
		return angles[0]+angles[1]+angles[2];
	}
	
	/**
	 * Sets the centers.
	 *
	 * @param centers the new centers
	 */
	public void setCenters(double [] centers) {
		this.centers = centers;
	}
	
	/**
	 * Gets the centers.
	 *
	 * @return the centers
	 */
	public double [] getCenters() {
		return centers;
	}
	
	/**
	 * Sets the blocking.
	 *
	 * @param blocking the new blocking
	 */
	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}
	
	/**
	 * Checks if is blocking.
	 *
	 * @return true, if is blocking
	 */
	public boolean isBlocking() {
		return blocking;
	}
	
	/**
	 * Sets the scale.
	 *
	 * @param scale the new scale
	 */
	public void setScale(double scale[]) {
		this.scale = scale;
	}
	
	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public double[] getScale() {
		return scale;
	}
	
}
