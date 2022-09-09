package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.addons.kinematics.math.ITransformNRChangeListener;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.Log;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace;
import com.neuronrobotics.sdk.pid.PIDConfiguration;



// TODO: Auto-generated Javadoc
/**
 * The Class LinkConfiguration.
 */
public class LinkConfiguration implements ITransformNRChangeListener {
	private ArrayList<ILinkConfigurationChangeListener> listeners=null;
	/** The name. */
	private String name="newLink";// = getTagValue("name",eElement);
	
	/** The type. */
	private String type="virtual";
	
	/** The index. */
	private int index=0;// = Double.parseDouble(getTagValue("index",eElement));
	
	/** The totla number of links. */
	private int totlaNumberOfLinks=0;
	
	/** The link index. */
	private int linkIndex = 0;
	
	/** The scale. */
	//private double length;// = Double.parseDouble(getTagValue("length",eElement));
	private double scale=1.0;// = Double.parseDouble(getTagValue("scale",eElement));
	
	/** The upper limit. */
	private double upperLimit=100000;// = Double.parseDouble(getTagValue("upperLimit",eElement));
	
	/** The lower limit. */
	private double lowerLimit=-100000;// = Double.parseDouble(getTagValue("lowerLimit",eElement));
	
	/** The k. */
	private double k[] = new double[]{1,0,0};
	
	/** The inverted. */
	private boolean inverted=false;
	
	/** The is latch. */
	private boolean isLatch=false;
	
	/** The index latch. */
	private double indexLatch=0;
	
	/** The is stop on latch. */
	private boolean isStopOnLatch=false;
	
	/** The homing ticks per second. */
	private int homingTicksPerSecond=10000000;
	
	/** The upper velocity. */
	private double velocityLimit = 100000000;
	
	/** The device scripting name. */
	private String deviceScriptingName=null;
	private double deviceTheoreticalMax =180;
	private double deviceTheoreticalMin =0;
	private double mass=0.01;// KG
	private TransformNR centerOfMassFromCentroid=new TransformNR();
	private TransformNR imuFromCentroid=new TransformNR();
	/** The static offset. */
	private double staticOffset=0;
	
	private ArrayList<LinkConfiguration> slaveLinks = new ArrayList<LinkConfiguration>();
	
	/**
	 * This is the flag for setting the direction of the velocity lock out for limit switches
	 */
	private boolean invertVelocity=false;
	
	/**
	 * This is the flag for setting the direction of the velocity lock out for limit switches
	 */
	private boolean invertLimitVelocityPolarity=false;
	

