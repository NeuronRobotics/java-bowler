package com.neuronrobotics.sdk.addons.walker;


import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

// TODO: Auto-generated Javadoc
/**
 * The Class WalkerServoLink.
 */
public class WalkerServoLink extends ServoRotoryLink {
	
	/** The link len. */
	private double linkLen;
	
	/** The type. */
	private String type;
	
	/**
	 * Instantiates a new walker servo link.
	 *
	 * @param srv the srv
	 * @param conf the conf
	 * @param linkLen the link len
	 * @param type the type
	 */
	public WalkerServoLink(ServoChannel srv,LinkConfiguration conf, double linkLen, String type) {
		super(srv,conf);
		setLinkLen(linkLen);
		setType(type);
	}
	
	/**
	 * Sets the link len.
	 *
	 * @param linkLen the new link len
	 */
	private void setLinkLen(double linkLen) {
		this.linkLen = linkLen;
	}
	
	/**
	 * Gets the link len.
	 *
	 * @return the link len
	 */
	public double getLinkLen() {
		return linkLen;
	}

	/**
	 * Load home values from dy io.
	 */
	public void loadHomeValuesFromDyIO() {
		this.setHome(getCurrentPosition());
		if(getHome()>getUpperLimit())
			setUpperLimit(getHome()+1);
		if(getHome()<getLowerLimit())
			setLowerLimit(getHome()-1);
	}

	/**
	 * Gets the link xml.
	 *
	 * @return the link xml
	 */
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
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink#flush(double)
	 */
	@Override
	public void flush(double time) {
		super.flush(time);
	}



}
