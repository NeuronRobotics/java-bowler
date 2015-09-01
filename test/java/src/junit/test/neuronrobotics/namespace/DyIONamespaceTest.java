package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.serial.SerialConnection;
import com.neuronrobotics.sdk.util.ThreadUtil;


public class DyIONamespaceTest {
	
	private static DyIO harness=null;
	private static DyIO testDevice=null;
	private boolean useHarness = false;
	
	private static int msTimeout = 3000;
	private static int testLoop =5;
	private static double integral= 0;
	
	@Before
	public void setUp() throws Exception {

		if(harness == null && testDevice == null ){
			//Log.enableDebugPrint();
			DyIO.disableFWCheck();
			Log.enableInfoPrint();
			
			if(useHarness ){
				//Change this MAC address to match your tester/testee mapping
				SerialConnection testerConection = SerialConnection.getConnectionByMacAddress(new MACAddress("25:2a:25:2a:25:2a"));
				if(testerConection!=null){
					harness = new DyIO(testerConection);
					harness.connect();
					harness.setServoPowerSafeMode(false);
					for(int i=0;i<harness.getPIDChannelCount();i++){
						harness.ConfigureDynamicPIDChannels(new DyPIDConfiguration(i));
						harness.ConfigurePIDController(new PIDConfiguration(i));
					}
					System.out.println("Using harness for this test");
				}else{
					System.out.println("No harness for this test");
					useHarness=false;
				}
			}
			//Change this MAC address to match your tester/testee mapping
			//SerialConnection targetConection = SerialConnection.getConnectionByMacAddress(new MACAddress("74:F7:26:00:00:00"));
			
			SerialConnection targetConection =  new SerialConnection("/dev/DyIO0");
			assertTrue(targetConection!=null);
			targetConection.setSynchronusPacketTimeoutTime(10000);
			testDevice = new DyIO(targetConection);
			testDevice.connect();
			testDevice.setServoPowerSafeMode(false);
			for(int i=0;i<testDevice.getPIDChannelCount();i++){
				testDevice.ConfigureDynamicPIDChannels(new DyPIDConfiguration(i));
				testDevice.ConfigurePIDController(new PIDConfiguration(i));
			}
			int numPins = testDevice.getDyIOChannelCount();
			if(numPins!=24){
				fail("wrong size of channels");
			}
			Log.debug("Setting all inputs");
			//Devices as input
			for(int i=0;i<numPins;i++){
				if(useHarness ){
					//harness.setMode(i, DyIOChannelMode.DIGITAL_OUT);
					if(harness.getChannel(i).getMode() != DyIOChannelMode.DIGITAL_IN)
						harness.setMode(i, DyIOChannelMode.DIGITAL_IN);
				}
				//testDevice.setMode(i, DyIOChannelMode.DIGITAL_OUT);
				if(testDevice.getChannel(i).getMode() != DyIOChannelMode.DIGITAL_IN)
					testDevice.setMode(i, DyIOChannelMode.DIGITAL_IN);
			}
			
			Log.debug("Devices Set Up");

		}
	}
	
	@After
	public void teardown(){
		if(harness != null)
			harness.disconnect();
		if(testDevice != null)
			testDevice.disconnect();
		harness=null;
		testDevice=null;
	}
	
	@Test public void DyIOConfigurationSave(){
		if(!testDevice.isAvailable())
			fail();
		
		testDevice.setServoPowerSafeMode(false);
		for(int i=0;i<testDevice.getChannels().size();i++){
			for(int j=0;j<129;j+=64){
				//System.out.println("Setting up servo: "+i);
				ServoChannel srv = new ServoChannel(testDevice,i);
				int testNumber = j;
				//System.out.println("Saving value to: "+testNumber);
				srv.SavePosition(testNumber);
				//System.out.println("Setting up Digital in ");
				new DigitalInputChannel(testDevice,i);
			}
			ServoChannel srv = new ServoChannel(testDevice,i);
			//System.out.println("Saving value to: "+testNumber);
			srv.SavePosition(128);
			//System.out.println("Setting up Digital in ");
			new DigitalInputChannel(testDevice,i);
		}
		

		
	}
	
	
	@Test public void DyIONameTest(){
//
//		if(!testDevice.isAvailable())
//			fail();
//		int level = Log.getMinimumPrintLevel();
//		Log.enableInfoPrint();
//		String name = testDevice.getInfo();
//		System.out.println("Name is:"+name+ " "+new ByteList(name));
//		String setName;
//		if(name.contains("My DyIO"))
//			setName="My DyIO2";
//		else
//			setName="My DyIO";
//		System.out.println("Setting:"+setName);
//		testDevice.setInfo(setName);
//		
//		String newName = testDevice.getInfo();
//		
//		System.out.println("New name is:"+newName+ " "+new ByteList(newName));
//		testDevice.setInfo(name);
//	
//		assertTrue(setName.contains(newName));
//		assertTrue(name.contains(testDevice.getInfo()));
//		assertTrue(testDevice.ping() );
//		
//		Log.setMinimumPrintLevel(level);
		
	}
	