	private HashMap<String , String[]> vitamins= new HashMap<String, String[]>();
	private HashMap<String , String> vitaminVariant= new HashMap<String, String>();
	private boolean passive = false;
	private boolean newAbs=false;
	/**
	 * Instantiates a new link configuration.
	 *
	 * @param eElement the e element
	 */
	public LinkConfiguration(Element eElement){
    	setName(XmlFactory.getTagValue("name",eElement));
    	setHardwareIndex(Integer.parseInt(XmlFactory.getTagValue("index",eElement)));
    	setScale(Double.parseDouble(XmlFactory.getTagValue("scale",eElement)));
    	try{
    		setDeviceTheoreticalMax(Double.parseDouble(XmlFactory.getTagValue("deviceTheoreticalMax",eElement)));
    	}catch (Exception e){
    		newAbs=true;
    	}try{
    		setDeviceTheoreticalMin(Double.parseDouble(XmlFactory.getTagValue("deviceTheoreticalMin",eElement)));
    	}catch (Exception e){
    		newAbs=true;
    	}
    	setUpperLimit(Double.parseDouble(XmlFactory.getTagValue("upperLimit",eElement)));
    	setLowerLimit(Double.parseDouble(XmlFactory.getTagValue("lowerLimit",eElement)));
    	try{
    		setDeviceScriptingName(XmlFactory.getTagValue("deviceName",eElement));		
    	}catch(NullPointerException e){
    		// no device from connection engine specified
    	}
    	try{
    		invertLimitVelocityPolarity=XmlFactory.getTagValue("invertLimitVelocityPolarity",eElement).contains("true");

    	}catch(NullPointerException e){
    		// no device from connection engine specified
    	}
    	try{
    		setTypeString(XmlFactory.getTagValue("type",eElement));
    		try {
    			setTypeString(getTypeString());
    		}catch(NoSuchElementException e) {
    			setTypeString(LinkType.VIRTUAL.getName());
    			setTypeString("virtual");
    		}
    	}catch (NullPointerException e){
    		setTypeString(LinkType.PID.getName());
    	}
    	if(getTypeEnum()==LinkType.PID){
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
    	}catch (Exception e){
    		
    	}
    	try{
    		setStaticOffset(Double.parseDouble(XmlFactory.getTagValue("staticOffset",eElement)));
    	}catch (Exception e){
    		
    	}
    	

    	
    	try{
    		setMassKg(Double.parseDouble(XmlFactory.getTagValue("mass",eElement)));
    	}catch (Exception e){
    		
    	}
    	try{
    		setElectroMechanicalType(XmlFactory.getTagValue("electroMechanicalType",eElement));
    	}catch (Exception e){
    		
    	}
    	
    	try{
    		setElectroMechanicalSize(XmlFactory.getTagValue("electroMechanicalSize",eElement));
    	}catch (Exception e){
    		
    	}
    	try{
    		setShaftType(XmlFactory.getTagValue("shaftType",eElement));
    	}catch (Exception e){
    		
    	}
    	
    	try{
    		setShaftSize(XmlFactory.getTagValue("shaftSize",eElement));
    	}catch (Exception e){
    		
    	}
    	try{
    		setPassive(Boolean.parseBoolean(XmlFactory.getTagValue("passive",eElement)));
    	}catch (Exception e){
    		
    	}
    	NodeList nodListofLinks = eElement.getChildNodes();
		
		for (int i = 0; i < nodListofLinks .getLength(); i++) {			
		    Node linkNode = nodListofLinks.item(i);
	    	try{
	    		if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("centerOfMassFromCentroid")) {
			    	Element cntr = (Element)linkNode;	    	    
			    	setCenterOfMassFromCentroid(new TransformNR(	Double.parseDouble(XmlFactory.getTagValue("x",cntr)),
								    			Double.parseDouble(XmlFactory.getTagValue("y",cntr)),
								    			Double.parseDouble(XmlFactory.getTagValue("z",cntr)), 
								    			new RotationNR(new double[]{	Double.parseDouble(XmlFactory.getTagValue("rotw",cntr)),
								    							Double.parseDouble(XmlFactory.getTagValue("rotx",cntr)),
								    							Double.parseDouble(XmlFactory.getTagValue("roty",cntr)),
								    							Double.parseDouble(XmlFactory.getTagValue("rotz",cntr))})));	 
			    }
	    	}catch (Exception e){
	    		
	    	}
	    	try{
	    		if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("imuFromCentroid")) {
			    	Element cntr = (Element)linkNode;	    	    
			    	setimuFromCentroid(new TransformNR(	Double.parseDouble(XmlFactory.getTagValue("x",cntr)),
								    			Double.parseDouble(XmlFactory.getTagValue("y",cntr)),
								    			Double.parseDouble(XmlFactory.getTagValue("z",cntr)), 
								    			new RotationNR(new double[]{	Double.parseDouble(XmlFactory.getTagValue("rotw",cntr)),
								    							Double.parseDouble(XmlFactory.getTagValue("rotx",cntr)),
								    							Double.parseDouble(XmlFactory.getTagValue("roty",cntr)),
								    							Double.parseDouble(XmlFactory.getTagValue("rotz",cntr))})));	 
			    }
	    	}catch (Exception e){
	    		
	    	}try{
	    		if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("vitamins")) {    	    
			    	getVitamins((Element)linkNode)	 ;
			    }
	    	}catch (Exception e){
	    		
	    	}
		}
    	isLatch=XmlFactory.getTagValue("isLatch",eElement).contains("true");
    	setIndexLatch(Double.parseDouble(XmlFactory.getTagValue("indexLatch",eElement)));
    	isStopOnLatch=XmlFactory.getTagValue("isStopOnLatch",eElement).contains("true");
    	if(staticOffset>getUpperLimit() || staticOffset<getLowerLimit() )
    	   Log.error("PID group "+getHardwareIndex()+" staticOffset is "+staticOffset+" but needs to be between "+getUpperLimit()+" and "+getLowerLimit());
    	//System.out.println("Interted"+ inverted);
	}
	
	/**
	 * Instantiates a new link configuration.
	 *
	 * @param args the args
	 */
	public LinkConfiguration(Object[] args) {
		setName((String)args[6]);
		setHardwareIndex((Integer)args[0]);
    	setScale((Double)args[5]);
    	setUpperLimit((Integer)args[4]);
    	setLowerLimit((Integer)args[3]);
    	setTypeString(LinkType.PID.getName());

    	setTotlaNumberOfLinks((Integer)args[1]);
    	fireChangeEvent();
	}
	/**
	 * Gets the vitamins.
	 *
	 * @param doc the doc
	 */
	protected void getVitamins(Element doc) {

		try {
			NodeList nodListofLinks = doc.getChildNodes();
			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				Node linkNode = nodListofLinks.item(i);
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("vitamin")) {
					Element e = (Element) linkNode;
					setVitamin(XmlFactory.getTagValue("name",e),
							XmlFactory.getTagValue("type",e),
							XmlFactory.getTagValue("id",e)
							);
					try{
						setVitaminVariant(XmlFactory.getTagValue("name",e),
								XmlFactory.getTagValue("variant",e));
					}catch(Exception ex){}
				}
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * Add a vitamin to this link
	 * @param 	name the name of this vitamin, 
				if the name already exists, the data will be overwritten. 
	 * @param type the vitamin type, this maps the the json filename
	 * @param id the part ID, theis maps to the key in the json for the vitamin
	 */
	public void setVitamin(String name, String type, String id){
		if(getVitamins().get(name)==null){
			getVitamins().put(name, new String[2]);
		}
		getVitamins().get(name)[0]=type;
		getVitamins().get(name)[1]=id;
		fireChangeEvent();
	}
	/**
	 * Set a purchasing code for a vitamin
	 * @param name name of vitamin
	 * @param tagValue2 Purchaning code
	 */
	public void setVitaminVariant(String name, String tagValue2) {
		vitaminVariant.put(name, tagValue2);
		fireChangeEvent();
	}
	/**
	 * Get a purchaing code for a vitamin
	 * @param name name of vitamin
	 * @return
	 */
	public String getVitaminVariant(String name) {
		return vitaminVariant.get(name);
	}

	/**
	 * Instantiates a new link configuration.
	 */
	public LinkConfiguration() {
		//default values
	}

	/**
	 * Instantiates a new link configuration.
	 *
	 * @param home the home
	 * @param llimit the llimit
	 * @param ulimit the ulimit
	 * @param d the d
	 */
	public LinkConfiguration(int home, int llimit, int ulimit, double d) {
    	setScale(d);
    	setUpperLimit(ulimit);
    	setLowerLimit(llimit);
    	setStaticOffset(home);
    	fireChangeEvent();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s="LinkConfiguration: \n\tName: "+getName();
		if(deviceScriptingName!=null)
			s="Device Name: \n\tName: "+getDeviceScriptingName();
		s+=	"\n\tType: "+getTypeEnum()+" "+getTypeString();
		s+=	"\n\tHardware Board Index: "+getHardwareIndex();
		s+=	"\n\tScale: "+getScale();
		s+=	"\n\tUpper Limit: "+getUpperLimit();
		s+=	"\n\tLower Limit: "+getLowerLimit();
		s+=	"\n\tHoming Ticks Per Second: "+getHomingTicksPerSecond();
		return s;
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	/*
	 * 
	 * Generate the xml configuration to generate a link of this configuration. 
	 */
	public String getXml(){
		String DevStr=deviceScriptingName!= null?"<deviceName>"+getDeviceScriptingName()+"</deviceName>\n":"";
		String slaves="";
		for(int i=0;i<slaveLinks.size();i++){
			slaves+="\n\t<slaveLink>\n"+slaveLinks.get(i).getXml()+"\n\t</slaveLink>\n";
		}
		String allVitamins="";
		for(String key: getVitamins().keySet()){
			String v = "\t\t<vitamin>\n";
			v+=		"\t\t\t<name>"+key+"</name>\n"+
					"\t\t\t<type>"+getVitamins().get(key)[0]+"</type>\n"+
					"\t\t\t<id>"+getVitamins().get(key)[1]+"</id>\n";
			if (getVitaminVariant(key)!=null){
				v+=		"\t\t\t<variant>"+getVitamins().get(key)[1]+"</variant>\n";
			}
			v+="\t\t</vitamin>\n";
			allVitamins+=v;
		}
		
		return "\t<name>"+getName()+"</name>\n"+
				"\t"+DevStr+
				"\t<type>"+getTypeString()+"</type>\n"+
				"\t<index>"+getHardwareIndex()+"</index>\n"+
				"\t<scale>"+getScale()+"</scale>\n"+
				"\t<upperLimit>"+getUpperLimit()+"</upperLimit>\n"+
				"\t<lowerLimit>"+getLowerLimit()+"</lowerLimit>\n"+
				"\t<upperVelocity>"+getUpperVelocity()+"</upperVelocity>\n"+
				"\t<lowerVelocity>"+getLowerVelocity()+"</lowerVelocity>\n"+
				"\t<staticOffset>"+getStaticOffset()+"</staticOffset>\n"+
				"\t<deviceTheoreticalMax>"+getDeviceTheoreticalMax()+"</deviceTheoreticalMax>\n"+
				"\t<deviceTheoreticalMin>"+getDeviceTheoreticalMin()+"</deviceTheoreticalMin>\n"+
				"\t<isLatch>"+isLatch()+"</isLatch>\n"+
				"\t<indexLatch>"+getIndexLatch()+"</indexLatch>\n"+
				"\t<isStopOnLatch>"+isStopOnLatch()+"</isStopOnLatch>\n"+	
				"\t<homingTPS>"+getHomingTicksPerSecond()+"</homingTPS>\n"+
				"\n\t<vitamins>\n"+allVitamins+"\n\t</vitamins>\n"+
				"\t<passive>"+isPassive()+"</passive>\n"+
				"\t<mass>"+getMassKg()+"</mass>\n"+
				"\t<centerOfMassFromCentroid>"+getCenterOfMassFromCentroid().getXml()+"</centerOfMassFromCentroid>\n"+
				"\t<imuFromCentroid>"+getimuFromCentroid().getXml()+"</imuFromCentroid>\n"
				+slaves;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		Log.info("Setting controller name: "+name);
		this.name = name;
		fireChangeEvent();
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * sets the hardware index for maping this kinematics link to its assocaited hardware index.
	 *
	 * @param index the new hardware index
	 */
	public void setHardwareIndex(int index) {
		this.index = index;
		fireChangeEvent();
	}
	
	/**
	 * gets the hardware index for maping this kinematics link to its assocaited hardware index.
	 *
	 * @return the hardware index
	 */
	public int getHardwareIndex() {
		return index;
	}

	/**
	 * Sets the scale.
	 *
	 * @param scale the new scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
		fireChangeEvent();
	}
	
	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}

	public double getDeviceTheoreticalMax() {
		return deviceTheoreticalMax;
	}

	public void setDeviceTheoreticalMax(double deviceTheoreticalMax) {
		this.deviceTheoreticalMax = deviceTheoreticalMax;
		fireChangeEvent();
	}

	public double getDeviceTheoreticalMin() {
		return deviceTheoreticalMin;
	}

	public void setDeviceTheoreticalMin(double deviceTheoreticalMin) {
		this.deviceTheoreticalMin = deviceTheoreticalMin;
		fireChangeEvent();
	}
	
	/**
	 * Sets the upper limit.
	 *
	 * @param upperLimit the new upper limit
	 */
	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
		if(upperLimit>getDeviceTheoreticalMax()) {
			if(!newAbs)
				this.upperLimit=getDeviceTheoreticalMax();	
			else
				setDeviceTheoreticalMax(upperLimit);
				
		}
		fireChangeEvent();
	}
	
	/**
	 * Gets the upper limit.
	 *
	 * @return the upper limit
	 */
	public double getUpperLimit() {
		return upperLimit;
	}
	
	/**
	 * Sets the lower limit.
	 *
	 * @param lowerLimit the new lower limit
	 */
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
		if(lowerLimit<getDeviceTheoreticalMin())
		{
			if(!newAbs) {
				this.lowerLimit=getDeviceTheoreticalMin();	
			}else {
				setDeviceTheoreticalMin(lowerLimit);
			}
		}
				
		fireChangeEvent();
	}
	
	/**
	 * Gets the lower limit.
	 *
	 * @return the lower limit
	 */
	public double getLowerLimit() {
		return lowerLimit;
	}
	
	/**
	 * Gets the kp.
	 *
	 * @return the kp
	 */
	public double getKP() {
		return k[0];
	}
	
	/**
	 * Gets the ki.
	 *
	 * @return the ki
	 */
	public double getKI() {
		return k[1];
	}
	
	/**
	 * Gets the kd.
	 *
	 * @return the kd
	 */
	public double getKD() {
		return k[2];
	}
	
	/**
	 * Sets the kp.
	 *
	 * @param kP the new kp
	 */
	public void setKP(double kP) {
		k[0] = kP;
		fireChangeEvent();
	}
	
	/**
	 * Sets the ki.
	 *
	 * @param kI the new ki
	 */
	public void setKI(double kI) {
		k[1] = kI;
		fireChangeEvent();
	}
	
	/**
	 * Sets the kd.
	 *
	 * @param kD the new kd
	 */
	public void setKD(double kD) {
		k[2] = kD;
		fireChangeEvent();
	}
	
	/**
	 * Sets the inverted.
	 *
	 * @param inverted the new inverted
	 */
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
		fireChangeEvent();
	}
	
	/**
	 * Checks if is inverted.
	 *
	 * @return true, if is inverted
	 */
	public boolean isInverted() {
		return inverted;
	}
	
	/**
	 * Sets the index latch.
	 *
	 * @param indexLatch the new index latch
	 */
	public void setIndexLatch(double indexLatch) {
		
		if(indexLatch>getDeviceTheoreticalMax())
			indexLatch= getDeviceTheoreticalMax();
		if(indexLatch<getDeviceTheoreticalMin())
			indexLatch=getDeviceTheoreticalMin();
		this.indexLatch = indexLatch;
		fireChangeEvent();
	}
	
	/**
	 * Gets the index latch.
	 *
	 * @return the index latch
	 */
	public double getIndexLatch() {
		return indexLatch;
	}
	
	/**
	 * Sets the latch.
	 *
	 * @param isLatch the new latch
	 */
	public void setLatch(boolean isLatch) {
		this.isLatch = isLatch;
	}
	
	/**
	 * Checks if is latch.
	 *
	 * @return true, if is latch
	 */
	public boolean isLatch() {
		return isLatch;
	}
	
	/**
	 * Sets the stop on latch.
	 *
	 * @param isStopOnLatch the new stop on latch
	 */
	public void setStopOnLatch(boolean isStopOnLatch) {
		this.isStopOnLatch = isStopOnLatch;
		fireChangeEvent();
	}
	
	/**
	 * Checks if is stop on latch.
	 *
	 * @return true, if is stop on latch
	 */
	public boolean isStopOnLatch() {
		return isStopOnLatch;
	}
	
	/**
	 * Sets the homing ticks per second.
	 *
	 * @param homingTicksPerSecond the new homing ticks per second
	 */
	public void setHomingTicksPerSecond(int homingTicksPerSecond) {
		this.homingTicksPerSecond = homingTicksPerSecond;
		fireChangeEvent();
	}
	
	/**
	 * Gets the homing ticks per second.
	 *
	 * @return the homing ticks per second
	 */
	public int getHomingTicksPerSecond() {
		return homingTicksPerSecond;
	}
	

	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	LinkType getTypeEnum() {
		return LinkType.fromString(type);
	}
	
	/**
	 * Sets the upper velocity.
	 *
	 * @param upperVelocity the new upper velocity
	 */
	public void setUpperVelocity(double upperVelocity) {
		this.velocityLimit = upperVelocity;
		fireChangeEvent();
	}
	
	/**
	 * Gets the upper velocity.
	 *
	 * @return the upper velocity
	 */
	public double getUpperVelocity() {
		return velocityLimit;
	}

	
	/**
	 * Gets the lower velocity.
	 *
	 * @return the lower velocity
	 */
	public double getLowerVelocity() {
		return -velocityLimit;
	}
	
	/**
	 * THis is the index of this link in its kinematics chain.
	 *
	 * @return the link index
	 */
	public int getLinkIndex() {
		return linkIndex;
	}
	
	/**
	 * This sets the index of the link in itts kinematic chain.
	 *
	 * @param linkIndex the new link index
	 */
	public void setLinkIndex(int linkIndex) {
		this.linkIndex = linkIndex;
		fireChangeEvent();
	}
	
	/**
	 * Gets the totla number of links.
	 *
	 * @return the totla number of links
	 */
	public int getTotlaNumberOfLinks() {
		return totlaNumberOfLinks;
	}
	
	/**
	 * Sets the totla number of links.
	 *
	 * @param totlaNumberOfLinks the new totla number of links
	 */
	public void setTotlaNumberOfLinks(int totlaNumberOfLinks) {
		this.totlaNumberOfLinks = totlaNumberOfLinks;
		fireChangeEvent();
	}
	
	/**
	 * Gets the pid configuration.
	 *
	 * @return the pid configuration
	 */
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
	
	/**
	 * Sets the pid configuration.
	 *
	 * @param pid the new pid configuration
	 */
	public void setPidConfiguration(IPidControlNamespace pid) {
		PIDConfiguration conf = pid.getPIDConfiguration(getHardwareIndex());
    	if(getTypeEnum()==LinkType.PID){
	    	k[0]=conf.getKP();
	    	k[1]=conf.getKI();
	    	k[2]=conf.getKD();
	    	inverted=conf.isInverted();
	    	setHomingTicksPerSecond(10000);
    	}
    	
    	isLatch=conf.isUseLatch();
    	indexLatch=(int) conf.getIndexLatch();
    	isStopOnLatch=conf.isStopOnIndex();
    	fireChangeEvent();
//    	if(indexLatch>getUpperLimit() || indexLatch<getLowerLimit() )
//    	    throw new RuntimeException("PID group "+getHardwareIndex()+" Index latch is "+indexLatch+" but needs to be between "+getUpperLimit()+" and "+getLowerLimit());
    	
	}
	
	/**
	 * Gets the device scripting name.
	 *
	 * @return the device scripting name
	 */
	public String getDeviceScriptingName() {
		return deviceScriptingName;
	}
	
	/**
	 * Sets the device scripting name.
	 *
	 * @param deviceScriptingName the new device scripting name
	 */
	public void setDeviceScriptingName(String deviceScriptingName) {
		this.deviceScriptingName = deviceScriptingName;
		fireChangeEvent();
	}
	
	/**
	 * Gets the static offset.
	 *
	 * @return the static offset
	 */
	public double getStaticOffset() {
		return staticOffset;
	}
	
	/**
	 * Sets the static offset.
	 *
	 * @param staticOffset the new static offset
	 */
	public void setStaticOffset(double staticOffset) {
		if(staticOffset>getUpperLimit())
			staticOffset= getUpperLimit();
		if(staticOffset<getLowerLimit())
			staticOffset=getLowerLimit();
		this.staticOffset = staticOffset;
		fireChangeEvent();
	}


	public boolean isInvertLimitVelocityPolarity() {
		return invertLimitVelocityPolarity;
	}

	public void setInvertLimitVelocityPolarity(boolean invertVelocity) {
		this.invertLimitVelocityPolarity = invertVelocity;
		fireChangeEvent();
	}

	public ArrayList<LinkConfiguration> getSlaveLinks() {
		return slaveLinks;
	}

	public void setSlaveLinks(ArrayList<LinkConfiguration> slaveLinks) {
		this.slaveLinks = slaveLinks;
		fireChangeEvent();
	}

	public double getMassKg() {
		return mass;
	}
	public void setMassKg(double mass) {
		this.mass = mass;
		fireChangeEvent();
	}
	public TransformNR getCenterOfMassFromCentroid() {
		return centerOfMassFromCentroid;
	}
	public void setCenterOfMassFromCentroid(TransformNR com) {
		if(this.centerOfMassFromCentroid!=null)
			this.centerOfMassFromCentroid.removeChangeListener(this);
		this.centerOfMassFromCentroid = com;
		this.centerOfMassFromCentroid.addChangeListener(this);
		fireChangeEvent();
	}
	public TransformNR getimuFromCentroid() {
		return imuFromCentroid;
	}
	public void setimuFromCentroid(TransformNR imu) {
		if(this.imuFromCentroid!=null)
			this.imuFromCentroid.removeChangeListener(this);
		this.imuFromCentroid = imu;
		this.imuFromCentroid.addChangeListener(this);
		fireChangeEvent();
	}
