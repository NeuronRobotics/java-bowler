package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodeDevice;
import com.neuronrobotics.sdk.common.DeviceManager;

import gnu.io.NRSerialPort;

public class GCODETest {

	private static final Class<GcodeDevice> GCODECONTOLLER = GcodeDevice.class;
	private static final String GCODE = "GCODE";
	private static final String portname = "/dev/ttyUSB0";

	@BeforeClass
	public static void loadGCodeDevice() {
		boolean hasPort = false;
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
		boolean hasPort = false;
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			if (s.contentEquals(portname))
				hasPort = true;
		}
		if (hasPort) {
			GcodeDevice device = GCODECONTOLLER.cast(DeviceManager.getSpecificDevice(GCODECONTOLLER, GCODE));
			device.disconnect();
		}
	}

	@Test
	public void M105() {
		boolean hasPort = false;
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			if (s.contentEquals(portname))
				hasPort = true;
		}
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
	public void G1() {
		boolean hasPort = false;
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			if (s.contentEquals(portname))
				hasPort = true;
		}
		if (hasPort) {
			GcodeDevice device = GCODECONTOLLER.cast(DeviceManager.getSpecificDevice(GCODECONTOLLER, GCODE));

			String response = device.runLine("G0 X100 Y100 Z100 E100 F3000");
			if (response.length() > 0)
				System.out.println("Gcode line run: " + response);
			else {
				fail("No response");
			}
			response = device.runLine("G0 X0 Y0 Z0 E0 F3000");
			if (response.length() > 0)
				System.out.println("Gcode line run: " + response);
			else {
				fail("No response");
			}

		}
	}

}
