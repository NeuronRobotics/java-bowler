package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.transform.Affine;

import javax.management.RuntimeErrorException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
//import com.neuronrobotics.sdk.addons.kinematics.PidRotoryLink;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
//import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;
// TODO: Auto-generated Javadoc
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;

/**
 * The Class AbstractKinematicsNR.
 */
public abstract class AbstractKinematicsNR extends NonBowlerDevice implements IPIDEventListener, ILinkListener {
	
	/** The configurations. */
	private ArrayList<PIDConfiguration> pidConfigurations= new ArrayList<PIDConfiguration>();

	/** The task space update listeners. */
	private ArrayList<ITaskSpaceUpdateListenerNR> taskSpaceUpdateListeners = new ArrayList<ITaskSpaceUpdateListenerNR>();
	
	/** The joint space update listeners. */
	protected ArrayList<IJointSpaceUpdateListenerNR> jointSpaceUpdateListeners = new ArrayList<IJointSpaceUpdateListenerNR>();
	
	/** The reg listeners. */
	private ArrayList<IRegistrationListenerNR> regListeners= new ArrayList<IRegistrationListenerNR>();
	
	/** The mobile bases. */
	private ArrayList<MobileBase> mobileBases = new ArrayList<MobileBase>();
	
	/** The dh engine. */
	private String [] dhEngine =new String[]{"https://gist.github.com/bcb4760a449190206170.git","DefaultDhSolver.groovy"}; 
	
	/** The cad engine. */
	private String [] cadEngine =new String[]{"https://gist.github.com/bcb4760a449190206170.git","ThreeDPrintCad.groovy"};  


	/** The current joint space positions. */
	/*This is in RAW joint level ticks*/
	protected double[] currentJointSpacePositions=null;
	
	/** The current joint space target. */
	protected double [] currentJointSpaceTarget;
	
	/** The current pose target. */
	private TransformNR currentPoseTarget=new TransformNR();
	
	/** The base2 fiducial. */
	private TransformNR base2Fiducial=new TransformNR();
	
	/** The fiducial2 ras. */
	private TransformNR fiducial2RAS=new TransformNR();
	
	/** The no flush. */
	private boolean noFlush = false;
	
	/** The no xml config. */
	private boolean noXmlConfig=true;
	
	/** The dh parameters chain. */
	private DHChain dhParametersChain=null;
	
	/** The root. */
	private Affine root = new Affine();
	
	/* The device */
	/** The factory. */
	//private IPIDControl device =null;
	private LinkFactory factory=null;
	
	/** The retry number before fail. */
	private int retryNumberBeforeFail = 5;
	
	
	/**
	 * Gets the root listener.
	 *
	 * @return the root listener
	 */
	public Affine getRootListener() {
		return root;
	}

	/**
	 * Sets the root listener.
	 *
	 * @param listener the new root listener
	 */
	void setRootListener(Affine listener) {
		this.root = listener;
	}
	
	/**
	 * This method tells the connection object to disconnect its pipes and close out the connection. Once this is called, it is safe to remove your device.
	 */
	
	public abstract void disconnectDevice();
	
	/**
	 * Connect device.
	 *
	 * @return true, if successful
	 */
	public abstract  boolean connectDevice();
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#getNamespacesImp()
	 */
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		ArrayList<String> back = new ArrayList<String>();
		back.add("bcs.cartesian.*");
		return back;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#disconnectDeviceImp()
	 */
	public void disconnectDeviceImp(){
		getFactory().removeLinkListener(this);
		IPidControlNamespace device = getFactory().getPid();
		if(device!=null)
			device.removePIDEventListener(this);
		disconnectDevice();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#connectDeviceImp()
	 */
	public  boolean connectDeviceImp(){
		return connectDevice();
	}
		

	/**
	 * Instantiates a new abstract kinematics nr.
	 */
	public AbstractKinematicsNR(){
//		File l = new File("RobotLog_"+getDate()+"_"+System.currentTimeMillis()+".txt");
//		//File e = new File("RobotError_"+getDate()+"_"+System.currentTimeMillis()+".txt");
//		try {
//			PrintStream p =new PrintStream(l);
//			Log.setOutStream(new PrintStream(p));
//			Log.setErrStream(new PrintStream(p));						
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
		setDhParametersChain(new DHChain( this));
	}
	
	/**
	 * Instantiates a new abstract kinematics nr.
	 *
	 * @param configFile the config file
	 * @param f the f
	 */
	public AbstractKinematicsNR(InputStream configFile,LinkFactory f){
		this();
		Document doc =XmlFactory.getAllNodesDocument(configFile);
		NodeList nodListofLinks = doc.getElementsByTagName("appendage");
		for (int i = 0; i < 1; i++) {			
		    Node linkNode = nodListofLinks.item(i);
		    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
				noXmlConfig=false;
				if(configFile!=null && f!=null){
					setDevice(f,loadConfig((Element) linkNode));
				}
		    }else{
		    	Log.info("Not Element Node");
		    }
		}

		
	}
	
