package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import java.lang.reflect.GenericDeclaration;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class BcsCoreTest {

	@Test
	public void testBcsCore() {
		Log.enableInfoPrint(true);
		List<String> connections = SerialConnection.getAvailableSerialPorts();
		if(connections.size() ==0)
			fail();
		GenericDevice device = new GenericDevice(new SerialConnection(connections.get(0)));
		if(!device.connect()){
			fail();
		}
		if(device.ping() == null ){
			fail();
		}
		
	}

}
