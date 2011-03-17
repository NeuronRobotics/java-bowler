package com.neuronrobotics.sdk.addons.walker;

import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class Link {
	private double linkLen;
	private double scale;
	private int upperLimit;
	private int lowerLimit;
	private int home;
	private ServoChannel srv;
	//Position set point
	private double pos=0;
	//Actual servo position
	private int srvVal=0;
	//private float time=0;
	private String type;
	
	public Link(ServoChannel srv,int home,int lowerLimit,int upperLimit,double scale,double linkLen, String type){
		this.setLinkLen(linkLen);
		this.scale=scale;
		this.upperLimit=upperLimit;
		this.lowerLimit=lowerLimit;
		this.home=home;
		this.setServoChannel(srv);
		this.setType(type);
	}
	public void Home(){
		setPosition(this.home,2);
		updateServo(2);
	}
	private void setPosition(int val,float time) {
		if(val>upperLimit)
			val=upperLimit;
		if(val<lowerLimit) {
			//System.out.println("Attempting to set to value:"+val+" is below limit:"+lowerLimit);
			val=lowerLimit;
		}
		if(srvVal == val)
			return;
		srvVal=val;
	}
	public void updateServo(double time) {
		getServoChannel().SetPosition(srvVal, (float) time);
	}
	
	public void incrementAngle(double inc,double time){
		setAngle(pos+inc,time);
	}
	public void setAngle(double pos,double time) {
		this.pos = pos;
		setPosition(((int) (pos/scale))+home,(float) time);
	}
	public double getAngle() {
		return ((srvVal-home)*scale);
	}
	private void setLinkLen(double linkLen) {
		this.linkLen = linkLen;
	}
	public double getLinkLen() {
		return linkLen;
	}
	public void save() {
		getServoChannel().SavePosition(srvVal);
	}
	public double getMax() {
		// TODO Auto-generated method stub
		return (upperLimit-home)*scale;
	}
	public double getMin() {
		// TODO Auto-generated method stub
		return (lowerLimit-home)*scale;
	}
	public boolean isMax() {
		if(srvVal == upperLimit) {
			System.out.println("Servo value is :" +srvVal+" upper limit is"+ upperLimit);
			return true;
		}
		return false;
	}
	public boolean isMin() {
		if(srvVal == lowerLimit) {
			System.out.println("Servo value is :" +srvVal+" lower limit is"+ lowerLimit);
			return true;
		}
		return false;
	}
	public void flush() {
		getServoChannel().flush();	
	}
	public void loadHomeValuesFromDyIO() {
		this.home = getServoChannel().getValue();
		if(home>upperLimit)
			upperLimit=home+1;
		if(home<lowerLimit)
			lowerLimit=home-1;
	}
	public void setCurrentAsUpperLimit() {
		upperLimit = getServoChannel().getValue();
	}
	public void setCurrentAsLowerLimit() {
		lowerLimit = getServoChannel().getValue();
	}
	public void setCurrentAsAngle(double angle) {
		double current = (double)(getServoChannel().getValue()-home);
		scale = angle/current;
	}
	public String getLinkXML() {
		String s="		<link>\n"+
"			<ulimit>"+upperLimit+"</ulimit>\n"+
"			<llimit>"+lowerLimit+"</llimit>\n"+
"			<home>"+home+"</home>\n"+
"			<channel>"+getServoChannel().getChannel().getNumber()+"</channel>\n"+
"			<inverse>"+((scale>0)?1:-1)+"</inverse>\n"+
"			<linkLen>"+linkLen+"</linkLen>\n"+
"			<scale>"+Math.abs(scale)+"</scale>\n"+
"			<type>"+getType()+"</type>\n"+
"		</link>\n";
		return s;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setServoChannel(ServoChannel srv) {
		this.srv = srv;
	}
	public ServoChannel getServoChannel() {
		return srv;
	}
}
