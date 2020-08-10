package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.DHChain;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.DhInverseSolver;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.LinkType;
import com.neuronrobotics.sdk.addons.kinematics.MobileBase;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GCodeHeater;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodeDevice;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodePrismatic;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodeRotory;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.IGCodeChannel;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.DeviceManager;
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
	public void linkFactoryPrismatic() {
		if (hasPort) {
			LinkFactory lf = new LinkFactory();
			LinkConfiguration confp = new LinkConfiguration();
			confp.setTypeString(LinkType.GCODE_STEPPER_PRISMATIC.getName());
			confp.setDeviceScriptingName(GCODE);
			confp.setHardwareIndex(0);
			confp.setScale(1);
			AbstractLink link = lf.getLink(confp);
			assertEquals(link.getClass(), GcodePrismatic.class);// checks to see
																// a real device
																// was created
			link.setTargetEngineeringUnits(100.5);
			link.flush(1);// take 2 seconds to flush

			LinkConfiguration confp2 = new LinkConfiguration();
			confp2.setTypeString(LinkType.GCODE_STEPPER_PRISMATIC.getName());
			confp2.setDeviceScriptingName(GCODE);
			confp2.setHardwareIndex(1);
			confp2.setScale(1);
			AbstractLink link2 = lf.getLink(confp2);
			assertEquals(link2.getClass(), GcodePrismatic.class);// checks to
																	// see a
																	// real
																	// device
																	// was
																	// created
			link2.setTargetEngineeringUnits(100.5);
			link2.flush(1);// take 2 seconds to flush

			link2.setTargetEngineeringUnits(0);
			link.setTargetEngineeringUnits(0);
			// coordinated motion flush
			lf.flush(1);

		}
	}

	@Test
	public void loadFromXml() {
		if (hasPort) {

			MobileBase cnc = new MobileBase(GCODETest.class.getResourceAsStream("cnc.xml"));
			DHParameterKinematics arm = cnc.getAppendages().get(0);
			arm.setInverseSolver(new DhInverseSolver() {
				@Override
				public double[] inverseKinematics(TransformNR target, double[] jointSpaceVector, DHChain chain) {
					double[] inv = new double[jointSpaceVector.length];
					// inv[2] = target.getX();
					inv[1] = target.getY();
					inv[0] = target.getX();
					for (int i = 3; i < inv.length && i < jointSpaceVector.length; i++)
						inv[i] = jointSpaceVector[i];
					return inv;
				}
			});
			for (LinkConfiguration l : arm.getLinkConfigurations()) {
				AbstractLink link = arm.getFactory().getLink(l);
				assertTrue(IGCodeChannel.class.isAssignableFrom(link.getClass()));// checks
																					// to
																					// see
																					// a
																					// real
																					// device
																					// was
																					// created
			}
			System.out.println("Moving using the kinematics");
			try {
				arm.setDesiredTaskSpaceTransform(new TransformNR(10, 10, 0, new RotationNR()), 1);
				arm.setDesiredTaskSpaceTransform(new TransformNR(), 1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void linkFactoryRotory() {
		if (hasPort) {
			LinkFactory lf = new LinkFactory();
			LinkConfiguration confp = new LinkConfiguration();
			confp.setTypeString(LinkType.GCODE_STEPPER_ROTORY.getName());
			confp.setDeviceScriptingName(GCODE);
			confp.setHardwareIndex(0);
			confp.setScale(1);
			AbstractLink link = lf.getLink(confp);
			assertEquals(link.getClass(), GcodeRotory.class);// checks to see a
																// real device
																// was created
			link.setTargetEngineeringUnits(100.5);
			link.flush(1);// take 2 seconds to flush

			LinkConfiguration confp2 = new LinkConfiguration();
			confp2.setTypeString(LinkType.GCODE_STEPPER_ROTORY.getName());
			confp2.setDeviceScriptingName(GCODE);
			confp2.setHardwareIndex(1);
			confp2.setScale(1);
			AbstractLink link2 = lf.getLink(confp2);
			assertEquals(link2.getClass(), GcodeRotory.class);// checks to see a
																// real device
																// was created
			link2.setTargetEngineeringUnits(100.5);
			link2.flush(1);// take 2 seconds to flush

			link2.setTargetEngineeringUnits(0);
			link.setTargetEngineeringUnits(0);
			// coordinated motion flush
			lf.flush(1);

		}
	}

	@Test
	public void linkFactoryTool() {
		if (hasPort) {
			LinkFactory lf = new LinkFactory();
			LinkConfiguration confp = new LinkConfiguration();
			confp.setTypeString(LinkType.GCODE_STEPPER_TOOL.getName());
			confp.setDeviceScriptingName(GCODE);
			confp.setHardwareIndex(0);
			confp.setScale(1);
			AbstractLink link = lf.getLink(confp);
			assertEquals(link.getClass(), GcodeRotory.class);// checks to see a
																// real device
																// was created
			link.setTargetEngineeringUnits(100.5);
			link.flush(1);// take 2 seconds to flush

			LinkConfiguration confp2 = new LinkConfiguration();
			confp2.setTypeString(LinkType.GCODE_STEPPER_TOOL.getName());
			confp2.setDeviceScriptingName(GCODE);
			confp2.setHardwareIndex(1);
			confp2.setScale(1);
			AbstractLink link2 = lf.getLink(confp2);
			assertEquals(link2.getClass(), GcodeRotory.class);// checks to see a
																// real device
																// was created
			link2.setTargetEngineeringUnits(100.5);
			link2.flush(1);// take 2 seconds to flush

			link2.setTargetEngineeringUnits(0);
			link.setTargetEngineeringUnits(0);
			// coordinated motion flush
			lf.flush(5);

		}
	}

	@Test
	public void linkFactoryHeater() {
		if (hasPort) {
			LinkFactory lf = new LinkFactory();
			LinkConfiguration confp = new LinkConfiguration();
			confp.setTypeString(LinkType.GCODE_HEATER_TOOL.getName());
			confp.setDeviceScriptingName(GCODE);
			confp.setHardwareIndex(0);
			confp.setScale(1);
			AbstractLink link = lf.getLink(confp);
			assertEquals(link.getClass(), GCodeHeater.class);// checks to see a
																// real device
																// was created
			link.setTargetEngineeringUnits(25);
			link.flush(1);// take 2 seconds to flush

			LinkConfiguration confp2 = new LinkConfiguration();
			confp2.setTypeString(LinkType.GCODE_HEATER_TOOL.getName());
			confp2.setDeviceScriptingName(GCODE);
			confp2.setHardwareIndex(1);
			confp2.setScale(1);
			AbstractLink link2 = lf.getLink(confp2);
			assertEquals(link2.getClass(), GCodeHeater.class);// checks to see a
																// real device
																// was created
			link2.setTargetEngineeringUnits(25);
			link2.flush(1);// take 2 seconds to flush

			link2.setTargetEngineeringUnits(0);
			link.setTargetEngineeringUnits(0);
			// coordinated motion flush
			lf.flush(5);

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
			response = device.runLine("G1 X100.2 Y100.2 Z0 E10 F6000");
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
