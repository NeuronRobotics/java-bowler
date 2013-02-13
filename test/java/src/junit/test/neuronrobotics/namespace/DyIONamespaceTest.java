package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DyIONamespaceTest {

	@Test
	public void test() {

		DyIO dyio=new DyIO();
		
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		dyio.enableDebug();
		
		dyio.getRevisions();
		
		String name = dyio.getInfo();
		String setName ="My DyIO";
		
		dyio.setInfo(setName);
		
		String newName = dyio.getInfo();
		
		dyio.disableDebug();
		dyio.setInfo(name);
		dyio.enableDebug();
		
		double volts = dyio.getBatteryVoltage(true);
		
		dyio.setServoPowerSafeMode(true);
	
		assertTrue(setName.contains(newName));
		assertTrue(name.contains(dyio.getInfo()));
		
		dyio.disconnect();
		
	}

}
