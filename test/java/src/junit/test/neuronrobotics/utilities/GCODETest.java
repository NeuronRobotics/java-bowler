package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import javax.security.auth.login.FailedLoginException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.LinkType;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodeDevice;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

import gnu.io.NRSerialPort;

public class GCODETest {

	private static final Class<GcodeDevice> GCODECONTOLLER = GcodeDevice.class;
	private static final String GCODE = "GCODE";
	private static final String portname = "/dev/ttyUSB0";
	private static boolean hasPort;

	@BeforeClass
	public static void loadGCodeDevice() {
		hasPort = false;
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			if (s.contentEquals(portname))
				hasPort = true;
		}
		if (hasPort) {
			GcodeDevice device;
			NRSerialPort port = new NRSerialPort(portname, 115200);
			device = new GcodeDevice(port);
			device.connect();
			DeviceManager.addConnection(device, GCODE);
		}
	}

	@AfterClass
	public static void closeGCodeDevice() {

		if (hasPort) {
			GcodeDevice device = GCODECONTOLLER.cast(DeviceManager.getSpecificDevice(GCODECONTOLLER, GCODE));
			device.disconnect();
		}
	}

	@Test
	public void M105() {

		if (hasPort) {
			GcodeDevice device = GCODECONTOLLER.cast(DeviceManager.getSpecificDevice(GCODECONTOLLER, GCODE));

			String response = device.runLine("M105");
			if (response.length() > 0)
				System.out.println("Gcode line run: " + response);
			else {
				fail("No response");
			}

		}
	}
	@Test
	public void linkFactoryPrismatic(){
		if (hasPort) {
			LinkFactory lf = new LinkFactory();
			LinkConfiguration confp = new LinkConfiguration();
			confp.setType(LinkType.GCODE_STEPPER_PRISMATIC);
			confp.setDeviceScriptingName(GCODE);
			AbstractLink link = lf.getLink(confp);
			assertEquals(link.getClass(), VirtualGenericPIDDevice.class);// checks to see a real device was created
		}
	}

	@Test
	public void G1() {

		if (hasPort) {
			GcodeDevice device = GCODECONTOLLER.cast(DeviceManager.getSpecificDevice(GCODECONTOLLER, GCODE));
			String response = device.runLine("G90");// Absolute mode
			if (response.length() > 0)
				System.out.println("Gcode line run: " + response);
			else {
				fail("No response");
			}
			response = device.runLine("G1 X10 Y10 Z10 E10 F3000");
			if (response.length() > 0)
				System.out.println("Gcode line run: " + response);
			else {
				fail("No response");
			}
			response = device.runLine("G1 X0 Y0 Z0 E0 F3000");
			if (response.length() > 0)
				System.out.println("Gcode line run: " + response);
			else {
				fail("No response");
			}

		}
	}

}
