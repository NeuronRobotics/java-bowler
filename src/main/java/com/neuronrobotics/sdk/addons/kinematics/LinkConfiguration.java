package com.neuronrobotics.sdk.addons.kinematics;

import org.w3c.dom.Element;

import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.Log;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace;
import com.neuronrobotics.sdk.pid.PIDConfiguration;



public class LinkConfiguration {
	private String name="newLink";// = getTagValue("name",eElement);
	private LinkType type=LinkType.VIRTUAL;
	private int index=0;// = Double.parseDouble(getTagValue("index",eElement));
	private int totlaNumberOfLinks=0;
	private int linkIndex = 0;
	//private double length;// = Double.parseDouble(getTagValue("length",eElement));
	private double scale=1.0;// = Double.parseDouble(getTagValue("scale",eElement));
	private double upperLimit=100000;// = Double.parseDouble(getTagValue("upperLimit",eElement));
	private double lowerLimit=-100000;// = Double.parseDouble(getTagValue("lowerLimit",eElement));
	private double k[] = new double[]{1,0,0};
	private boolean inverted=false;
	private boolean isLatch=false;
	private int indexLatch=0;
	private boolean isStopOnLatch=false;
	private int homingTicksPerSecond=10000000;
	private double upperVelocity = 100000000;
	private double lowerVelocity = -100000000;
	private String deviceScriptingName=null;
	private double staticOffset=0;
	
	public LinkConfiguration(Element eElement){
    	setName(XmlFactory.getTagValue("name",eElement));
    	setHardwareIndex(Integer.parseInt(XmlFactory.getTagValue("index",eElement)));
    	setScale(Double.parseDouble(XmlFactory.getTagValue("scale",eElement)));
    	setUpperLimit(Double.parseDouble(XmlFactory.getTagValue("upperLimit",eElement)));
    	setLowerLimit(Double.parseDouble(XmlFactory.getTagValue("lowerLimit",eElement)));
    	try{
    		setDeviceScriptingName(XmlFactory.getTagValue("deviceName",eElement));		
    	}catch(NullPointerException e){
    		// no device from connection engine specified
    	}
    	try{
    		setType(LinkType.fromString(XmlFactory.getTagValue("type",eElement)));
    	}catch (NullPointerException e){
    		setType(LinkType.PID);
    	}
    	if(getType()==LinkType.PID){
    		try{
		    	k[0]=Double.parseDouble(XmlFactory.getTagValue("pGain",eElement));
		    	k[1]=Double.parseDouble(XmlFactory.getTagValue("iGain",eElement));
		    	k[2]=Double.parseDouble(XmlFactory.getTagValue("dGain",eElement));
		    	inverted=XmlFactory.getTagValue("isInverted",eElement).contains("true");
		    	setHomingTicksPerSecond(Integer.parseInt(XmlFactory.getTagValue("homingTPS",eElement)));
    		}catch (Exception ex){}
    	}
    	
    	try{
    		setUpperVelocity(Double.parseDouble(XmlFactory.getTagValue("upperVelocity",eElement)));
    		setLowerVelocity(Double.parseDouble(XmlFactory.getTagValue("lowerVelocity",eElement)));
    	}catch (Exception e){
    		
    	}
    	try{
    		setStaticOffset(Double.parseDouble(XmlFactory.getTagValue("staticOffset",eElement)));
    	}catch (Exception e){
    		
    	}
    	
    	isLatch=XmlFactory.getTagValue("isLatch",eElement).contains("true");
    	indexLatch=Integer.parseInt(XmlFactory.getTagValue("indexLatch",eElement));
    	isStopOnLatch=XmlFactory.getTagValue("isStopOnLatch",eElement).contains("true");
    	if(staticOffset>getUpperLimit() || staticOffset<getLowerLimit() )
    	   Log.error("PID group "+getHardwareIndex()+" staticOffset is "+staticOffset+" but needs to be between "+getUpperLimit()+" and "+getLowerLimit());
    	//System.out.println("Interted"+ inverted);
	}
	public LinkConfiguration(Object[] args) {
		setName((String)args[6]);
		setHardwareIndex((Integer)args[0]);
    	setScale((Double)args[5]);
    	setUpperLimit((Integer)args[4]);
    	setLowerLimit((Integer)args[3]);
    	setType(LinkType.PID);
    	setTotlaNumberOfLinks((Integer)args[1]);
	}
	public LinkConfiguration() {
		//default values
	}

	public LinkConfiguration(int home, int llimit, int ulimit, double d) {
    	setScale(d);
    	setUpperLimit(ulimit);
    	setLowerLimit(llimit);
    	setStaticOffset(home);
	}
	public String toString(){
		String s="LinkConfiguration: \n\tName: "+getName();
		if(deviceScriptingName!=null)
			s="Device Name: \n\tName: "+getDeviceScriptingName();
		s+=	"\n\tType: "+getType();
		s+=	"\n\tHardware Board Index: "+getHardwareIndex();
		s+=	"\n\tScale: "+getScale();
		s+=	"\n\tUpper Limit: "+getUpperLimit();
		s+=	"\n\tLower Limit: "+getLowerLimit();
		s+=	"\n\tHoming Ticks Per Second: "+getHomingTicksPerSecond();
		return s;
	}
	
