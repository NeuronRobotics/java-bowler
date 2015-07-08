package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.Log;

public class MobileBase extends AbstractKinematicsNR {
	
	private ArrayList<DHParameterKinematics> legs=new ArrayList<DHParameterKinematics>();
	private ArrayList<DHParameterKinematics> appendages=new ArrayList<DHParameterKinematics>();
	private ArrayList<DHParameterKinematics> steerable=new ArrayList<DHParameterKinematics>();
	private ArrayList<DHParameterKinematics> drivable=new ArrayList<DHParameterKinematics>();
	

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

}
