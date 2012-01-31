package com.neuronrobotics.sdk.addons.kinematics.xml;
import java.io.InputStream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlFactory {
	
	public static InputStream getDefaultConfigurationStream(String file) {
		return XmlFactory.class.getResourceAsStream(file);
	}
	public static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	   // System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
}