	@Test public void DyIOPowerTest(){
		if(!testDevice.isAvailable())
			fail();
		double volts = testDevice.getBatteryVoltage(true);
		
		testDevice.setServoPowerSafeMode(true);


	}
	
	@Test public void DyIOModesTest(){
		if(!testDevice.isAvailable())
			fail();
		Log.debug("DyIOModesTest");
		ArrayList<DyIOChannelMode> modes = testDevice.getAllChannelModes();
		if(modes.size()!=24){
			fail("Returned mode list of wrong size");
		}
		for(int i=0;i<modes.size();i++){
			testDevice.setMode(i, DyIOChannelMode.DIGITAL_IN);
		}
		for(int i=0;i<modes.size();i++){
			
			if(modes.get(i)==DyIOChannelMode.DIGITAL_IN){
				modes.set(i, DyIOChannelMode.DIGITAL_OUT);
			}else{
				modes.set(i, DyIOChannelMode.DIGITAL_IN);
			}
			//System.out.println("Setting "+i+" to mode "+modes.get(i));
			testDevice.setMode(i, modes.get(i));
			long startTime = System.currentTimeMillis();
			do{		
				ThreadUtil.wait(1);
				if((System.currentTimeMillis()-startTime)> msTimeout){
					System.err.println("Pin test failed "+i+" in "+(System.currentTimeMillis()-startTime));
					fail("DyIOModesTest Pin:"+i+" Tester: "+testDevice.getMode(i)+" trying to set "+modes.get(i));
				}
			}while(testDevice.getMode(i)!= modes.get(i));
		}
		ThreadUtil.wait(100);
		Log.debug("Reading channel modes");
		ArrayList<DyIOChannelMode> modesAfter = testDevice.getAllChannelModes();
		Log.debug("Checking channel modes");
		for(int i=0;i<modes.size();i++){
			if(modes.get(i)!=modesAfter.get(i)){
				fail("Mode set on "+i+" failed. "+modes.get(i)+" got: "+modesAfter.get(i));
			}
			if(modes.get(i)!=testDevice.getMode(i)){
				fail("Mode set on "+i+" failed. "+modes.get(i)+" got: "+testDevice.getMode(i));
			}
			testDevice.setMode(i, DyIOChannelMode.DIGITAL_IN);
		}
	}
	
	@Test public void DyIOAnalogInputTest(){
		if(!testDevice.isAvailable() || harness == null)
			fail();
		int numPins = testDevice.getDyIOChannelCount();
		
		//Test device as input
		for(int i=0;i<numPins;i++){
			if(i == 13)
				i=14;
			if(testDevice.getChannel(i).canBeMode(DyIOChannelMode.ANALOG_IN )){
				
				int testerIndex = i;
				harness.setMode(testerIndex, DyIOChannelMode.DIGITAL_OUT);
				testDevice.setMode(i, DyIOChannelMode.ANALOG_IN);
				integral= 0;
				boolean state=false;
				for(int j=0;j<testLoop;j++){
					int pinState = state?1023:0;

					long startTime = System.currentTimeMillis();
					harness.setValue(testerIndex, state?1:0);
					boolean ok=false;
					
					do{		
						ThreadUtil.wait(1);
						if((System.currentTimeMillis()-startTime)> msTimeout){
							System.err.println("Pin test failed "+i);
							fail("DyIOAnalogInputTest Pin:"+i+" Tester:"+testerIndex+" setting to: "+pinState+" got:"+testDevice.getValue(i));
						}
						
						if(state){
							ok = testDevice.getValue(i)<900;
						}else{
							ok = testDevice.getValue(i)!=0;
						}
					}while(ok);
					integral+=(System.currentTimeMillis()-startTime);
					state = !state;
				}
				System.out.println("PASS DyIOAnalogInputTest "+i+" in "+(integral/testLoop)+"ms");
				harness.setMode(testerIndex, DyIOChannelMode.DIGITAL_IN);
				testDevice.setMode(testerIndex, DyIOChannelMode.DIGITAL_IN);
			}else{
				System.out.println("Pin "+i+" can not be analog in");
			}
		}
	}
	
	
	@Test public void DyIOInputTest(){
		if(!testDevice.isAvailable() || harness == null)
			fail();
		int numPins = testDevice.getDyIOChannelCount();
		
		//Test device as input
		for(int i=0;i<numPins;i++){
			if(i == 13)
				i=14;
			int testerIndex = i;
			if(i == 16)
				testerIndex=17;
			if(i == 17)
				testerIndex=16;
			harness.setMode(testerIndex, DyIOChannelMode.DIGITAL_OUT);
			testDevice.setMode(i, DyIOChannelMode.DIGITAL_IN);
			//System.out.println("Pin Input test "+i);
			boolean state=true;
			integral= 0;
			for(int j=0;j<testLoop;j++){
				int pinState = state?1:0;
				long startTime = System.currentTimeMillis();
				harness.setValue(testerIndex, pinState);
				do{		
					ThreadUtil.wait(1);
					if((System.currentTimeMillis()-startTime)> msTimeout){
						System.err.println("Pin test failed "+i);
						fail("DyIOInputTest Pin:"+i+" Tester:"+testerIndex+" setting to: "+pinState+" got:"+testDevice.getValue(i));
					}
				}while(testDevice.getValue(i)!=pinState);
				integral+=(System.currentTimeMillis()-startTime);
				state = !state;
			}
			System.out.println("PASS DyIOInputTest "+i+" in "+(integral/testLoop)+"ms");
			harness.setMode(testerIndex, DyIOChannelMode.DIGITAL_IN);
			
		}
	}
	

	
	@Test public void DyIOOutputTest(){
		if(!testDevice.isAvailable() || harness == null)
			fail();
		int numPins = testDevice.getDyIOChannelCount();

		//test device as output
		for(int i=0;i<numPins;i++){
	
			if(i == 16)
				i=18;
			if(i == 13)
				i=14;
			int testerIndex = i;
			testDevice.setMode(i, DyIOChannelMode.DIGITAL_OUT);
			harness.setMode(testerIndex, DyIOChannelMode.DIGITAL_IN);
				
			boolean state=true;
			integral= 0;
			boolean fail=false;
			for(int j=0;j<testLoop;j++){

				int pinState = state?1:0;

				long startTime = System.currentTimeMillis();
				testDevice.setValue(testerIndex, state?1:0);
				fail=false;
				do{		
					ThreadUtil.wait(1);
					if((System.currentTimeMillis()-startTime)> msTimeout){
						//System.err.println("Pin test failed "+i);
						fail("DyIOOutputTest Pin:"+i+" Tester:"+testerIndex+" setting to: "+pinState+" got:"+harness.getValue(i));
						fail=true;
					}
				}while(harness.getValue(i)!=pinState && fail==false);
				integral+=(System.currentTimeMillis()-startTime);
				state = !state;
				if(fail)
					System.err.println("DyIOOutputTest Pin:"+i+" Tester:"+testerIndex+" setting to: "+pinState+" got:"+harness.getValue(i));
			}

			System.out.println("PASS DyIOOutputTest "+i+" in "+(integral/testLoop)+"ms");
			testDevice.setMode(i, DyIOChannelMode.DIGITAL_IN);
		}
		
	}
	