	/*
	 * 
	 * Generate the xml configuration to generate a link of this configuration. 
	 */
	public String getXml(){
		String DevStr=deviceScriptingName!= null?"<deviceName>"+getDeviceScriptingName()+"</deviceName>\n":"";
		
		return "\t<name>"+getName()+"</name>\n"+
				"\t"+DevStr+
				"\t<type>"+getType()+"</type>\n"+
				"\t<index>"+getHardwareIndex()+"</index>\n"+
				"\t<scale>"+getScale()+"</scale>\n"+
				"\t<upperLimit>"+getUpperLimit()+"</upperLimit>\n"+
				"\t<lowerLimit>"+getLowerLimit()+"</lowerLimit>\n"+
				"\t<upperVelocity>"+upperVelocity+"</upperVelocity>\n"+
				"\t<lowerVelocity>"+lowerVelocity+"</lowerVelocity>\n"+
				"\t<staticOffset>"+staticOffset+"</staticOffset>\n"+
				"\t<isLatch>"+isLatch+"</isLatch>\n"+
				"\t<indexLatch>"+indexLatch+"</indexLatch>\n"+
				"\t<isStopOnLatch>"+isStopOnLatch+"</isStopOnLatch>\n"+	
				"\t<homingTPS>"+getHomingTicksPerSecond()+"</homingTPS>\n";
	}
	

	public void setName(String name) {
		Log.info("Setting controller name: "+name);
		this.name = name;
	}
	public String getName() {
		return name;
	}
	/**
	 * sets the hardware index for maping this kinematics link to its assocaited hardware index
	 * @param index
	 */
	public void setHardwareIndex(int index) {
		this.index = index;
	}
	/**
	 * gets the hardware index for maping this kinematics link to its assocaited hardware index
	 * @param index
	 */
	public int getHardwareIndex() {
		return index;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
	public double getScale() {
		return scale;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}
	public double getUpperLimit() {
		return upperLimit;
	}
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public double getLowerLimit() {
		return lowerLimit;
	}
	public double getKP() {
		return k[0];
	}
	public double getKI() {
		return k[1];
	}
	public double getKD() {
		return k[2];
	}
	public void setKP(double kP) {
		k[0] = kP;
	}
	public void setKI(double kI) {
		k[1] = kI;
	}
	public void setKD(double kD) {
		k[2] = kD;
	}
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
	public boolean isInverted() {
		return inverted;
	}
	public void setIndexLatch(int indexLatch) {
		this.indexLatch = indexLatch;
	}
	public int getIndexLatch() {
		return indexLatch;
	}
	public void setLatch(boolean isLatch) {
		this.isLatch = isLatch;
	}
	public boolean isLatch() {
		return isLatch;
	}
	public void setStopOnLatch(boolean isStopOnLatch) {
		this.isStopOnLatch = isStopOnLatch;
	}
	public boolean isStopOnLatch() {
		return isStopOnLatch;
	}
	public void setHomingTicksPerSecond(int homingTicksPerSecond) {
		this.homingTicksPerSecond = homingTicksPerSecond;
	}
	public int getHomingTicksPerSecond() {
		return homingTicksPerSecond;
	}
	public void setType(LinkType type) {
		if(type!=null)
			this.type = type;
		else
			this.type=LinkType.VIRTUAL;
	}
	public LinkType getType() {
		return type;
	}
	public void setUpperVelocity(double upperVelocity) {
		this.upperVelocity = upperVelocity;
	}
	public double getUpperVelocity() {
		return upperVelocity;
	}
	public void setLowerVelocity(double lowerVelocity) {
		this.lowerVelocity = lowerVelocity;
	}
	public double getLowerVelocity() {
		return lowerVelocity;
	}
	/**
	 * THis is the index of this link in its kinematics chain
	 * @return
	 */
	public int getLinkIndex() {
		return linkIndex;
	}
	/**
	 * This sets the index of the link in itts kinematic chain
	 * @param linkIndex
	 */
	public void setLinkIndex(int linkIndex) {
		this.linkIndex = linkIndex;
	}
	public int getTotlaNumberOfLinks() {
		return totlaNumberOfLinks;
	}
	public void setTotlaNumberOfLinks(int totlaNumberOfLinks) {
		this.totlaNumberOfLinks = totlaNumberOfLinks;
	}
	
	public PIDConfiguration getPidConfiguration(){
		PIDConfiguration pid = new PIDConfiguration();
		pid.setKD(getKD());
		pid.setGroup(getHardwareIndex());
		pid.setStopOnIndex(isStopOnLatch());
		pid.setKI(getKI());
		pid.setKP(getKP());
		pid.setIndexLatch(getIndexLatch());
		pid.setInverted(isInverted());
		return pid;
	}
	public void setPidConfiguration(IPidControlNamespace pid) {
		PIDConfiguration conf = pid.getPIDConfiguration(getHardwareIndex());
    	if(getType()==LinkType.PID){
	    	k[0]=conf.getKP();
	    	k[1]=conf.getKI();
	    	k[2]=conf.getKD();
	    	inverted=conf.isInverted();
	    	setHomingTicksPerSecond(10000);
    	}
    	
    	isLatch=conf.isUseLatch();
    	indexLatch=(int) conf.getIndexLatch();
    	isStopOnLatch=conf.isStopOnIndex();
//    	if(indexLatch>getUpperLimit() || indexLatch<getLowerLimit() )
//    	    throw new RuntimeException("PID group "+getHardwareIndex()+" Index latch is "+indexLatch+" but needs to be between "+getUpperLimit()+" and "+getLowerLimit());
    	
	}
	public String getDeviceScriptingName() {
		return deviceScriptingName;
	}
	public void setDeviceScriptingName(String deviceScriptingName) {
		this.deviceScriptingName = deviceScriptingName;
	}
	public double getStaticOffset() {
		return staticOffset;
	}
	public void setStaticOffset(double staticOffset) {
		this.staticOffset = staticOffset;
	}
	
}
