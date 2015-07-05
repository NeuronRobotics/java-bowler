package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

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
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;

public abstract class AbstractKinematicsNR extends NonBowlerDevice implements IPIDEventListener, ILinkListener {
	
	/** The configurations. */
	private ArrayList<PIDConfiguration> pidConfigurations= new ArrayList<PIDConfiguration>();

	private ArrayList<ITaskSpaceUpdateListenerNR> taskSpaceUpdateListeners = new ArrayList<ITaskSpaceUpdateListenerNR>();
	protected ArrayList<IJointSpaceUpdateListenerNR> jointSpaceUpdateListeners = new ArrayList<IJointSpaceUpdateListenerNR>();
	private ArrayList<IRegistrationListenerNR> regListeners= new ArrayList<IRegistrationListenerNR>();	

	/*This is in RAW joint level ticks*/
	protected double[] currentJointSpacePositions=null;
	protected double [] currentJointSpaceTarget;
	private TransformNR currentPoseTarget=new TransformNR();
	private TransformNR base2Fiducial=new TransformNR();
	private TransformNR fiducial2RAS=new TransformNR();
	
	private boolean noFlush = false;
	private boolean noXmlConfig=true;
	
	/**
	 * This method tells the connection object to disconnect its pipes and close out the connection. Once this is called, it is safe to remove your device.
	 */
	
	public abstract void disconnectDevice();
	
