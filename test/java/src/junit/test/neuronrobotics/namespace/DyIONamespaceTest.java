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
	
	@Test public void DyIONameTest(){
		DyIO dyio= DyIORegestry.get();
		if(!dyio.isAvailable())
			fail();
		
		String name = dyio.getInfo();
		
		String setName;
		if(name.contains("My DyIO"))
			setName="My DyIO2";
		else
			setName="My DyIO";
		
		dyio.setInfo(setName);
		
		String newName = dyio.getInfo();
		

		dyio.setInfo(name);
	
		assertTrue(setName.contains(newName));
		assertTrue(name.contains(dyio.getInfo()));
		assertTrue(dyio.ping() );

	}
	
	@Test public void DyIOPowerTest(){
		DyIO dyio= DyIORegestry.get();
		if(!dyio.isAvailable())
			fail();
		assertTrue(DyIORegestry.get().hasNamespace("neuronrobotics.dyio.*"));
		
		double volts = dyio.getBatteryVoltage(true);
		
		dyio.setServoPowerSafeMode(true);


	}
	
	@Test public void DyIOModesTest(){
		DyIO dyio= DyIORegestry.get();
		if(!dyio.isAvailable())
			fail();

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

	@Test
	public void dyioNamespaceTest() {
		DyIO dyio= DyIORegestry.get();
		if(!dyio.isAvailable())
			fail();
		assertTrue(DyIORegestry.get().hasNamespace("neuronrobotics.dyio.*"));
		
		
		dyio.getRevisions();
		

		
	}

}
