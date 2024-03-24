package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.addons.kinematics.math.ITransformNRChangeListener;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;

public class VitaminLocation implements ITransformNRChangeListener {
	ArrayList<Runnable> listeners=new  ArrayList<>();

	private String name;
	private String type;
	private String size;
	private TransformNR location=null;

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
	
	public void addChangeListener(Runnable r) {
		if(listeners.contains(r))
			return;
		listeners.add(r);
	}
	public void removeChangeListener(Runnable r) {
		if(listeners.contains(r))
			listeners.remove(r);
	}
	void fireChangeEvent() {
		if (listeners != null) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					listeners.get(i).run();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	
	}
	public String getXML() {
		
		return "\n<vitamin>\n"+
				"<name>"+name+"</name>\n"+
				"<type>"+type+"</type>\n"+
				"<id>"+size+"</id>\n"+
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
		fireChangeEvent();
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
		fireChangeEvent();
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
		fireChangeEvent();
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
	public void setLocation(TransformNR l) {
		if (location==null)
			throw new RuntimeException("location can not be null");
		if(l!=null)
			l.removeChangeListener(this);
		this.location = l;
		location.addChangeListener(this);
	}

	@Override
	public void event(TransformNR changed) {
		fireChangeEvent();
	}

}
