package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodeDevice;
import com.neuronrobotics.sdk.common.DeviceManager;

import gnu.io.NRSerialPort;

public class GCODETest {

	private static final String GCODE = "GCODE";

	@Before
	public void setUp() throws Exception {
		NRSerialPort  port = new NRSerialPort("/dev/ttyUSB0", 250000);
		GcodeDevice device = new GcodeDevice(port);
		device.connect();
		DeviceManager.addConnection(device, GCODE);
		
		
	}

	@After
	public void tearDown() throws Exception {
		DeviceManager.getSpecificDevice(GcodeDevice.class, GCODE).disconnect();
		
	}

	@Test
	public void test() {
		Object  d  = DeviceManager.getSpecificDevice(GcodeDevice.class, GCODE);
		if(d==null){
			return;
		}
		GcodeDevice device = (GcodeDevice)d;
		
		System.out.println("Gcode line run: "+device.runLine("M105"));
		
	}

}
