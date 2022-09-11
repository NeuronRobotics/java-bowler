package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
//import java.util.concurrent.CountDownLatch;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.imu.IMU;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IDeviceConnectionEventListener;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
//import com.neuronrobotics.sdk.addons.kinematics.PidRotoryLink;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import com.neuronrobotics.sdk.common.TickToc;
import com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.InterpolationEngine;
import com.neuronrobotics.sdk.pid.InterpolationType;
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
@SuppressWarnings("restriction")
public abstract class AbstractKinematicsNR extends NonBowlerDevice implements IPIDEventListener, ILinkListener {
	
	

	/** The configurations. */
	private ArrayList<PIDConfiguration> pidConfigurations = new ArrayList<PIDConfiguration>();

	/** The task space update listeners. */
	private ArrayList<ITaskSpaceUpdateListenerNR> taskSpaceUpdateListeners = new ArrayList<ITaskSpaceUpdateListenerNR>();

	/** The joint space update listeners. */
	protected ArrayList<IJointSpaceUpdateListenerNR> jointSpaceUpdateListeners = new ArrayList<IJointSpaceUpdateListenerNR>();

	/** The reg listeners. */
	private ArrayList<IRegistrationListenerNR> regListeners = new ArrayList<IRegistrationListenerNR>();

	/** The mobile bases. */
	private ArrayList<MobileBase> mobileBases = new ArrayList<MobileBase>();

	/** The dh engine. */
	private String[] dhEngine = new String[] { "https://github.com/madhephaestus/carl-the-hexapod.git",
			"DefaultDhSolver.groovy" };

	/** The cad engine. */
	private String[] cadEngine = new String[] { "https://github.com/madhephaestus/carl-the-hexapod.git",
			"ThreeDPrintCad.groovy" };

	/** The current joint space positions. */
	/* This is in RAW joint level ticks */
	//protected double[] currentJointSpacePositions = null;

	/** The current joint space target. */
//	public double[] currentJointSpaceTarget;

	/** The current pose target. */
	private TransformNR currentPoseTarget = new TransformNR();

	/** The base2 fiducial. */
	private TransformNR base2Fiducial = new TransformNR();

	/** The fiducial2 ras. */
	private TransformNR fiducial2RAS = new TransformNR();

	/** The no flush. */
	private boolean noFlush = false;

	/** The no xml config. */
	private boolean noXmlConfig = true;

	/** The dh parameters chain. */
	private DHChain dhParametersChain = null;

	/** The root. */
	private Object root;

	/* The device */
	/** The factory. */
	// private IPIDControl device =null;
	private LinkFactory factory = null;

	/** The retry number before fail. */
	private int retryNumberBeforeFail = 5;
	/**
	 * The object for communicating IMU information and registering it with the
	 * hardware
	 */
	private IMU imu = new IMU();
	
	private Runnable renderWrangler=null;

	/**
	 * Gets the root listener.
	 *
	 * @return the root listener
	 */
	public Object getRootListener() {
		return root;
	}

	/**
	 * Sets the root listener.
	 *
	 * @param listener the new root listener
	 */
	public void setRootListener(Object listener) {
		this.root = listener;
	}

	/**
	 * This method tells the connection object to disconnect its pipes and close out
	 * the connection. Once this is called, it is safe to remove your device.
	 */

	public abstract void disconnectDevice();

	/**
	 * Connect device.
	 *
	 * @return true, if successful
	 */
	public abstract boolean connectDevice();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#getNamespacesImp()
	 */
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		ArrayList<String> back = new ArrayList<String>();
		back.add("bcs.cartesian.*");
		return back;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#disconnectDeviceImp()
	 */
	public void disconnectDeviceImp() {
		getFactory().removeLinkListener(this);
		for (LinkConfiguration lf : getFactory().getLinkConfigurations())
			if (getFactory().getPid(lf) != null)
				getFactory().getPid(lf).removePIDEventListener(this);

		disconnectDevice();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#connectDeviceImp()
	 */
	public boolean connectDeviceImp() {
		return connectDevice();
	}

	/**
	 * Instantiates a new abstract kinematics nr.
	 */
	public AbstractKinematicsNR() {
//		File l = new File("RobotLog_"+getDate()+"_"+System.currentTimeMillis()+".txt");
//		//File e = new File("RobotError_"+getDate()+"_"+System.currentTimeMillis()+".txt");
//		try {
//			PrintStream p =new PrintStream(l);
//			Log.setOutStream(new PrintStream(p));
//			Log.setErrStream(new PrintStream(p));						
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
		setDhParametersChain(new DHChain(this));
	}

	/**
	 * Instantiates a new abstract kinematics nr.
	 *
	 * @param configFile the config file
	 * @param f          the f
	 */
	public AbstractKinematicsNR(InputStream configFile, LinkFactory f) {
		this();
		if(configFile==null||f==null)
			return;
		Document doc = XmlFactory.getAllNodesDocument(configFile);
		NodeList nodListofLinks = doc.getElementsByTagName("appendage");
		for (int i = 0; i < 1; i++) {
			Node linkNode = nodListofLinks.item(i);
			if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
				noXmlConfig = false;
				if (configFile != null && f != null) {
					setDevice(f, loadConfig((Element) linkNode));
				}
			} else {
				Log.info("Not Element Node");
			}
		}
		

	}

