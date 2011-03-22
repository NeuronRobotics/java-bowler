package com.neuronrobotics.sdk.addons.walker;

import java.util.ArrayList;

public class Leg {
	ArrayList<Link> links = new ArrayList<Link>();
	private static final double M_PI = Math.PI; 
	private double xOffset,yOffset,thetaOffset;
	//private double xLockSetPoint;
	//private double xLocal,yLocal,zLocal;
	private double xSetPoint,ySetPoint,zSetPoint;
	private boolean gotHip=false;
	private boolean gotKnee=false;
	private boolean gotAnkle=false;
	public Leg(double x, double y, double theta){
		this.xOffset=x;
		this.yOffset=y;
		this.thetaOffset=theta;
		links.add(null);
		links.add(null);
		links.add(null);
	}
	public Link getHipLink() {
		return links.get(0);
	}
	public Link getKneeLink() {
		return links.get(1);
	}
	public Link getAnkleLink() {
		return links.get(2);
	}
	public void addLink(Link l){
		String type = l.getType();
		if(type.equalsIgnoreCase("hip")){
			links.set(0,l);
			 gotHip=true;
		}
		else if(type.equalsIgnoreCase("knee")){
			links.set(1,l);
			gotKnee=true;
		}
		else if(type.equalsIgnoreCase("ankle")){
			links.set(2,l);
			gotAnkle=true;
		}
		else{
			throw new RuntimeException("Unknown link type"+type);
		}
	}
	public boolean legOk(){
		if((gotHip && gotKnee && gotAnkle))			
			loadCartesianLocal();
		return (gotHip && gotKnee && gotAnkle);
	}
	public void incrementHip(double inc,double time){
		getHipLink().incrementAngle(inc, time);
	}
	public void incrementKnee(double inc,double time){
		getKneeLink().incrementAngle(inc, time);
	}
	public void incrementAnkle(double inc,double time){
		getAnkleLink().incrementAngle(inc, time);
	}
	
	public void setHip(double inc,double time){
		
		getHipLink().setAngle(inc, time);
	}
	public void setKnee(double inc,double time){
		getKneeLink().setAngle(inc, time);
	}
	public void setAnkle(double inc,double time){
		getAnkleLink().setAngle(inc, time);
	}
	
	private double [] calcCartesianLocal(double hip,double knee,double ankle) {
		double [] pos = new double[3];
		double l1 = getKneeLink().getLinkLen();
		double l2 = getAnkleLink().getLinkLen();
		double l3 = getHipLink().getLinkLen();
		double vect =(l1* cos(ToRadians(knee))+l2* cos(ToRadians(knee)+ToRadians(ankle))+(l3));
		pos[2]=(l1* sin(ToRadians(knee))+l2* sin(ToRadians(knee)+ToRadians(ankle)));
		pos[0]=vect*Math.cos(ToRadians(hip));
		pos[1]=vect*Math.sin(ToRadians(hip));
		return pos;
	}
	
