package com.neuronrobotics.sdk.addons.walker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkType;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOPowerState;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;


// TODO: Auto-generated Javadoc
/**
 * The Class BasicWalker.
 */
public class BasicWalker {
	
	/** The legs. */
	private ArrayList<Leg> legs=new ArrayList<Leg>();
	
	/** The link len. */
	private double scale=0,inverse=0,linkLen = 0;
	
	/** The theta. */
	private double x,y,theta;
	
	/** The channel. */
	private int    llimit,ulimit,home,channel;
	
	/** The dyio. */
	private DyIO dyio;
	
	/** The use hardware. */
	private boolean useHardware = true;
	
	/**
	 * Instantiates a new basic walker.
	 *
	 * @param d the d
	 */
	public BasicWalker(DyIO d) {
		setDyio(d);
		getDyio().setCachedMode(true);
		System.out.println("Loading default configuration");
		parse(BasicWalkerConfig.getDefaultConfigurationStream());
	}
	
	/**
	 * Adds the leg.
	 *
	 * @param x the x
	 * @param y the y
	 * @param theta the theta
	 * @param links the links
	 */
	public void addLeg(double x, double y, double theta,ArrayList<WalkerServoLink> links) {
		Leg tmpLeg = new Leg(x,y,theta);
		for(WalkerServoLink l:links) {
			 tmpLeg.addLink(l);
		}
		legs.add(tmpLeg);
	}
	
	/**
	 * Instantiates a new basic walker.
	 *
	 * @param f the f
	 * @param d the d
	 */
	public BasicWalker(File f,DyIO d){
		//useHardware = false;
		if(useHardware){
			setDyio(d);
		}
		getDyio().setCachedMode(true);
		parse(f);
	}
	
	/**
	 * Instantiates a new basic walker.
	 *
	 * @param is the is
	 * @param d the d
	 */
	public BasicWalker(InputStream is,DyIO d){
		//useHardware = false;
		if(useHardware){
			setDyio(d);
		}
		getDyio().setCachedMode(true);
		parse(is);
	}
	
	/**
	 * Parses the.
	 *
	 * @param f the f
	 */
	private void parse(File f) {
		InputStream is = null;
		try {
			is= new FileInputStream(f);
		}
		catch(IOException e) {
			System.err.println("Error Writing/Reading Streams.");
		}
		if(is!=null)
			parse(is);
	}
	
	/**
	 * Parses the.
	 *
	 * @param is the is
	 */
	private void parse(InputStream is) {
		/**
		 * sample code from
		 * http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
		 */
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;
	    Document doc = null;
	    try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//System.out.println("Parsing File...");
		NodeList nList = doc.getElementsByTagName("leg");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			//System.out.println("Leg # "+temp);
		    Node nNode = nList.item(temp);
		    ArrayList<WalkerServoLink> legLinks = new ArrayList<WalkerServoLink>();
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element) nNode;
		    	x = Double.parseDouble(getTagValue("x",eElement));
		    	y = Double.parseDouble(getTagValue("y",eElement));
		    	theta = Double.parseDouble(getTagValue("theta",eElement));
		    	//Leg tmpLeg = new Leg(x,y,theta);
		    	
