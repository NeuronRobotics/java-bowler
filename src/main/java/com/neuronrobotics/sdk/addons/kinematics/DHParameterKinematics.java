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

import com.neuronrobotics.sdk.addons.kinematics.TransformFactory;
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


// TODO: Auto-generated Javadoc
/**
 * The Class DHParameterKinematics.
 */
public class DHParameterKinematics extends AbstractKinematicsNR implements ITaskSpaceUpdateListenerNR, IJointSpaceUpdateListenerNR{
	
	/** The chain. */
	private DHChain chain=null;

	/** The links listeners. */
	private ArrayList<Affine> linksListeners = new ArrayList<Affine>();
	
	/** The current target. */
	private Affine currentTarget = new Affine();
	
	/** The disconnecting. */
	boolean disconnecting=false;

	/** The l. */
	IDeviceConnectionEventListener l = new IDeviceConnectionEventListener() {
		@Override public void onDisconnect(BowlerAbstractDevice source) {
			if(!disconnecting){
				disconnecting=true;
				disconnect();
			}
			
		}
		@Override public void onConnect(BowlerAbstractDevice source) {}
	} ;

	private ArrayList<LinkConfiguration> configs;
	
	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param bad the bad
	 * @param linkStream the link stream
	 */
	public DHParameterKinematics( BowlerAbstractDevice bad, Element  linkStream ){
		super(linkStream,new LinkFactory(bad));
		setChain(getDhParametersChain());
		if(getFactory().getDyio()!=null)
			getFactory().getDyio().addConnectionEventListener(l);
	}
	
	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param bad the bad
	 * @param linkStream the link stream
	 */
	public DHParameterKinematics( BowlerAbstractDevice bad, InputStream  linkStream ){
		super(linkStream,new LinkFactory(bad));
		setChain(getDhParametersChain());
		if(getFactory().getDyio()!=null)
			getFactory().getDyio().addConnectionEventListener(l);
	}
	
	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param bad the bad
	 * @param linkStream the link stream
	 * @param depricated the depricated
	 */
	@Deprecated
	public DHParameterKinematics( BowlerAbstractDevice bad, InputStream linkStream ,InputStream depricated ){
		this(bad, linkStream);
	}
	
	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param bad the bad
	 */
	public DHParameterKinematics(BowlerAbstractDevice bad) {
		this(bad,XmlFactory.getDefaultConfigurationStream("TrobotLinks.xml"));
	}

	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param bad the bad
	 * @param file the file
	 */
	public DHParameterKinematics(BowlerAbstractDevice bad, String file) {
		this(bad,XmlFactory.getDefaultConfigurationStream(file));
	}

	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param bad the bad
	 * @param configFile the config file
	 * @throws FileNotFoundException the file not found exception
	 */
	public DHParameterKinematics(BowlerAbstractDevice bad,  File configFile) throws FileNotFoundException {
		this(bad,new FileInputStream(configFile));
	}
	
	/**
	 * Instantiates a new DH parameter kinematics.
	 */
	public DHParameterKinematics() {
		this(null,XmlFactory.getDefaultConfigurationStream("TrobotLinks.xml"));
	}
	

	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param file the file
	 */
	public DHParameterKinematics( String file) {
		this(null,XmlFactory.getDefaultConfigurationStream(file));
	}

	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param linkStream the link stream
	 */
	public DHParameterKinematics( Element linkStream) {
		this(null,linkStream);
	}
	
