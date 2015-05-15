package com.neuronrobotics.sdk.addons.kinematics.xml;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlFactory {
	
	public static InputStream getDefaultConfigurationStream(String file) {
		return XmlFactory.class.getResourceAsStream(file);
	}
	public static Document getAllNodesDocument(InputStream config) {
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
	    return doc;
	}
	
	
	public static NodeList getAllNodesFromTag(String sTag, InputStream config){
		Document doc =getAllNodesDocument(config);
		//Parsing XML File and store in LinkConfiguration
		return doc.getElementsByTagName(sTag);
	}
	
	public static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	   // System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
	public static Double getTagValueDouble(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	   // System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return Double.parseDouble(nValue.getNodeValue());    
	}
}

