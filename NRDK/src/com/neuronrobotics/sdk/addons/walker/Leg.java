package com.neuronrobotics.sdk.addons.walker;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.Log;

public class Leg {
	ArrayList<WalkerServoLink> links = new ArrayList<WalkerServoLink>();
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
	public WalkerServoLink getHipLink() {
		return links.get(0);
	}
	public WalkerServoLink getKneeLink() {
		return links.get(1);
	}
	public WalkerServoLink getAnkleLink() {
		return links.get(2);
	}
	public void addLink(WalkerServoLink l){
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
	public void incrementHip(double inc){
		getHipLink().incrementAngle(inc);
		//getHipLink().flush(time);
	}
	public void incrementKnee(double inc){
		getKneeLink().incrementAngle(inc);
		//getKneeLink().flush(time);
	}
	public void incrementAnkle(double inc){
		getAnkleLink().incrementAngle(inc);
		//getAnkleLink().flush(time);
	}
	
	public void setHip(double inc){
		try{
			getHipLink().setTargetAngle(inc);
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		//getHipLink().flush(time);
	}
	public void setKnee(double inc){
		try{
			getKneeLink().setTargetAngle(inc);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//getKneeLink().flush(time);
	}
	public void setAnkle(double inc){
		try{
	
			getAnkleLink().setTargetAngle(inc);
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		//getAnkleLink().flush(time);
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
		double hip = (getHipLink().getTargetAngle());
		double knee = (getKneeLink().getTargetAngle());
		double ankle = (getAnkleLink().getTargetAngle());
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
	public void incrementX(double val){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0]+val,pos[1],pos[2]);
		}catch(RuntimeException e) {
			stepToSetpoint();
		}
		fix();
	}
	public void incrementY(double val){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0],pos[1]+val,pos[2]);
		}catch(RuntimeException e) {
			Log.enableDebugPrint(true);
			Log.error("Error in increment y");
			e.printStackTrace();
			stepToSetpoint();
		}
		//fix(time);
	}
	public void incrementZ(double val){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0],pos[1],pos[2]+val);
		}catch(RuntimeException e) {
			stepToSetpoint();
		}
		fix();
	}
	
	public void setZ(double val) {
		double [] pos = getCartesian();
		setCartesian(pos[0],pos[1],val);
	}
	
	public void setCartesian(double x,double y,double z){
		x-=xOffset;
		y-=yOffset;
		double vect =  sqrt(x*x+y*y);
		double angle = atan2(y,x)-ToRadians(thetaOffset);
		x=cos(angle)*vect;
		y=sin(angle)*vect;
		setCartesianLocal(x,y,z);
	}
	public void setCartesianLocal(double xSet,double ySet,double zSet){
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
		setHip(hip);
		setKnee(knee);
		setAnkle(ankle);
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
		for(WalkerServoLink l: links ) {
			l.Home();
		}
	}
	public void save() {
		for(WalkerServoLink l: links ) {
			l.save();
		}
		
	}
	public boolean hitMaxAngleHip() {
		return getHipLink().isMaxAngle();
	}
	public boolean hitMinAngleHip() {
		return getHipLink().isMinAngle();
	}
	public void setStartPoint() {
		double [] start = getCartesian();
		xSetPoint=start[0];
		ySetPoint=start[1];
		zSetPoint=start[2];
	}
	public void toMinAngleHip() {
		stepToHipAngle(getHipLink().getMinAngle()+10);
	}
	public void toMaxAngleHip() {
		stepToHipAngle(getHipLink().getMaxAngle()-10);
	}
	public void stepToSetpoint() {
		double [] current = getCartesian();

		liftLeg();
		
		setCartesian(xSetPoint,ySetPoint, current[2]+1);
		
		putLegDown();
	}
	
	
	private double resetTime = 0;
	
	public void stepToHipAngle(double hip) {

		liftLeg();
		try{
			getHipLink().setTargetAngle(hip);
		}catch(Exception ex){
			//ex.printStackTrace();
		}

		double [] adjusted = getCartesian();
		setCartesian(xSetPoint,adjusted[1], adjusted[2]);
		
		putLegDown();
	}
	
	private void liftLeg() {
		//System.out.println("Lifting leg ");
		double [] current = getCartesian();
		setCartesian(xSetPoint,current[1], current[2]+.5);
		cacheLinkPositions();
		flush(resetTime);
		//try {Thread.sleep((long) (resetTime*1000));} catch (InterruptedException e) {}
		//System.out.println("Lifting leg done");
	}
	
	private void putLegDown() {
		//System.out.println("Putting leg down");
		cacheLinkPositions();
		flush(resetTime);
		//try {Thread.sleep((long) (resetTime*1000));} catch (InterruptedException e) {}
		setZ(zSetPoint);
		cacheLinkPositions();
		flush(resetTime);
		//try {Thread.sleep((long) (resetTime*1000));} catch (InterruptedException e) {}
		//System.out.println("Putting leg down done");
	}
	
	public void fix() {
		double [] current = getCartesianLocal();
		if(Math.abs(current[0])<(getHipLink().getLinkLen()*2) ) {
			//System.out.println("Legnth too short");
			stepToSetpoint();
			return;
		}
		
		if(getAnkleLink().getTargetAngle()>-50) {
			//System.out.println("Ankle over extended");
			stepToSetpoint();
			return;
		}
		
		if(hitMaxAngleHip()||hitMinAngleHip()) {
			//System.out.println("Fixing hip");
			if(hitMaxAngleHip()) {
				toMinAngleHip();
				return;
			}if(hitMinAngleHip()) {
				toMaxAngleHip();
				return;
			}
		}
		
	}
	public void cacheLinkPositions() {
		for(WalkerServoLink l: links ) {
			l.cacheTargetValue();
		}
	}
	public void flush(double time) {
		for(WalkerServoLink l: links ) {
			try {
				l.flush(time);
			}catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}
	public double getThetaOffset() {
		return thetaOffset;
	}
	public void turn(double degrees) {
		double rad = ToRadians(degrees);
		double [] current = getCartesian();
		double theta,currentVectLen,x,y;
		//System.out.println("Attempting to turn,  starting x "+current[0]+" starting y "+current[1] );
		theta = atan2(current[1], current[0])+rad;
		currentVectLen = Math.sqrt((current[1]*current[1])+(current[0]*current[0]));
		x=currentVectLen*cos(theta);
		y=currentVectLen*sin(theta);
		
		//System.out.println("Attempting to turn, vector legnth: "+currentVectLen + " angle: "+(theta/M_PI)*180+" new x "+x+" new y "+y  );
		setCartesian(x, y, current[2]);
	}
	public void loadHomeValuesFromDyIO() {
		for(WalkerServoLink l: links ) {
			l.loadHomeValuesFromDyIO();
		}
	}
	public String getLegXML() {
		String s="	<leg>\n"+
"		<x>"+xOffset+"</x>\n"+
"		<y>"+yOffset+"</y>\n"+
"		<theta>"+thetaOffset+"</theta>\n";
		for(WalkerServoLink l: links ) {
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