	/**
	 * Instantiates a new DH parameter kinematics.
	 *
	 * @param configFile the config file
	 * @throws FileNotFoundException the file not found exception
	 */
	public DHParameterKinematics( File configFile) throws FileNotFoundException {
		this(null,new FileInputStream(configFile));
	}


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)throws Exception {
		return getDhChain().inverseKinematics(taskSpaceTransform, getCurrentJointSpaceVector());
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#forwardKinematics(double[])
	 */
	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		if(jointSpaceVector == null || getDhChain() == null)
			return new TransformNR();
		TransformNR rt = getDhChain().forwardKinematics(jointSpaceVector);
		return rt;
	}
	
	
	
	/**
	 * Gets the Jacobian matrix.
	 *
	 * @return a matrix representing the Jacobian for the current configuration
	 */
	public Matrix getJacobian(){
		long time = System.currentTimeMillis();
		Matrix m = getDhChain().getJacobian(getCurrentJointSpaceVector());
		System.out.println("Jacobian calc took: "+(System.currentTimeMillis()-time));
		return m;
	}
	
	/**
	 * Gets the chain transformations.
	 *
	 * @return the chain transformations
	 */
	public ArrayList<TransformNR> getChainTransformations(){
		return getChain().getChain(getCurrentJointSpaceVector());
	}

	/**
	 * Sets the dh chain.
	 *
	 * @param chain the new dh chain
	 */
	public void setDhChain(DHChain chain) {
		this.setChain(chain);
	}

	/**
	 * Gets the dh chain.
	 *
	 * @return the dh chain
	 */
	public DHChain getDhChain() {
		return getChain();
	}

	/**
	 * Gets the chain.
	 *
	 * @return the chain
	 */
	public DHChain getChain() {
		return chain;
	}

	/**
	 * Sets the chain.
	 *
	 * @param chain the new chain
	 */
	public void setChain(DHChain chain) {
		this.chain = chain;
		ArrayList<DHLink> dhLinks = chain.getLinks();
		for(int i=linksListeners.size();i<dhLinks.size();i++){
			linksListeners.add(new Affine());
		}
		LinkFactory lf = getFactory();
		configs = lf.getLinkConfigurations();
		for(int i=0;i<dhLinks.size();i++){
			dhLinks.get(i).setListener(linksListeners.get(i));
			dhLinks.get(i).setRootListener(getRootListener());
			//This mapps together the position of the links in the kinematics and the link actions themselves (used for cameras and tools)
			lf.getLink(configs.get(i)).setGlobalPositionListener(linksListeners.get(i));
			if(getLinkConfiguration(i).getType().isTool()){
				dhLinks.get(i).setLinkType(DhLinkType.TOOL);
			}else if(getLinkConfiguration(i).getType().isPrismatic())
				dhLinks.get(i).setLinkType(DhLinkType.PRISMATIC);
			else
				dhLinks.get(i).setLinkType(DhLinkType.ROTORY);
		}
		addPoseUpdateListener(this);
		addJointSpaceListener(this);
		try {
			currentJointSpacePositions=null;
			currentJointSpaceTarget=null;
			//setDesiredJointSpaceVector(getCurrentJointSpaceVector(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		
		String xml = "";
		
		xml+="\t<cadEngine>\n";
		xml+="\t\t<gist>"+getCadEngine()[0]+"</gist>\n";
		xml+="\t\t<file>"+getCadEngine()[1]+"</file>\n";
		xml+="\t</cadEngine>\n";
		
		xml+="\t<kinematics>\n";
		xml+="\t\t<gist>"+getDhEngine()[0]+"</gist>\n";
		xml+="\t\t<file>"+getDhEngine()[1]+"</file>\n";
		xml+="\t</kinematics>\n";
		
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

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#disconnectDevice()
	 */
	@Override
	public void disconnectDevice() {
		// TODO Auto-generated method stub
		removePoseUpdateListener(this);
		removeJointSpaceUpdateListener(this);
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
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTargetTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,
			TransformNR pose) {
		// TODO Auto-generated method stub
		//TransformFactory.getTransform(pose, getCurrentTargetAffine());
	}

	/**
	 * Gets the inverse solver.
	 *
	 * @return the inverse solver
	 */
	public DhInverseSolver getInverseSolver() {
		return chain.getInverseSolver();
	}

	/**
	 * Sets the inverse solver.
	 *
	 * @param inverseSolver the new inverse solver
	 */
	public void setInverseSolver(DhInverseSolver inverseSolver) {
		chain.setInverseSolver(inverseSolver);
	}

	/**
	 * Gets the current target affine.
	 *
	 * @return the current target affine
	 */
	public Affine getCurrentTargetAffine() {
		return currentTarget;
	}

	/**
	 * Adds the new link.
	 *
	 * @param newLink the new link
	 * @param dhLink the dh link
	 */
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

	/**
	 * Removes the link.
	 *
	 * @param index the index
	 */
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
	
	/**
	 * Update cad locations.
	 */
	public void updateCadLocations(){
		double[] joints =getCurrentJointSpaceVector();
		getChain().getChain(joints);
		onJointSpaceUpdate(this, getCurrentJointSpaceVector());
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, double[])
	 */
	@Override
	public void onJointSpaceUpdate(final AbstractKinematicsNR source, final double[] joints) {
				ArrayList<TransformNR> ll;
				if(getChain().getCachedChain().size()==0 ){
					ll= getChain().getChain(joints);
				}else
					ll= getChain().getCachedChain();
				//System.out.println("Updating "+source.getScriptingName()+" links # "+linkPos.size());
				for(int i=0;i<ll.size();i++) {
					final ArrayList<TransformNR> linkPos = ll;
					final int index=i;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try{
								TransformFactory.getTransform(linkPos.get(index), getChain().getLinks().get(index).getListener());
								
							}catch(Exception ex){
								//ex.printStackTrace();
							}
						}
					});
				}
	
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceTargetUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, double[])
	 */
	@Override
	public void onJointSpaceTargetUpdate(AbstractKinematicsNR source,
			double[] joints) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceLimit(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, int, com.neuronrobotics.sdk.addons.kinematics.JointLimit)
	 */
	@Override
	public void onJointSpaceLimit(AbstractKinematicsNR source, int axis,
			JointLimit event) {
		// TODO Auto-generated method stub
		
	}







}