//	private String electroMechanicalType = "hobbyServo";
//	private String electroMechanicalSize = "standardMicro";
//	private String shaftType = "hobbyServoHorn";
//	private String shaftSize = "standardMicro1";
	
	private String[] getCoreShaftPart(){
		if(vitamins.get("shaft")==null){
			vitamins.put("shaft", new String[]{"hobbyServoHorn","standardMicro1"});
		}
		return vitamins.get("shaft");
	}
	private String[] getCoreEmPart(){
		if(vitamins.get("electroMechanical")==null){
			vitamins.put("electroMechanical", new String[]{"hobbyServo","standardMicro"});
		}
		return vitamins.get("electroMechanical");
	}
	public String getElectroMechanicalType() {
		return getCoreEmPart()[0] ;
	}

	public void setElectroMechanicalType(String electroMechanicalType) {
		getCoreEmPart()[0] = electroMechanicalType;
		fireChangeEvent();
	}

	public String getElectroMechanicalSize() {
		return getCoreEmPart()[1] ;
	}

	public void setElectroMechanicalSize(String electroMechanicalSize) {
		getCoreEmPart()[1] = electroMechanicalSize;
		fireChangeEvent();
	}

	public String getShaftType() {
		return getCoreShaftPart()[0];
	}

	public void setShaftType(String shaftType) {
		getCoreShaftPart()[0] = shaftType;
		fireChangeEvent();
	}

	public String getShaftSize() {
		return getCoreShaftPart()[1];
	}

	public void setShaftSize(String shaftSize) {
		getCoreShaftPart()[1] = shaftSize;
		fireChangeEvent();
	}

	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean passive) {
		this.passive = passive;
		fireChangeEvent();
	}

	public HashMap<String , String[]> getVitamins() {
		return vitamins;
	}

	public void setVitamins(HashMap<String , String[]> vitamins) {
		this.vitamins = vitamins;
		fireChangeEvent();
	}

	public String getTypeString() {
		return type;
	}

	public void setTypeString(String typeString) {
		if(typeString==null)
			throw new NullPointerException();
		type=typeString;
		fireChangeEvent();
	}
	
	 /**
	 * Checks if is virtual.
	 *
	 * @return true, if is virtual
	 */
	public boolean isVirtual(){
		 switch(getTypeEnum()){

		case DUMMY:
		case VIRTUAL:
			return true;
		case USERDEFINED:
			if(getTypeString().toLowerCase().contains("virtual")){
				return true;
			}
		default:
			return false;
		 }
	 }
	 
	 /**
	 * Checks if is tool.
	 *
	 * @return true, if is tool
	 */
	public boolean isTool(){
		 switch(getTypeEnum()){
		case PID_TOOL:
		case GCODE_STEPPER_TOOL:
		case GCODE_HEATER_TOOL:
			return true;
		case USERDEFINED:
			if(getTypeString().toLowerCase().contains("tool")){
				return true;
			}	
		default:
			return false;
		 
		 } 
	 }
	 
	 /**
	 * Checks if is prismatic.
	 *
	 * @return true, if is prismatic
	 */
	public boolean isPrismatic(){
		 switch(getTypeEnum()){
		case ANALOG_PRISMATIC:
		case PID_PRISMATIC:
		case GCODE_STEPPER_PRISMATIC:
			return true;
		case USERDEFINED:
			if(getTypeString().toLowerCase().contains("prismatic")){
				return true;
			}
		default:
			return false;

		 } 
	 }

	public void addChangeListener(ILinkConfigurationChangeListener l) {
		if(!getListeners().contains(l))
			getListeners().add(l);
	}
	public void removeChangeListener(ILinkConfigurationChangeListener l) {
		if(getListeners().contains(l))
			getListeners().remove(l);
	}
	public void clearChangeListener() {
		getListeners().clear();
		listeners=null;
	}
	public ArrayList<ILinkConfigurationChangeListener> getListeners() {
		if(listeners==null)
			listeners=new ArrayList<ILinkConfigurationChangeListener>();
		return listeners;
	}

	private void fireChangeEvent() {
		if(listeners!=null) {
			for(int i=0;i<listeners.size();i++) {
				try {
					listeners.get(i).event(this);
				}catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	@Override
	public void event(TransformNR changed) {
		fireChangeEvent();
	}
	
}
