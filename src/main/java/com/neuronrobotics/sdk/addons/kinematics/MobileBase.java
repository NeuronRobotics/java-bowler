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
import com.neuronrobotics.sdk.common.Log;

public class MobileBase extends AbstractKinematicsNR{
	
	private ArrayList<DHParameterKinematics> legs=new ArrayList<DHParameterKinematics>();
	private ArrayList<DHParameterKinematics> appendages=new ArrayList<DHParameterKinematics>();
	private ArrayList<DHParameterKinematics> steerable=new ArrayList<DHParameterKinematics>();
	private ArrayList<DHParameterKinematics> drivable=new ArrayList<DHParameterKinematics>();
	private DrivingType driveType = DrivingType.NONE;
	
	private IDriveEngine walkingDriveEngine = new WalkingDriveEngine();
	private IDriveEngine wheeledDriveEngine = new WheeledDriveEngine();

	public MobileBase(){}// used for building new bases live
	
	public MobileBase(InputStream configFile){
		this();
		Document doc =XmlFactory.getAllNodesDocument(configFile);
		NodeList nodListofLinks = doc.getElementsByTagName("mobilebase");
		if(nodListofLinks.getLength()>1){
			throw new RuntimeException("only one mobile base is allowed per level");
		}
		for (int i = 0; i < nodListofLinks.getLength(); i++) {			
		    Node linkNode = nodListofLinks.item(i);
		    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
		    	Element e = (Element) linkNode;
		    	loadConfigs( e);
		    }else{
		    	
		    }
		}
	}
	
	public MobileBase(Element doc) {
		
		loadConfigs( doc);
		
	}
	
	private void loadConfigs(Element doc){
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
	
	private void loadLimb(Element doc,String tag, ArrayList<DHParameterKinematics> list){
		NodeList legNodes 			= doc.getElementsByTagName(tag);
		for (int i = 0; i < legNodes.getLength(); i++) {			
		    Node linkNode = legNodes.item(i);
		    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
		    	Element e = (Element) linkNode;
		    	final DHParameterKinematics kin = new DHParameterKinematics(e);
		    	list.add(kin);
		    	addRegistrationListener(new IRegistrationListenerNR() {
					@Override
					public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,
							TransformNR regestration) {
						Log.debug("Motion of mobile base event ");
						//this represents motion of the mobile base
						kin.setGlobalToFiducialTransform(regestration);
						kin.getCurrentTaskSpaceTransform();
					}
					
					@Override
					public void onBaseToFiducialUpdate(AbstractKinematicsNR source,
							TransformNR regestration) {
						// update the joints on the motion
						kin.getCurrentTaskSpaceTransform();
					}
				});
		    }
		}
	}



	@Override
	public void disconnectDevice() {
		// TODO Auto-generated method stub

	}
	
	

	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<DHParameterKinematics> getLegs() {
		return legs;
	}

	public void setLegs(ArrayList<DHParameterKinematics> legs) {
		this.legs = legs;
	}

	public ArrayList<DHParameterKinematics> getAppendages() {
		return appendages;
	}

	public void setAppendages(ArrayList<DHParameterKinematics> appendages) {
		this.appendages = appendages;
	}
	
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
	/*
	 * 
	 * Generate the xml configuration to generate an XML of this robot. 
	 */
	public String getEmbedableXml(){
		String xml = "";
		for(DHParameterKinematics l:legs){
			xml+="<leg>\n";
			xml+=l.getEmbedableXml();
			xml+="\n</leg>\n";
		}
		for(DHParameterKinematics l:appendages){
			xml+="<appendage>\n";
			xml+=l.getEmbedableXml();
			xml+="\n</appendage>\n";
		}
		ArrayList<DHLink> dhLinks = getDhParametersChain().getLinks();
		for(int i=0;i<dhLinks.size();i++){
			xml+="<link>\n";
			xml+=getLinkConfiguration(i).getXml();
			xml+=dhLinks.get(i).getXml();
			xml+="\n</link>\n";
		}
		
		xml+="\n<ZframeToRAS\n>";
		xml+=getFiducialToGlobalTransform().getXml();
		xml+="\n</ZframeToRAS>\n";
		
		xml+="\n<baseToZframe>\n";
		xml+=getRobotToFiducialTransform().getXml();
		xml+="\n</baseToZframe>\n";
		return xml;
	}

	public ArrayList<DHParameterKinematics> getSteerable() {
		return steerable;
	}

	public void setSteerable(ArrayList<DHParameterKinematics> steerable) {
		this.steerable = steerable;
	}

	public ArrayList<DHParameterKinematics> getDrivable() {
		return drivable;
	}

	public void setDrivable(ArrayList<DHParameterKinematics> drivable) {
		this.drivable = drivable;
	}

	public IDriveEngine getWalkingDriveEngine() {
		return walkingDriveEngine;
	}

	public void setWalkingDriveEngine(IDriveEngine walkingDriveEngine) {
		this.walkingDriveEngine = walkingDriveEngine;
	}

	public IDriveEngine getWheeledDriveEngine() {
		return wheeledDriveEngine;
	}

	public void setWheeledDriveEngine(IDriveEngine wheeledDriveEngine) {
		this.wheeledDriveEngine = wheeledDriveEngine;
	}

	public DrivingType getDriveType() {
		return driveType;
	}

	public void setDriveType(DrivingType driveType) {
		this.driveType = driveType;
	}

	
	public void DriveStraight(double cm, double seconds) {
		switch(driveType){
		case DRIVING:
			getWheeledDriveEngine().DriveStraight(this,cm, seconds);
			break;
		case NONE:
			break;
		case WALKING:
			getWalkingDriveEngine().DriveStraight(this,cm, seconds);
			break;
		}
	}

	
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		// TODO Auto-generated method stub
		switch(driveType){
		case DRIVING:
			getWheeledDriveEngine().DriveArc(this,cmRadius, degrees, seconds);
			break;
		case NONE:
			break;
		case WALKING:
			getWalkingDriveEngine().DriveArc(this,cmRadius, degrees, seconds);
			break;
		}
	}

	
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
	}

	
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
	}

}
