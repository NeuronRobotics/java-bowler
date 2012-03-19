package com.neuronrobotics.sdk.addons.kinematics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;

//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.ILinkListener;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.Rotation;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
//import com.neuronrobotics.sdk.addons.kinematics.PidRotoryLink;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.IPIDControl;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
//import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public abstract class AbstractKinematics implements IPIDEventListener, ILinkListener {
	
	/** The configurations. */
	private ArrayList<LinkConfiguration> linkConfigurations= new ArrayList<LinkConfiguration>();
	private ArrayList<PIDConfiguration> pidConfigurations= new ArrayList<PIDConfiguration>();

	private ArrayList<ITaskSpaceUpdateListener> taskSpaceUpdateListeners = new ArrayList<ITaskSpaceUpdateListener>();
	private ArrayList<IJointSpaceUpdateListener> jointSpaceUpdateListeners = new ArrayList<IJointSpaceUpdateListener>();
	private ArrayList<IRegistrationListener> regListeners= new ArrayList<IRegistrationListener>();	
	
	/*This is in RAW joint level ticks*/
	private double[] currentJointSpacePositions=null;
	private double [] currentJointSpaceTarget;
	private Transform currentPoseTarget=new Transform();
	private Transform base2Fiducial=new Transform();
	private Transform fiducial2RAS=new Transform();
	/* The device */
	//private IPIDControl device =null;
	private LinkFactory factory=null;

	public AbstractKinematics(InputStream configFile,LinkFactory f){
		loadConfig(configFile);
		setDevice(f);
	}
	private String getDate(){
		Timestamp t = new Timestamp(System.currentTimeMillis());
		return t.toString().split("\\ ")[0];
	}
	/**
	 * Load XML configuration file, 
	 * then store in LinkConfiguration (ArrayList type)
	 */
	protected void loadConfig(InputStream config){
		File l = new File("RobotLog_"+getDate()+"_"+System.currentTimeMillis()+".txt");
		//File e = new File("RobotError_"+getDate()+"_"+System.currentTimeMillis()+".txt");
		try {
			PrintStream p =new PrintStream(l);
			Log.setOutStream(new PrintStream(p));
			Log.setErrStream(new PrintStream(p));						
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Log.enableSystemPrint(true);
		Log.enableDebugPrint(true);
		
		//InputStream config = XmlFactory.getDefaultConfigurationStream("DyioServo.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;
	    Document doc = null;
	    try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(config);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//Parsing XML File and store in LinkConfiguration
		NodeList nList = doc.getElementsByTagName("link");
		for (int temp = 0; temp < nList.getLength(); temp++) {			
		    Node nNode = nList.item(temp);
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	getLinkConfigurations().add(new LinkConfiguration((Element) nNode));
		    }else{
		    	Log.info("Not Element Node");
		    }
		}
		
		try{
			Node zframeToRASConfig = doc.getElementsByTagName("ZframeToRAS").item(0);
		    if (zframeToRASConfig.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element)zframeToRASConfig;	    		    
		    	setZframeToGlobalTransform(new Transform(	Double.parseDouble(XmlFactory.getTagValue("x",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("y",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("z",eElement)), 
							    			new Rotation(	Double.parseDouble(XmlFactory.getTagValue("rotw",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotx",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("roty",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotz",eElement)))));	    	
		    }else{
		    	throw new RuntimeException("No Z frame to RAS transform defined");
		    }
		}catch (Exception ex){
			Log.warning("No Z frame to RAS transform defined");
		}
		
		try{
		    Node baseToZframeConfig = doc.getElementsByTagName("baseToZframe").item(0);
		    if (baseToZframeConfig.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element)baseToZframeConfig;	    	    
		    	setBaseToZframeTransform(new Transform(	Double.parseDouble(XmlFactory.getTagValue("x",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("y",eElement)),
							    			Double.parseDouble(XmlFactory.getTagValue("z",eElement)), 
							    			new Rotation(	Double.parseDouble(XmlFactory.getTagValue("rotw",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotx",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("roty",eElement)),
							    							Double.parseDouble(XmlFactory.getTagValue("rotz",eElement)))));	    	
		    }else{
		    	throw new RuntimeException("No base to Z frame transform defined");
		    }
		}catch (Exception ex){
			Log.warning("No base to Z frame transform defined");
		}
		
	}

	public LinkConfiguration getLinkConfiguration(int linkIndex){
		return getLinkConfigurations().get(linkIndex);
	}
	
	public ArrayList<LinkConfiguration> getLinkConfigurations() {
		return linkConfigurations;
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
	
	protected void setDevice(LinkFactory f){
		Log.info("Loading device: "+f.getClass()+" "+f);
		setFactory(f);
		//Log.enableDebugPrint(true);
		for(LinkConfiguration c:getLinkConfigurations()){
			getFactory().getLink(c);
			Log.info("Axis #"+c.getHardwareIndex()+" Configuration:\n"+c);
			if(c.getType().contains("pid")){
				IPIDControl device = getFactory().getPid();
				try{
					PIDConfiguration tmpConf = device.getPIDConfiguration(c.getHardwareIndex());
					tmpConf.setGroup(c.getHardwareIndex());
					tmpConf.setKP(c.getKP());
					tmpConf.setKI(c.getKI());
					tmpConf.setKD(c.getKD());
					tmpConf.setEnabled(true);
					tmpConf.setInverted(c.isInverted());
					tmpConf.setAsync(true);
				
					tmpConf.setUseLatch(false);
					tmpConf.setIndexLatch(c.getIndexLatch());
					tmpConf.setStopOnIndex(false);
					
					Log.info("Axis #"+c.getHardwareIndex()+tmpConf);
					getAxisPidConfiguration().add(tmpConf);
					setLinkCurrentConfiguration(c.getHardwareIndex(),tmpConf);
					//Send configuration for ONE axis
					device.ConfigurePIDController(tmpConf);		
				}catch(Exception ex){				
					System.err.println("Configuration #"+c.getHardwareIndex()+" failed!!");
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
	public Transform getCurrentTaskSpaceTransform() {
		Transform fwd  = forwardKinematics(getCurrentJointSpaceVector());
		Log.info("Getting robot task space "+fwd);
		Transform taskSpaceTransform=forwardOffset(fwd);
		Log.info("Getting global task space "+taskSpaceTransform);
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
		
		String jointString = "[";
		for(int i=0;i<jointSpaceVect.length;i++){
			jointString+=jointSpaceVect[i]+" ";
		}
		jointString+="]";
		Log.info("Getting pos joint space in mm/deg: "+jointString);
		
//		jointString = "[";
//		for(int i=0;i<currentLinkSpacePositions.length;i++){
//			jointString+=currentLinkSpacePositions[i]+" ";
//		}
//		jointString+="]";
//		Log.info("Getting pos link space in ticks: "+jointString);
		
		return jointSpaceVect;
	}
	
	/**
	 * This calculates the target pose 
	 * @param taskSpaceTransform
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public double[] setDesiredTaskSpaceTransform(Transform taskSpaceTransform, double seconds) throws Exception{
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
			throw new IndexOutOfBoundsException("Vector must be "+getNumberOfLinks()+" mm,radians "); 
		}
		String joints = "[";
		for(int i=0;i<jointSpaceVect.length;i++){
			joints+=jointSpaceVect[i]+" ";
		}
		joints+="]";
		Log.info("Setting target joints: "+joints);

//		for(LinkConfiguration c:getLinkConfigurations()){
//			int i= getLinkConfigurations().indexOf(c);
//			//Converting engineering units to RAW 
//			int scaled = (int) (jointSpaceVect[i]/c.getScale());
//			if(scaled>c.getUpperLimit()){
//				Log.error("Joint hit software bound, index "+i+" attempted: "+scaled+" bounded to :"+c.getUpperLimit());
////				JOptionPane.showMessageDialog(new JFrame(),"Out of upper range", "Range warning",
////						JOptionPane.WARNING_MESSAGE);
//				throw new Exception("Joint hit Upper software bound, index "+i+" attempted: "+scaled+" bounded to :"+c.getUpperLimit());
//				//scaled = (int) c.getUpperLimit();
//			}
//			if(scaled<c.getLowerLimit()){
//				Log.error("Joint hit software bound, index "+i+" attempted: "+scaled+" bounded to :"+c.getLowerLimit());
////				JOptionPane.showMessageDialog(new JFrame(),"Out of lower range", "Range warning",
////						JOptionPane.WARNING_MESSAGE);
//				//scaled = (int) c.getLowerLimit();
//				throw new Exception("Joint hit Lower software bound, index "+i+" attempted: "+scaled+" bounded to :"+c.getUpperLimit());
//			}
//			targets[c.getHardwareIndex()]=scaled;
//		}
		
		//TODO HACK, fix in the ethernet firmware
//		factory.setCachedTargets(jointSpaceVect);
//		factory.flush(seconds);
		
		for(int i=0;i<getNumberOfLinks();i++){
			setDesiredJointAxisValue(i, jointSpaceVect[i],  seconds);
		}
		
		
		currentJointSpaceTarget = jointSpaceVect;
		fireTargetJointsUpdate();
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
		getFactory().getLink(c).flush(seconds);
		fireTargetJointsUpdate();
		return;
	}
	
	private void firePoseUpdate(){
		Log.info("Pose update");
		for(ITaskSpaceUpdateListener p:taskSpaceUpdateListeners){
			p.onTaskSpaceUpdate(this, getCurrentTaskSpaceTransform());
		}
		for(IJointSpaceUpdateListener p:jointSpaceUpdateListeners){
			p.onJointSpaceUpdate(this, getCurrentJointSpaceVector());
		}
	}

	private void fireTargetJointsUpdate(){
		Transform fwd  = forwardKinematics(currentJointSpaceTarget);
		setCurrentPoseTarget(forwardOffset(fwd));
		for(ITaskSpaceUpdateListener p:taskSpaceUpdateListeners){
			p.onTargetTaskSpaceUpdate(this, getCurrentPoseTarget());
		}
		for(IJointSpaceUpdateListener p:jointSpaceUpdateListeners){
			p.onJointSpaceTargetUpdate(this, currentJointSpaceTarget);
		}
	}
	private void fireJointSpaceLimitUpdate(int axis,JointLimit event){
		for(IJointSpaceUpdateListener p:jointSpaceUpdateListeners){
			p.onJointSpaceLimit(this, axis, event);
		}
	}
	/**
	 * 
	 * @return
	 */
	public Transform getFiducialToGlobalTransform() {
		return fiducial2RAS;
	}

	private void setBaseToZframeTransform(Transform baseToFiducial) {
		Log.info("Setting Fiducial To base Transform "+baseToFiducial);
		this.base2Fiducial = baseToFiducial;
		for(IRegistrationListener r: regListeners){
			r.onBaseToFiducialUpdate(this, baseToFiducial);
		}
	}
	
	private void setZframeToGlobalTransform(Transform fiducialToRAS) {
		setGlobalToFiducialTransform(fiducialToRAS);
	}

		
	public Transform getRobotToFiducialTransform() {
		return base2Fiducial;
	}
	
	public void setGlobalToFiducialTransform(Transform frameToBase) {
		Log.info("Setting Global To Fiducial Transform "+frameToBase);
		this.fiducial2RAS = frameToBase;
		for(IRegistrationListener r: regListeners){
			r.onFiducialToGlobalUpdate(this, frameToBase);
		}
	}
	
	private Transform inverseOffset(Transform t){
		//System.out.println("RobotToFiducialTransform "+getRobotToFiducialTransform());
		//System.out.println("FiducialToRASTransform "+getFiducialToRASTransform());		
		Matrix rtz = getFiducialToGlobalTransform().getMatrixTransform().inverse();
		Matrix ztr = getRobotToFiducialTransform().getMatrixTransform().inverse();

		Matrix current = t.getMatrixTransform();
		Matrix mForward = rtz.times(ztr).times(current);
		
		return new Transform( mForward);
	}
	private Transform forwardOffset(Transform t){
		Matrix btt = getRobotToFiducialTransform().getMatrixTransform();
		Matrix ftb = getFiducialToGlobalTransform().getMatrixTransform();
		Matrix current = t.getMatrixTransform();
		Matrix mForward = ftb.times(btt).times(current);
		
		return new Transform( mForward);
	}
	
	public void addJointSpaceListener(IJointSpaceUpdateListener l){
		if(jointSpaceUpdateListeners.contains(l))
			return;
		jointSpaceUpdateListeners.add(l);
	}
	public void removeJointSpaceUpdateListener(IJointSpaceUpdateListener l){
		if(jointSpaceUpdateListeners.contains(l))
			jointSpaceUpdateListeners.remove(l);
	}
	
	public void addRegistrationListener(IRegistrationListener l){
		if(regListeners.contains(l))
			return;
		regListeners.add(l);
		l.onBaseToFiducialUpdate(this, getRobotToFiducialTransform());
	}
	public void removeRegestrationUpdateListener(IRegistrationListener l){
		if(regListeners.contains(l))
			regListeners.remove(l);
	}
	
	public void addPoseUpdateListener(ITaskSpaceUpdateListener l){
		if(taskSpaceUpdateListeners.contains(l))
			return;
		taskSpaceUpdateListeners.add(l);
	}
	public void removePoseUpdateListener(ITaskSpaceUpdateListener l){
		if(taskSpaceUpdateListeners.contains(l))
			taskSpaceUpdateListeners.remove(l);
	}
	
	@Override
	public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue){
		for(LinkConfiguration c:getLinkConfigurations()){
			AbstractLink tmp = getFactory().getLink(c);
			if(tmp == source){//Check to see if this lines up with a known link
				Log.info("Got PID event "+source+" value="+engineeringUnitsValue);
				
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
	private synchronized void runHome(PIDChannel joint, int tps){
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
		if(conf.getType().contains("pid")){
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
	public abstract double[] inverseKinematics(Transform taskSpaceTransform) throws Exception;
	
	 /**
		* Forward kinematics
		* @param jointSpaceVector the joint space vector
		* @return 6x1 vector in task space, unit in mm,radians [x,y,z,rotx,rotY,rotZ]
		*/
	public abstract Transform forwardKinematics(double[] jointSpaceVector);

	public Transform getCurrentPoseTarget() {
		if(currentPoseTarget == null)
			currentPoseTarget = new Transform();
		return currentPoseTarget;
	}

	public void setCurrentPoseTarget(Transform currentPoseTarget) {
		this.currentPoseTarget = currentPoseTarget;
	}
	public void setFactory(LinkFactory factory) {
		this.factory = factory;
	}
	public LinkFactory getFactory() {
		return factory;
	}

}
