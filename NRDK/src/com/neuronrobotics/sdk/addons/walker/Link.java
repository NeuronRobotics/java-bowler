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
		this.setScale(scale);
		this.setUpperLimit(upperLimit);
		this.setLowerLimit(lowerLimit);
		this.setHome(home);
		this.setServoChannel(srv);
		this.setType(type);
	}
	public void Home(){
		setPosition(this.getHome(),2);
		updateServo(2);
	}
	private void setPosition(int val,float time) {
		if(val>getUpperLimit())
			val=getUpperLimit();
		if(val<getLowerLimit()) {
			//System.out.println("Attempting to set to value:"+val+" is below limit:"+lowerLimit);
			val=getLowerLimit();
		}
		if(getServoSetPoint() == val)
			return;
		setServoSetPoint(val);
		updateServo(time);
	}
	public void updateServo(double time) {
		getServoChannel().SetPosition(getServoSetPoint(), (float) time);
	}
	
	public void incrementAngle(double inc,double time){
		setAngle(pos+inc,time);
	}
	public void setAngle(double pos,double time) {
		this.pos = pos;
		setPosition(((int) (pos/getScale()))+getHome(),(float) time);
	}
	public double getAngle() {
		return ((getServoSetPoint()-getHome())*getScale());
	}
	private void setLinkLen(double linkLen) {
		this.linkLen = linkLen;
	}
	public double getLinkLen() {
		return linkLen;
	}
	public void save() {
		getServoChannel().SavePosition(getServoSetPoint());
	}
	
	public double getMax() {
		// TODO Auto-generated method stub
		return (getUpperLimit()-getHome())*getScale();
	}
	public double getMin() {
		// TODO Auto-generated method stub
		return (getLowerLimit()-getHome())*getScale();
	}
	public boolean isMax() {
		if(getServoSetPoint() == getUpperLimit()) {
			System.out.println("Servo value is :" +getServoSetPoint()+" upper limit is"+ getUpperLimit());
			return true;
		}
		return false;
	}
	public boolean isMin() {
		if(getServoSetPoint() == getLowerLimit()) {
			System.out.println("Servo value is :" +getServoSetPoint()+" lower limit is"+ getLowerLimit());
			return true;
		}
		return false;
	}
	public void flush() {
		
		getServoChannel().flush();	
	}
	public void loadHomeValuesFromDyIO() {
		this.setHome(getServoChannel().getValue());
		if(getHome()>getUpperLimit())
			setUpperLimit(getHome()+1);
		if(getHome()<getLowerLimit())
			setLowerLimit(getHome()-1);
	}
	public void setCurrentAsUpperLimit() {
		setUpperLimit(getServoChannel().getValue());
	}
	public void setCurrentAsLowerLimit() {
		setLowerLimit(getServoChannel().getValue());
	}
	public void setCurrentAsAngle(double angle) {
		double current = (double)(getServoChannel().getValue()-getHome());
		if(current != 0)
			setScale(angle/current);
	}
	public String getLinkXML() {
		String s="		<link>\n"+
"			<ulimit>"+getUpperLimit()+"</ulimit>\n"+
"			<llimit>"+getLowerLimit()+"</llimit>\n"+
"			<home>"+getHome()+"</home>\n"+
"			<channel>"+getServoChannel().getChannel().getNumber()+"</channel>\n"+
"			<inverse>"+((getScale()>0)?1:-1)+"</inverse>\n"+
"			<linkLen>"+linkLen+"</linkLen>\n"+
"			<scale>"+Math.abs(getScale())+"</scale>\n"+
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
		//System.out.println("Setting new servo channel: "+srv.getChannel().getNumber());
		srv.getChannel().setCachedMode(true);
		this.srv = srv;
	}
	public ServoChannel getServoChannel() {
		return srv;
	}
	public void setScale(double scale) {
		this.scale = scale;
	}
	public double getScale() {
		return scale;
	}
	public void setUpperLimit(int upperLimit) {
		this.upperLimit = upperLimit;
	}
	public int getUpperLimit() {
		return upperLimit;
	}
	public void setLowerLimit(int lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public int getLowerLimit() {
		return lowerLimit;
	}
	public void setHome(int home) {
		this.home = home;
	}
	public int getHome() {
		return home;
	}
	public void setServoValue(int val) {
		setPosition(val,0);
		srv.flush();
	}
	public void setServoSetPoint(int srvVal) {
		this.srvVal = srvVal;
	}
	public int getServoSetPoint() {
		return srvVal;
	}
}
