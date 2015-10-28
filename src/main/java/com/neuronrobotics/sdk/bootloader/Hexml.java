package com.neuronrobotics.sdk.bootloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
 

// TODO: Auto-generated Javadoc
/**
 * The Class Hexml.
 */
public class Hexml {
	
	/** The cores. */
	ArrayList<Core> cores = new ArrayList<Core>();
	
	/** The revision. */
	private String revision="";
	
	/**
	 * Instantiates a new hexml.
	 *
	 * @param hexml the hexml
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Hexml(File hexml) throws ParserConfigurationException, SAXException, IOException{
		/**
		 * sample code from
		 * http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
		 */
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;
	    Document doc = null;
	
		dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(hexml);
		doc.getDocumentElement().normalize();

		////System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		loadRevision(doc);
		//NodeList nList = doc.getElementsByTagName("revision");
		//revision = getTagValue("revision",(Element)nList.item(0));
		////System.out.println("Revision is:"+revision);
		NodeList nList = doc.getElementsByTagName("core");
		for (int temp = 0; temp < nList.getLength(); temp++) {
		    Node nNode = nList.item(temp);	    
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element) nNode;
			    int index = Integer.parseInt(getTagValue("index",eElement));
			    //int word = Integer.parseInt(getTagValue("wordSize",eElement));
			    NRBootCoreType type = NRBootCoreType.find(getTagValue("type",eElement));
			    if (type == null) {
			    	System.err.println("Failed to get a core type for: "+getTagValue("type",eElement));
			    	continue;
			    }
			    String hexFile = getTagValue("hex",eElement);
			    ArrayList<hexLine> lines=new ArrayList<hexLine>();
			    String[] tokens = hexFile.split("\n");
			    for (int i=0;i<tokens.length;i++){
			    	try {
						lines.add(new hexLine(tokens[i]));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			    Core tmp = new Core(index, lines, type);
			    ////System.out.println("Adding new core: "+tmp);
			    cores.add(tmp);
		    }
		 }


	}
	
	/**
	 * Load revision.
	 *
	 * @param doc the doc
	 */
	private void loadRevision(Document doc) {
		try{
				NodeList nlList= doc.getElementsByTagName("revision").item(0).getChildNodes();
				Node nValue = (Node) nlList.item(0);
				revision = nValue.getNodeValue();
		}catch(NullPointerException e){
			revision = "0.0.0";
		}
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
	    return nValue.getNodeValue();    
	}

	/**
	 * Gets the cores.
	 *
	 * @return the cores
	 */
	public ArrayList<Core> getCores(){
		return cores;
	}
	
	/**
	 * Gets the revision.
	 *
	 * @return the revision
	 */
	public String getRevision() {
		return revision;
	}
}
