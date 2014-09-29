package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.serial.SerialConnection;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class BcsCoreTest {

	@Test
	public void testBcsCore() {
		Log.enableInfoPrint();
		List<String> connections = SerialConnection.getAvailableSerialPorts();
		if(connections.size() ==0)
			fail();
		GenericDevice device = new GenericDevice(new SerialConnection(connections.get(0)));
		if(!device.connect()){
			fail();
		}
		if(!device.ping()  ){
			fail();
		}
		ArrayList<String> namespaces = device.getNamespaces();
		
		if(namespaces.size()==0){
			fail();
		}
		for(String s:namespaces){
			 ArrayList<RpcEncapsulation> rpcs = device.getRpcList(s);
			 System.out.println(s);
			 if(rpcs == null)
				 fail();
			 for(RpcEncapsulation enc : rpcs){
				 System.out.println(enc);
			 }
			 
		}
		
		ThreadUtil.wait(2000);
		device.disconnect();
		
	}

}
