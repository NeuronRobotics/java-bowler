package com.neuronrobotics.sdk.addons.kinematics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.parallel.ParallelGroup;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.DeviceManager;

// TODO: Auto-generated Javadoc
/**
 * The Class MobileBase.
 */
public class MobileBase extends AbstractKinematicsNR {

	/** The legs. */
	private final ArrayList<DHParameterKinematics> legs = new ArrayList<DHParameterKinematics>();

	/** The appendages. */
	private final ArrayList<DHParameterKinematics> appendages = new ArrayList<DHParameterKinematics>();

	/** The steerable. */
	private final ArrayList<DHParameterKinematics> steerable = new ArrayList<DHParameterKinematics>();

	/** The drivable. */
	private final ArrayList<DHParameterKinematics> drivable = new ArrayList<DHParameterKinematics>();

	/** The walking drive engine. */
	private IDriveEngine walkingDriveEngine = new WalkingDriveEngine();

	/** The walking engine. */
	private String[] walkingEngine = new String[] { "https://github.com/madhephaestus/carl-the-hexapod.git",
			"WalkingDriveEngine.groovy" };

	private HashMap<String , String[]> vitamins= new HashMap<String, String[]>();
	private HashMap<String , String> vitaminVariant= new HashMap<String, String>();
	
	/** The self source. */
	private String[] selfSource = new String[2];

	private double mass = 0.5;// KG
	private TransformNR centerOfMassFromCentroid = new TransformNR();

	private TransformNR IMUFromCentroid = new TransformNR();

	private HashMap<String, ParallelGroup> parallelGroups = new HashMap<String, ParallelGroup>();

	/**
	 * Instantiates a new mobile base.
	 */
	public MobileBase() {
	}// used for building new bases live

	/**
	 * Instantiates a new mobile base.
	 *
	 * @param configFile
	 *            the config file
	 */
	public MobileBase(InputStream configFile) {
		this();
		Document doc = XmlFactory.getAllNodesDocument(configFile);
		NodeList nodListofLinks = doc.getElementsByTagName("root");

		if (nodListofLinks.getLength() != 1) {
			// System.out.println("Found "+nodListofLinks.getLength());
			throw new RuntimeException("one mobile base is needed per level");
		}
		NodeList rootNode = nodListofLinks.item(0).getChildNodes();

		for (int i = 0; i < rootNode.getLength(); i++) {

			Node linkNode = rootNode.item(i);
			if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contains("mobilebase")) {
				Element e = (Element) linkNode;
				loadConfigs(e);
			}
		}