	@Test public void DyIOServoTest(){
		if(!testDevice.isAvailable())
			fail();
		int numPins = testDevice.getDyIOChannelCount();
		testDevice.setServoPowerSafeMode(false);// make sure the servos can be enabled
		//test device as output
		for(int i=0;i<numPins;i++){
			ServoChannel sc = new ServoChannel(testDevice, i);
			
			boolean state=false;
			int pinState = state?200:20;
			for(int j=0;j<testLoop;j++){
				long startTime = System.currentTimeMillis();
				sc.SetPosition(pinState, 1.0);
				do{		
					ThreadUtil.wait(1);
					if((System.currentTimeMillis()-startTime)> msTimeout){
						//System.err.println("Pin test failed "+i);
						fail("Servo Test Pin:"+i+" Tester:"+i+" setting to: "+pinState+" got:"+sc.getValue());
					}
				}while(sc.getValue()!=pinState );
				integral+=(System.currentTimeMillis()-startTime);
				state = !state;
			}
		}
		
	}
	
	@Test public void DyIOServoCoordinatedMotionTest(){
		if(!testDevice.isAvailable())
			fail();
		int numPins = testDevice.getDyIOChannelCount();
		testDevice.setServoPowerSafeMode(false);// make sure the servos can be enabled
		//test device as output
		ArrayList<ServoChannel> srvs = new ArrayList<ServoChannel>();
		for(int i=0;i<numPins;i++){
			ServoChannel s = new ServoChannel(testDevice, i);
			
			srvs.add(s);
		}
		testDevice.setCachedMode(true);
		boolean state=false;
		for(int j=0;j<testLoop;j++){
			int pinState = state?200:20;
			for(int i=0;i<numPins;i++){
				srvs.get(i).SetPosition(pinState, 1.0);
			}
			testDevice.flushCache(1.0);
			boolean done = false;
			long startTime = System.currentTimeMillis();
			do{		
				ThreadUtil.wait(1);
				if(!done){
					done=true;
					for(int i=0;i<numPins;i++){
						if(done)
						if(testDevice.getValue(i) != pinState){
							done=false;
						}
					}
				}
			}while(!done && (System.currentTimeMillis()-startTime)< msTimeout);
		}
	}

	@Test
	public void dyioNamespaceTest() {
		if(!testDevice.isAvailable())
			fail();
		assertTrue(testDevice.hasNamespace("neuronrobotics.dyio.*"));
		testDevice.getRevisions();
	
	}

}