	/**
	 * Instantiates a new abstract kinematics nr.
	 *
	 * @param doc the doc
	 * @param f   the f
	 */
	public AbstractKinematicsNR(Element doc, LinkFactory f) {
		this();
		noXmlConfig = false;
		if (doc != null && f != null) {
			setDevice(f, loadConfig(doc));
		}

	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	private String getDate() {
		Timestamp t = new Timestamp(System.currentTimeMillis());
		return t.toString().split("\\ ")[0];
	}

	/**
	 * Load XML configuration file, then store in LinkConfiguration (ArrayList
	 * type).
	 *
	 * @param doc the doc
	 * @return the array list
	 */
	protected ArrayList<LinkConfiguration> loadConfig(Element doc) {
		ArrayList<LinkConfiguration> localConfigsFromXml = new ArrayList<LinkConfiguration>();

		NodeList nodListofLinks = doc.getChildNodes();
		setGitCadEngine(getGitCodes(doc, "cadEngine"));
		setGitDhEngine(getGitCodes(doc, "kinematics"));
		for (int i = 0; i < nodListofLinks.getLength(); i++) {
			Node linkNode = nodListofLinks.item(i);

			if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("link")) {
				LinkConfiguration newLinkConf = new LinkConfiguration((Element) linkNode);
				localConfigsFromXml.add(newLinkConf);

				NodeList dHParameters = linkNode.getChildNodes();
				// System.out.println("Link "+newLinkConf.getName()+" has "+dHParameters
				// .getLength()+" children");
				for (int x = 0; x < dHParameters.getLength(); x++) {
					Node nNode = dHParameters.item(x);
					if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.getNodeName().contentEquals("DHParameters")) {
						Element dhNode = (Element) nNode;
						DHLink newLink = new DHLink(dhNode);
						getDhParametersChain().addLink(newLink);// 0->1
						NodeList mobileBasesNodeList = dhNode.getChildNodes();
						for (int j = 0; j < mobileBasesNodeList.getLength(); j++) {
							Node mb = mobileBasesNodeList.item(j);
							if (mb.getNodeType() == Node.ELEMENT_NODE && mb.getNodeName().contentEquals("mobilebase")) {
								final MobileBase newMobileBase = new MobileBase((Element) mb);
								mobileBases.add(newMobileBase);
								newLink.setMobileBaseXml(newMobileBase);
								addConnectionEventListener(new IDeviceConnectionEventListener() {
									
									@Override
									public void onDisconnect(BowlerAbstractDevice source) {
										mobileBases.remove(newMobileBase);
									}
									
									@Override
									public void onConnect(BowlerAbstractDevice source) {
									}
								});
							}
						}
					} else {
						if (nNode.getNodeType() == Node.ELEMENT_NODE
								&& nNode.getNodeName().contentEquals("slaveLink")) {
							// System.out.println("Slave link found: ");
							LinkConfiguration jc = new LinkConfiguration((Element) nNode);
							// System.out.println(jc);
							newLinkConf.getSlaveLinks().add(jc);
						}
					}
				}
			} else if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("name")) {
				try {
					setScriptingName(XmlFactory.getTagValue("name", doc));
				} catch (Exception E) {
					E.printStackTrace();
				}
			} else if (linkNode.getNodeType() == Node.ELEMENT_NODE
					&& linkNode.getNodeName().contentEquals("ZframeToRAS")) {
				Element eElement = (Element) linkNode;
				try {
					setZframeToGlobalTransform(new TransformNR(
							Double.parseDouble(XmlFactory.getTagValue("x", eElement)),
							Double.parseDouble(XmlFactory.getTagValue("y", eElement)),
							Double.parseDouble(XmlFactory.getTagValue("z", eElement)),
							new RotationNR(new double[] { Double.parseDouble(XmlFactory.getTagValue("rotw", eElement)),
									Double.parseDouble(XmlFactory.getTagValue("rotx", eElement)),
									Double.parseDouble(XmlFactory.getTagValue("roty", eElement)),
									Double.parseDouble(XmlFactory.getTagValue("rotz", eElement)) })));
				} catch (Exception ex) {
					ex.printStackTrace();
					setZframeToGlobalTransform(new TransformNR());
				}
			} else if (linkNode.getNodeType() == Node.ELEMENT_NODE
					&& linkNode.getNodeName().contentEquals("baseToZframe")) {
				Element eElement = (Element) linkNode;
				try {
					setBaseToZframeTransform(new TransformNR(Double.parseDouble(XmlFactory.getTagValue("x", eElement)),
							Double.parseDouble(XmlFactory.getTagValue("y", eElement)),
							Double.parseDouble(XmlFactory.getTagValue("z", eElement)),
							new RotationNR(new double[] { Double.parseDouble(XmlFactory.getTagValue("rotw", eElement)),
									Double.parseDouble(XmlFactory.getTagValue("rotx", eElement)),
									Double.parseDouble(XmlFactory.getTagValue("roty", eElement)),
									Double.parseDouble(XmlFactory.getTagValue("rotz", eElement)) })));
				} catch (Exception ex) {
					ex.printStackTrace();
					setBaseToZframeTransform(new TransformNR());
				}
			} else {
				// System.err.println(linkNode.getNodeName());
				// Log.error("Node not known: "+linkNode.getNodeName());
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
	public String getXml() {
		String xml = "<root>\n";
		xml += "\n<appendage>";
		xml += "\n<name>" + getScriptingName() + "</name>\n";
		for (int i = 0; i < getLinkConfigurations().size(); i++) {
			xml += "<link>\n";
			xml += getLinkConfiguration(i).getXml();
			xml += "\n</link>\n";
		}
		xml += "\n<ZframeToRAS>\n";
		xml += getFiducialToGlobalTransform().getXml();
		xml += "\n</ZframeToRAS>\n";

		xml += "\n<baseToZframe>\n";
		xml += getRobotToFiducialTransform().getXml();
		xml += "\n</baseToZframe>\n";
		xml += "\n</appendage>";
		xml += "\n</root>";
		return xml;
	}

	/**
	 * Gets the link configuration.
	 *
	 * @param linkIndex the link index
	 * @return the link configuration
	 */
	public LinkConfiguration getLinkConfiguration(int linkIndex) {
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
	public PIDConfiguration getLinkCurrentConfiguration(int chan) {
		return getAxisPidConfiguration().get(chan);
	}

	/**
	 * Sets the link current configuration.
	 *
	 * @param chan the chan
	 * @param c    the c
	 */
	public void setLinkCurrentConfiguration(int chan, PIDConfiguration c) {
		getAxisPidConfiguration().set(chan, c);
	}

	/**
	 * Gets the device.
	 *
	 * @return the device
	 */
	protected LinkFactory getDevice() {
		return getFactory();
	}

	/**
	 * Gets the abstract link.
	 *
	 * @param index the index
	 * @return the abstract link
	 */
	public AbstractLink getAbstractLink(int index) {
		return getFactory().getLink(getLinkConfiguration(index));
	}

	/**
	 * Sets the device.
	 *
	 * @param f           the f
	 * @param linkConfigs the link configs
	 */
	protected void setDevice(LinkFactory f, ArrayList<LinkConfiguration> linkConfigs) {
		Log.info("Loading device: " + f.getClass() + " " + f);
		setFactory(f);
		// Log.enableDebugPrint(true);
		for (int i = 0; i < linkConfigs.size(); i++) {
			LinkConfiguration c = linkConfigs.get(i);
			c.setLinkIndex(i);
			getFactory().getLink(c);
			Log.info("\nAxis #" + i + " Configuration:\n" + c);
			if (c.getTypeEnum() == LinkType.PID) {
				IPidControlNamespace device = getFactory().getPid(c);
				try {
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

					Log.info("\nAxis #" + i + " " + tmpConf);
					getAxisPidConfiguration().add(tmpConf);
					// setLinkCurrentConfiguration(i,tmpConf);
					// Send configuration for ONE axis
					device.ConfigurePIDController(tmpConf);
				} catch (Exception ex) {
					Log.error("Configuration #" + i + " failed!!");
					ex.printStackTrace();
				}
				device.addPIDEventListener(this);
			}
		}
		getCurrentTaskSpaceTransform();
		getFactory().addLinkListener(this);
		getDhParametersChain().setFactory(getFactory());

		// filling up the d-h parameters so the chain sizes match
		while (getDhParametersChain().getLinks().size() < linkConfigs.size()) {
			getDhParametersChain().addLink(new DHLink(0, 0, 0, 0));
		}
	}

	/**
	 * Gets the number of links defined in the configuration file.
	 *
	 * @return number of links in XML
	 */
	public int getNumberOfLinks() {
		return getLinkConfigurations().size();
	}

	/**
	 * This takes a reading of the robots position and converts it to a joint space
	 * vector This vector is converted to task space and returned .
	 *
	 * @return taskSpaceVector in mm,radians [x,y,z,rotx,rotY,rotZ]
	 */
	public TransformNR getCurrentTaskSpaceTransform() {
		TransformNR fwd = forwardKinematics(getCurrentJointSpaceVector());
		if (fwd == null)
			throw new RuntimeException("Implementations of the kinematics need to return a transform not null");
		// Log.info("Getting robot task space "+fwd);
		TransformNR taskSpaceTransform = forwardOffset(fwd);
		// Log.info("Getting global task space "+taskSpaceTransform);
		return taskSpaceTransform;
	}
	public double readLinkValue(int index) {
		return getFactory().getLink(getLinkConfiguration(index)).getCurrentEngineeringUnits();
	}
	public double readLinkTarget(int index) {
		return getFactory().getLink(getLinkConfiguration(index)).getTargetEngineeringUnits();
	}
	/**
	 * This takes a reading of the robots position and converts it to a joint pace
	 * vector This vector is converted to Joint space and returned .
	 *
	 * @return JointSpaceVector in mm,radians
	 */
	public double[] getCurrentJointSpaceVector() {
		double[] jointSpaceVect = new double[getNumberOfLinks()];
		for (int i = 0; i < getNumberOfLinks(); i++) {
			// double pos =
			// currentLinkSpacePositions[getLinkConfigurations().get(i).getHardwareIndex()];
			// Here the RAW values are converted to engineering units
			try {
				jointSpaceVect[i] = readLinkValue(i);
			}catch(Exception e) {
				jointSpaceVect[i]=0;
			}
		}

		return jointSpaceVect;
	}
	
	public double[] getCurrentJointSpaceTarget() {

		double[]	currentJointSpaceTarget=new double[getNumberOfLinks()];
		for(int i=0;i<currentJointSpaceTarget.length;i++) {
			currentJointSpaceTarget[i]=readLinkTarget(i);
		}
		return currentJointSpaceTarget;
	}
	public double getCurrentLinkEngineeringUnits(int linkIndex) {
		return getFactory().getLink(getLinkConfiguration(linkIndex)).getCurrentEngineeringUnits();
	}

	/**
	 * This calculates the target pose .
	 *
	 * @param taskSpaceTransform the task space transform
	 * @param seconds            the time for the transition to take from current
	 *                           position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public double[] setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) throws Exception {
		TickToc.tic("setDesiredTaskSpaceTransform start");
		Log.info("Setting target pose: " + taskSpaceTransform);
		setCurrentPoseTarget(taskSpaceTransform);
		TickToc.tic("setCurrentPoseTarget");
		TransformNR inverseOffset = inverseOffset(taskSpaceTransform);
		TickToc.tic("inverseOffset");
		double[] jointSpaceVect = inverseKinematics(inverseOffset);
		TickToc.tic("inverseKinematics");
		if(checkVector(this,jointSpaceVect,seconds)) {
			TickToc.tic("checkVector success");
			if (jointSpaceVect == null)
				throw new RuntimeException("The kinematics model must return an array, not null");
			
			_setDesiredJointSpaceVector(jointSpaceVect, seconds,false);
			TickToc.tic("_setDesiredJointSpaceVector complete");
			return jointSpaceVect;
		}else
			TickToc.tic("checkVector fail");
		
		double[] currentJointSpaceTarget2 = getCurrentJointSpaceTarget();
		TickToc.tic("getCurrentJointSpaceTarget");
		return currentJointSpaceTarget2;
	}

	/**
	 * Checks the desired pose for ability for the IK to calculate a valid pose.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @return True if pose is reachable, false if it is not
	 */
	public static boolean checkTaskSpaceTransform(AbstractKinematicsNR dev, TransformNR taskSpaceTransform, double seconds) {
		try {
			double[] jointSpaceVect = dev.inverseKinematics(dev.inverseOffset(taskSpaceTransform));
			return checkVector(dev, jointSpaceVect,seconds);
		} catch (Throwable ex) {
			//Log.error(ex);
			//ex.printStackTrace();
			return false;
		}
	}
	/**
	 * Checks the desired pose for ability for the IK to calculate a valid pose.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @return True if pose is reachable, false if it is not
	 */
	public static boolean checkTaskSpaceTransform(AbstractKinematicsNR dev, TransformNR taskSpaceTransform) {
		return checkTaskSpaceTransform(dev,taskSpaceTransform,0);
	}
	private static boolean checkVector(AbstractKinematicsNR dev, double[] jointSpaceVect, double seconds) {
		double[] current = dev.getCurrentJointSpaceTarget();
		for (int i = 0; i < jointSpaceVect.length; i++) {
			AbstractLink link = dev.factory.getLink(dev.getLinkConfiguration(i));
			double val = jointSpaceVect[i];
			Double double1 = new Double(val);
			if(double1.isNaN() ||double1.isInfinite() ) {
				Log.error(dev.getScriptingName()+" Link "+i+" Invalid unput "+double1);
				return false;
			}
			if (val > link.getMaxEngineeringUnits()) {
				Log.error(dev.getScriptingName()+" Link "+i+" can not reach "+val+" limited to "+link.getMaxEngineeringUnits());
				return false;
			}
			if (val < link.getMinEngineeringUnits()) {
				Log.error(dev.getScriptingName()+" Link "+i+" can not reach "+val+" limited to "+link.getMinEngineeringUnits());
				return false;
			}
			if(seconds>0) {
				double maxVel = Math.abs(link.getMaxVelocityEngineeringUnits());
				double deltaPosition = Math.abs(current[i] - jointSpaceVect[i]);
				double computedVelocity = deltaPosition/seconds;		
				if((computedVelocity-maxVel)>0.0001) {
					Log.error("Link "+i+" can not move at rate of "+computedVelocity+" capped at "+maxVel+" requested position of "+jointSpaceVect[i]+" from current position of "+current[i]+" in "+seconds+" seconds");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks the desired pose for ability for the IK to calculate a valid pose.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @return True if pose is reachable, false if it is not
	 */
	public boolean checkTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) {
		return AbstractKinematicsNR.checkTaskSpaceTransform(this, taskSpaceTransform,seconds);
	}
	/**
	 * Checks the desired pose for ability for the IK to calculate a valid pose.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @return True if pose is reachable, false if it is not
	 */
	public boolean checkTaskSpaceTransform(TransformNR taskSpaceTransform) {
		return checkTaskSpaceTransform(this, taskSpaceTransform,0);
	}
	
	/**
	 * get the best possible time for a translation by checking the joint velocities
	 * 
	 * @param currentTaskSpaceTransform new tip location to check
	 * @return the time of translation at best possible speed based on checking each link
	 */
	public double getBestTime(TransformNR currentTaskSpaceTransform) {
		double[] jointSpaceVect;
		double best = 0;
		try {
			jointSpaceVect = inverseKinematics(inverseOffset(currentTaskSpaceTransform));
			best = getBestTime(jointSpaceVect);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return best;
	}
	/**
	 * get the best possible time for a translation by checking the joint velocities
	 * 
	 * @param jointSpaceVect new joint pose
	 * @return the time of translation at best possible speed based on checking each link
	 */
	public double getBestTime(double[] jointSpaceVect) {
		double best=0;
		double[] current = getCurrentJointSpaceTarget();
		for (int i = 0; i < current.length; i++) {
			AbstractLink link = getAbstractLink(i);
			double maxVel = Math.abs(link.getMaxVelocityEngineeringUnits());
			double deltaPosition = Math.abs(current[i] - jointSpaceVect[i]);
			double seconds = deltaPosition / maxVel;
			if (seconds > best)
				best = seconds + Double.MIN_VALUE;
		}
		return best;
	}
	
	
	/**
	 * This calculates the target pose .
	 *
	 * @param jointSpaceVect the joint space vect
	 * @param seconds        the time for the transition to take from current
	 *                       position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public  double[] setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds) throws Exception {
		return _setDesiredJointSpaceVector(jointSpaceVect,seconds,true);
	}
	/**
	 * This calculates the target pose .
	 *
	 * @param jointSpaceVect the joint space vect
	 * @param seconds        the time for the transition to take from current
	 *                       position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	private  double[] _setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds, boolean fireTaskUpdate) throws Exception {
		if (jointSpaceVect.length != getNumberOfLinks()) {
			throw new IndexOutOfBoundsException("Vector must be " + getNumberOfLinks()
					+ " links, actual number of links = " + jointSpaceVect.length);
		}

		//synchronized(AbstractKinematicsNR.class) {
			int except = 0;
			Exception e = null;
			TickToc.tic("Set hardware values start");
			do {
				try {
					factory.setCachedTargets(jointSpaceVect);
					TickToc.tic("Cached targets ");
					if (!isNoFlush()) {
						//
						factory.flush(seconds);
						TickToc.tic("_setDesiredJointSpaceVector flush "+seconds);
						//
					}
					except = 0;
					e = null;
				} catch (Exception ex) {
					except++;
					e = ex;
					e.printStackTrace();
				}
			} while (except > 0 && except < getRetryNumberBeforeFail());
			if (e != null)
				throw new RuntimeException("Limit On "+getScriptingName()+" "+e.getMessage());

			TickToc.tic("Copy Vector");
			TransformNR fwd = forwardKinematics(getCurrentJointSpaceTarget());
			TickToc.tic("FK from vector");
			fireTargetJointsUpdate(getCurrentJointSpaceTarget(), fwd);
			TickToc.tic("Joint space updates");
			if(fireTaskUpdate) {
				setCurrentPoseTarget(forwardOffset(fwd));	
				TickToc.tic("task space updates");
			}
			
		//}
		return jointSpaceVect;
	}
	/**
	 * Calc forward.
	 *
	 * @param jointSpaceVect the joint space vect
	 * @return the transform nr
	 */
	public TransformNR calcForward(double[] jointSpaceVect) {
		return forwardOffset(forwardKinematics(jointSpaceVect));
	}

	/**
	 * Calc home.
	 *
	 * @return the transform nr
	 */
	public TransformNR calcHome() {
		double homevect[] = new double[getNumberOfLinks()];
		for (int i = 0; i < homevect.length; i++) {
			homevect[i] = 0;
		}
		return forwardOffset(forwardKinematics(homevect));
	}

	/**
	 * Sets an individual target joint position .
	 *
	 * @param axis    the joint index to set
	 * @param value   the value to set it to
	 * @param seconds the time for the transition to take from current position to
	 *                target, unit seconds
	 * @throws Exception If there is a workspace error
	 */
	public  void setDesiredJointAxisValue(int axis, double value, double seconds) throws Exception {
		//synchronized(AbstractKinematicsNR.class) {
			LinkConfiguration c = getLinkConfiguration(axis);

			Log.info("Setting single target joint in mm/deg, axis=" + axis + " value=" + value);
			try {
				getFactory().getLink(c).setTargetEngineeringUnits(value);
			} catch (Exception ex) {
				throw new Exception("Joint hit software bound, index " + axis + " attempted: " + value + " boundes: U="
						+ c.getUpperLimit() + ", L=" + c.getLowerLimit());
			}
			if (!isNoFlush()) {
				int except = 0;
				Exception e = null;
				do {
					try {
						getFactory().getLink(c).flush(seconds);
						except = 0;
						e = null;
					} catch (Exception ex) {
						except++;
						e = ex;
					}
				} while (except > 0 && except < getRetryNumberBeforeFail());
				if (e != null)
					throw e;
			}
			TransformNR fwd = forwardKinematics(getCurrentJointSpaceTarget());
			fireTargetJointsUpdate(getCurrentJointSpaceTarget(), fwd);
			setCurrentPoseTarget(forwardOffset(fwd));
		//}
		return;
	}

	/**
	 * Fire pose transform.
	 *
	 * @param transform the transform
	 */
	protected void firePoseTransform(TransformNR transform) {
		for (int i = 0; i < taskSpaceUpdateListeners.size(); i++) {
			ITaskSpaceUpdateListenerNR p = taskSpaceUpdateListeners.get(i);
			p.onTaskSpaceUpdate(this, transform);
		}
	}

	/**
	 * Fire pose update.
	 */
	public void firePoseUpdate() {
		// Log.info("Pose update");
		firePoseTransform(getCurrentTaskSpaceTransform());

		double[] vect = getCurrentJointSpaceVector();

		for (int i = 0; i < jointSpaceUpdateListeners.size(); i++) {
			IJointSpaceUpdateListenerNR p = jointSpaceUpdateListeners.get(i);
			p.onJointSpaceUpdate(this, vect);
		}
	}

	/**
	 * Fire target joints update.
	 *
	 * @param jointSpaceVector the joint space vector
	 * @param fwd              the fwd
	 */
	protected void fireTargetJointsUpdate(double[] jointSpaceVector, TransformNR fwd) {

		
		for (IJointSpaceUpdateListenerNR p : jointSpaceUpdateListeners) {
			p.onJointSpaceTargetUpdate(this, getCurrentJointSpaceTarget());
		}
	}

	/**
	 * Fire joint space limit update.
	 *
	 * @param axis  the axis
	 * @param event the event
	 */
	private void fireJointSpaceLimitUpdate(int axis, JointLimit event) {
		for (IJointSpaceUpdateListenerNR p : jointSpaceUpdateListeners) {
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
		if (baseToFiducial == null) {
			Log.error("Fiducial can not be null " + baseToFiducial);
			new Exception().printStackTrace(System.out);
			return;
		}
		Log.info("Setting Fiducial To base Transform " + baseToFiducial);
		this.base2Fiducial = baseToFiducial;
		for (IRegistrationListenerNR r : regListeners) {
			r.onBaseToFiducialUpdate(this, baseToFiducial);
		}
		
//		Platform.runLater(new Runnable() {
//
//			@Override
//			public void run() {
//				
//				TransformNR forwardOffset = forwardOffset(new TransformNR());
//				if(forwardOffset!=null && getRootListener()!=null)
//					TransformFactory.nrToObject(forwardOffset, getRootListener());
//			}
//		});
		runRenderWrangler();
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
	public void setGlobalToFiducialTransform(TransformNR frameToBase, boolean fireUpdate) {
		if (frameToBase == null) {
			Log.error("Fiducial can not be null " + frameToBase);
			new Exception("Fiducial can not be null ").printStackTrace(System.out);
			return;
		}
		Log.info("Setting Global To Fiducial Transform " + frameToBase);
		this.fiducial2RAS = frameToBase;
		if(!fireUpdate)
			return;
		for (IRegistrationListenerNR r : regListeners) {
			r.onFiducialToGlobalUpdate(this, frameToBase);
		}
		
		runRenderWrangler();
		
	}
	/**
	 * Sets the global to fiducial transform.
	 *
	 * @param frameToBase the new global to fiducial transform
	 */
	public void setGlobalToFiducialTransform(TransformNR frameToBase) {
		setGlobalToFiducialTransform(frameToBase, true);
	}
	/**
	 * Inverse offset.
	 *
	 * @param t the t
	 * @return the transform nr
	 */
	public TransformNR inverseOffset(TransformNR t) {
		// System.out.println("RobotToFiducialTransform
		// "+getRobotToFiducialTransform());
		// System.out.println("FiducialToRASTransform "+getFiducialToRASTransform());
		Matrix globalToFeducialInverse = getFiducialToGlobalTransform().getMatrixTransform().inverse();
		Matrix feducialToLimbInverse = getRobotToFiducialTransform().getMatrixTransform().inverse();

		Matrix NewGlobalSpaceTarget = t.getMatrixTransform();
		Matrix limbSpaceTarget = feducialToLimbInverse.times(globalToFeducialInverse).times(NewGlobalSpaceTarget);

		return new TransformNR(limbSpaceTarget);
	}

	/**
	 * Forward offset.
	 *
	 * @param t the t
	 * @return the transform nr
	 */
	public TransformNR forwardOffset(TransformNR t) {
		Matrix feducialToLimb = getRobotToFiducialTransform().getMatrixTransform();
		Matrix globaltoFeducial = getFiducialToGlobalTransform().getMatrixTransform();
		Matrix fkOfLimb = t.getMatrixTransform();
		Matrix mForward = globaltoFeducial.times(feducialToLimb).times(fkOfLimb);
		return new TransformNR(mForward);
	}

	/**
	 * Adds the joint space listener.
	 *
	 * @param l the l
	 */
	public void addJointSpaceListener(IJointSpaceUpdateListenerNR l) {
		if (jointSpaceUpdateListeners.contains(l) || l == null)
			return;
		jointSpaceUpdateListeners.add(l);
	}

	/**
	 * Removes the joint space update listener.
	 *
	 * @param l the l
	 */
	public void removeJointSpaceUpdateListener(IJointSpaceUpdateListenerNR l) {
		if (jointSpaceUpdateListeners.contains(l))
			jointSpaceUpdateListeners.remove(l);
	}

	/**
	 * Adds the registration listener.
	 *
	 * @param l the l
	 */
	public void addRegistrationListener(IRegistrationListenerNR l) {
		if (regListeners.contains(l) || l == null)
			return;
		regListeners.add(l);
		l.onBaseToFiducialUpdate(this, getRobotToFiducialTransform());
	}

	/**
	 * Removes the regestration update listener.
	 *
	 * @param l the l
	 */
	public void removeRegestrationUpdateListener(IRegistrationListenerNR l) {
		if (regListeners.contains(l))
			regListeners.remove(l);
	}

	/**
	 * Adds the pose update listener.
	 *
	 * @param l the l
	 */
	public void addPoseUpdateListener(ITaskSpaceUpdateListenerNR l) {
		if (taskSpaceUpdateListeners.contains(l) || l == null) {
			return;
		}
		// new RuntimeException("adding "+l.getClass().getName()).printStackTrace();
		taskSpaceUpdateListeners.add(l);
	}

	/**
	 * Removes the pose update listener.
	 *
	 * @param l the l
	 */
	public void removePoseUpdateListener(ITaskSpaceUpdateListenerNR l) {
		if (taskSpaceUpdateListeners.contains(l)) {
			// new RuntimeException("Removing "+l.getClass().getName()).printStackTrace();
			taskSpaceUpdateListeners.remove(l);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.neuronrobotics.sdk.addons.kinematics.ILinkListener#onLinkPositionUpdate(
	 * com.neuronrobotics.sdk.addons.kinematics.AbstractLink, double)
	 */
	@Override
	public void onLinkPositionUpdate(AbstractLink source, double engineeringUnitsValue) {
		for (LinkConfiguration c : getLinkConfigurations()) {
			AbstractLink tmp = getFactory().getLink(c);
			if (tmp == source) {// Check to see if this lines up with a known link
//				// Log.info("Got PID event "+source+" value="+engineeringUnitsValue);
//				if(new Double(engineeringUnitsValue).isNaN()) {
//					new RuntimeException("Link values can not ne NaN").printStackTrace();					
//					engineeringUnitsValue=0;
//				}
//				ArrayList<LinkConfiguration> linkConfigurations = getLinkConfigurations();
//				if(linkConfigurations!=null) {
//					int indexOf = linkConfigurations.indexOf(c);
//					if(currentJointSpacePositions!=null)
//						if(indexOf>=0 && indexOf<currentJointSpacePositions.length)
//							currentJointSpacePositions[indexOf] = engineeringUnitsValue;
//				}
				firePoseUpdate();
				return;
			}
		}
		Log.error("Got UKNOWN PID event " + source);
	}
	


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDEvent(com.neuronrobotics.
	 * sdk.pid.PIDEvent)
	 */
	@Override
	public void onPIDEvent(PIDEvent e) {
		// Ignore and use Link space events
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDLimitEvent(com.
	 * neuronrobotics.sdk.pid.PIDLimitEvent)
	 */
	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		for (int i = 0; i < getNumberOfLinks(); i++) {
			if (getLinkConfiguration(i).getHardwareIndex() == e.getGroup())
				fireJointSpaceLimitUpdate(i, new JointLimit(i, e, getLinkConfiguration(i)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDReset(int, int)
	 */
	@Override
	public void onPIDReset(int group, float currentValue) {
		// ignore at this level
	}

	/**
	 * This method uses the latch values to home all of the robot links.
	 */
	public void homeAllLinks() {

		for (int i = 0; i < getNumberOfLinks(); i++) {

			homeLink(i);
			// ThreadUtil.wait(2000);
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
	 * @param tps   the tps
	 */
	private void runHome(PIDChannel joint, int tps) {
		IPIDEventListener listen = new IPIDEventListener() {

			@Override
			public void onPIDReset(int group, float currentValue) {
			}

			@Override
			public void onPIDLimitEvent(PIDLimitEvent e) {
				homeTime = 0;// short circut the waiting loop
				Log.debug("Homing PID Limit event " + e);
			}

			@Override
			public void onPIDEvent(PIDEvent e) {
				homeTime = System.currentTimeMillis();
			}
		};
		joint.addPIDEventListener(listen);
		homeTime = System.currentTimeMillis();

		joint.SetPIDSetPoint(tps, 0);
		Log.info("Homing output to value: " + tps);
		while ((System.currentTimeMillis() < homeTime + 3000)) {
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
		if (link < 0 || link >= getNumberOfLinks()) {
			throw new IndexOutOfBoundsException(
					"There are only " + getNumberOfLinks() + " known links, requested:" + link);
		}
		LinkConfiguration conf = getLinkConfiguration(link);
		if (conf.getTypeEnum() == LinkType.PID) {
			getFactory().getPid(conf).removePIDEventListener(this);
			// Range is in encoder units
			double range = Math.abs(conf.getUpperLimit() - conf.getLowerLimit()) * 2;

			Log.info("Homing link:" + link + " to latch value: " + conf.getIndexLatch());
			PIDConfiguration pidConf = getLinkCurrentConfiguration(link);
			PIDChannel joint = getFactory().getPid(conf).getPIDChannel(conf.getHardwareIndex());

			// Clear the index
			pidConf.setStopOnIndex(false);
			pidConf.setUseLatch(false);
			pidConf.setIndexLatch(conf.getIndexLatch());
			joint.ConfigurePIDController(pidConf);// Sets up the latch

			// Move forward to stop
			runHome(joint, (int) (range));

			// Enable index
			pidConf.setStopOnIndex(true);
			pidConf.setUseLatch(true);
			pidConf.setIndexLatch(conf.getIndexLatch());
			joint.ConfigurePIDController(pidConf);// Sets up the latch
			// Move negative to the index
			runHome(joint, (int) (range * -1));

			pidConf.setStopOnIndex(false);
			pidConf.setUseLatch(false);
			pidConf.setIndexLatch(conf.getIndexLatch());
			joint.ConfigurePIDController(pidConf);// Shuts down the latch

			try {
				setDesiredJointAxisValue(link, 0, 0);// go to zero instead of to the index itself
			} catch (Exception e) {
				e.printStackTrace();
			}
			getFactory().getPid(conf).addPIDEventListener(this);
		} else {
			getFactory().getLink(getLinkConfiguration(link)).Home();
			getFactory().flush(1000);
		}
	}

	/**
	 * This is a quick stop for all axis of the robot.
	 */
	public void emergencyStop() {
		for (LinkConfiguration lf : getFactory().getLinkConfigurations())
			if (getFactory().getPid(lf) != null)
				getFactory().getPid(lf).killAllPidGroups();
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
		if (currentPoseTarget == null)
			currentPoseTarget = calcHome();
		return currentPoseTarget;
	}

	/**
	 * Sets the current pose target.
	 *
	 * @param currentPoseTarget the new current pose target
	 */
	public void setCurrentPoseTarget(TransformNR currentPoseTarget) {
		this.currentPoseTarget = currentPoseTarget;
		for (ITaskSpaceUpdateListenerNR p : taskSpaceUpdateListeners) {
			p.onTargetTaskSpaceUpdate(this, currentPoseTarget);
		}
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
		if (factory == null)
			factory = new LinkFactory();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.addons.kinematics.ILinkListener#onLinkLimit(com.
	 * neuronrobotics.sdk.addons.kinematics.AbstractLink,
	 * com.neuronrobotics.sdk.pid.PIDLimitEvent)
	 */
	@Override
	public void onLinkLimit(AbstractLink arg0, PIDLimitEvent arg1) {
		for (int i = 0; i < getNumberOfLinks(); i++) {
			if (getLinkConfiguration(i).getHardwareIndex() == arg0.getLinkConfiguration().getHardwareIndex())
				fireJointSpaceLimitUpdate(i, new JointLimit(i, arg1, arg0.getLinkConfiguration()));
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
	public String[] getGitDhEngine() {
		return dhEngine;
	}

	/**
	 * Sets the dh engine.
	 *
	 * @param dhEngine the new dh engine
	 */
	public void setGitDhEngine(String[] dhEngine) {
		if (dhEngine != null && dhEngine[0] != null && dhEngine[1] != null)
			this.dhEngine = dhEngine;
	}

	/**
	 * Gets the cad engine.
	 *
	 * @return the cad engine
	 */
	public String[] getGitCadEngine() {
		return cadEngine;
	}

	/**
	 * Sets the cad engine.
	 *
	 * @param cadEngine the new cad engine
	 */
	public void setGitCadEngine(String[] cadEngine) {
		if (cadEngine != null && cadEngine[0] != null && cadEngine[1] != null)
			this.cadEngine = cadEngine;
	}

	/**
	 * Gets the code.
	 *
	 * @param e   the e
	 * @param tag the tag
	 * @return the code
	 */
	protected String getCode(Element e, String tag) {
		try {
			NodeList nodListofLinks = e.getChildNodes();

			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				Node linkNode = nodListofLinks.item(i);
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals(tag)) {
					return XmlFactory.getTagValue(tag, e);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		throw new RuntimeException("No tag " + tag + " found");
	}

	/**
	 * Gets the gist codes.
	 *
	 * @param doc the doc
	 * @param tag the tag
	 * @return the gist codes
	 */
	protected String[] getGitCodes(Element doc, String tag) {
		String[] content = new String[3];
		try {
			NodeList nodListofLinks = doc.getChildNodes();
			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				Node linkNode = nodListofLinks.item(i);
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals(tag)) {
					Element e = (Element) linkNode;
					try {
						if (getCode(e, "gist") != null)
							content[0] = "https://gist.github.com/" + getCode(e, "gist") + ".git";
					} catch (Exception ex) {

					}
					try {
						if (getCode(e, "git") != null)
							content[0] = getCode(e, "git");
					} catch (Exception ex) {

					}
					try {
						if (getCode(e, "parallelGroup") != null)
							content[2] = getCode(e, "parallelGroup");
					} catch (Exception ex) {

					}
					content[1] = getCode(e, "file");
				}
			}
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public IMU getImu() {
		return imu;
	}

	// New helper functions to make the API simpler

	public void boundedLinkValueSet(int index, double value) throws Exception {
		value = boundToLinkLimits(index, value);
		double[] vect = getCurrentJointSpaceVector();
		vect[index] = value;
		setDesiredJointSpaceVector(vect, 0);
	}

	public double boundToLinkLimits(int index, double value) {
		AbstractLink l1 = getAbstractLink(index);
		if (value > l1.getMaxEngineeringUnits()) {
			value = l1.getMaxEngineeringUnits();
		}
		if (value < l1.getMinEngineeringUnits()) {
			value = l1.getMinEngineeringUnits();
		}
		return value;
	}

	public double linkMass(int linkIndex) {
		return getLinkConfiguration(linkIndex).getMassKg();
	}

	/**
	 * Gets the max engineering units.
	 *
	 * @return the max engineering units
	 */
	public double getMaxEngineeringUnits(int linkIndex) {
		return getAbstractLink(linkIndex).getMaxEngineeringUnits();
	}

	/**
	 * Gets the min engineering units.
	 *
	 * @return the min engineering units
	 */
	public double getMinEngineeringUnits(int linkIndex) {

		return getAbstractLink(linkIndex).getMinEngineeringUnits();
	}
	public String getElectroMechanicalType(int linkIndex) {
		return getLinkConfiguration(linkIndex).getElectroMechanicalType() ;
	}

	public void setElectroMechanicalType(int linkIndex,String electroMechanicalType) {
		getLinkConfiguration(linkIndex).setElectroMechanicalType(electroMechanicalType);
	}

	public String getElectroMechanicalSize(int linkIndex) {
		return getLinkConfiguration(linkIndex).getElectroMechanicalSize() ;
	}

	public void setElectroMechanicalSize(int linkIndex,String electroMechanicalSize) {
		getLinkConfiguration(linkIndex).setElectroMechanicalSize(electroMechanicalSize);
	}

	public String getShaftType(int linkIndex) {
		return getLinkConfiguration(linkIndex).getShaftType();
	}

	public void setShaftType(int linkIndex,String shaftType) {
		getLinkConfiguration(linkIndex).setShaftType(shaftType);
	}

	public String getShaftSize(int linkIndex) {
		return getLinkConfiguration(linkIndex).getShaftSize();
	}
	/**
	 * Override this method to specify a larger range
	 */
	public void setDeviceMaximumValue(int linkIndex,double max) {
		getLinkConfiguration(linkIndex).setDeviceTheoreticalMax(max);
	}
	/**
	 * Override this method to specify a larger range

	 */
	public void setDeviceMinimumValue(int linkIndex,double min) {
		getLinkConfiguration(linkIndex).setDeviceTheoreticalMin(min);
	}
	/**
	 * Override this method to specify a larger range
	 * @return the maximum value possible for a link
	 */
	public double getDeviceMaximumValue(int linkIndex) {
		return getLinkConfiguration(linkIndex).getDeviceTheoreticalMax();
	}
	/**
	 * Override this method to specify a larger range
	 * @return the minimum value possible for a link
	 */
	public double getDeviceMinimumValue(int linkIndex) {
		return getLinkConfiguration(linkIndex).getDeviceTheoreticalMin();
	}
	public void addChangeListener(int linkIndex,ILinkConfigurationChangeListener l) {
		getLinkConfiguration(linkIndex).addChangeListener(l);
	}
	public void removeChangeListener(int linkIndex,ILinkConfigurationChangeListener l) {
		getLinkConfiguration(linkIndex).removeChangeListener(l);
	}
	public void clearChangeListener(int linkIndex) {
		getLinkConfiguration(linkIndex).clearChangeListener();
	}



	public void runRenderWrangler() {
		firePoseUpdate();
		if(renderWrangler!=null)
			try {
				renderWrangler.run();
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}

	public void setRenderWrangler(Runnable renderWrangler) {
		this.renderWrangler = renderWrangler;
	}

	public TransformNR getDeltaToTarget(TransformNR target) {
		TransformNR startingPoint = getCurrentPoseTarget();
		// create a transform thats a delta from the current pose to the new pose
		return startingPoint.inverse().times(target);
	}
	
	public TransformNR getTipAlongTrajectory(TransformNR startingPoint,TransformNR deltaToTarget,double unitIncrement) {
		return startingPoint.times(deltaToTarget.scale(unitIncrement));
	}
	public void asyncInterpolatedMove(TransformNR target, double seconds, InterpolationType type,IOnInterpolationDone listener, double ...conf ) {
		new Thread(()->{
			try {
				InterpolationMoveState s = blockingInterpolatedMove(target, seconds, type, conf);
				listener.done(s);
			}catch(Throwable t) {
				t.printStackTrace();
				listener.done(InterpolationMoveState.FAULT);
			}
		}).start();
		
	}
	
	public InterpolationMoveState blockingInterpolatedMove(TransformNR target, double seconds, InterpolationType type, double ...conf ) {
		InterpolationEngine engine = new InterpolationEngine();
		long currentTimeMillis = System.currentTimeMillis();
		TransformNR delta =getDeltaToTarget(target);
		TransformNR startingPoint = getCurrentPoseTarget();
		if (checkTaskSpaceTransform(target)) {
			if (!checkTaskSpaceTransform(target, seconds)) {
				// if the robot can not acive that speed, then compute the best possible time
				double bestTime = getBestTime(target);
				// if speed is capped and no valid, then just cap the speed and print a warning
				if (bestTime > seconds) {
					seconds = bestTime;
				}
			}
			
			engine.setSetpointWithTime(currentTimeMillis,1,seconds,type,conf);
			double ms = seconds * 1000;
			double msPerStep = 10;
			double steps = ms / msPerStep;
			double unitIncrement = 1.0 / steps;
			// iterate over all of the time slices to perfoem a task-space interpolation
			for (double i = 0; i < (1 + unitIncrement); i += unitIncrement) {
				// compute the next tip location
				// the delta of the overall translation above is scaled by the unit vector
				// of the translation
				// the new tip point here calculated is multiplied by the starting point to get
				// a global space tip target
				TransformNR nextPoint = getTipAlongTrajectory(startingPoint,delta,engine.getInterpolationUnitIncrement(System.currentTimeMillis()));
				// now the best time for this increment is calculated
				double bestTime = getBestTime(nextPoint);
				// error check for the best time being below the commanded time
				if (bestTime > msPerStep / 1000.0) {
					// print an error in the event of speed capped
				}
				// perform one last tip and speed check of the increment
				if (checkTaskSpaceTransform(nextPoint, bestTime)) {
					// send the tip update to the simulator
					try {
						setDesiredTaskSpaceTransform(nextPoint, bestTime);
					} catch (Exception e) {
						return InterpolationMoveState.FAULT;
					}
				} else {
					// incremental tip failed, fault
					return InterpolationMoveState.FAULT;
				}
				ThreadUtil.wait((int) msPerStep);
			}
		}else {
			return InterpolationMoveState.FAULT;
		}
		return InterpolationMoveState.READY;
	}
}
