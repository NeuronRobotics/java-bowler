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
import org.jivesoftware.smack.packet.Presence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.neuronrobotics.application.xmpp.IConversationFactory;




// TODO: Auto-generated Javadoc
/**
 * The Class GoogleChatEngine.
 */
public class GoogleChatEngine implements ChatManagerListener {
	
	/** The username. */
	private static String username = "user@gmail.com";
	
	/** The password. */
	private static String password = "pass1234";
	
	
	/** The host. */
	private static String host = "talk.google.com";
	
	/** The service. */
	private static String service = "gmail.com";
	
	/** The port. */
	private static int port = 5222;
	
	/** The conn config. */
	private ConnectionConfiguration connConfig;
	
	/** The connection. */
	private XMPPConnection connection;
	
	/** The presence. */
	private Presence presence;
	
	/** The chatmanager. */
	private ChatManager chatmanager;
	
	/** The google chats. */
	ArrayList<GoogleChat> googleChats = new ArrayList<GoogleChat> ();
	
	/** The responder. */
	private IConversationFactory responder;
	
	/**
	 * Instantiates a new google chat engine.
	 *
	 * @param responder the responder
	 * @param user the user
	 * @param pass the pass
	 * @throws XMPPException the XMPP exception
	 */
	public GoogleChatEngine(IConversationFactory responder,String user,String pass) throws XMPPException {
		username=user;
        password=pass;
        setup(responder);
	}
	
	/**
	 * Instantiates a new google chat engine.
	 *
	 * @param responder the responder
	 * @param config the config
	 * @throws XMPPException the XMPP exception
	 */
	public GoogleChatEngine(IConversationFactory responder,InputStream config) throws XMPPException {
        setLoginInfo(config);
        setup(responder);
	}
	
	/**
	 * Sets the up.
	 *
	 * @param responder the new up
	 * @throws XMPPException the XMPP exception
	 */
	private void setup(IConversationFactory responder) throws XMPPException {
		this.responder=responder;
		if((MessageListener.class.isInstance(responder)))
			throw new RuntimeException("Instance of IConversationFactory must also implement org.jivesoftware.smack.MessageListener");
		connConfig = new ConnectionConfiguration(host, port, service);
        connection = new XMPPConnection(connConfig);
        connection.connect();
        connection.login(username, password);
        presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);
        chatmanager = connection.getChatManager();
        chatmanager.addChatListener(this);
	}
	
	/**
	 * Sets the login info.
	 *
	 * @param config the new login info
	 */
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
	
	/**
	 * Gets the tag value.
	 *
	 * @param sTag the s tag
	 * @param eElement the e element
	 * @return the tag value
	 */
	public static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    //System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
	
	/**
	 * Gets the new message listener.
	 *
	 * @return the new message listener
	 */
	private MessageListener getNewMessageListener(){
		return (MessageListener)responder.getConversation();
	}
	
	/* (non-Javadoc)
	 * @see org.jivesoftware.smack.ChatManagerListener#chatCreated(org.jivesoftware.smack.Chat, boolean)
	 */
	@Override
	public void chatCreated(Chat arg0, boolean arg1) {
		// TODO Auto-generated method stub
		arg0.addMessageListener( getNewMessageListener());
		googleChats.add(new GoogleChat(arg0));
	}
	
	/**
	 * Start chat.
	 *
	 * @param user the user
	 * @return the google chat
	 */
	public GoogleChat startChat(String user){
        Chat chat = chatmanager.createChat(user,getNewMessageListener());
        GoogleChat c = new GoogleChat(chat);
        googleChats.add(c);
		return c;
	}
	
	/**
	 * Start chat.
	 *
	 * @param user the user
	 * @param listener the listener
	 * @return the google chat
	 */
	public GoogleChat startChat(String user, MessageListener listener){
        Chat chat = chatmanager.createChat(user, listener);
        GoogleChat c = new GoogleChat(chat);
        googleChats.add(c);
		return c;
	}
	
	/**
	 * Gets the chats.
	 *
	 * @return the chats
	 */
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
	
	/**
	 * Disconnect.
	 */
	public void disconnect(){
		connection.disconnect();
	}

	

}
