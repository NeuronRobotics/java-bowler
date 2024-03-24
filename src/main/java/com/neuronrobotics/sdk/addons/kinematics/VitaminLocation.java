package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;

public class VitaminLocation {

	private String name;
	private String type;
	private String size;
	private TransformNR location;

	public VitaminLocation(String name, String type, String size, TransformNR location) {
		this.setName(name);
		this.setType(type);
		this.setSize(size);
		this.setLocation(location);
	}

	public VitaminLocation(Element vitamins) {
		setName(XmlFactory.getTagValue("name", vitamins));
		setType(XmlFactory.getTagValue("type", vitamins));
		setSize(XmlFactory.getTagValue("id", vitamins));

		NodeList nodListofLinks = vitamins.getChildNodes();
		TransformNR tf=null;
		for (int i = 0; i < nodListofLinks.getLength(); i++) {
			Node linkNode = nodListofLinks.item(i);
			if(linkNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element eElement = (Element) linkNode;
			if(linkNode.getNodeName().contentEquals("pose")) {
				tf=XmlFactory.getTransform(eElement);
			}
		}
		if(tf==null)
			tf=new TransformNR();
		setLocation(tf);
				
	}
	
	public String getXML() {
		
		return "\n<vitamin>\n"+
				"<name>"+name+"</name>\n"+
				"<type>"+type+"</type>\n"+
				"<size>"+size+"</size>\n"+
				"<pose>"+location.getXml()+"</pose>\n"+
		"</vitamin>\n"
		;
	}
	public static ArrayList<VitaminLocation> getVitamins(Element doc) {
		ArrayList<VitaminLocation> locations = new ArrayList<>();
		try {
			NodeList nodListofLinks = doc.getChildNodes();
			for (int i = 0; i < nodListofLinks.getLength(); i++) {
				Node linkNode = nodListofLinks.item(i);
				if (linkNode.getNodeType() == Node.ELEMENT_NODE && linkNode.getNodeName().contentEquals("vitamin")) {
					Element e = (Element) linkNode;
					locations.add(new VitaminLocation(e));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return locations;
	}
	
	public static String getAllXML(ArrayList<VitaminLocation> list) {
		
		String vitamins="\n<vitamins>\n";
		for(VitaminLocation loc:list) {
			vitamins+=loc.getXML();
		}
		return vitamins+"\n</vitamins>\n";
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if (name==null)
			throw new RuntimeException("Name can not be null");
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {

		if (type==null)
			throw new RuntimeException("type can not be null");
		this.type = type;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		if (size==null)
			throw new RuntimeException("size can not be null");
		this.size = size;
	}

	/**
	 * @return the location
	 */
	public TransformNR getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(TransformNR location) {
		if (location==null)
			throw new RuntimeException("location can not be null");
		this.location = location;
	}

}
