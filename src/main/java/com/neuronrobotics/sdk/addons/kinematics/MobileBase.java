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
import com.neuronrobotics.sdk.addons.kinematics.time.ITimeProvider;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.common.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class MobileBase.
 */
public class MobileBase extends AbstractKinematicsNR implements ILinkConfigurationChangeListener,
		IOnMobileBaseRenderChange, IJointSpaceUpdateListenerNR, IHardwareSyncPulseReciver, IHardwareSyncPulseProvider {

	/** The legs. */
	private final ArrayList<DHParameterKinematics> legs = new ArrayList<DHParameterKinematics>();

	/** The appendages. */
	private final ArrayList<DHParameterKinematics> appendages = new ArrayList<DHParameterKinematics>();

	/** The steerable. */
	private final ArrayList<DHParameterKinematics> steerable = new ArrayList<DHParameterKinematics>();

	/** The drivable. */
	private final ArrayList<DHParameterKinematics> drivable = new ArrayList<DHParameterKinematics>();

	/** The drivable. */
	private final ArrayList<IOnMobileBaseRenderChange> changeListeners = new ArrayList<IOnMobileBaseRenderChange>();

	/** The walking drive engine. */
	private IDriveEngine walkingDriveEngine = new WalkingDriveEngine();

	/** The walking engine. */
	private String[] walkingEngine = new String[] { "https://github.com/madhephaestus/carl-the-hexapod.git",
			"WalkingDriveEngine.groovy" };

	private ArrayList<VitaminLocation> vitamins = new ArrayList<>();
	private HashMap<String, String> vitaminVariant = new HashMap<String, String>();

	/** The self source. */
	private String[] selfSource = new String[2];

	private double mass = 0.5;// KG
	private TransformNR centerOfMassFromCentroid = new TransformNR();

	private TransformNR IMUFromCentroid = new TransformNR();

	private HashMap<String, ParallelGroup> parallelGroups = new HashMap<String, ParallelGroup>();
	private ICalcLimbHomeProvider homeProvider = null;

	/**
	 * Instantiates a new mobile base.
	 */
	public MobileBase() {
	}// used for building new bases live

	/**
	 * Calc home.
	 *
	 * @return the transform nr
	 */
	public TransformNR calcHome(DHParameterKinematics limb) {
		try {
			return homeProvider.calcHome(limb);
		} catch (Throwable t) {
		}
		return limb.calcHome();
	}

	public HashMap<DHParameterKinematics, TransformNR> getTipLocations() {

		HashMap<DHParameterKinematics, TransformNR> tipList = new HashMap<DHParameterKinematics, TransformNR>();
		for (DHParameterKinematics leg : legs) {
			// Read the location of the foot before moving the body
			TransformNR home = calcHome(leg);
			tipList.put(leg, home);
		}
		return tipList;
	}
	public DHParameterKinematics getLimb(AbstractLink l) {
		for(DHParameterKinematics k:getAllDHChains()) {
			if(k.getLinkIndex(l)>=0)
				return k;
		}
		return null;
	}
	public boolean pose(TransformNR newAbsolutePose) throws Exception {
		HashMap<DHParameterKinematics, TransformNR> tipLocations = getTipLocations();

		return pose(newAbsolutePose, getIMUFromCentroid(), tipLocations);
	}

	public boolean poseAroundPoint(TransformNR newAbsolutePose, TransformNR around) throws Exception {
		HashMap<DHParameterKinematics, TransformNR> tipLocations = getTipLocations();

		return pose(newAbsolutePose, around, tipLocations);
	}

	public boolean pose(TransformNR newAbsolutePose, TransformNR around,
			HashMap<DHParameterKinematics, TransformNR> tipList) throws Exception {
		TransformNR newPoseTransformedToIMUCenter = newAbsolutePose.times(around.inverse());
		TransformNR newPoseAdjustedBacktoRobotCenterFrame = around.times(newPoseTransformedToIMUCenter);
		TransformNR previous = getFiducialToGlobalTransform();
		// Perform a pose opperation
		setGlobalToFiducialTransform(newPoseAdjustedBacktoRobotCenterFrame);

		for (DHParameterKinematics leg : legs) {
			TransformNR pose = tipList.get(leg);
			if (leg.checkTaskSpaceTransform(pose))// move the leg only is the pose of hte limb is possible
				leg.setDesiredTaskSpaceTransform(pose, 0);// set leg to the location of where the foot was
			else {
				setGlobalToFiducialTransform(previous);
				for (DHParameterKinematics l : legs) {
					TransformNR p = tipList.get(l);
					l.setDesiredTaskSpaceTransform(p, 0);// set leg to the location of where the foot was
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Instantiates a new mobile base.
	 *
	 * @param configFile the config file
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

	}

	/**
	 * Instantiates a new mobile base.
	 *
	 * @param doc the doc
	 */
	public MobileBase(Element doc) {

		loadConfigs(doc);

	}

	public ParallelGroup getParallelGroup(String name) {
		if (name == null)
			throw new RuntimeException("No groups named null allowed");
		if (getParallelGroups().get(name) == null) {
			getParallelGroups().put(name, new ParallelGroup(name));
		}
		return getParallelGroups().get(name);
	}

	public Set<String> getParallelGroupNames() {
		return getParallelGroups().keySet();
	}

	public ArrayList<DHParameterKinematics> getAllParallelGroups() {
		ArrayList<DHParameterKinematics> list = new ArrayList<DHParameterKinematics>();
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

	public void addLimbToParallel(DHParameterKinematics limb, TransformNR tipOffset, String name, String relativeLimb,
			int relativeIndex) {
		removeLimFromParallel(limb);
		ParallelGroup g = getParallelGroup(name);
		g.addLimb(limb, tipOffset, relativeLimb, relativeIndex);
	}

	private void removeLimFromParallel(DHParameterKinematics limb) {
		ParallelGroup g = getParallelGroup(limb);
		if (g != null) {
			g.removeLimb(limb);
		}
		if (g.getConstituantLimbs().size() == 0) {
			getParallelGroups().remove(g.getNameOfParallelGroup());
		}
	}

	/**
	 * Load configs.
	 *
	 * @param doc the doc
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

		loadVitamins(doc);
		loadLimb(doc, "leg", legs);
		loadLimb(doc, "drivable", drivable);
		loadLimb(doc, "steerable", steerable);
		loadLimb(doc, "appendage", appendages);
		try {
			String massString = getTag(doc, "mass");
			setMassKg(Double.parseDouble(massString));
		} catch (Exception e) {
			e.printStackTrace();
		}

		TransformNR cmcenter = loadTransform("centerOfMassFromCentroid", doc);
		if (cmcenter != null)
			setCenterOfMassFromCentroid(cmcenter);
		TransformNR IMUcenter = loadTransform("imuFromCentroid", doc);
		if (IMUcenter != null)
			setIMUFromCentroid(IMUcenter);
		TransformNR baseToZframe = loadTransform("baseToZframe", doc);
		setRobotToFiducialTransform(baseToZframe);
		
		fireBaseUpdates();
	}

	public void initializeParalellGroups() {
		for (String key : getParallelGroups().keySet()) {
			if (key != null) {
				ParallelGroup g = getParallelGroups().get(key);
				// Clean up broken configurations
				if (g.getConstituantLimbs().size() == 0) {
					getParallelGroups().remove(g.getNameOfParallelGroup());
				} else
					try {
						g.setDesiredTaskSpaceTransform(g.calcHome(), 1.0);
					} catch (Exception e) {
						e.printStackTrace();
					}

			}
		}
	}

	private TransformNR loadTransform(String tagname, Element e) {

		try {
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

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return new TransformNR();
	}

	/**
	 * Gets the name.
	 *
	 * @param e   the e
	 * @param tag the tag
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
	 * @param e   the e
	 * @param tag the tag
	 * @return the name
	 */
	private String getParallelGroup(Element e) {
		return getTag(e, "parallelGroup");
	}
	
	private String findNameTag(Node e) {
		NodeList firstLevelList = e.getChildNodes();
		for(int i=0;i<firstLevelList.getLength();i++) {
			Node tester = firstLevelList.item(i);
			if(tester.getNodeType()!=Node.ELEMENT_NODE)
				continue;
			Element elementTester = (Element)tester;
			if(elementTester.getNodeName().contentEquals("name"))
				return elementTester.getChildNodes().item(0).getNodeValue();
		}
		return null;
	}

	/**
	 * Gets the localTag
	 *
	 * @param e   the e
	 * @param tag the tag
	 * @return the name
	 */
	private String getTag(Element e, String tagname) {
		try {
			String nameOfElement = findNameTag(e);
			if(tagname.contentEquals("name"))
				return nameOfElement;
			//System.out.println("Searching for "+tagname+" in "+nameOfElement);
			NodeList nodListofLinks = e.getElementsByTagName(tagname);
			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				boolean isDirectChild=true;
				Node linkNode = nodListofLinks.item(i);
				Node parentNode = linkNode.getParentNode();
				String parentName = findNameTag(parentNode);
				isDirectChild=nameOfElement.contentEquals(parentName);
				if(!isDirectChild)
					continue;
				if(linkNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				if(!linkNode.getNodeName().contentEquals(tagname))
					continue;
				String nodeValue = linkNode.getChildNodes().item(0).getNodeValue();
				return nodeValue;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Load limb.
	 *
	 * @param doc  the doc
	 * @param tag  the tag
	 * @param list the list
	 */
	private void loadLimb(Element doc, String tag, ArrayList<DHParameterKinematics> list) {
		NodeList nodListofLinks = doc.getChildNodes();
		for (int i = 0; i < nodListofLinks.getLength(); i++) {
			Node linkNode = nodListofLinks.item(i);
			if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals(tag)) {
				Element e = (Element) linkNode;
				final String name = getname(e);
				// System.out.println("Loading arm "+name);
				DHParameterKinematics kin = (DHParameterKinematics) DeviceManager
						.getSpecificDevice(DHParameterKinematics.class, name);
				if (kin == null) {
					kin = new DHParameterKinematics(e);

				}
				kin.setScriptingName(name);
				String parallel = getParallelGroup(e);
				// System.out.println("paralell "+parallel);
				if (parallel != null) {
					System.out.println("Loading Paralell group " + parallel + " limb " + name);
					TransformNR paraOffset = loadTransform("parallelGroupTipOffset", e);
					String relativeName = getTag(e, "relativeTo");
					int index = 0;
					try {
						index = Integer.parseInt(getTag(e, "relativeToLink"));
					} catch (Exception ex) {
						paraOffset = null;
						relativeName = null;
					}
					ParallelGroup parallelGroup = getParallelGroup(parallel);
					parallelGroup.setScriptingName(parallel);
					parallelGroup.setupReferencedLimbStartup(kin, paraOffset, relativeName, index);
//					if(!list.contains(parallelGroup)) {
//						list.add(parallelGroup);
//					}
				}
				// else {
				list.add(kin);
				// }
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
		for (DHParameterKinematics kin : getAllDHChains()) {
			if (!kin.connect()) {
				Log.error("Connection failed!");
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#
	 * inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math. TransformNR)
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
	 * @param doc  the doc
	 * @param tag  the tag
	 * @param list the list
	 */
	private void loadVitamins(Element doc) {
		NodeList nodListofLinks = doc.getChildNodes();
		for (int i = 0; i < nodListofLinks.getLength(); i++) {
			Node linkNode = nodListofLinks.item(i);
			try {
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("vitamins")) {
					getVitamins((Element) linkNode);
				}
			} catch (Exception e) {

			}
		}
	}

	public ArrayList<VitaminLocation> getVitamins() {
		return vitamins;
	}

	/**
	 * Gets the vitamins.
	 *
	 * @param doc the doc
	 */
	private void getVitamins(Element doc) {

		try {
			vitamins = VitaminLocation.getVitamins(doc);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Add a vitamin to this link
	 * 
	 * @param name the name of this vitamin, if the name already exists, the data
	 *             will be overwritten.
	 * @param type the vitamin type, this maps the the json filename
	 * @param id   the part ID, theis maps to the key in the json for the vitamin
	 */
	public void setVitamin(VitaminLocation location) {
		if(vitamins.contains(location))
			return;
		vitamins.add(location);
		
	}
	public void removeVitamin(VitaminLocation loc) {
		if(vitamins.contains(loc))
			vitamins.remove(loc);
		//fireChangeEvent();
	}

	/**
	 * Set a purchasing code for a vitamin
	 * 
	 * @param name      name of vitamin
	 * @param tagValue2 Purchaning code
	 */
	public void setVitaminVariant(String name, String tagValue2) {
		vitaminVariant.put(name, tagValue2);
	}

	/**
	 * Get a purchaing code for a vitamin
	 * 
	 * @param name name of vitamin
	 * @return
	 */
	public String getVitaminVariant(String name) {
		return vitaminVariant.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#getXml()
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

//		String allVitamins = "";
//		for (String key : getVitamins().keySet()) {
//			String v = "\t\t<vitamin>\n";
//			v += "\t\t\t<name>" + key + "</name>\n" + "\t\t\t<type>" + getVitamins().get(key)[0] + "</type>\n"
//					+ "\t\t\t<id>" + getVitamins().get(key)[1] + "</id>\n";
//			if (getVitaminVariant(key) != null) {
//				v += "\t\t\t<variant>" + getVitamins().get(key)[1] + "</variant>\n";
//			}
//			v += "\t\t</vitamin>\n";
//			allVitamins += v;
//		}
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
			xml = makeLimbTag(xml, l);
			xml += "\n</leg>\n";
		}
		for (DHParameterKinematics l : appendages) {
			xml += "<appendage>\n";
			xml = makeLimbTag(xml, l);
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
		xml += VitaminLocation.getAllXML(vitamins);
		xml += "\n</mobilebase>\n";
		setGlobalToFiducialTransform(location);
		return xml;
	}

	private String makeLimbTag(String xml, DHParameterKinematics l) {
		xml += "\n<name>" + l.getScriptingName() + "</name>\n";
		for (String key : getParallelGroups().keySet()) {
			ParallelGroup parallelGroup = getParallelGroups().get(key);
			for (DHParameterKinematics pL : parallelGroup.getConstituantLimbs())

				if (pL == l) {
					xml += "\n<parallelGroup>" + key + "</parallelGroup>\n";
					if (parallelGroup.getTipOffset(l) != null) {
						xml += "\n<parallelGroupTipOffset>\n" + parallelGroup.getTipOffset(l).getXml()
								+ "\n\t<relativeTo>" + parallelGroup.getTipOffsetRelativeName(l) + "</relativeTo>\n"
								+ "\n\t<relativeToLink>" + parallelGroup.getTipOffsetRelativeIndex(l)
								+ "</relativeToLink>\n" + "\n</parallelGroupTipOffset>\n";
					}
				}
		}
		xml += l.getEmbedableXml();
		return xml;
	}
	
	public boolean isWheel(AbstractLink link) {
		ArrayList<DHParameterKinematics> possible= new ArrayList<>();
		possible.addAll(getSteerable());
		possible.addAll(getDrivable());
		for(DHParameterKinematics kin:possible) {
			for(int i=0;i<kin.getNumberOfLinks();i++) {
			
				MobileBase mb = kin.getFollowerMobileBase(i);
				if(mb!=null) {
					if(mb.isWheel(link))
						return true;
				}
			}
			if(kin.getAbstractLink(kin.getNumberOfLinks()-1)==link){
				return true;
			}
		}
		return false;
	}
	
	public boolean isFoot(AbstractLink link) {
		ArrayList<DHParameterKinematics> possible= new ArrayList<>();
		possible.addAll(legs);
		for(DHParameterKinematics kin:possible) {
			for(int i=0;i<kin.getNumberOfLinks();i++) {
				MobileBase mb = kin.getFollowerMobileBase(i);
				if(mb!=null) {
					if(mb.isFoot(link))
						return true;
				}
			}
			if(kin.getAbstractLink(kin.getNumberOfLinks()-1)==link){
				return true;
			}

		}
		return false;
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
	 * @param walkingDriveEngine the new walking drive engine
	 */
	public void setWalkingDriveEngine(IDriveEngine walkingDriveEngine) {
		this.walkingDriveEngine = walkingDriveEngine;
	}

	/**
	 * Drive arc.
	 *
	 * @param newPose the new pose
	 * @param seconds the seconds
	 */
	public void DriveArc(TransformNR newPose, double seconds) {
		getWalkingDriveEngine().DriveArc(this, newPose, seconds);
		updatePositions();
	}

	/**
	 * Drive velocity straight.
	 *
	 * @param cmPerSecond the cm per second
	 */
	public void DriveVelocityStraight(double cmPerSecond) {
		getWalkingDriveEngine().DriveVelocityStraight(this, cmPerSecond);

		updatePositions();
	}

	/**
	 * Drive velocity arc.
	 *
	 * @param degreesPerSecond the degrees per second
	 * @param cmRadius         the cm radius
	 */
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		getWalkingDriveEngine().DriveVelocityArc(this, degreesPerSecond, cmRadius);

		updatePositions();
	}

	/**
	 * Update positions.
	 */
	public void updatePositions() {
		runRenderWrangler();
		fireIOnMobileBaseRenderChange();
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
	 * @param walkingEngine the new walking engine
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
	 * index 0 is GIT url
	 * index 1 is filename
	 *
	 * @param selfSource the new self source
	 */
	public void setGitSelfSource(String[] selfSource) {
		this.selfSource = selfSource;
	}

	public double getMassKg() {
		
		return mass;
	}

	public void setMassKg(double mass) {
		System.out.println("Mass of device " + getScriptingName() + " is " + mass);
		//new RuntimeException().printStackTrace();
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


	
	private void fireBaseUpdates() {
		TransformNR frameToBase = forwardOffset(new TransformNR()); 
		for (DHParameterKinematics l : getAllDHChains()) {
			l.setGlobalToFiducialTransform(frameToBase);
		}
	}

	public void shutDownParallel(ParallelGroup group) {
		group.close();
		parallelGroups.remove(group.getNameOfParallelGroup());
	}

	private HashMap<String, ParallelGroup> getParallelGroups() {
		return parallelGroups;
	}

	@Override
	public boolean connect() {
		super.connect();

		for (DHParameterKinematics kin : this.getAllDHChains()) {
			for (int i = 0; i < kin.getNumberOfLinks(); i++) {
				MobileBase m = kin.getDhLink(i).getSlaveMobileBase();
				if (m != null) {
					m.connect();
				}
			}
		}
		for (DHParameterKinematics kin : getAllDHChains()) {
			addListeners(kin);
		}
		addRegistrationListener(new IRegistrationListenerNR() {
			@Override
			public void onFiducialToGlobalUpdate(AbstractKinematicsNR source, TransformNR regestration) {
				fireBaseUpdates();
			}

			@Override
			public void onBaseToFiducialUpdate(AbstractKinematicsNR source, TransformNR regestration) {
				fireBaseUpdates();
			}
		});
		return isAvailable();
	}

	private void addListeners(DHParameterKinematics kin) {
		for (int i = 0; i < kin.getNumberOfLinks(); i++) {
			kin.addChangeListener(i, this);
			MobileBase m = kin.getDhLink(i).getSlaveMobileBase();
			if (m != null) {
				m.addIOnMobileBaseRenderChange(this);
			}

		}
		kin.addJointSpaceListener(this);
		kin.getFactory().addIHardwareSyncPulseReciver(this);
		
	}

	public static void main(String[] args) throws Exception {
		File f = new File("paralleloutput.xml");

		MobileBase pArm = new MobileBase(new FileInputStream(f));
//		pArm.isAvailable();
//		pArm.connect();
//		pArm.connectDeviceImp();
//		pArm.connectDevice();

		String xmlParsed = pArm.getXml();
		BufferedWriter writer = null;

		writer = new BufferedWriter(new FileWriter("paralleloutput2.xml"));
		writer.write(xmlParsed);

		if (writer != null)
			writer.close();

		ParallelGroup group = pArm.getParallelGroup("ParallelArmGroup");

		TransformNR Tip = group.getCurrentTaskSpaceTransform();

		group.setDesiredTaskSpaceTransform(Tip.copy().translateX(-1), 0);
		for (DHParameterKinematics limb : group.getConstituantLimbs()) {
			TransformNR TipOffset = group.getTipOffset().get(limb);
			TransformNR newTip = limb.getCurrentTaskSpaceTransform().times(TipOffset);

			System.out.println("Expected tip to be " + Tip.getX() + " and got: " + newTip.getX());
			// assertTrue(!Double.isNaN(Tip.getX()));
			// assertEquals(Tip.getX(), newTip.getX(), .1);
		}

	}

	private void fireIOnMobileBaseRenderChange() {
		for (int i = 0; i < changeListeners.size(); i++) {
			IOnMobileBaseRenderChange l = changeListeners.get(i);
			if(l!=null)
				l.onIOnMobileBaseRenderChange();
		}
	}

	public void setHomeProvider(ICalcLimbHomeProvider homeProvider) {
		this.homeProvider = homeProvider;
	}

	public void addIOnMobileBaseRenderChange(IOnMobileBaseRenderChange l) {
		if (changeListeners.contains(l))
			return;
		changeListeners.add(l);
	}

	public void removeIOnMobileBaseRenderChange(IOnMobileBaseRenderChange l) {
		if (changeListeners.contains(l))
			changeListeners.remove(l);
	}

	public void clearIOnMobileBaseRenderChange() {

		changeListeners.clear();
	}

	@Override
	public void event(LinkConfiguration newConf) {
		// TODO Auto-generated method stub
		fireIOnMobileBaseRenderChange();
	}

	@Override
	public void onIOnMobileBaseRenderChange() {
		// TODO Auto-generated method stub
		fireIOnMobileBaseRenderChange();
	}

	@Override
	public void onJointSpaceUpdate(AbstractKinematicsNR source, double[] joints) {
		// TODO Auto-generated method stub
		fireIOnMobileBaseRenderChange();
	}

	@Override
	public void onJointSpaceTargetUpdate(AbstractKinematicsNR source, double[] joints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJointSpaceLimit(AbstractKinematicsNR source, int axis, JointLimit event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sync() {
		doSync();
	}
	@Override
	public  void setTimeProvider(ITimeProvider t) {
		super.setTimeProvider(t);
		for(DHParameterKinematics k:getAllDHChains()) {
			k.setTimeProvider(getTimeProvider());
		}
	}

}
