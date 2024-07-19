package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.annotations.Expose;
import com.neuronrobotics.sdk.addons.kinematics.math.ITransformNRChangeListener;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;

public class VitaminLocation implements ITransformNRChangeListener {
	@Expose (serialize = false, deserialize = false)
	ArrayList<Runnable> listeners=new  ArrayList<>();

	private String name;
	private String type;
	private String size;
	private TransformNR location=null;
	private boolean isScript =false;

	private VitaminFrame frame=VitaminFrame.DefaultFrame;
//	public VitaminLocation() {
//		this.setName("NO NAME");
//		this.setType("NO TYPE");
//		this.setSize("NO SIZE");
//		this.setLocation(new TransformNR());
//	}
	@Deprecated
	public VitaminLocation(String name, String type, String size, TransformNR location) {
		this(false,name,type,size,location);
		new RuntimeException("@Deprecated, please specifiy if this is a script, assuming it is not for now").printStackTrace();
	}
	@Deprecated
	public VitaminLocation(String name, String type, String size, TransformNR location,IVitaminHolder h) {
		this(false,name,type,size,location,h);
		new RuntimeException("@Deprecated, please specifiy if this is a script, assuming it is not for now").printStackTrace();
	}
	public VitaminLocation(boolean isScript,String name, String type, String size, TransformNR location) {
		this.setName(name);
		this.setType(type);
		this.setSize(size);
		this.setLocation(location);
		setScript(isScript);
	}
	public VitaminLocation(boolean isScript,String name, String type, String size, TransformNR location,IVitaminHolder h) {
		this(isScript,name,type,size,location);
		try {
			h.addVitamin(this);
		}catch(Throwable t){
			System.out.println("Vitamin "+name+" exists in "+h);
		}
	}
	public VitaminLocation(Element vitamins) {
		setName(XmlFactory.getTagValue("name", vitamins));
		setType(XmlFactory.getTagValue("type", vitamins));
		setSize(XmlFactory.getTagValue("id", vitamins));
		String scriptyness=null;
		try {
			scriptyness=XmlFactory.getTagValue("script", vitamins);
		}catch(Exception ex) {}//ignore
		if(scriptyness==null) {
			isScript=false;
		}else{
			setScript(Boolean.parseBoolean(scriptyness));
		}
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
		try {
			setFrame(VitaminFrame.fromString( XmlFactory.getTagValue("frame", vitamins)));
		}catch(NullPointerException ex) {
			if(name.contentEquals("electroMechanical")) {
				setFrame(VitaminFrame.previousLinkTip);
			}
			if(name.contentEquals("shaft")) {
				setFrame(VitaminFrame.LinkOrigin);
			}
		}
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
		
		return "\n\t\t<vitamin>\n"+
				"\t\t\t<name>"+name+"</name>\n"+
				"\t\t\t<type>"+type+"</type>\n"+
				"\t\t\t<id>"+size+"</id>\n"+
				"\t\t\t<pose>"+location.getXml()+"\t\t\t</pose>\n"+
				"\t\t\t<frame>"+getFrame().getText()+"</frame>\n"+
				"\t\t\t<script>"+isScript()+"</script>\n"+
		"\t\t</vitamin>\n"
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
		
		String vitamins="\n\t<vitamins>\n";
		for(VitaminLocation loc:list) {
			vitamins+=loc.getXML();
		}
		return vitamins+"\n\t</vitamins>\n";
		
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
		if(l==location) {
			fireChangeEvent();
			return;
		}			
		if (l==null)
			throw new RuntimeException("location can not be null");
		if(l!=null)
			l.removeChangeListener(this);
		this.location = l;
		location.addChangeListener(this);
		fireChangeEvent();
	}

	@Override
	public void event(TransformNR changed) {
		fireChangeEvent();
	}

	/**
	 * 	
	// the MobilBase root, or the tip of the link
	DefaultFrame("default"), 
	// the place on the link where the previous one ends, where the shaft for the motor that turns it should be
	LinkOrigin("origin"), 
	// The tip of the previous link. the place where the motor that turns a link would be mounted. if the first link this would be the limbs root
	LastLinkTip("lastlink");
	 * @return the frame
	 */
	public VitaminFrame getFrame() {
		if(frame==null)
			return VitaminFrame.DefaultFrame;
		return frame;
	}

	/**
	// the MobilBase root, or the tip of the link
	DefaultFrame("default"), 
	// the place on the link where the previous one ends, where the shaft for the motor that turns it should be
	LinkOrigin("origin"), 
	// The tip of the previous link. the place where the motor that turns a link would be mounted. if the first link this would be the limbs root
	LastLinkTip("lastlink");
	 * @param frame the frame to set
	 */
	public void setFrame(VitaminFrame frame) {
		if(frame==null)
			throw new NullPointerException("Frame can not be null");
		this.frame = frame;
		fireChangeEvent();

	}
	public boolean isScript() {
		return isScript;
	}
	public void setScript(boolean isScript) {
		this.isScript = isScript;
		fireChangeEvent();
	}

}
