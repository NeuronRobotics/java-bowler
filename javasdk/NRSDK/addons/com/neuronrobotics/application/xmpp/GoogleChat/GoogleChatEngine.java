package com.neuronrobotics.application.xmpp.GoogleChat;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smack.packet.Message;

import org.jivesoftware.smack.packet.Presence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.neuronrobotics.application.xmpp.IConversationFactory;




public class GoogleChatEngine implements ChatManagerListener {
	
	private static String username = "user@gmail.com";
	private static String password = "pass1234";
	
	
	private static String host = "talk.google.com";
	private static String service = "gmail.com";
	private static int port = 5222;
	
	private ConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private Presence presence;
	private ChatManager chatmanager;
	ArrayList<GoogleChat> googleChats = new ArrayList<GoogleChat> ();
	private IConversationFactory responder;
	public GoogleChatEngine(IConversationFactory responder,InputStream config) throws XMPPException{
		this.responder = responder;
		if((MessageListener.class.isInstance(responder)))
			throw new RuntimeException("Instance of IConversationFactory must also implement org.jivesoftware.smack.MessageListener");
		connConfig = new ConnectionConfiguration(host, port, service);
        connection = new XMPPConnection(connConfig);
        connection.connect();
        setLoginInfo(config);
        connection.login(username, password);
        presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);
        chatmanager = connection.getChatManager();
        chatmanager.addChatListener(this);
	}
	private void setLoginInfo(InputStream config) {
		//InputStream config = GoogleChatEngine.class.getResourceAsStream("loginInfo.xml");
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
		//System.out.println("Parsing File...");
		NodeList nList = doc.getElementsByTagName("login");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			//System.out.println("Leg # "+temp);
			Element eElement = (Element)nList.item(temp);
			username = getTagValue("username",eElement);
	    	password = getTagValue("password",eElement);
	    	
		}
	}
	
	public static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    //System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
	private MessageListener getNewMessageListener(){
		return (MessageListener)responder.getConversation();
	}
	@Override
	public void chatCreated(Chat arg0, boolean arg1) {
		// TODO Auto-generated method stub
		arg0.addMessageListener( getNewMessageListener());
		googleChats.add(new GoogleChat(arg0));
	}
	
	public GoogleChat startChat(String user){
        Chat chat = chatmanager.createChat(user,getNewMessageListener());
        GoogleChat c = new GoogleChat(chat);
        googleChats.add(c);
		return c;
	}
	public GoogleChat startChat(String user, MessageListener listener){
        Chat chat = chatmanager.createChat(user, listener);
        GoogleChat c = new GoogleChat(chat);
        googleChats.add(c);
		return c;
	}
	
	public ArrayList<GoogleChat> getChats(){
		ArrayList<GoogleChat> tmp = new ArrayList<GoogleChat>();
		for(GoogleChat c:googleChats){
			if(c!=null && c.isAlive() )
				tmp.add(c);
		}
		googleChats=tmp;
		tmp = new ArrayList<GoogleChat>();
		for(GoogleChat c:googleChats){
			if(c!=null && c.isAlive() )
				tmp.add(c);
		}
		return tmp;
	}
	
	public void disconnect(){
		connection.disconnect();
	}

}
