package com.neuronrobotics.sdk.addons.walker;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class Leg.
 */
public class Leg {
	
	/** The links. */
	ArrayList<WalkerServoLink> links = new ArrayList<WalkerServoLink>();
	
	/** The Constant M_PI. */
	private static final double M_PI = Math.PI; 
	
	/** The theta offset. */
	private double xOffset,yOffset,thetaOffset;
	//private double xLockSetPoint;
	/** The z set point. */
	//private double xLocal,yLocal,zLocal;
	private double xSetPoint,ySetPoint,zSetPoint;
	
	/** The got hip. */
	private boolean gotHip=false;
	
	/** The got knee. */
	private boolean gotKnee=false;
	
	/** The got ankle. */
	private boolean gotAnkle=false;
	
	/**
	 * Instantiates a new leg.
	 *
	 * @param x the x
	 * @param y the y
	 * @param theta the theta
	 */
	public Leg(double x, double y, double theta){
		this.xOffset=x;
		this.yOffset=y;
		this.thetaOffset=theta;
		links.add(null);
		links.add(null);
		links.add(null);
	}
	
	/**
	 * Gets the hip link.
	 *
	 * @return the hip link
	 */
	public WalkerServoLink getHipLink() {
		return links.get(0);
	}
	
	/**
	 * Gets the knee link.
	 *
	 * @return the knee link
	 */
	public WalkerServoLink getKneeLink() {
		return links.get(1);
	}
	
	/**
	 * Gets the ankle link.
	 *
	 * @return the ankle link
	 */
	public WalkerServoLink getAnkleLink() {
		return links.get(2);
	}
	
	/**
	 * Adds the link.
	 *
	 * @param l the l
	 */
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
	
	/**
	 * Leg ok.
	 *
	 * @return true, if successful
	 */
	public boolean legOk(){
		if((gotHip && gotKnee && gotAnkle))			
			loadCartesianLocal();
		return (gotHip && gotKnee && gotAnkle);
	}
	
	/**
	 * Increment hip.
	 *
	 * @param inc the inc
	 */
	public void incrementHip(double inc){
		getHipLink().incrementAngle(inc);
		//getHipLink().flush(time);
	}
	
	/**
	 * Increment knee.
	 *
	 * @param inc the inc
	 */
	public void incrementKnee(double inc){
		getKneeLink().incrementAngle(inc);
		//getKneeLink().flush(time);
	}
	
	/**
	 * Increment ankle.
	 *
	 * @param inc the inc
	 */
	public void incrementAnkle(double inc){
		getAnkleLink().incrementAngle(inc);
		//getAnkleLink().flush(time);
	}
	
	/**
	 * Sets the hip.
	 *
	 * @param inc the new hip
	 */
	public void setHip(double inc){
		try{
			getHipLink().setTargetAngle(inc);
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		//getHipLink().flush(time);
	}
	
	/**
	 * Sets the knee.
	 *
	 * @param inc the new knee
	 */
	public void setKnee(double inc){
		try{
			getKneeLink().setTargetAngle(inc);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//getKneeLink().flush(time);
	}
	
	/**
	 * Sets the ankle.
	 *
	 * @param inc the new ankle
	 */
	public void setAnkle(double inc){
		try{
	
			getAnkleLink().setTargetAngle(inc);
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		//getAnkleLink().flush(time);
	}
	
	/**
	 * Calc cartesian local.
	 *
	 * @param hip the hip
	 * @param knee the knee
	 * @param ankle the ankle
	 * @return the double[]
	 */
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
	
	/**
	 * Load cartesian local.
	 *
	 * @return the double[]
	 */
	private double [] loadCartesianLocal(){
		double hip = (getHipLink().getTargetAngle());
		double knee = (getKneeLink().getTargetAngle());
		double ankle = (getAnkleLink().getTargetAngle());
		return calcCartesianLocal(hip,knee,ankle);
	}
	
	/**
	 * Calc cartesian.
	 *
	 * @param loc the loc
	 * @return the double[]
	 */
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
	
	/**
	 * Gets the cartesian.
	 *
	 * @return the cartesian
	 */
	public double [] getCartesian(){
		return calcCartesian(loadCartesianLocal());
	}
	
	/**
	 * Gets the cartesian local.
	 *
	 * @return the cartesian local
	 */
	public double [] getCartesianLocal(){
		return loadCartesianLocal();
	}
	
	/**
	 * Increment x.
	 *
	 * @param val the val
	 */
	public void incrementX(double val){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0]+val,pos[1],pos[2]);
		}catch(RuntimeException e) {
			stepToSetpoint();
		}
		fix();
	}
	