	private double [] loadCartesianLocal(){
		double hip = (getHipLink().getAngle());
		double knee = (getKneeLink().getAngle());
		double ankle = (getAnkleLink().getAngle());
		return calcCartesianLocal(hip,knee,ankle);
	}
	private double [] calcCartesian(double [] loc) {
		double [] pos = new double[3];
		double vect =  sqrt(loc[0]*loc[0]+loc[1]*loc[1]);
		double angle = atan2(loc[1],loc[0])+ToRadians(thetaOffset);
		double x=(cos(angle)*vect)+xOffset;
		double y=(sin(angle)*vect)+yOffset;
		pos[0]=x;
		pos[1]=y;
		pos[2]=loc[2];
		return pos;
	}
	public double [] getCartesian(){
		return calcCartesian(loadCartesianLocal());
	}
	public double [] getCartesianLocal(){
		return loadCartesianLocal();
	}
	public void incrementX(double val, double time){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0]+val,pos[1],pos[2],time);
		}catch(RuntimeException e) {
			stepToSetpoint(time);
		}
		fix(time);
	}
	public void incrementY(double val, double time){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0],pos[1]+val,pos[2],time);
		}catch(RuntimeException e) {
			System.err.println("Error in increment y");
			stepToSetpoint(time);
		}
		//fix(time);
	}
	public void incrementZ(double val, double time){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0],pos[1],pos[2]+val,time);
		}catch(RuntimeException e) {
			stepToSetpoint(time);
		}
		fix(time);
	}
	
	public void setZ(double val,double time) {
		double [] pos = getCartesian();
		setCartesian(pos[0],pos[1],val,time);
	}
	
	public void setCartesian(double x,double y,double z,double time){
		x-=xOffset;
		y-=yOffset;
		double vect =  sqrt(x*x+y*y);
		double angle = atan2(y,x)-ToRadians(thetaOffset);
		x=cos(angle)*vect;
		y=sin(angle)*vect;
		setCartesianLocal(x,y,z,time);
	}
	public void setCartesianLocal(double xSet,double ySet,double zSet,double time){
		double l1 = getKneeLink().getLinkLen();
		double l2 = getAnkleLink().getLinkLen();
		double l3 = getHipLink().getLinkLen();
		double thetaLocal = Math.atan2(ySet, xSet);
		
		
		xSet -= Math.cos(thetaLocal)*l3;
		ySet -= Math.sin(thetaLocal)*l3;
		
		double vect = sqrt(xSet*xSet+ySet*ySet);
		
		//System.out.println("Theta local: "+thetaLocal+", Links: "+l3+","+l1+","+l2+" vector distance: "+vect+", z: "+zSet);
		
		if (vect > l1+l2) {
			throw new RuntimeException("Hypotenus too long: "+vect+" longer then "+l1+l2);
		}
		double x=vect;
		double y=zSet;
		double elbow = 0;
		elbow =(-1*acos(((x*x+y*y)-(l1*l1+l2*l2))/(2*l1*l2)));
		elbow *=(180.0/M_PI);

		double shoulder =0;
		shoulder =(atan2(y,x)+acos((x*x+y*y+l1*l1-l2*l2)/(2*l1*sqrt(x*x+y*y))));
		shoulder *=(180.0/M_PI);
		
		double knee=shoulder;
		double ankle=elbow;
		
		double hip = thetaLocal*(180.0/M_PI);
		setHip(hip, time);
		setKnee(knee, time);
		setAnkle(ankle, time);
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
	public void Home() {
		for(Link l: links ) {
			l.Home();
		}
	}
	public void save() {
		for(Link l: links ) {
			l.save();
		}
		
	}
	public boolean hitMaxHip() {
		return getHipLink().isMax();
	}
	public boolean hitMinHip() {
		return getHipLink().isMin();
	}
	public void setStartPoint() {
		double [] start = getCartesian();
		xSetPoint=start[0];
		ySetPoint=start[1];
		zSetPoint=start[2];
	}
	public void toMinHip(double time) {
		stepToHipAngle(getHipLink().getMin()+10, time);
	}
	public void toMaxHip(double time) {
		stepToHipAngle(getHipLink().getMax()-10, time);
	}
	public void stepToSetpoint(double time) {
		double [] current = getCartesian();
		time/=10;
		liftLeg(time);
		
		setCartesian(xSetPoint,ySetPoint, current[2]+1, time);
		
		putLegDown(time);
	}
	
	public void stepToHipAngle(double hip,double time) {
		time/=20;
		liftLeg(time);
		
		getHipLink().setAngle(hip, time);
		double [] adjusted = getCartesian();
		setCartesian(xSetPoint,adjusted[1], adjusted[2], time);
		
		putLegDown(time);
	}
	
	private void liftLeg(double time) {
		double [] current = getCartesian();
		setCartesian(xSetPoint,current[1], current[2]+.5, time);
		updateServos(time);
		flush();
		try {Thread.sleep((long) (time*1000));} catch (InterruptedException e) {}
	}
	private void putLegDown(double time) {
		updateServos(time);
		flush();
		try {Thread.sleep((long) (time*1000));} catch (InterruptedException e) {}
		setZ(zSetPoint, time);
		updateServos(time);
		flush();
		try {Thread.sleep((long) (time*1000));} catch (InterruptedException e) {}
	}
	
	public void fix(double time) {
		double [] current = getCartesianLocal();
		if(Math.abs(current[0])<(getHipLink().getLinkLen()*2) ) {
			System.out.println("Legnth too short");
			stepToSetpoint(time);
			return;
		}
		
		if(getAnkleLink().getAngle()>-50) {
			System.out.println("Ankle over extended");
			stepToSetpoint(time);
			return;
		}
		
		if(hitMaxHip()||hitMinHip()) {
			System.out.println("Fixing hip");
			if(hitMaxHip()) {
				toMinHip(time);
				return;
			}if(hitMinHip()) {
				toMaxHip(time);
				return;
			}
		}
		
	}
	public void updateServos(double time) {
		for(Link l: links ) {
			l.updateServo(time);
		}
	}
	public void flush() {
		for(Link l: links ) {
			l.flush();
		}
	}
	public double getThetaOffset() {
		return thetaOffset;
	}
	public void turn(double degrees, double time) {
		double rad = ToRadians(degrees);
		double [] current = getCartesian();
		double theta,currentVectLen,x,y;
		//System.out.println("Attempting to turn,  starting x "+current[0]+" starting y "+current[1] );
		theta = atan2(current[1], current[0])+rad;
		currentVectLen = Math.sqrt((current[1]*current[1])+(current[0]*current[0]));
		x=currentVectLen*cos(theta);
		y=currentVectLen*sin(theta);
		
		//System.out.println("Attempting to turn, vector legnth: "+currentVectLen + " angle: "+(theta/M_PI)*180+" new x "+x+" new y "+y  );
		setCartesian(x, y, current[2], time);
	}
	public void loadHomeValuesFromDyIO() {
		for(Link l: links ) {
			l.loadHomeValuesFromDyIO();
		}
	}
	public String getLegXML() {
		String s="	<leg>\n"+
"		<x>"+xOffset+"</x>\n"+
"		<y>"+yOffset+"</y>\n"+
"		<theta>"+thetaOffset+"</theta>\n";
		for(Link l: links ) {
			s+=l.getLinkXML();
		}
		s+="	</leg>\n";
		return s;
	}
	public double getLexXOffset() {
		// TODO Auto-generated method stub
		return xOffset;
	}
	public double  getLexYOffset() {
		// TODO Auto-generated method stub
		return yOffset;
	}
	public double  getLexThetaOffset() {
		// TODO Auto-generated method stub
		return thetaOffset;
	}
}