		addRegistrationListener(new IRegistrationListenerNR() {
			@Override
			public void onFiducialToGlobalUpdate(AbstractKinematicsNR source, TransformNR regestration) {

				for (DHParameterKinematics kin : getAllDHChains()) {
					// Log.debug("Motion of mobile base event ");
					// this represents motion of the mobile base
					kin.setGlobalToFiducialTransform(regestration);
				}

			}

			@Override
			public void onBaseToFiducialUpdate(AbstractKinematicsNR source, TransformNR regestration) {
			}
		});
	}

	/**
	 * Instantiates a new mobile base.
	 *
	 * @param doc
	 *            the doc
	 */
	public MobileBase(Element doc) {

		loadConfigs(doc);

	}

	public ParallelGroup getParallelGroup(String name) {
		if (getParallelGroups().get(name) == null) {
			getParallelGroups().put(name, new ParallelGroup());
		}
		return getParallelGroups().get(name);
	}

	public Set<String> getParallelGroupNames() {
		return getParallelGroups().keySet();
	}

	public ArrayList<ParallelGroup> getAllParallelGroups() {
		ArrayList<ParallelGroup> list = new ArrayList<ParallelGroup>();
		for (String name : getParallelGroupNames()) {
			list.add(getParallelGroup(name));
		}
		return list;
	}

	public ParallelGroup getParallelGroup(DHParameterKinematics limb) {
		for (String name : getParallelGroupNames()) {
			for (DHParameterKinematics dh : getParallelGroup(name).getConstituantLimbs()) {
				if (dh == limb) {
					return getParallelGroup(name);
				}
			}
		}
		return null;
	}

	public void addLimbToParallel(DHParameterKinematics limb, TransformNR tipOffset, String name) {
		removeLimFromParallel(limb);
		ParallelGroup g = getParallelGroup(name);
		g.addLimb(limb, tipOffset);
	}

	private void removeLimFromParallel(DHParameterKinematics limb) {
		ParallelGroup g = getParallelGroup(limb);
		if (g != null) {
			g.removeLimb(limb);
		}
	}

	/**
	 * Load configs.
	 *
	 * @param doc
	 *            the doc
	 */
	private void loadConfigs(Element doc) {
		setScriptingName(XmlFactory.getTagValue("name", doc));

		setGitCadEngine(getGitCodes(doc, "cadEngine"));
		setGitWalkingEngine(getGitCodes(doc, "driveEngine"));
		try {
			String[] paralellCad = getGitCodes(doc, "parallelCadEngine");
			getParallelGroup(paralellCad[2]).setGitCadToolEngine(paralellCad);
		} catch (Exception e) {

		}

		loadVitamins( doc);
		loadLimb(doc, "leg", legs);
		loadLimb(doc, "drivable", drivable);
		loadLimb(doc, "steerable", steerable);
		loadLimb(doc, "appendage", appendages);
		try {
			setMassKg(Double.parseDouble(XmlFactory.getTagValue("mass", doc)));
		} catch (Exception e) {

		}

		TransformNR cmcenter = loadTransform("centerOfMassFromCentroid", doc);
		if (cmcenter != null)
			setCenterOfMassFromCentroid(cmcenter);
		TransformNR IMUcenter = loadTransform("imuFromCentroid", doc);
		if (IMUcenter != null)
			setIMUFromCentroid(IMUcenter);

		for (String key : getParallelGroups().keySet()) {
			if (key != null) {
				ParallelGroup g = getParallelGroups().get(key);
				try {
					g.setDesiredTaskSpaceTransform(g.getConstituantLimbs().get(0).calcHome(), 1.0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private TransformNR loadTransform(String tagname, Element e) {

		try{
			NodeList nodListofLinks = e.getChildNodes();
	
			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				Node linkNode = nodListofLinks.item(i);
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals(tagname)) {
					Element cntr = (Element) linkNode;
					return new TransformNR(Double.parseDouble(XmlFactory.getTagValue("x", cntr)),
							Double.parseDouble(XmlFactory.getTagValue("y", cntr)),
							Double.parseDouble(XmlFactory.getTagValue("z", cntr)),
							new RotationNR(new double[] { Double.parseDouble(XmlFactory.getTagValue("rotw", cntr)),
									Double.parseDouble(XmlFactory.getTagValue("rotx", cntr)),
									Double.parseDouble(XmlFactory.getTagValue("roty", cntr)),
									Double.parseDouble(XmlFactory.getTagValue("rotz", cntr)) }));
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
			
		}
		return new TransformNR();
	}

	/**
	 * Gets the name.
	 *
	 * @param e
	 *            the e
	 * @param tag
	 *            the tag
	 * @return the name
	 */
	private String getname(Element e) {
		String name = getTag(e, "name");
		if (name == null)
			name = "nonamespecified";
		return name;
	}

	/**
	 * Gets the contents in the group.
	 *
	 * @param e
	 *            the e
	 * @param tag
	 *            the tag
	 * @return the name
	 */
	private String getParallelGroup(Element e) {
		return getTag(e,  "parallelGroup");
	}

	/**
	 * Gets the localTag
	 *
	 * @param e
	 *            the e
	 * @param tag
	 *            the tag
	 * @return the name
	 */
	private String getTag(Element e, String tagname) {
		try {
			NodeList nodListofLinks = e.getChildNodes();

			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				Node linkNode = nodListofLinks.item(i);
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals(tagname)) {
					return XmlFactory.getTagValue(tagname, e);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Load limb.
	 *
	 * @param doc
	 *            the doc
	 * @param tag
	 *            the tag
	 * @param list
	 *            the list
	 */
	private void loadLimb(Element doc, String tag, ArrayList<DHParameterKinematics> list) {
		NodeList nodListofLinks = doc.getChildNodes();
		for (int i = 0; i < nodListofLinks.getLength(); i++) {
			Node linkNode = nodListofLinks.item(i);
			if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals(tag)) {
				Element e = (Element) linkNode;
				final String name = getname(e);
				//System.out.println("Loading arm "+name);
				DHParameterKinematics kin = (DHParameterKinematics) DeviceManager
						.getSpecificDevice(DHParameterKinematics.class, name);
				if (kin == null) {
					kin = new DHParameterKinematics(e);

					// DeviceManager.addConnection(kin, name);
				}
				kin.setScriptingName(name);
				list.add(kin);
				String parallel = getParallelGroup(e);
				//System.out.println("paralell "+parallel);
				if (parallel != null) {
					TransformNR paraOffset = loadTransform("parallelGroupTipOffset", e);
					if (paraOffset == null) {
						paraOffset = new TransformNR();
					}
					getParallelGroup(parallel).addLimb(kin, paraOffset);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#
	 * disconnectDevice()
	 */
	@Override
	public void disconnectDevice() {
		for (DHParameterKinematics kin : getAllDHChains()) {
			kin.disconnect();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#
	 * connectDevice()
	 */
	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#
	 * inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math.
	 * TransformNR)
	 */
	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform) throws Exception {
		// TODO Auto-generated method stub
		return new double[getNumberOfLinks()];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#
	 * forwardKinematics(double[])
	 */
	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return new TransformNR();
	}

	/**
	 * Gets the legs.
	 *
	 * @return the legs
	 */
	public ArrayList<DHParameterKinematics> getLegs() {
		return legs;
	}

	/**
	 * Gets the appendages.
	 *
	 * @return the appendages
	 */
	public ArrayList<DHParameterKinematics> getAppendages() {
		return appendages;
	}

	/**
	 * Gets the all dh chains.
	 *
	 * @return the all dh chains
	 */
	public ArrayList<DHParameterKinematics> getAllDHChains() {
		ArrayList<DHParameterKinematics> copy = new ArrayList<DHParameterKinematics>();
		for (DHParameterKinematics l : legs) {
			copy.add(l);
		}
		for (DHParameterKinematics l : appendages) {
			copy.add(l);

		}
		for (DHParameterKinematics l : steerable) {
			copy.add(l);
		}
		for (DHParameterKinematics l : drivable) {
			copy.add(l);
		}
		return copy;
	}
	/**
	 * Load limb.
	 *
	 * @param doc
	 *            the doc
	 * @param tag
	 *            the tag
	 * @param list
	 *            the list
	 */
	private void loadVitamins(Element doc) {
		NodeList nodListofLinks = doc.getChildNodes();
		for (int i = 0; i < nodListofLinks.getLength(); i++) {
			Node linkNode = nodListofLinks.item(i);
			try{
	    		if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("vitamins")) {    	    
			    	getVitamins((Element)linkNode)	 ;
			    }
	    	}catch (Exception e){
	    		
	    	}
		}
	}
	
	public HashMap<String , String[]> getVitamins() {
		return vitamins;
	}
	/**
	 * Gets the vitamins.
	 *
	 * @param doc the doc
	 */
	private void getVitamins(Element doc) {

		try {
			NodeList nodListofLinks = doc.getChildNodes();
			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				Node linkNode = nodListofLinks.item(i);
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("vitamin")) {
					Element e = (Element) linkNode;
					setVitamin(XmlFactory.getTagValue("name",e),
							XmlFactory.getTagValue("type",e),
							XmlFactory.getTagValue("id",e)
							);
					try{
						setVitaminVariant(XmlFactory.getTagValue("name",e),
								XmlFactory.getTagValue("variant",e));
					}catch(Exception ex){}
				}
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * Add a vitamin to this link
	 * @param 	name the name of this vitamin, 
				if the name already exists, the data will be overwritten. 
	 * @param type the vitamin type, this maps the the json filename
	 * @param id the part ID, theis maps to the key in the json for the vitamin
	 */
	public void setVitamin(String name, String type, String id){
		if(getVitamins().get(name)==null){
			getVitamins().put(name, new String[2]);
		}
		getVitamins().get(name)[0]=type;
		getVitamins().get(name)[1]=id;
	}
	/**
	 * Set a purchasing code for a vitamin
	 * @param name name of vitamin
	 * @param tagValue2 Purchaning code
	 */
	public void setVitaminVariant(String name, String tagValue2) {
		vitaminVariant.put(name, tagValue2);
	}
	/**
	 * Get a purchaing code for a vitamin
	 * @param name name of vitamin
	 * @return
	 */
	public String getVitaminVariant(String name) {
		return vitaminVariant.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#getXml()
	 */
	/*
	 * 
	 * Generate the xml configuration to generate an XML of this robot.
	 */
	public String getXml() {
		String xml = "<root>\n";
		xml += getEmbedableXml();
		xml += "\n</root>";
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
	public String getEmbedableXml() {
		TransformNR location = getFiducialToGlobalTransform();
		setGlobalToFiducialTransform(new TransformNR());
		
		String allVitamins="";
		for(String key: getVitamins().keySet()){
			String v = "\t\t<vitamin>\n";
			v+=		"\t\t\t<name>"+key+"</name>\n"+
					"\t\t\t<type>"+getVitamins().get(key)[0]+"</type>\n"+
					"\t\t\t<id>"+getVitamins().get(key)[1]+"</id>\n";
			if (getVitaminVariant(key)!=null){
				v+=		"\t\t\t<variant>"+getVitamins().get(key)[1]+"</variant>\n";
			}
			v+="\t\t</vitamin>\n";
			allVitamins+=v;
		}
		String xml = "<mobilebase>\n";

		xml += "\t<cadEngine>\n";
		xml += "\t\t<git>" + getGitCadEngine()[0] + "</git>\n";
		xml += "\t\t<file>" + getGitCadEngine()[1] + "</file>\n";
		xml += "\t</cadEngine>\n";

		xml += "\t<driveEngine>\n";
		xml += "\t\t<git>" + getGitWalkingEngine()[0] + "</git>\n";
		xml += "\t\t<file>" + getGitWalkingEngine()[1] + "</file>\n";
		xml += "\t</driveEngine>\n";

		for (String key : getParallelGroups().keySet()) {
			ParallelGroup g = getParallelGroups().get(key);
			if (key != null) {
				xml += "\t<parallelCadEngine>\n";
				xml += "\t\t<parallelGroup>" + key + "</parallelGroup>\n";
				xml += "\t\t<git>" + g.getGitCadToolEngine()[0] + "</git>\n";
				xml += "\t\t<file>" + g.getGitCadToolEngine()[1] + "</file>\n";
				xml += "\t</parallelCadEngine>\n";
			}
		}

		xml += "\n<name>" + getScriptingName() + "</name>\n";
		for (DHParameterKinematics l : legs) {
			xml += "<leg>\n";
			xml += "\n<name>" + l.getScriptingName() + "</name>\n";
			xml += l.getEmbedableXml();
			xml += "\n</leg>\n";
		}
		for (DHParameterKinematics l : appendages) {
			xml += "<appendage>\n";
			xml += "\n<name>" + l.getScriptingName() + "</name>\n";
			for (String key : getParallelGroups().keySet()) {
				for (DHParameterKinematics pL : getParallelGroups().get(key).getConstituantLimbs())
					if (pL == l) {
						xml += "\n<parallelGroup>" + key + "</parallelGroup>\n";
						xml += "\t<parallelGroupTipOffset>\n"
								+ getParallelGroups().get(key).getTipOffset().get(l).getXml()
								+ "\n</parallelGroupTipOffset>\n";
					}
			}
			xml += l.getEmbedableXml();
			xml += "\n</appendage>\n";
		}

		for (DHParameterKinematics l : steerable) {
			xml += "<steerable>\n";
			xml += "\n<name>" + l.getScriptingName() + "</name>\n";
			xml += l.getEmbedableXml();
			xml += "\n</steerable>\n";
		}
		for (DHParameterKinematics l : drivable) {
			xml += "<drivable>\n";
			xml += "\n<name>" + l.getScriptingName() + "</name>\n";
			xml += l.getEmbedableXml();
			xml += "\n</drivable>\n";
		}

		xml += "\n<ZframeToRAS>\n";
		xml += getFiducialToGlobalTransform().getXml();
		xml += "\n</ZframeToRAS>\n";

		xml += "\n<baseToZframe>\n";
		xml += getRobotToFiducialTransform().getXml();
		xml += "\n</baseToZframe>\n" + "\t<mass>" + getMassKg() + "</mass>\n" + "\t<centerOfMassFromCentroid>"
				+ getCenterOfMassFromCentroid().getXml() + "</centerOfMassFromCentroid>\n" + "\t<imuFromCentroid>"
				+ getIMUFromCentroid().getXml() + "</imuFromCentroid>\n";
		xml += "\n<vitamins>\n"+allVitamins+"\n</vitamins>\n";
		xml += "\n</mobilebase>\n";
		setGlobalToFiducialTransform(location);
		return xml;
	}

	/**
	 * Gets the steerable.
	 *
	 * @return the steerable
	 */
	public ArrayList<DHParameterKinematics> getSteerable() {
		return steerable;
	}

	/**
	 * Gets the drivable.
	 *
	 * @return the drivable
	 */
	public ArrayList<DHParameterKinematics> getDrivable() {
		return drivable;
	}

	/**
	 * Gets the walking drive engine.
	 *
	 * @return the walking drive engine
	 */
	private IDriveEngine getWalkingDriveEngine() {

		return walkingDriveEngine;
	}

	/**
	 * Sets the walking drive engine.
	 *
	 * @param walkingDriveEngine
	 *            the new walking drive engine
	 */
	public void setWalkingDriveEngine(IDriveEngine walkingDriveEngine) {
		this.walkingDriveEngine = walkingDriveEngine;
	}

	/**
	 * Drive arc.
	 *
	 * @param newPose
	 *            the new pose
	 * @param seconds
	 *            the seconds
	 */
	public void DriveArc(TransformNR newPose, double seconds) {
		getWalkingDriveEngine().DriveArc(this, newPose, seconds);
		updatePositions();
	}

	/**
	 * Drive velocity straight.
	 *
	 * @param cmPerSecond
	 *            the cm per second
	 */
	public void DriveVelocityStraight(double cmPerSecond) {
		getWalkingDriveEngine().DriveVelocityStraight(this, cmPerSecond);

		updatePositions();
	}

	/**
	 * Drive velocity arc.
	 *
	 * @param degreesPerSecond
	 *            the degrees per second
	 * @param cmRadius
	 *            the cm radius
	 */
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		getWalkingDriveEngine().DriveVelocityArc(this, degreesPerSecond, cmRadius);

		updatePositions();
	}

	/**
	 * Update positions.
	 */
	public void updatePositions() {
		for (DHParameterKinematics kin : getAppendages()) {
			// System.err.println("Updating arm: "+kin.getScriptingName());
			kin.updateCadLocations();
		}
		for (DHParameterKinematics kin : getDrivable()) {
			// System.err.println("Updating getDrivable:
			// "+kin.getScriptingName());
			kin.updateCadLocations();
		}
		for (DHParameterKinematics kin : getSteerable()) {
			// System.err.println("Updating getSteerable:
			// "+kin.getScriptingName());
			kin.updateCadLocations();
		}

	}

	/**
	 * Gets the walking engine.
	 *
	 * @return the walking engine
	 */
	public String[] getGitWalkingEngine() {
		return walkingEngine;
	}

	/**
	 * Sets the walking engine.
	 *
	 * @param walkingEngine
	 *            the new walking engine
	 */
	public void setGitWalkingEngine(String[] walkingEngine) {
		if (walkingEngine != null && walkingEngine[0] != null && walkingEngine[1] != null)
			this.walkingEngine = walkingEngine;
	}

	/**
	 * Gets the self source.
	 *
	 * @return the self source
	 */
	public String[] getGitSelfSource() {
		return selfSource;
	}

	/**
	 * Sets the self source.
	 *
	 * @param selfSource
	 *            the new self source
	 */
	public void setGitSelfSource(String[] selfSource) {
		this.selfSource = selfSource;
	}

	public double getMassKg() {
		return mass;
	}

	public void setMassKg(double mass) {
		this.mass = mass;
	}

	public TransformNR getCenterOfMassFromCentroid() {
		return centerOfMassFromCentroid;
	}

	public void setCenterOfMassFromCentroid(TransformNR centerOfMassFromCentroid) {
		this.centerOfMassFromCentroid = centerOfMassFromCentroid;
	}

	public TransformNR getIMUFromCentroid() {
		return IMUFromCentroid;
	}

	public void setIMUFromCentroid(TransformNR centerOfMassFromCentroid) {
		this.IMUFromCentroid = centerOfMassFromCentroid;
	}
	public void setFiducialToGlobalTransform(TransformNR globe) {
		setGlobalToFiducialTransform(globe);
		
	}
	/**
	 * Sets the global to fiducial transform.
	 *
	 * @param frameToBase the new global to fiducial transform
	 */
	@Override
	public void setGlobalToFiducialTransform(TransformNR frameToBase) {
		super.setGlobalToFiducialTransform(frameToBase);
		for(DHParameterKinematics l:getAllDHChains()) {
			l.setGlobalToFiducialTransform(frameToBase);
		}
	}


	private HashMap<String, ParallelGroup> getParallelGroups() {
		return parallelGroups;
	}



	public static void main(String[] args) throws Exception {
		File f = new File("paralleloutput.xml");

		MobileBase pArm = new MobileBase(new FileInputStream(f));
		String xmlParsed = pArm.getXml();
		BufferedWriter writer = null;

		writer = new BufferedWriter(new FileWriter("paralleloutput2.xml"));
		writer.write(xmlParsed);

		if (writer != null)
			writer.close();
		
		ParallelGroup group = pArm.getParallelGroup("ParallelArmGroup");
		
		TransformNR Tip = group.getCurrentTaskSpaceTransform();
		

		group.setDesiredTaskSpaceTransform(Tip.copy().translateX(-1), 0);
		for(DHParameterKinematics limb:group.getConstituantLimbs()){
			TransformNR TipOffset = group.getTipOffset().get(limb);
			TransformNR newTip = limb.getCurrentTaskSpaceTransform().times(TipOffset);
			
			System.out.println("Expected tip to be "+Tip.getX()+" and got: "+newTip.getX());
			//assertTrue(!Double.isNaN(Tip.getX()));
			//assertEquals(Tip.getX(), newTip.getX(), .1);
		}
		
		
	}

}