		    	NodeList links = eElement.getElementsByTagName("link");
		    	for (int i = 0; i < links.getLength(); i++) {
		    		//System.out.println("\tLink # "+i);
		    		Node lNode = links.item(i);
		    		if (lNode.getNodeType() == Node.ELEMENT_NODE) {
			    		Element lElement = (Element) lNode;
			    		llimit=Integer.parseInt(getTagValue("llimit",lElement));
			    		ulimit=Integer.parseInt(getTagValue("ulimit",lElement));
			    		home=Integer.parseInt(getTagValue("home",lElement));
			    		channel=Integer.parseInt(getTagValue("channel",lElement));
			    		inverse=Double.parseDouble(getTagValue("inverse",lElement));
			    		scale = Double.parseDouble(getTagValue("scale",lElement));
			    		linkLen = Double.parseDouble(getTagValue("linkLen",lElement));
			    		String type = getTagValue("type",lElement);
			    		if(useHardware){
				    		ServoChannel srv = new ServoChannel(getDyio().getChannel(channel));
				    		WalkerServoLink tmpLink = new WalkerServoLink(srv,new LinkConfiguration(home,llimit,ulimit,(scale*inverse)),linkLen,type);
				
				    		legLinks.add(tmpLink);
			    		}
		    		}
		    	}
		    	addLeg(x,y,theta,legLinks);
		    }else{
		    	//System.out.println("Not Element Node");
		    }
		}
		System.out.println("Populated Hexapod.");
	}
	
	/**
	 * Load home values from dy io.
	 */
	public void loadHomeValuesFromDyIO() {
		for(Leg l:legs) {
			l.loadHomeValuesFromDyIO();
			l.save();
		}
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	public String getXML() {
		String s="<hexapod>\n";
		for(Leg l:legs) {
			s+=l.getLegXML();
		}
		s+="\n</hexapod>";
		return s;
	}
	
	/**
	 * Write xml.
	 *
	 * @param f the f
	 */
	public void writeXML(File f) {
		writeXML(f,getXML());
	}
	
	/**
	 * Write xml.
	 *
	 * @param f the f
	 * @param xml the xml
	 */
	public void writeXML(File f,String xml) {
	    try {
	    	Writer output = new BufferedWriter(new FileWriter(f));
			output.write(xml);
		    output.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Initialize.
	 */
	public void initialize() {
		int leg=0;
		for (Leg l:legs){
			double hipStart=0.0-l.getThetaOffset();
			if(hipStart>90)
				hipStart-=180;
			if(hipStart<-90)
				hipStart+=180;	

			//System.out.println("Leg : "+leg+" is set to : "+hipStart);
			l.setHip(hipStart);
			l.setKnee(0);
			l.setAnkle(-90);
			l.setStartPoint();
			leg++;
		}
		updateAllServos((float) .5);
		try {Thread.sleep(2000);} catch (InterruptedException e) {}
	}
	
	/**
	 * Home.
	 */
	public void Home() {
		for (Leg l:legs){
			l.Home();
		}
		getDyio().flushCache(2);
	}
	
	/**
	 * Save.
	 */
	public void save() {
		for (Leg l:legs){
			l.save();
		}
	}
	
	/**
	 * Turn body.
	 *
	 * @param degrees the degrees
	 * @param time the time
	 */
	public void turnBody(double degrees,double time) {
		degrees*=-1;
		for (Leg l:legs){
			l.turn(degrees);
		}
		updateAllServos((float) time);
		fixAll(time);
	}
	
	/**
	 * Increment all y.
	 *
	 * @param inc the inc
	 * @param time the time
	 */
	public void incrementAllY(double inc,double time) {
		inc*=-1;
		for (Leg l:legs){
			l.incrementY(inc);
		}
		updateAllServos((float) time);
		fixAll(time);
	}
	
	/**
	 * Increment all x.
	 *
	 * @param inc the inc
	 * @param time the time
	 */
	public void incrementAllX(double inc,double time) {
		inc*=-1;
		for (Leg l:legs){
			l.incrementX(inc);
		}
		updateAllServos((float) time);
		fixAll(time);
	}

	/**
	 * Increment all z.
	 *
	 * @param inc the inc
	 * @param time the time
	 */
	public void incrementAllZ(double inc,double time) {
		inc*=-1;
		for (Leg l:legs){
			l.incrementZ(inc);
		}
		updateAllServos((float) time);
		fixAll(time);
	}
	
	/**
	 * Gets the legs.
	 *
	 * @return the legs
	 */
	public ArrayList<Leg> getLegs(){
		return legs;
	}
	
	/**
	 * Fix all.
	 *
	 * @param time the time
	 */
	public void fixAll(double time) {
		for (Leg l:legs){
			l.fix();
		}
		//updateAllServos((float) time);
	}
	
	/**
	 * Update all servos.
	 *
	 * @param time the time
	 */
	public void updateAllServos(double time) {
		for (Leg l:legs){
			l.cacheLinkPositions();
		}
		getDyio().flushCache((float) time);
	}
	
	/**
	 * Gets the tag value.
	 *
	 * @param sTag the s tag
	 * @param eElement the e element
	 * @return the tag value
	 */
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    //System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
	
	/**
	 * Disconnect.
	 */
	public void disconnect() {
		getDyio().disconnect();
	}
	
	/**
	 * Sets the dyio.
	 *
	 * @param dyio the new dyio
	 */
	private void setDyio(DyIO dyio) {
		if(((dyio.getBankAState()==DyIOPowerState.REGULATED) || (dyio.getBankBState()==DyIOPowerState.REGULATED))){
			System.err.println("Invalid Power Switch configuration!");
			throw new RuntimeException("Invalid Power Switch configuration for hexapod!");
		}
		dyio.setServoPowerSafeMode(false);
		this.dyio = dyio;
	}
	
	/**
	 * Gets the dyio.
	 *
	 * @return the dyio
	 */
	private DyIO getDyio() {
		return dyio;
	}

}
