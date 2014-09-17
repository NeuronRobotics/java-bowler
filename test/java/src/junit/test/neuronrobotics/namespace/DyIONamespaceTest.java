package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class DyIONamespaceTest {
	
	private static DyIO tester=null;
	private static DyIO target=null;
	
	@Before
	public void setUp() throws Exception {
		DyIO.disableFWCheck();
		Log.enableDebugPrint();
		
		SerialConnection testerConection = SerialConnection.getConnectionByMacAddress(new MACAddress("74:F7:26:80:00:7C"));
		assertTrue(testerConection!=null);
		tester = new DyIO(testerConection);
		tester.connect();
		
		SerialConnection targetConection = SerialConnection.getConnectionByMacAddress(new MACAddress("74:F7:26:00:00:00"));
		assertTrue(targetConection!=null);
		targetConection.setSynchronusPacketTimeoutTime(5000);
		target = new DyIO(targetConection);
		target.connect();
		
		
	}
	
	@After
	public void shutdownDevices(){
		target.disconnect();
		if(tester!=null)
			tester.disconnect();
	}
	
	@Test public void DyIONameTest(){

		if(!target.isAvailable())
			fail();
		
		String name = target.getInfo();
		
		String setName;
		if(name.contains("My DyIO"))
			setName="My DyIO2";
		else
			setName="My DyIO";
		
		target.setInfo(setName);
		
		String newName = target.getInfo();
		

		target.setInfo(name);
	
		assertTrue(setName.contains(newName));
		assertTrue(name.contains(target.getInfo()));
		assertTrue(target.ping() );

	}
	
	@Test public void DyIOPowerTest(){
		if(!target.isAvailable())
			fail();
		double volts = target.getBatteryVoltage(true);
		
		target.setServoPowerSafeMode(true);


	}
	
	@Test public void DyIOModesTest(){
		if(!target.isAvailable())
			fail();

		ArrayList<DyIOChannelMode> modes = target.getAllChannelModes();
		for(int i=0;i<modes.size();i++){
			
			if(modes.get(i)==DyIOChannelMode.DIGITAL_IN){
				modes.set(i, DyIOChannelMode.DIGITAL_OUT);
			}else{
				modes.set(i, DyIOChannelMode.DIGITAL_IN);
			}
			target.setMode(i, modes.get(i));
		}
		
		ArrayList<DyIOChannelMode> modesAfter = target.getAllChannelModes();
		for(int i=0;i<modes.size();i++){
			assertTrue(modes.get(i)==modesAfter.get(i));
			assertTrue(modes.get(i)==target.getMode(i));
		}
	}
	
	@Test public void DyIOValuesTest(){
		if(!target.isAvailable() || tester == null)
			fail();
		int numPins = target.getDyIOChannelCount();
		for(int i=0;i<numPins;i++){
			int testerIndex = numPins-1-i;
			tester.setMode(testerIndex, DyIOChannelMode.DIGITAL_OUT);
			target.setMode(i, DyIOChannelMode.DIGITAL_IN);
			
			boolean state=true;
			for(int j=0;j<5;j++){
				int pinState = state?1:0;
				tester.setValue(testerIndex, pinState);
				int gotValue = target.getValue(i);
				System.out.println(" Pin:"+i+" Tester:"+testerIndex+" setting to: "+pinState+" got:"+gotValue);
				assertTrue(gotValue==pinState);
				state = !state;
			}
		}
		
	}

	@Test
	public void dyioNamespaceTest() {
		if(!target.isAvailable())
			fail();
		assertTrue(target.hasNamespace("neuronrobotics.dyio.*"));
		target.getRevisions();
	
	}

}