	/**
	 * Instantiates a new abstract kinematics nr.
	 *
	 * @param doc the doc
	 * @param f the f
	 */
	public AbstractKinematicsNR(Element doc,LinkFactory f){
		this();
		noXmlConfig=false;
		if(doc!=null && f!=null){
			setDevice(f,loadConfig(doc));
		}

		
	}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	private String getDate(){
		Timestamp t = new Timestamp(System.currentTimeMillis());
		return t.toString().split("\\ ")[0];
	}
	
	/**
	 * Load XML configuration file, 
	 * then store in LinkConfiguration (ArrayList type).
	 *
	 * @param doc the doc
	 * @return the array list
	 */
	protected ArrayList<LinkConfiguration> loadConfig(Element doc){
		ArrayList<LinkConfiguration> localConfigsFromXml=new ArrayList<LinkConfiguration>();
		
		
		NodeList nodListofLinks = doc.getChildNodes();
		setGitCadEngine(getGitCodes( doc,"cadEngine"));
		setGitDhEngine(getGitCodes( doc,"kinematics"));
		for (int i = 0; i < nodListofLinks .getLength(); i++) {			
		    Node linkNode = nodListofLinks.item(i);
		    
		    if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("link")) {
		    	LinkConfiguration newLinkConf = new LinkConfiguration((Element) linkNode);
		    	localConfigsFromXml.add(newLinkConf);
		    	
		    	NodeList dHParameters =linkNode.getChildNodes();
		    	//System.out.println("Link "+newLinkConf.getName()+" has "+dHParameters .getLength()+" children");
				for (int x = 0; x < dHParameters .getLength(); x++) {			
				    Node nNode = dHParameters.item(x);
				    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.getNodeName().contentEquals("DHParameters")) {
				    	Element dhNode =(Element)	nNode;
				    	DHLink newLink = new DHLink(dhNode);
				    	getDhParametersChain().addLink(newLink);//0->1
				    	NodeList mobileBasesNodeList = dhNode.getChildNodes();
						for (int j = 0; j < mobileBasesNodeList.getLength(); j++) {			
						    Node mb = mobileBasesNodeList.item(j);
						    if (mb.getNodeType() == Node.ELEMENT_NODE && mb.getNodeName().contentEquals("mobilebase")) {
						    	final MobileBase newMobileBase = new MobileBase((Element)mb);
						    	mobileBases.add(newMobileBase);
						    	newLink.setMobileBaseXml(newMobileBase);
						    	newLink.addDhLinkPositionListener(new IDhLinkPositionListener() {
									@Override
									public void onLinkGlobalPositionChange(TransformNR newPose) {
										Log.debug("Motion in the D-H link has caused this mobile base to move");
										newMobileBase.setGlobalToFiducialTransform(newPose);
									}
								});
						    }
						}
				    }else{
					    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.getNodeName().contentEquals("slaveLink")) {
					    	//System.out.println("Slave link found: ");
					    	LinkConfiguration jc =new LinkConfiguration((Element) nNode);
					    	//System.out.println(jc);
					    	newLinkConf.getSlaveLinks().add(jc);
					    }
				    }
				}
		    }else if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("name")) {
		    	try{
		    		setScriptingName(XmlFactory.getTagValue("name",doc));
		    	}catch(Exception E){
		    		E.printStackTrace();
		    	}
		    }
		    else if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("ZframeToRAS")) {
		    	Element eElement = (Element)linkNode;	    		    
		    	setZframeToGlobalTransform(new TransformNR(	Double.parseDouble(XmlFactory.getTagValue("x",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("y",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("z",eElement)), 
							    			new RotationNR(new double[]{	Double.parseDouble(XmlFactory.getTagValue("rotw",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotx",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("roty",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotz",eElement))})));	
		    }else if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("baseToZframe")) {
		    	Element eElement = (Element)linkNode;	    	    
		    	setBaseToZframeTransform(new TransformNR(	Double.parseDouble(XmlFactory.getTagValue("x",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("y",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("z",eElement)), 
							    			new RotationNR(new double[]{	Double.parseDouble(XmlFactory.getTagValue("rotw",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotx",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("roty",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotz",eElement))})));	 
		    }else{
		    	//System.err.println(linkNode.getNodeName());
		    	Log.error("Node not known: "+linkNode.getNodeName());
		    }
		}

		
		return localConfigsFromXml;
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	/*
	 * 
	 * Generate the xml configuration to generate an XML of this robot. 
	 */
	public String getXml(){
		String xml = "<root>\n";
		xml+="\n<appendage>";
		xml+="\n<name>"+getScriptingName()+"</name>\n";
		for(int i=0;i<getLinkConfigurations().size();i++){
			xml+="<link>\n";
			xml+=getLinkConfiguration(i).getXml();
			xml+="\n</link>\n";
		}
		xml+="\n<ZframeToRAS>\n";
		xml+=getFiducialToGlobalTransform().getXml();
		xml+="\n</ZframeToRAS>\n";
		
		xml+="\n<baseToZframe>\n";
		xml+=getRobotToFiducialTransform().getXml();
		xml+="\n</baseToZframe>\n";
		xml+="\n</appendage>";
		xml+="\n</root>";
		return xml;
	}


	/**
	 * Gets the link configuration.
	 *
	 * @param linkIndex the link index
	 * @return the link configuration
	 */
	public LinkConfiguration getLinkConfiguration(int linkIndex){
		return getLinkConfigurations().get(linkIndex);
	}
	
	/**
	 * Gets the link configurations.
	 *
	 * @return the link configurations
	 */
	public ArrayList<LinkConfiguration> getLinkConfigurations() {

		return getFactory().getLinkConfigurations();
	
	}

	
	/**
	 * Gets the link current configuration.
	 *
	 * @param chan the chan
	 * @return the link current configuration
	 */
	public PIDConfiguration getLinkCurrentConfiguration(int chan){
		return getAxisPidConfiguration().get(chan);
	}
	
	/**
	 * Sets the link current configuration.
	 *
	 * @param chan the chan
	 * @param c the c
	 */
	public void setLinkCurrentConfiguration(int chan,PIDConfiguration c){
		getAxisPidConfiguration().set(chan, c);
	}
	
	/**
	 * Gets the device.
	 *
	 * @return the device
	 */
	protected LinkFactory getDevice(){
		return getFactory();
	}
	
	/**
	 * Gets the abstract link.
	 *
	 * @param index the index
	 * @return the abstract link
	 */
	public AbstractLink getAbstractLink(int index){
		return getFactory().getLink(getLinkConfiguration(index));
	}
	
	/**
	 * Sets the device.
	 *
	 * @param f the f
	 * @param linkConfigs the link configs
	 */
	protected void setDevice(LinkFactory f, ArrayList<LinkConfiguration> linkConfigs){
		Log.info("Loading device: "+f.getClass()+" "+f);
		setFactory(f);
		//Log.enableDebugPrint(true);
		for(int i=0;i<linkConfigs.size();i++){
			LinkConfiguration c = linkConfigs.get(i);
			c.setLinkIndex(i);
			getFactory().getLink(c);
			Log.info("\nAxis #"+i+" Configuration:\n"+c);
			if(c.getType()==LinkType.PID){
				IPidControlNamespace device = getFactory().getPid();
				try{
					PIDConfiguration tmpConf = device.getPIDConfiguration(c.getHardwareIndex());
					tmpConf.setGroup(c.getHardwareIndex());
					tmpConf.setKP(c.getKP());
					tmpConf.setKI(c.getKI());
					tmpConf.setKD(c.getKD());
					tmpConf.setEnabled(true);
					tmpConf.setInverted(c.isInverted());
					tmpConf.setAsync(false);
				
					tmpConf.setUseLatch(false);
					tmpConf.setIndexLatch(c.getIndexLatch());
					tmpConf.setStopOnIndex(false);
					
					Log.info("\nAxis #"+i+" "+tmpConf);
					getAxisPidConfiguration().add(tmpConf);
					setLinkCurrentConfiguration(i,tmpConf);
					//Send configuration for ONE axis
					device.ConfigurePIDController(tmpConf);		
				}catch(Exception ex){				
					Log.error("Configuration #"+i+" failed!!");
					ex.printStackTrace();
				}
				device.addPIDEventListener(this);
			}
		}
		getCurrentTaskSpaceTransform();
		getFactory().addLinkListener(this);
		getDhParametersChain().setFactory(getFactory());
		
		
		

		//filling up the d-h parameters so the chain sizes match
		while(getDhParametersChain().getLinks().size() < linkConfigs.size()){
			getDhParametersChain().addLink(new DHLink(0,0,0,0));
		}
	}
	
	/**
	 * Gets the number of links defined in the configuration file.
	 *
	 * @return number of links in XML
	 */
	public int getNumberOfLinks(){
		return getLinkConfigurations().size();
	}
	
	/**
	 * This takes a reading of the robots position and converts it to a joint space vector
	 * This vector is converted to task space and returned .
	 *
	 * @return taskSpaceVector in mm,radians [x,y,z,rotx,rotY,rotZ]
	 */
	public TransformNR getCurrentTaskSpaceTransform() {
		TransformNR fwd  = forwardKinematics(getCurrentJointSpaceVector());
		if(fwd==null)
			throw new RuntimeException("Implementations of the kinematics need to return a transform not null");
		//Log.info("Getting robot task space "+fwd);
		TransformNR taskSpaceTransform=forwardOffset(fwd);
		//Log.info("Getting global task space "+taskSpaceTransform);
		return taskSpaceTransform;
	}
	
	/**
	 * This takes a reading of the robots position and converts it to a joint pace vector
	 * This vector is converted to Joint space and returned .
	 *
	 * @return JointSpaceVector in mm,radians
	 */
	public double[] getCurrentJointSpaceVector() {
		if(currentJointSpacePositions==null){
			//Happens once and only once on the first initialization
			currentJointSpacePositions= new double [getNumberOfLinks()];
			currentJointSpaceTarget  = new double [getNumberOfLinks()];
			for(int i=0;i<getNumberOfLinks();i++){
				//double pos = currentLinkSpacePositions[getLinkConfigurations().get(i).getHardwareIndex()];
				//Here the RAW values are converted to engineering units
				try{
					currentJointSpacePositions[i] = getFactory().getLink(getLinkConfiguration(i)).getCurrentEngineeringUnits();
				}catch (Exception ex){
					currentJointSpacePositions[i] =0;
				}
			}
			firePoseUpdate();
		}
		double [] jointSpaceVect = new double[getNumberOfLinks()];
		for(int i=0;i<getNumberOfLinks();i++){
			//double pos = currentLinkSpacePositions[getLinkConfigurations().get(i).getHardwareIndex()];
			//Here the RAW values are converted to engineering units
			jointSpaceVect[i] = currentJointSpacePositions[i];
		}
		
		return jointSpaceVect;
	}
	
	/**
	 * This calculates the target pose .
	 *
	 * @param taskSpaceTransform the task space transform
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public double[]  setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) throws Exception{
		Log.info("Setting target pose: "+taskSpaceTransform);
		setCurrentPoseTarget(taskSpaceTransform);
		
		double [] jointSpaceVect = inverseKinematics(
									inverseOffset(taskSpaceTransform)
									);
		if(jointSpaceVect==null)
			throw new RuntimeException("The kinematics model muts return and array, not null");
		setDesiredJointSpaceVector(jointSpaceVect,  seconds);
		return jointSpaceVect;
	}
	
	/**
	 * Checks the desired pose for ability for the IK to calculate a valid pose.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @return True if pose is reachable, false if it is not
	 */
	public boolean checkTaskSpaceTransform(TransformNR taskSpaceTransform) {
		try{
			Log.info("Checking target pose: "+taskSpaceTransform);
			taskSpaceTransform = inverseOffset(taskSpaceTransform);
			double [] jointSpaceVect = inverseKinematics(taskSpaceTransform);
			double[] uLim=factory.getUpperLimits();
			double[] lLim=factory.getLowerLimits();
			for(int i=0;i<jointSpaceVect.length;i++){
				if(jointSpaceVect[i]>uLim[i])
					return false;
				if(jointSpaceVect[i]<lLim[i])
					return false;
			}
		}catch(Exception ex){
			return false;
		}
		return true;
	}
	
	/**
	 * This calculates the target pose .
	 *
	 * @param jointSpaceVect the joint space vect
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public synchronized double[] setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds) throws Exception{
		if(jointSpaceVect.length != getNumberOfLinks()){
			throw new IndexOutOfBoundsException("Vector must be "+getNumberOfLinks()+" links, actual number of links = "+jointSpaceVect.length); 
		}
		String joints = "[";
		for(int i=0;i<jointSpaceVect.length;i++){
			joints+=jointSpaceVect[i]+" ";
		}
		joints+="]";
		Log.info("Setting target joints: "+joints);

		int except=0;
		Exception e = null;
		do{
			try{
				factory.setCachedTargets(jointSpaceVect);
				if(!isNoFlush()){
					//
					factory.flush(seconds);
					//
				}
				except=0;
				e = null;
			}catch(Exception ex){
				except++;
				e=ex;
			}
		}while(except>0 && except <getRetryNumberBeforeFail());
		if(e!=null)
			throw e;
		
//		for(int i=0;i<getNumberOfLinks();i++){
//			setDesiredJointAxisValue(i, jointSpaceVect[i],  seconds);
//		}
		
		
		currentJointSpaceTarget = jointSpaceVect;
		TransformNR fwd  = forwardKinematics(currentJointSpaceTarget);
		fireTargetJointsUpdate(currentJointSpaceTarget,fwd );
		return jointSpaceVect;
	}
	
	/**
	 * Calc forward.
	 *
	 * @param jointSpaceVect the joint space vect
	 * @return the transform nr
	 */
	public TransformNR calcForward(double[] jointSpaceVect){
		return forwardOffset(forwardKinematics(jointSpaceVect));
	}
	
	/**
	 * Calc home.
	 *
	 * @return the transform nr
	 */
	public TransformNR calcHome(){
		double homevect[] = new double[getNumberOfLinks()];
		for(int i=0;i<homevect.length;i++){
			homevect[i]=0;
		}
		return forwardOffset(forwardKinematics(homevect));
	}
	
	/**
	 * Sets an individual target joint position .
	 *
	 * @param axis the joint index to set
	 * @param value the value to set it to
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @throws Exception If there is a workspace error
	 */
	public void setDesiredJointAxisValue(int axis, double value, double seconds) throws Exception{
		LinkConfiguration c = getLinkConfiguration(axis);

		Log.info("Setting single target joint in mm/deg, axis="+axis+" value="+value);
		
		currentJointSpaceTarget[axis] = value;
		try{
			getFactory().getLink(c).setTargetEngineeringUnits(value);
		}catch (Exception ex){
			throw new Exception("Joint hit software bound, index "+axis+" attempted: "+value+" boundes: U="+c.getUpperLimit()+ ", L="+c.getLowerLimit());
		}
		if(!isNoFlush()){
			int except=0;
			Exception e = null;
			do{
				try{
					getFactory().getLink(c).flush(seconds);
					except=0;
					e = null;
				}catch(Exception ex){
					except++;
					e=ex;
				}
			}while(except>0 && except <getRetryNumberBeforeFail());
			if(e!=null)
				throw e;	
		}
		TransformNR fwd  = forwardKinematics(currentJointSpaceTarget);
		fireTargetJointsUpdate(currentJointSpaceTarget,fwd );
		return;
	}
	
	/**
	 * Fire pose transform.
	 *
	 * @param transform the transform
	 */
	protected void firePoseTransform(TransformNR transform){
		for(int i=0;i<taskSpaceUpdateListeners.size();i++){
			ITaskSpaceUpdateListenerNR p=taskSpaceUpdateListeners.get(i);
			p.onTaskSpaceUpdate(this, transform);
		}
	}
	
	/**
	 * Fire pose update.
	 */
	protected void firePoseUpdate(){
		//Log.info("Pose update");
		firePoseTransform(getCurrentTaskSpaceTransform());

		double[] vect = getCurrentJointSpaceVector();
		
		for(int i=0;i<jointSpaceUpdateListeners.size();i++){
			IJointSpaceUpdateListenerNR p=jointSpaceUpdateListeners.get(i);
			p.onJointSpaceUpdate(this, vect);
		}
	}

	/**
	 * Fire target joints update.
	 *
	 * @param jointSpaceVector the joint space vector
	 * @param fwd the fwd
	 */
	protected void fireTargetJointsUpdate(double[] jointSpaceVector, TransformNR fwd ){
		
		setCurrentPoseTarget(forwardOffset(fwd));
		for(ITaskSpaceUpdateListenerNR p:taskSpaceUpdateListeners){
			p.onTargetTaskSpaceUpdate(this, getCurrentPoseTarget());
			//new RuntimeException("Fireing "+p.getClass().getName()).printStackTrace();
		}
		for(IJointSpaceUpdateListenerNR p:jointSpaceUpdateListeners){
			p.onJointSpaceTargetUpdate(this, currentJointSpaceTarget);
		}
	}
	
	/**
	 * Fire joint space limit update.
	 *
	 * @param axis the axis
	 * @param event the event
	 */
	private void fireJointSpaceLimitUpdate(int axis,JointLimit event){
		for(IJointSpaceUpdateListenerNR p:jointSpaceUpdateListeners){
			p.onJointSpaceLimit(this, axis, event);
		}
	}
	
	/**
	 * Gets the fiducial to global transform.
	 *
	 * @return the fiducial to global transform
	 */
	public TransformNR getFiducialToGlobalTransform() {
		return fiducial2RAS;
	}

	/**
	 * Sets the base to zframe transform.
	 *
	 * @param baseToFiducial the new base to zframe transform
	 */
	public void setBaseToZframeTransform(TransformNR baseToFiducial) {
		Log.info("Setting Fiducial To base Transform "+baseToFiducial);
		this.base2Fiducial = baseToFiducial;
		for(IRegistrationListenerNR r: regListeners){
			r.onBaseToFiducialUpdate(this, baseToFiducial);
		}
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				TransformFactory.nrToAffine(forwardOffset(new TransformNR()), root);
			}
		});
	}
	
	/**
	 * Sets the zframe to global transform.
	 *
	 * @param fiducialToRAS the new zframe to global transform
	 */
	private void setZframeToGlobalTransform(TransformNR fiducialToRAS) {
		setGlobalToFiducialTransform(fiducialToRAS);
	}

		
	/**
	 * Gets the robot to fiducial transform.
	 *
	 * @return the robot to fiducial transform
	 */
	public TransformNR getRobotToFiducialTransform() {
		return base2Fiducial;
	}
	
	/**
	 * Sets the global to fiducial transform.
	 *
	 * @param frameToBase the new global to fiducial transform
	 */
	public void setGlobalToFiducialTransform(TransformNR frameToBase) {
		Log.info("Setting Global To Fiducial Transform "+frameToBase);
		this.fiducial2RAS = frameToBase;
	
		for(IRegistrationListenerNR r: regListeners){
			r.onFiducialToGlobalUpdate(this, frameToBase);
		}
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				TransformFactory.nrToAffine(forwardOffset(new TransformNR()), root);
			}
		});
	}
	
	/**
	 * Inverse offset.
	 *
	 * @param t the t
	 * @return the transform nr
	 */
	public TransformNR inverseOffset(TransformNR t){
		//System.out.println("RobotToFiducialTransform "+getRobotToFiducialTransform());
		//System.out.println("FiducialToRASTransform "+getFiducialToRASTransform());		
		Matrix rtz = getFiducialToGlobalTransform().getMatrixTransform().inverse();
		Matrix ztr = getRobotToFiducialTransform().getMatrixTransform().inverse();

		Matrix current = t.getMatrixTransform();
		Matrix mForward = ztr.times(rtz).times(current);
		
		return new TransformNR( mForward);
	}
	
	/**
	 * Forward offset.
	 *
	 * @param t the t
	 * @return the transform nr
	 */
	public TransformNR forwardOffset(TransformNR t){
		Matrix btt = getRobotToFiducialTransform().getMatrixTransform();
		Matrix ftb = getFiducialToGlobalTransform().getMatrixTransform();
		Matrix current = t.getMatrixTransform();
		Matrix mForward = ftb.times(btt).times(current);
		return new TransformNR( mForward);
	}
	
	
	
	/**
	 * Adds the joint space listener.
	 *
	 * @param l the l
	 */
	public void addJointSpaceListener(IJointSpaceUpdateListenerNR l){
		if(jointSpaceUpdateListeners.contains(l) || l==null)
			return;
		jointSpaceUpdateListeners.add(l);
	}
	
	/**
	 * Removes the joint space update listener.
	 *
	 * @param l the l
	 */
	public void removeJointSpaceUpdateListener(IJointSpaceUpdateListenerNR l){
		if(jointSpaceUpdateListeners.contains(l))
			jointSpaceUpdateListeners.remove(l);
	}
	
	/**
	 * Adds the registration listener.
	 *
	 * @param l the l
	 */
	public void addRegistrationListener(IRegistrationListenerNR l){
		if(regListeners.contains(l)|| l==null)
			return;
		regListeners.add(l);
		l.onBaseToFiducialUpdate(this, getRobotToFiducialTransform());
	}
	
	/**
	 * Removes the regestration update listener.
	 *
	 * @param l the l
	 */
	public void removeRegestrationUpdateListener(IRegistrationListenerNR l){
		if(regListeners.contains(l))
			regListeners.remove(l);
	}
	
	/**
	 * Adds the pose update listener.
	 *
	 * @param l the l
	 */
	public void addPoseUpdateListener(ITaskSpaceUpdateListenerNR l){
		if(taskSpaceUpdateListeners.contains(l) || l==null){
			return;
		}
		//new RuntimeException("adding "+l.getClass().getName()).printStackTrace();
		taskSpaceUpdateListeners.add(l);
	}
	
	/**
	 * Removes the pose update listener.
	 *
	 * @param l the l
	 */
	public void removePoseUpdateListener(ITaskSpaceUpdateListenerNR l){
		if(taskSpaceUpdateListeners.contains(l)){
			//new RuntimeException("Removing "+l.getClass().getName()).printStackTrace();
			taskSpaceUpdateListeners.remove(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ILinkListener#onLinkPositionUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractLink, double)
	 */
	@Override
	public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue){
		for(LinkConfiguration c:getLinkConfigurations()){
			AbstractLink tmp = getFactory().getLink(c);
			if(tmp == source){//Check to see if this lines up with a known link
				//Log.info("Got PID event "+source+" value="+engineeringUnitsValue);
				
				currentJointSpacePositions[getLinkConfigurations().indexOf(c)]=engineeringUnitsValue;
				firePoseUpdate();
				return;
			}
		}
		Log.error("Got UKNOWN PID event "+source);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDEvent(com.neuronrobotics.sdk.pid.PIDEvent)
	 */
	@Override
	public void onPIDEvent(PIDEvent e) {
		// Ignore and use Link space events
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDLimitEvent(com.neuronrobotics.sdk.pid.PIDLimitEvent)
	 */
	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		for(int i=0;i<getNumberOfLinks();i++){
			if(getLinkConfiguration(i).getHardwareIndex() == e.getGroup())
				fireJointSpaceLimitUpdate(i,new JointLimit(i,e,getLinkConfiguration(i)));
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDReset(int, int)
	 */
	@Override
	public void onPIDReset(int group, int currentValue) {
		// ignore at this level
	}

	/**
	 * This method uses the latch values to home all of the robot links.
	 */
	public void homeAllLinks() {
		
		for(int i=0;i<getNumberOfLinks();i++){
			
			homeLink(i);
			//ThreadUtil.wait(2000);
		}
		
	} 
	
	/**
	 * This method uses the latch values to home the given link of the robot links.
	 *
	 */
	private long homeTime;

	/**
	 * Run home.
	 *
	 * @param joint the joint
	 * @param tps the tps
	 */
	private void runHome(PIDChannel joint, int tps){
		IPIDEventListener listen = new IPIDEventListener() {
			
			@Override
			public void onPIDReset(int group, int currentValue) {}
			
			@Override
			public void onPIDLimitEvent(PIDLimitEvent e) {
				homeTime=0;//short circut the waiting loop
				Log.debug("Homing PID Limit event "+e);
			}
			
			@Override
			public void onPIDEvent(PIDEvent e) {
				 homeTime=System.currentTimeMillis();
			}
		};
		joint.addPIDEventListener(listen);
		homeTime=System.currentTimeMillis();

		joint.SetPIDSetPoint(tps, 0);
		Log.info("Homing output to value: "+tps);
		while(	(System.currentTimeMillis() < homeTime+3000)){
			ThreadUtil.wait(100);
		}
		joint.removePIDEventListener(listen);
	}
	
	/**
	 * Home link.
	 *
	 * @param link the link
	 */
	public void homeLink(int link) {
		if(link<0 || link>=getNumberOfLinks()){
			throw new IndexOutOfBoundsException("There are only "+getNumberOfLinks()+" known links, requested:"+link);
		}
		LinkConfiguration conf = getLinkConfiguration(link);
		if(conf.getType() == LinkType.PID){
			getFactory().getPid().removePIDEventListener(this);
			//Range is in encoder units
			double range = Math.abs(conf.getUpperLimit()-conf.getLowerLimit())*2;
			
			Log.info("Homing link:"+link+" to latch value: "+conf.getIndexLatch());
			PIDConfiguration pidConf = getLinkCurrentConfiguration(link);
			PIDChannel joint = getFactory().getPid().getPIDChannel(conf.getHardwareIndex());
			
			
			//Clear the index
			pidConf.setStopOnIndex(false);
			pidConf.setUseLatch(false);
			pidConf.setIndexLatch(conf.getIndexLatch());
			joint.ConfigurePIDController(pidConf);//Sets up the latch
	
			//Move forward to stop
			runHome(joint,(int) (range));
			
			//Enable index
			pidConf.setStopOnIndex(true);
			pidConf.setUseLatch(true);
			pidConf.setIndexLatch(conf.getIndexLatch());
			joint.ConfigurePIDController(pidConf);//Sets up the latch
			//Move negative to the index
			runHome(joint,(int) (range*-1));
			
			pidConf.setStopOnIndex(false);
			pidConf.setUseLatch(false);
			pidConf.setIndexLatch(conf.getIndexLatch());
			joint.ConfigurePIDController(pidConf);//Shuts down the latch
	
			try {
				setDesiredJointAxisValue(link, 0, 0);// go to zero instead of to the index itself
			} catch (Exception e) {
				e.printStackTrace();
			}
			getFactory().getPid().addPIDEventListener(this);
		}else{
			getFactory().getLink(getLinkConfiguration(link)).Home();
			getFactory().flush(1000);
		}
	}
	/**
	 * This is a quick stop for all axis of the robot.
	 */
	public void emergencyStop(){
		getFactory().getPid().killAllPidGroups();
	}

//	public void setAxisPidConfiguration(ArrayList<PIDConfiguration> conf) {
//		this.pidConfigurations = conf;
//	}

	/**
 * Gets the axis pid configuration.
 *
 * @return the axis pid configuration
 */
public ArrayList<PIDConfiguration> getAxisPidConfiguration() {
		return pidConfigurations;
	}
	
	/**
	 * Inverse kinematics.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @return Nx1 vector in task space, in mm where N is number of links
	 * @throws Exception the exception
	 */ 
	public abstract double[] inverseKinematics(TransformNR taskSpaceTransform) throws Exception;
	
	 /**
 	 * Forward kinematics.
 	 *
 	 * @param jointSpaceVector the joint space vector
 	 * @return 6x1 vector in task space, unit in mm,radians [x,y,z,rotx,rotY,rotZ]
 	 */
	public abstract TransformNR forwardKinematics(double[] jointSpaceVector);

	/**
	 * Gets the current pose target.
	 *
	 * @return the current pose target
	 */
	public TransformNR getCurrentPoseTarget() {
		if(currentPoseTarget == null)
			currentPoseTarget = new TransformNR();
		return currentPoseTarget;
	}

	/**
	 * Sets the current pose target.
	 *
	 * @param currentPoseTarget the new current pose target
	 */
	public void setCurrentPoseTarget(TransformNR currentPoseTarget) {
		this.currentPoseTarget = currentPoseTarget;
	}
	
	/**
	 * Sets the factory.
	 *
	 * @param factory the new factory
	 */
	public void setFactory(LinkFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public LinkFactory getFactory() {
		if(factory==null)
			factory=new LinkFactory();
		return factory;
	}
	
	/**
	 * Sets the no flush.
	 *
	 * @param noFlush the new no flush
	 */
	public void setNoFlush(boolean noFlush) {
		this.noFlush = noFlush;
	}
	
	/**
	 * Checks if is no flush.
	 *
	 * @return true, if is no flush
	 */
	public boolean isNoFlush() {
		return noFlush;
	}
	
	/**
	 * Gets the retry number before fail.
	 *
	 * @return the retry number before fail
	 */
	public int getRetryNumberBeforeFail() {
		return retryNumberBeforeFail;
	}
	
	/**
	 * Sets the retry number before fail.
	 *
	 * @param retryNumberBeforeFail the new retry number before fail
	 */
	public void setRetryNumberBeforeFail(int retryNumberBeforeFail) {
		this.retryNumberBeforeFail = retryNumberBeforeFail;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ILinkListener#onLinkLimit(com.neuronrobotics.sdk.addons.kinematics.AbstractLink, com.neuronrobotics.sdk.pid.PIDLimitEvent)
	 */
	@Override
	public void onLinkLimit(AbstractLink arg0, PIDLimitEvent arg1) {
		for(int i=0;i<getNumberOfLinks();i++){
			if(getLinkConfiguration(i).getHardwareIndex() == arg0.getLinkConfiguration().getHardwareIndex())
				fireJointSpaceLimitUpdate(i,new JointLimit(i, arg1, arg0.getLinkConfiguration()));
		}
	}

	/**
	 * Sets the robot to fiducial transform.
	 *
	 * @param newTrans the new robot to fiducial transform
	 */
	public void setRobotToFiducialTransform(TransformNR newTrans) {
		setBaseToZframeTransform(newTrans);
	}

	/**
	 * Gets the dh parameters chain.
	 *
	 * @return the dh parameters chain
	 */
	public DHChain getDhParametersChain() {
		return dhParametersChain;
	}

	/**
	 * Sets the dh parameters chain.
	 *
	 * @param dhParametersChain the new dh parameters chain
	 */
	public void setDhParametersChain(DHChain dhParametersChain) {
		this.dhParametersChain = dhParametersChain;
	}
	
	/**
	 * Gets the dh engine.
	 *
	 * @return the dh engine
	 */
	public String [] getGitDhEngine() {
		return dhEngine;
	}

	/**
	 * Sets the dh engine.
	 *
	 * @param dhEngine the new dh engine
	 */
	public void setGitDhEngine(String [] dhEngine) {
		if(dhEngine!=null && dhEngine[0]!=null &&dhEngine[1]!=null)
			this.dhEngine = dhEngine;
	}

	/**
	 * Gets the cad engine.
	 *
	 * @return the cad engine
	 */
	public String [] getGitCadEngine() {
		return cadEngine;
	}

	/**
	 * Sets the cad engine.
	 *
	 * @param cadEngine the new cad engine
	 */
	public void setGitCadEngine(String [] cadEngine) {
		if(cadEngine!=null&& cadEngine[0]!=null &&cadEngine[1]!=null)
		this.cadEngine = cadEngine;
	}
	
	/**
	 * Gets the code.
	 *
	 * @param e the e
	 * @param tag the tag
	 * @return the code
	 */
	protected String getCode(Element e,String tag){
		try{
			NodeList nodListofLinks = e.getChildNodes();
			
			for (int i = 0; i < nodListofLinks .getLength(); i++) {			
			    Node linkNode = nodListofLinks.item(i);
			   if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals(tag)) {
			    	return XmlFactory.getTagValue(tag,e);
			    }
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		throw new RuntimeException("No tag "+tag+" found");
	}
	
	/**
	 * Gets the gist codes.
	 *
	 * @param doc the doc
	 * @param tag the tag
	 * @return the gist codes
	 */
	protected String [] getGitCodes(Element doc,String tag){
		String [] content =new String[2];
		try{
			NodeList nodListofLinks = doc.getChildNodes();
			for (int i = 0; i < nodListofLinks.getLength(); i++) {			
			    Node linkNode = nodListofLinks.item(i);
			    if (linkNode.getNodeType() == Node.ELEMENT_NODE&& linkNode.getNodeName().contentEquals(tag)) {
			    	Element e = (Element) linkNode;
			    	try{
				    	if(getCode( e,"gist")!=null)
				    		content[0]="https://gist.github.com/"+getCode( e,"gist")+".git";
			    	}catch(Exception ex){
			    		
			    	}
			    	try{
				    	if(getCode( e,"git")!=null)
				    		content[0]=getCode( e,"git");
		    		}catch(Exception ex){
			    		
			    	}
			    	content[1]=getCode( e,"file");
			    }
			}
			return content;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
