package com.neuronrobotics.sdk.addons.walker;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class WalkerServoLink extends AbstractLink {
	private double linkLen;
	private ServoChannel srv;
	private String type;
	
	public WalkerServoLink(ServoChannel srv,int home,int lowerLimit,int upperLimit,double scale,double linkLen, String type){
		super(home,lowerLimit,upperLimit,scale);
		setLinkLen(linkLen);
		setServoChannel(srv);
		setType(type);
	}
	
	private void setLinkLen(double linkLen) {
		this.linkLen = linkLen;
	}
	public double getLinkLen() {
		return linkLen;
	}
	public void save() {
		getServoChannel().SavePosition(getTargetValue());
	}

	public void loadHomeValuesFromDyIO() {
		this.setHome(getCurrentPosition());
		if(getHome()>getUpperLimit())
			setUpperLimit(getHome()+1);
		if(getHome()<getLowerLimit())
			setLowerLimit(getHome()-1);
	}
	public void setCurrentAsUpperLimit() {
		setUpperLimit(getCurrentPosition());
	}
	public void setCurrentAsLowerLimit() {
		setLowerLimit(getCurrentPosition());
	}
	public void setCurrentAsAngle(double angle) {
		double current = (double)(getCurrentPosition()-getHome());
		if(current != 0)
			setScale(angle/current);
	}
	public String getLinkXML() {
		String s="		<link>\n"+
"			<ulimit>"+getUpperLimit()+"</ulimit>\n"+
"			<llimit>"+getLowerLimit()+"</llimit>\n"+
"			<home>"+getHome()+"</home>\n"+
"			<channel>"+getServoChannel().getChannel().getChannelNumber()+"</channel>\n"+
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
	
	public void setServoValue(int val) {
		setPosition(val,0);
		srv.flush();
	}

	@Override
	public void cacheTargetValue() {
		getServoChannel().SetPosition(getTargetValue());
	}
	@Override
	public void flush(double time) {
		getServoChannel().SetPosition(getTargetValue(),(float) time);
		getServoChannel().flush();	
	}
	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return getServoChannel().getValue();
	}

}
