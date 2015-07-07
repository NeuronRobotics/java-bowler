package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class MobileBase extends DHParameterKinematics {
	
	private ArrayList<DHParameterKinematics> legs=new ArrayList<DHParameterKinematics>();
	private ArrayList<DHParameterKinematics> appendages=new ArrayList<DHParameterKinematics>();
	
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
		ArrayList<DHLink> dhLinks = getChain().getLinks();
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

}
