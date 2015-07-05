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

import javafx.application.Platform;
import javafx.scene.transform.Affine;
import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.gui.TransformFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;


public class DHParameterKinematics extends AbstractKinematicsNR implements ITaskSpaceUpdateListenerNR{
	
	private DHChain chain=null;

	private ArrayList<Affine> linksListeners = new ArrayList<Affine>();
	private Affine currentTarget = new Affine();
	boolean disconnecting=false;
	IConnectionEventListener l = new IConnectionEventListener() {
		@Override public void onDisconnect(BowlerAbstractConnection source) {
			if(!disconnecting){
				disconnecting=true;
				disconnect();
			}
			
		}
		@Override public void onConnect(BowlerAbstractConnection source) {}
	} ;
	
	public DHParameterKinematics() {
		this(new VirtualGenericPIDDevice(100000),"TrobotLinks.xml");
	}
	
	public DHParameterKinematics( DyIO dev) {
		this(dev,"TrobotLinks.xml");

	}
	public DHParameterKinematics( DyIO dev, String file) {
		this(dev,XmlFactory.getDefaultConfigurationStream(file),XmlFactory.getDefaultConfigurationStream(file));
	}
	public DHParameterKinematics( GenericPIDDevice dev) {
		this(dev,"TrobotLinks.xml");

	}
	
	public DHParameterKinematics( GenericPIDDevice dev, String file) {
		this(dev,XmlFactory.getDefaultConfigurationStream(file),XmlFactory.getDefaultConfigurationStream(file));
		
	}
	public DHParameterKinematics( BowlerAbstractDevice dev, InputStream linkStream, InputStream dhStream) {
		super(linkStream,new LinkFactory( dev));
		setChain(new DHChain(dhStream,getFactory()));
		dev.addConnectionEventListener(l);
	}
	public DHParameterKinematics( BowlerAbstractDevice dev, File configFile) throws FileNotFoundException {
		this(dev,new FileInputStream(configFile),new FileInputStream(configFile));
	}

	public DHParameterKinematics(InputStream linkStream, InputStream dhStream) {
		super(linkStream,new LinkFactory());
		setChain(new DHChain(dhStream,getFactory()));
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
		ArrayList<DHLink> dhLinks = chain.getLinks();
		for(int i=0;i<dhLinks.size();i++){
			xml+="<link>\n";
			xml+=getLinkConfiguration(i).getXml();
			xml+=dhLinks.get(i).getXml();
			xml+="\n</link>\n";
		}
		xml+="\n<ZframeToRAS>";
		xml+=getFiducialToGlobalTransform().getXml();
		xml+="\n</ZframeToRAS>";
		
		xml+="\n<baseToZframe>";
		xml+=getRobotToFiducialTransform().getXml();
		xml+="\n</baseToZframe>";
		xml+="\n</root>";
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




}
