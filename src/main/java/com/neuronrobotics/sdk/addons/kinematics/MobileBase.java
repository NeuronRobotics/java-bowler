package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;

// TODO: Auto-generated Javadoc
/**
 * The Class MobileBase.
 */
public class MobileBase extends AbstractKinematicsNR{
	
	/** The legs. */
	private final ArrayList<DHParameterKinematics> legs=new ArrayList<DHParameterKinematics>();
	
	/** The appendages. */
	private final ArrayList<DHParameterKinematics> appendages=new ArrayList<DHParameterKinematics>();
	
	/** The steerable. */
	private final ArrayList<DHParameterKinematics> steerable=new ArrayList<DHParameterKinematics>();
	
	/** The drivable. */
	private final ArrayList<DHParameterKinematics> drivable=new ArrayList<DHParameterKinematics>();
	
	/** The drive type. */
	private DrivingType driveType = DrivingType.NONE;
	
	/** The walking drive engine. */
	private IDriveEngine walkingDriveEngine = new WalkingDriveEngine();
	
	/** The wheeled drive engine. */
	private IDriveEngine wheeledDriveEngine = new WheeledDriveEngine();
	
	/** The walking engine. */
	private String [] walkingEngine =new String[]{"bcb4760a449190206170","WalkingDriveEngine.groovy"}; 
	
	/** The self source. */
	private String [] selfSource =new String[2];
	
	/**
	 * Instantiates a new mobile base.
	 */
	public MobileBase(){}// used for building new bases live
	