	/**
	 * Increment y.
	 *
	 * @param val the val
	 */
	public void incrementY(double val){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0],pos[1]+val,pos[2]);
		}catch(RuntimeException e) {
			Log.error("Error in increment y");
			e.printStackTrace();
			stepToSetpoint();
		}
		//fix(time);
	}
	
	/**
	 * Increment z.
	 *
	 * @param val the val
	 */
	public void incrementZ(double val){
		double [] pos = getCartesian();
		try {
			setCartesian(pos[0],pos[1],pos[2]+val);
		}catch(RuntimeException e) {
			stepToSetpoint();
		}
		fix();
	}
	
	/**
	 * Sets the z.
	 *
	 * @param val the new z
	 */
	public void setZ(double val) {
		double [] pos = getCartesian();
		setCartesian(pos[0],pos[1],val);
	}
	
	/**
	 * Sets the cartesian.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void setCartesian(double x,double y,double z){
		x-=xOffset;
		y-=yOffset;
		double vect =  sqrt(x*x+y*y);
		double angle = atan2(y,x)-ToRadians(thetaOffset);
		x=cos(angle)*vect;
		y=sin(angle)*vect;
		setCartesianLocal(x,y,z);
	}
	
	/**
	 * Sets the cartesian local.
	 *
	 * @param xSet the x set
	 * @param ySet the y set
	 * @param zSet the z set
	 */
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
	 * Home.
	 */
	public void Home() {
		for(WalkerServoLink l: links ) {
			l.Home();
		}
	}
	
	/**
	 * Save.
	 */
	public void save() {
		for(WalkerServoLink l: links ) {
			l.save();
		}
		
	}
	
	/**
	 * Hit max angle hip.
	 *
	 * @return true, if successful
	 */
	public boolean hitMaxAngleHip() {
		return getHipLink().isMaxAngle();
	}
	
	/**
	 * Hit min angle hip.
	 *
	 * @return true, if successful
	 */
	public boolean hitMinAngleHip() {
		return getHipLink().isMinAngle();
	}
	
	/**
	 * Sets the start point.
	 */
	public void setStartPoint() {
		double [] start = getCartesian();
		xSetPoint=start[0];
		ySetPoint=start[1];
		zSetPoint=start[2];
	}
	
	/**
	 * To min angle hip.
	 */
	public void toMinAngleHip() {
		stepToHipAngle(getHipLink().getMinAngle());
	}
	
	/**
	 * To max angle hip.
	 */
	public void toMaxAngleHip() {
		stepToHipAngle(getHipLink().getMaxAngle());
	}
	
	/**
	 * Step to setpoint.
	 */
	public void stepToSetpoint() {
		double [] current = getCartesian();

		liftLeg();
		
		setCartesian(xSetPoint,ySetPoint, current[2]+.2);
		
		putLegDown();
	}
	
	
	/** The reset time. */
	private double resetTime = 0;
	
	/**
	 * Step to hip angle.
	 *
	 * @param hip the hip
	 */
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
	
	/**
	 * Lift leg.
	 */
	private void liftLeg() {
		//System.out.println("Lifting leg ");
		double [] current = getCartesian();
		setCartesian(xSetPoint,current[1], current[2]+.5);
		cacheLinkPositions();
		flush(resetTime);
		//try {Thread.sleep((long) (resetTime*1000));} catch (InterruptedException e) {}
		//System.out.println("Lifting leg done");
	}
	
	/**
	 * Put leg down.
	 */
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
	
	/**
	 * Fix.
	 */
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
	
	/**
	 * Cache link positions.
	 */
	public void cacheLinkPositions() {
		for(WalkerServoLink l: links ) {
			l.cacheTargetValue();
		}
	}
	
	/**
	 * Flush.
	 *
	 * @param time the time
	 */
	public void flush(double time) {
		for(WalkerServoLink l: links ) {
			try {
				l.flush(time);
			}catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the theta offset.
	 *
	 * @return the theta offset
	 */
	public double getThetaOffset() {
		return thetaOffset;
	}
	
	/**
	 * Turn.
	 *
	 * @param degrees the degrees
	 */
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
	
	/**
	 * Load home values from dy io.
	 */
	public void loadHomeValuesFromDyIO() {
		for(WalkerServoLink l: links ) {
			l.loadHomeValuesFromDyIO();
		}
	}
	
	/**
	 * Gets the leg xml.
	 *
	 * @return the leg xml
	 */
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
	
	/**
	 * Gets the lex x offset.
	 *
	 * @return the lex x offset
	 */
	public double getLexXOffset() {
		// TODO Auto-generated method stub
		return xOffset;
	}
	
	/**
	 * Gets the lex y offset.
	 *
	 * @return the lex y offset
	 */
	public double  getLexYOffset() {
		// TODO Auto-generated method stub
		return yOffset;
	}
	
	/**
	 * Gets the lex theta offset.
	 *
	 * @return the lex theta offset
	 */
	public double  getLexThetaOffset() {
		// TODO Auto-generated method stub
		return thetaOffset;
	}
}
