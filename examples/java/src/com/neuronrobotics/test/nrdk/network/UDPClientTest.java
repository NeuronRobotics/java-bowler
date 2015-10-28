package com.neuronrobotics.test.nrdk.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.Log;
//import com.neuronrobotics.sdk.network.BowlerTCPServer;
import com.neuronrobotics.sdk.network.UDPBowlerConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class UDPClientTest.
 */
public class UDPClientTest extends BowlerAbstractDevice implements IBowlerDatagramListener{
	
	/** The clnt. */
	UDPBowlerConnection clnt;
	
	/**
	 * Instantiates a new UDP client test.
	 */
	public UDPClientTest(){
		Log.enableInfoPrint();
		clnt=new UDPBowlerConnection();
		
//		ArrayList<InetAddress>  addrs = clnt.getAllAddresses();
//		System.out.println("Availiable servers: "+addrs);
//		if (addrs.size()==0)
//			throw new RuntimeException();
//		clnt.setAddress(addrs.get(0));
		try {
			clnt.setAddress(InetAddress.getByName("192.168.1.10"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		setConnection(clnt);
		connect();
		
		System.out.println("Pinging");
		long start = System.currentTimeMillis();
		int numPings=10;
		for(int i=0;i<numPings;i++)
			if (!ping())
				throw new RuntimeException("Ping failed!");
		System.out.println("Ping average = "+(System.currentTimeMillis()-start)/numPings+"ms");
		clnt.disconnect();
		System.out.println("done");
		System.exit(0);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#onAllResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String [] args){
		try{
			new UDPClientTest();
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("###Client Failed out!");
			System.exit(1);
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#isAvailable()
	 */
	@Override
	public boolean isAvailable() throws InvalidConnectionException {
		// TODO Auto-generated method stub
		return clnt.isConnected();
	}
}