	/**
	 * Instantiates a new mobile base.
	 *
	 * @param configFile the config file
	 */
	public MobileBase(InputStream configFile){
		this();
		Document doc =XmlFactory.getAllNodesDocument(configFile);
		NodeList nodListofLinks = doc.getElementsByTagName("root");
		
		if(nodListofLinks.getLength()!=1 ){
			System.out.println("Found "+nodListofLinks.getLength());
			throw new RuntimeException("one mobile base is needed per level");
		}	
		NodeList rootNode  = nodListofLinks.item(0).getChildNodes();
		 
		
	    for(int i=0;i<rootNode.getLength();i++){
	    	
	        Node linkNode = rootNode.item(i);
			if (linkNode .getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contains("mobilebase")) {
		    	Element e = (Element) linkNode;
		    	loadConfigs( e);
		    }
	    }
	
    	addRegistrationListener(new IRegistrationListenerNR() {
			@Override
			public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,
					TransformNR regestration) {
				
				for(DHParameterKinematics kin:getAllDHChains()){
					//Log.debug("Motion of mobile base event ");
					//this represents motion of the mobile base
					kin.setGlobalToFiducialTransform(regestration);
				}

			}
			
			@Override
			public void onBaseToFiducialUpdate(AbstractKinematicsNR source,
					TransformNR regestration) {
			}
		});
	}
	
	/**
	 * Instantiates a new mobile base.
	 *
	 * @param doc the doc
	 */
	public MobileBase(Element doc) {
		
		loadConfigs( doc);
		
	}
	
	/**
	 * Load configs.
	 *
	 * @param doc the doc
	 */
	private void loadConfigs(Element doc){
		setScriptingName(XmlFactory.getTagValue("name",doc));
		
		setCadEngine(getGistCodes( doc,"cadEngine"));
		setWalkingEngine(getGistCodes( doc,"driveEngine"));
		loadLimb(doc,"leg",legs);
		loadLimb(doc,"drivable",drivable);
		loadLimb(doc,"steerable",steerable);
		loadLimb(doc,"appendage",appendages);
		try{
			setDriveType(DrivingType.fromString(XmlFactory.getTagValue("driveType",doc)));
		}catch(Exception ex ){
			setDriveType(DrivingType.NONE);
		}
	}
	
	/**
	 * Gets the name.
	 *
	 * @param e the e
	 * @param tag the tag
	 * @return the name
	 */
	private String getname(Element e,String tag){
		try{
			NodeList nodListofLinks = e.getChildNodes();
			
			for (int i = 0; i < nodListofLinks .getLength(); i++) {			
			    Node linkNode = nodListofLinks.item(i);
			   if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("name")) {
			    	return XmlFactory.getTagValue("name",e);
			    }
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return tag;
	}
	
	
	/**
	 * Load limb.
	 *
	 * @param doc the doc
	 * @param tag the tag
	 * @param list the list
	 */
	private void loadLimb(Element doc,String tag, ArrayList<DHParameterKinematics> list){
		NodeList nodListofLinks = doc.getChildNodes();
		for (int i = 0; i < nodListofLinks.getLength(); i++) {			
		    Node linkNode = nodListofLinks.item(i);
		    if (linkNode.getNodeType() == Node.ELEMENT_NODE&& linkNode.getNodeName().contentEquals(tag)) {
		    	Element e = (Element) linkNode;
		    	final String name =  getname( e,tag);
		    	
		    	DHParameterKinematics kin=(DHParameterKinematics) DeviceManager.getSpecificDevice(DHParameterKinematics.class, name);
		    	if(kin==null){
		    		kin= new DHParameterKinematics(e);
		    		
		    		DeviceManager.addConnection(kin, name);
		    	}
		    	kin.setScriptingName(name);
		    	list.add(kin);

		    }
		}
	}



	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#disconnectDevice()
	 */
	@Override
	public void disconnectDevice() {
		for(DHParameterKinematics kin:getAllDHChains()){
			kin.disconnect();
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#connectDevice()
	 */
	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)
			throws Exception {
		// TODO Auto-generated method stub
		return new double[ getNumberOfLinks()];
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#forwardKinematics(double[])
	 */
	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return new TransformNR();
	}

	/**
	 * Gets the legs.
	 *
	 * @return the legs
	 */
	public ArrayList<DHParameterKinematics> getLegs() {
		return legs;
	}

	/**
	 * Gets the appendages.
	 *
	 * @return the appendages
	 */
	public ArrayList<DHParameterKinematics> getAppendages() {
		return appendages;
	}

	
	/**
	 * Gets the all dh chains.
	 *
	 * @return the all dh chains
	 */
	public ArrayList<DHParameterKinematics> getAllDHChains() {
		ArrayList<DHParameterKinematics> copy = new ArrayList<DHParameterKinematics>();
		for(DHParameterKinematics l:legs){
			copy.add(l);	
		}
		for(DHParameterKinematics l:appendages){
			copy.add(l);	
		}
		for(DHParameterKinematics l:steerable){
			copy.add(l);	
		}
		for(DHParameterKinematics l:drivable){
			copy.add(l);	
		}
		return copy;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#getXml()
	 */
	/*
	 * 
	 * Generate the xml configuration to generate an XML of this robot. 
	 */
	public String getXml(){
		String xml = "<root>\n";
		xml+=getEmbedableXml();
		xml+="\n</root>";
		return xml;
	}
	
	/**
	 * Gets the embedable xml.
	 *
	 * @return the embedable xml
	 */
	/*
	 * 
	 * Generate the xml configuration to generate an XML of this robot. 
	 */
	public String getEmbedableXml(){
		TransformNR location = getFiducialToGlobalTransform();
		setGlobalToFiducialTransform(new TransformNR());
		String xml = "<mobilebase>\n";
		xml+="\n<driveType>"+getDriveType()+"</driveType>\n";
		
		xml+="\t<cadEngine>\n";
		xml+="\t\t<gist>"+getCadEngine()[0]+"</gist>\n";
		xml+="\t\t<file>"+getCadEngine()[1]+"</file>\n";
		xml+="\t</cadEngine>\n";
		
		xml+="\t<driveEngine>\n";
		xml+="\t\t<gist>"+getWalkingEngine()[0]+"</gist>\n";
		xml+="\t\t<file>"+getWalkingEngine()[1]+"</file>\n";
		xml+="\t</driveEngine>\n";
		
		xml+="\n<name>"+getScriptingName()+"</name>\n";
		for(DHParameterKinematics l:legs){
			xml+="<leg>\n";
			xml+="\n<name>"+l.getScriptingName()+"</name>\n";
			xml+=l.getEmbedableXml();
			xml+="\n</leg>\n";
		}
		for(DHParameterKinematics l:appendages){
			xml+="<appendage>\n";
			xml+="\n<name>"+l.getScriptingName()+"</name>\n";
			xml+=l.getEmbedableXml();
			xml+="\n</appendage>\n";
		}
		
		for(DHParameterKinematics l:steerable){
			xml+="<steerable>\n";
			xml+="\n<name>"+l.getScriptingName()+"</name>\n";
			xml+=l.getEmbedableXml();
			xml+="\n</steerable>\n";
		}
		for(DHParameterKinematics l:drivable){
			xml+="<drivable>\n";
			xml+="\n<name>"+l.getScriptingName()+"</name>\n";
			xml+=l.getEmbedableXml();
			xml+="\n</drivable>\n";
		}
		
		xml+="\n<ZframeToRAS>\n";
		xml+=getFiducialToGlobalTransform().getXml();
		xml+="\n</ZframeToRAS>\n";
		
		xml+="\n<baseToZframe>\n";
		xml+=getRobotToFiducialTransform().getXml();
		xml+="\n</baseToZframe>\n";
		xml+="\n</mobilebase>\n";
		setGlobalToFiducialTransform(location);
		return xml;
	}

	/**
	 * Gets the steerable.
	 *
	 * @return the steerable
	 */
	public ArrayList<DHParameterKinematics> getSteerable() {
		return steerable;
	}

	/**
	 * Gets the drivable.
	 *
	 * @return the drivable
	 */
	public ArrayList<DHParameterKinematics> getDrivable() {
		return drivable;
	}


	/**
	 * Gets the walking drive engine.
	 *
	 * @return the walking drive engine
	 */
	private IDriveEngine getWalkingDriveEngine() {
		return walkingDriveEngine;
	}

	/**
	 * Sets the walking drive engine.
	 *
	 * @param walkingDriveEngine the new walking drive engine
	 */
	public void setWalkingDriveEngine(IDriveEngine walkingDriveEngine) {
		this.walkingDriveEngine = walkingDriveEngine;
	}

	/**
	 * Gets the wheeled drive engine.
	 *
	 * @return the wheeled drive engine
	 */
	private IDriveEngine getWheeledDriveEngine() {
		return wheeledDriveEngine;
	}

	/**
	 * Sets the wheeled drive engine.
	 *
	 * @param wheeledDriveEngine the new wheeled drive engine
	 */
	public void setWheeledDriveEngine(IDriveEngine wheeledDriveEngine) {
		this.wheeledDriveEngine = wheeledDriveEngine;
	}

	/**
	 * Gets the drive type.
	 *
	 * @return the drive type
	 */
	public DrivingType getDriveType() {
		return driveType;
	}

	/**
	 * Sets the drive type.
	 *
	 * @param driveType the new drive type
	 */
	public void setDriveType(DrivingType driveType) {
		this.driveType = driveType;
	}
	
	/**
	 * Drive arc.
	 *
	 * @param newPose the new pose
	 * @param seconds the seconds
	 */
	public void DriveArc( TransformNR newPose, double seconds) {
		// TODO Auto-generated method stub
		switch(driveType){
		case DRIVING:
			getWheeledDriveEngine().DriveArc(this,newPose, seconds);
			break;
		case NONE:
			try {
				//do a simple coordinated motion task
				for(DHParameterKinematics dh:appendages){
					dh.setDesiredTaskSpaceTransform(newPose,  seconds);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case WALKING:
			getWalkingDriveEngine().DriveArc(this,newPose, seconds);
			break;
		}
		updatePositions();
	}

	
	/**
	 * Drive velocity straight.
	 *
	 * @param cmPerSecond the cm per second
	 */
	public void DriveVelocityStraight(double cmPerSecond) {
		// TODO Auto-generated method stub
		switch(driveType){
		case DRIVING:
			getWheeledDriveEngine().DriveVelocityStraight(this,cmPerSecond);
			break;
		case NONE:
			break;
		case WALKING:
			getWalkingDriveEngine().DriveVelocityStraight(this,cmPerSecond);
			break;
		}
		updatePositions();
	}

	
	/**
	 * Drive velocity arc.
	 *
	 * @param degreesPerSecond the degrees per second
	 * @param cmRadius the cm radius
	 */
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		// TODO Auto-generated method stub
		switch(driveType){
		case DRIVING:
			getWheeledDriveEngine().DriveVelocityArc(this,degreesPerSecond, cmRadius);
			break;
		case NONE:
			break;
		case WALKING:
			getWalkingDriveEngine().DriveVelocityArc(this,degreesPerSecond, cmRadius);
			break;
		}
		updatePositions();
	}
	
	/**
	 * Update positions.
	 */
	public void updatePositions(){
		for(DHParameterKinematics kin:getAppendages()){
			//System.err.println("Updating arm: "+kin.getScriptingName());
			kin.updateCadLocations();
		}
		for(DHParameterKinematics kin:getDrivable()){
			//System.err.println("Updating getDrivable: "+kin.getScriptingName());
			kin.updateCadLocations();
		}
		for(DHParameterKinematics kin:getSteerable()){
			//System.err.println("Updating getSteerable: "+kin.getScriptingName());
			kin.updateCadLocations();
		}

	}


	/**
	 * Gets the walking engine.
	 *
	 * @return the walking engine
	 */
	public String [] getWalkingEngine() {
		return walkingEngine;
	}

	/**
	 * Sets the walking engine.
	 *
	 * @param walkingEngine the new walking engine
	 */
	public void setWalkingEngine(String [] walkingEngine) {
		if(walkingEngine!=null && walkingEngine[0]!=null &&walkingEngine[1]!=null)
			this.walkingEngine = walkingEngine;
	}

	/**
	 * Gets the self source.
	 *
	 * @return the self source
	 */
	public String [] getSelfSource() {
		return selfSource;
	}

	/**
	 * Sets the self source.
	 *
	 * @param selfSource the new self source
	 */
	public void setSelfSource(String [] selfSource) {
		this.selfSource = selfSource;
	}

}
