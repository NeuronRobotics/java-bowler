package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DyIONamespaceTest {
	
	@Before
	public void setUp() throws Exception {
		if(!DyIORegestry.get().isAvailable()){
			System.out.println("DyIO test setting up DyIO");
			DyIO.disableFWCheck();

			Log.enableInfoPrint();
			if(ConnectionDialog.getBowlerDevice(DyIORegestry.get())){
				return;
			}
			PIDNamespaceTest.setPid(DyIORegestry.get().getPid());
		}else{
			PIDNamespaceTest.setPid(DyIORegestry.get().getPid());
			return;
		}
		fail("No device availible");
	}

	@Test
	public void dyioNamespaceTest() {
		DyIO dyio= DyIORegestry.get();
		if(!dyio.isAvailable())
			fail();
		assertTrue(DyIORegestry.get().hasNamespace("neuronrobotics.dyio.*"));
		
		
		dyio.getRevisions();
		
		String name = dyio.getInfo();
		String setName ="My DyIO";
		
		dyio.setInfo(setName);
		
		String newName = dyio.getInfo();
		

		dyio.setInfo(name);
		
		
		double volts = dyio.getBatteryVoltage(true);
		
		dyio.setServoPowerSafeMode(true);
	
		assertTrue(setName.contains(newName));
		assertTrue(name.contains(dyio.getInfo()));
		assertTrue(dyio.ping() );
		
		ArrayList<DyIOChannelMode> modes = dyio.getAllChannelModes();
		for(int i=0;i<modes.size();i++){
			
			if(modes.get(i)==DyIOChannelMode.DIGITAL_IN){
				modes.set(i, DyIOChannelMode.DIGITAL_OUT);
			}else{
				modes.set(i, DyIOChannelMode.DIGITAL_IN);
			}
			dyio.setMode(i, modes.get(i));
		}
		
		ArrayList<DyIOChannelMode> modesAfter = dyio.getAllChannelModes();
		for(int i=0;i<modes.size();i++){
			assertTrue(modes.get(i)==modesAfter.get(i));
			assertTrue(modes.get(i)==dyio.getMode(i));
		}
		
		
	}

}