	public abstract  boolean connectDevice();
	
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		ArrayList<String> back = new ArrayList<String>();
		back.add("bcs.cartesian.*");
		return back;
	}
	
	public void disconnectDeviceImp(){
		getFactory().removeLinkListener(this);
		IPidControlNamespace device = getFactory().getPid();
		if(device!=null)
			device.removePIDEventListener(this);
		disconnectDevice();
	}
	
	public  boolean connectDeviceImp(){
		return connectDevice();
	}
		
	/* The device */
	//private IPIDControl device =null;
	private LinkFactory factory=null;
	
	private int retryNumberBeforeFail = 5;
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
	}
	public AbstractKinematicsNR(InputStream configFile,LinkFactory f){
		this();
		noXmlConfig=false;
		if(configFile!=null && f!=null){
			setDevice(f,loadConfig(configFile));
		}
		
	}
	private String getDate(){
		Timestamp t = new Timestamp(System.currentTimeMillis());
		return t.toString().split("\\ ")[0];
	}
	/**
	 * Load XML configuration file, 
	 * then store in LinkConfiguration (ArrayList type)
	 */
	protected ArrayList<LinkConfiguration> loadConfig(InputStream config){
		ArrayList<LinkConfiguration> localConfigsFromXml=new ArrayList<LinkConfiguration>();

		Document doc =XmlFactory.getAllNodesDocument(config);
		NodeList nList = doc.getElementsByTagName("link");
		NodeList zf = 	 doc.getElementsByTagName("ZframeToRAS");
		NodeList bf = 	 doc.getElementsByTagName("baseToZframe");
		
		for (int i = 0; i < nList.getLength(); i++) {			
		    Node nNode = nList.item(i);
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	localConfigsFromXml.add(new LinkConfiguration((Element) nNode));
		    }else{
		    	Log.info("Not Element Node");
		    }
		}
		
		try{
			Node zframeToRASConfig = zf.item(0);
			if(zframeToRASConfig!=null)
		    if (zframeToRASConfig.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element)zframeToRASConfig;	    		    
		    	setZframeToGlobalTransform(new TransformNR(	Double.parseDouble(XmlFactory.getTagValue("x",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("y",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("z",eElement)), 
							    			new RotationNR(	Double.parseDouble(XmlFactory.getTagValue("rotw",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotx",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("roty",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotz",eElement)))));	    	
		    }else{
		    	throw new RuntimeException("No Z frame to RAS transform defined");
		    }
		}catch (Exception ex){
			ex.printStackTrace();
			Log.warning("No Z frame to RAS transform defined");
		}
		
		try{
			
		    Node baseToZframeConfig = bf.item(0);
		    if(baseToZframeConfig!=null)
		    if (baseToZframeConfig.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element)baseToZframeConfig;	    	    
		    	setBaseToZframeTransform(new TransformNR(	Double.parseDouble(XmlFactory.getTagValue("x",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("y",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("z",eElement)), 
							    			new RotationNR(	Double.parseDouble(XmlFactory.getTagValue("rotw",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotx",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("roty",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotz",eElement)))));	    	
		    }else{
		    	throw new RuntimeException("No base to Z frame transform defined");
		    }
		}catch (Exception ex){
			ex.printStackTrace();
			Log.warning("No base to Z frame transform defined");
		}
		return localConfigsFromXml;
	}
	
	/*
	 * 
	 * Generate the xml configuration to generate an XML of this robot. 
	 */
	public String getXml(){
		String xml = "<root>\n";
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
		xml+="\n</root>";
		return xml;
	}


	public LinkConfiguration getLinkConfiguration(int linkIndex){
		return getLinkConfigurations().get(linkIndex);
	}
	
	public ArrayList<LinkConfiguration> getLinkConfigurations() {

		return getFactory().getLinkConfigurations();
	
	}

	
	public PIDConfiguration getLinkCurrentConfiguration(int chan){
		return getAxisPidConfiguration().get(chan);
	}
	public void setLinkCurrentConfiguration(int chan,PIDConfiguration c){
		getAxisPidConfiguration().set(chan, c);
	}
	
	protected LinkFactory getDevice(){
		return getFactory();
	}
	public AbstractLink getAbstractLink(int index){
		return getFactory().getLink(getLinkConfiguration(index));
	}
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
					System.err.println("Configuration #"+i+" failed!!");
					ex.printStackTrace();
				}
				device.addPIDEventListener(this);
			}
		}
		getCurrentTaskSpaceTransform();
		getFactory().addLinkListener(this);
	}
	/**
	 * Gets the number of links defined in the configuration file
	 * @return number of links in XML
	 */
	public int getNumberOfLinks(){
		return getLinkConfigurations().size();
	}
	
	/**
	 * This takes a reading of the robots position and converts it to a joint space vector
	 * This vector is converted to task space and returned 
	 * @return taskSpaceVector in mm,radians [x,y,z,rotx,rotY,rotZ]
	 */
	public TransformNR getCurrentTaskSpaceTransform() {
		TransformNR fwd  = forwardKinematics(getCurrentJointSpaceVector());
		//Log.info("Getting robot task space "+fwd);
		TransformNR taskSpaceTransform=forwardOffset(fwd);
		//Log.info("Getting global task space "+taskSpaceTransform);
		return taskSpaceTransform;
	}
	
	/**
	 * This takes a reading of the robots position and converts it to a joint pace vector
	 * This vector is converted to Joint space and returned 
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
	 * This calculates the target pose 
	 * @param taskSpaceTransform
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public double[] setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) throws Exception{
		Log.info("Setting target pose: "+taskSpaceTransform);
		setCurrentPoseTarget(taskSpaceTransform);
		taskSpaceTransform = inverseOffset(taskSpaceTransform);
		double [] jointSpaceVect = inverseKinematics(taskSpaceTransform);
		setDesiredJointSpaceVector(jointSpaceVect,  seconds);
		return jointSpaceVect;
	}
	
	/**
	 * This calculates the target pose 
	 * @param JointSpaceVector the target joint space vector
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public double[] setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds) throws Exception{
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
	 * Sets an individual target joint position 
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
	
	protected void firePoseTransform(TransformNR transform){
		for(int i=0;i<taskSpaceUpdateListeners.size();i++){
			ITaskSpaceUpdateListenerNR p=taskSpaceUpdateListeners.get(i);
			p.onTaskSpaceUpdate(this, transform);
		}
	}
	
	protected void firePoseUpdate(){
		//Log.info("Pose update");
		firePoseTransform(getCurrentTaskSpaceTransform());

		double[] vect = getCurrentJointSpaceVector();
		
		for(int i=0;i<jointSpaceUpdateListeners.size();i++){
			IJointSpaceUpdateListenerNR p=jointSpaceUpdateListeners.get(i);
			p.onJointSpaceUpdate(this, vect);
		}
	}

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
	private void fireJointSpaceLimitUpdate(int axis,JointLimit event){
		for(IJointSpaceUpdateListenerNR p:jointSpaceUpdateListeners){
			p.onJointSpaceLimit(this, axis, event);
		}
	}
	/**
	 * 
	 * @return
	 */
	public TransformNR getFiducialToGlobalTransform() {
		return fiducial2RAS;
	}

	private void setBaseToZframeTransform(TransformNR baseToFiducial) {
		Log.info("Setting Fiducial To base Transform "+baseToFiducial);
		this.base2Fiducial = baseToFiducial;
		for(IRegistrationListenerNR r: regListeners){
			r.onBaseToFiducialUpdate(this, baseToFiducial);
		}
	}
	
	private void setZframeToGlobalTransform(TransformNR fiducialToRAS) {
		setGlobalToFiducialTransform(fiducialToRAS);
	}

		
	public TransformNR getRobotToFiducialTransform() {
		return base2Fiducial;
	}
	
	public void setGlobalToFiducialTransform(TransformNR frameToBase) {
		Log.info("Setting Global To Fiducial Transform "+frameToBase);
		this.fiducial2RAS = frameToBase;
		for(IRegistrationListenerNR r: regListeners){
			r.onFiducialToGlobalUpdate(this, frameToBase);
		}
	}
	
	protected TransformNR inverseOffset(TransformNR t){
		//System.out.println("RobotToFiducialTransform "+getRobotToFiducialTransform());
		//System.out.println("FiducialToRASTransform "+getFiducialToRASTransform());		
		Matrix rtz = getFiducialToGlobalTransform().getMatrixTransform().inverse();
		Matrix ztr = getRobotToFiducialTransform().getMatrixTransform().inverse();

		Matrix current = t.getMatrixTransform();
		Matrix mForward = rtz.times(ztr).times(current);
		
		return new TransformNR( mForward);
	}
	protected TransformNR forwardOffset(TransformNR t){
		Matrix btt = getRobotToFiducialTransform().getMatrixTransform();
		Matrix ftb = getFiducialToGlobalTransform().getMatrixTransform();
		Matrix current = t.getMatrixTransform();
		Matrix mForward = ftb.times(btt).times(current);
		
		return new TransformNR( mForward);
	}
	
	public void addJointSpaceListener(IJointSpaceUpdateListenerNR l){
		if(jointSpaceUpdateListeners.contains(l) || l==null)
			return;
		jointSpaceUpdateListeners.add(l);
	}
	public void removeJointSpaceUpdateListener(IJointSpaceUpdateListenerNR l){
		if(jointSpaceUpdateListeners.contains(l))
			jointSpaceUpdateListeners.remove(l);
	}
	
	public void addRegistrationListener(IRegistrationListenerNR l){
		if(regListeners.contains(l)|| l==null)
			return;
		regListeners.add(l);
		l.onBaseToFiducialUpdate(this, getRobotToFiducialTransform());
	}
	public void removeRegestrationUpdateListener(IRegistrationListenerNR l){
		if(regListeners.contains(l))
			regListeners.remove(l);
	}
	
	public void addPoseUpdateListener(ITaskSpaceUpdateListenerNR l){
		if(taskSpaceUpdateListeners.contains(l) || l==null){
			new RuntimeException("not adding "+l.getClass().getName()).printStackTrace();
			return;
		}
		//new RuntimeException("adding "+l.getClass().getName()).printStackTrace();
		taskSpaceUpdateListeners.add(l);
	}
	public void removePoseUpdateListener(ITaskSpaceUpdateListenerNR l){
		if(taskSpaceUpdateListeners.contains(l)){
			//new RuntimeException("Removing "+l.getClass().getName()).printStackTrace();
			taskSpaceUpdateListeners.remove(l);
		}
	}
	
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
	
	@Override
	public void onPIDEvent(PIDEvent e) {
		// Ignore and use Link space events
	}

	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		for(int i=0;i<getNumberOfLinks();i++){
			if(getLinkConfiguration(i).getHardwareIndex() == e.getGroup())
				fireJointSpaceLimitUpdate(i,new JointLimit(i,e,getLinkConfiguration(i)));
		}
	}

	@Override
	public void onPIDReset(int group, int currentValue) {
		// ignore at this level
	}

	/**
	 * This method uses the latch values to home all of the robot links
	 */
	public void homeAllLinks() {
		
		for(int i=0;i<getNumberOfLinks();i++){
			
			homeLink(i);
			//ThreadUtil.wait(2000);
		}
		
	} 
	/**
	 * This method uses the latch values to home the given link of the robot links
	 * @param link The link to be homed
	 */
	private long homeTime;
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

	public ArrayList<PIDConfiguration> getAxisPidConfiguration() {
		return pidConfigurations;
	}
	
	/**
	 * Inverse kinematics.
	 *
	 * @param taskSpaceVector the task space vector
	 * @return Nx1 vector in task space, in mm where N is number of links
	 * @throws Exception 
	 */ 
	public abstract double[] inverseKinematics(TransformNR taskSpaceTransform) throws Exception;
	
	 /**
		* Forward kinematics
		* @param jointSpaceVector the joint space vector
		* @return 6x1 vector in task space, unit in mm,radians [x,y,z,rotx,rotY,rotZ]
		*/
	public abstract TransformNR forwardKinematics(double[] jointSpaceVector);

	public TransformNR getCurrentPoseTarget() {
		if(currentPoseTarget == null)
			currentPoseTarget = new TransformNR();
		return currentPoseTarget;
	}

	public void setCurrentPoseTarget(TransformNR currentPoseTarget) {
		this.currentPoseTarget = currentPoseTarget;
	}
	public void setFactory(LinkFactory factory) {
		this.factory = factory;
	}
	public LinkFactory getFactory() {
		return factory;
	}
	public void setNoFlush(boolean noFlush) {
		this.noFlush = noFlush;
	}
	public boolean isNoFlush() {
		return noFlush;
	}
	public int getRetryNumberBeforeFail() {
		return retryNumberBeforeFail;
	}
	public void setRetryNumberBeforeFail(int retryNumberBeforeFail) {
		this.retryNumberBeforeFail = retryNumberBeforeFail;
	}
	
	@Override
	public void onLinkLimit(AbstractLink arg0, PIDLimitEvent arg1) {
		for(int i=0;i<getNumberOfLinks();i++){
			if(getLinkConfiguration(i).getHardwareIndex() == arg0.getLinkConfiguration().getHardwareIndex())
				fireJointSpaceLimitUpdate(i,new JointLimit(i, arg1, arg0.getLinkConfiguration()));
		}
	}

}
