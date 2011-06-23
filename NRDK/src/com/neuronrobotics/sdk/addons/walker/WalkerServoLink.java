package com.neuronrobotics.sdk.addons.walker;


import com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class WalkerServoLink extends ServoRotoryLink {
	private double linkLen;
	private String type;
	
	public WalkerServoLink(ServoChannel srv,int home,int lowerLimit,int upperLimit,double scale,double linkLen, String type){
		super(srv,home,lowerLimit,upperLimit,scale);
		setLinkLen(linkLen);
		setType(type);
	}
	
	private void setLinkLen(double linkLen) {
		this.linkLen = linkLen;
	}
	public double getLinkLen() {
		return linkLen;
	}

	public void loadHomeValuesFromDyIO() {
		this.setHome(getCurrentPosition());
		if(getHome()>getUpperLimit())
			setUpperLimit(getHome()+1);
		if(getHome()<getLowerLimit())
			setLowerLimit(getHome()-1);
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


}
