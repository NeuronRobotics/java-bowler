package com.neuronrobotics.sdk.addons.kinematics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
//import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.w3c.dom.Element;

import javafx.application.Platform;
import javafx.scene.transform.Affine;
import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.gui.TransformFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.common.IDeviceConnectionEventListener;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;


public class DHParameterKinematics extends AbstractKinematicsNR implements ITaskSpaceUpdateListenerNR{
	
	private DHChain chain=null;

	private ArrayList<Affine> linksListeners = new ArrayList<Affine>();
	private Affine currentTarget = new Affine();
	boolean disconnecting=false;
	IDeviceConnectionEventListener l = new IDeviceConnectionEventListener() {
		@Override public void onDisconnect(BowlerAbstractDevice source) {
			if(!disconnecting){
				disconnecting=true;
				disconnect();
			}
			
		}
		@Override public void onConnect(BowlerAbstractDevice source) {}
	} ;
	
	public DHParameterKinematics( BowlerAbstractDevice bad, Element  linkStream ){
		super(linkStream,new LinkFactory(bad));
		setChain(getDhParametersChain());
		if(getFactory().getDyio()!=null)
			getFactory().getDyio().addConnectionEventListener(l);
	}
	
	public DHParameterKinematics( BowlerAbstractDevice bad, InputStream  linkStream ){
		super(linkStream,new LinkFactory(bad));
		setChain(getDhParametersChain());
		if(getFactory().getDyio()!=null)
			getFactory().getDyio().addConnectionEventListener(l);
	}
	@Deprecated
	public DHParameterKinematics( BowlerAbstractDevice bad, InputStream linkStream ,InputStream depricated ){
		this(bad, linkStream);
	}
	
	public DHParameterKinematics(BowlerAbstractDevice bad) {
		this(bad,XmlFactory.getDefaultConfigurationStream("TrobotLinks.xml"));
	}

	public DHParameterKinematics(BowlerAbstractDevice bad, String file) {
		this(bad,XmlFactory.getDefaultConfigurationStream(file));
	}

	public DHParameterKinematics(BowlerAbstractDevice bad,  File configFile) throws FileNotFoundException {
		this(bad,new FileInputStream(configFile));
	}
	
	public DHParameterKinematics() {
		this(null,XmlFactory.getDefaultConfigurationStream("TrobotLinks.xml"));
	}
	

	public DHParameterKinematics( String file) {
		this(null,XmlFactory.getDefaultConfigurationStream(file));
	}

	public DHParameterKinematics( Element linkStream) {
		this(null,linkStream);
	}
	
	public DHParameterKinematics( File configFile) throws FileNotFoundException {
		this(null,new FileInputStream(configFile));
	}


	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)throws Exception {
		return getDhChain().inverseKinematics(taskSpaceTransform, getCurrentJointSpaceVector());
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		if(jointSpaceVector == null || getDhChain() == null)
			return new TransformNR();
		TransformNR rt = getDhChain().forwardKinematics(jointSpaceVector);
		return rt;
	}
	
	
	
	/**
	 * Gets the Jacobian matrix
	 * @return a matrix representing the Jacobian for the current configuration
	 */
	public Matrix getJacobian(){
		long time = System.currentTimeMillis();
		Matrix m = getDhChain().getJacobian(getCurrentJointSpaceVector());
		System.out.println("Jacobian calc took: "+(System.currentTimeMillis()-time));
		return m;
	}
	
	public ArrayList<TransformNR> getChainTransformations(){
		return getChain().getChain(getCurrentJointSpaceVector());
	}

	public void setDhChain(DHChain chain) {
		this.setChain(chain);
	}

	public DHChain getDhChain() {
		return getChain();
	}

	public DHChain getChain() {
		return chain;
	}

	public void setChain(DHChain chain) {
		this.chain = chain;
		ArrayList<DHLink> dhLinks = chain.getLinks();
		for(int i=linksListeners.size();i<dhLinks.size();i++){
			linksListeners.add(new Affine());
		}

		for(int i=0;i<dhLinks.size();i++){
			dhLinks.get(i).setListener(linksListeners.get(i));
			if(getLinkConfiguration(i).getType().isTool()){
				dhLinks.get(i).setDegenerate(true);
			}
		}
		addPoseUpdateListener(this);
		try {
			currentJointSpacePositions=null;
			currentJointSpaceTarget=null;
			setDesiredJointSpaceVector(getCurrentJointSpaceVector(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		ArrayList<DHLink> dhLinks = chain.getLinks();
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

	@Override
	public void disconnectDevice() {
		// TODO Auto-generated method stub
		removePoseUpdateListener(this);
	}

	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		
	}

	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,
			TransformNR pose) {
		// TODO Auto-generated method stub
		TransformFactory.getTransform(pose, getCurrentTargetAffine());
	}

	public DhInverseSolver getInverseSolver() {
		return chain.getInverseSolver();
	}

	public void setInverseSolver(DhInverseSolver inverseSolver) {
		chain.setInverseSolver(inverseSolver);
	}

	public Affine getCurrentTargetAffine() {
		return currentTarget;
	}

	public void addNewLink(LinkConfiguration newLink, DHLink dhLink) {
		LinkFactory factory  =getFactory();
		//remove the link listener while the number of links could chnage
		factory.removeLinkListener(this);
		factory.getLink(newLink);// adds new link internally
		DHChain chain =  getDhChain() ;
		chain.addLink(dhLink);
		//set the modified kinematics chain
		setChain(chain);
		//once the new link configuration is set up, re add the listener
		factory.addLinkListener(this);
	}

	public void removeLink(int index) {
		LinkFactory factory  =getFactory();
		//remove the link listener while the number of links could chnage
		factory.removeLinkListener(this);
		DHChain chain = getDhChain() ;
		chain.getLinks().remove(index);
		factory.deleteLink(index);
		//set the modified kinematics chain
		setChain(chain);
		//once the new link configuration is set up, re add the listener
		factory.addLinkListener(this);
	}






}
