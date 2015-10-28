package com.neuronrobotics.sdk.common;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.network.BowlerTCPClient;
import com.neuronrobotics.sdk.network.UDPBowlerConnection;


// TODO: Auto-generated Javadoc
/**
 * The Class ConfigManager.
 */
public class ConfigManager {
	
	/**
	 * Load default connection.
	 *
	 * @param filename the filename
	 * @return the bowler abstract connection
	 */
	public static BowlerAbstractConnection loadDefaultConnection(String filename) {
		try {
			
			File file = new File(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("connection");
			
			for (int s = 0; s < nodeLst.getLength(); s++) {
	
				Node fstNode = nodeLst.item(s);
	
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
	
					Element e = (Element) fstNode;
					
					String type = getElementValue(e, "type", "");
					String port = getElementValue(e, "port", "");
					String baud = getElementValue(e, "baud", "0");
					String host = getElementValue(e, "host", "127.0.0.1");
					
					if(type.equalsIgnoreCase("serial")) {
						//return new SerialConnection(port, Integer.parseInt(baud));
					}
					
					if(type.equalsIgnoreCase("udp")) {
						return new UDPBowlerConnection(Integer.parseInt(port));
					}
					
					if(type.equalsIgnoreCase("tcp")) {
						return new BowlerTCPClient(host, Integer.parseInt(port));
					}
				}
	
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Gets the element value.
	 *
	 * @param e the e
	 * @param key the key
	 * @param dval the dval
	 * @return the element value
	 */
	private static String getElementValue(Element e, String key, String dval) {
		NodeList elmntLst = e.getElementsByTagName(key);
		if(elmntLst.getLength() > 0) {
			Element elmnt = (Element) elmntLst.item(0);
			NodeList value = elmnt.getChildNodes();
			 ((Node) value.item(0)).getNodeValue();
			 return ((Node) value.item(0)).getNodeValue();
		}
		return dval;
	}
}
