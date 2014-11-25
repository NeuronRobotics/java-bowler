package junit.test.neuronrobotics.cartesian;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.replicator.driver.Slic3r;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class CartesianTestSuit {

	@Test
	public void test() {
		Log.enableDebugPrint();
		BowlerBoardDevice dev = new BowlerBoardDevice();
		dev.setConnection(new SerialConnection("/dev/BowlerDevice0"));
		dev.getConnection().setSynchronusPacketTimeoutTime(2000);
		assertTrue(dev.connect());
		System.out.println("Connection ok");
		
//		dev.setDesiredTaskSpaceTransform(new TransformNR(), 44);
//		dev.getCurrentTaskSpaceTransform();
//		dev.setDesiredJointSpaceVector(new double [] {150,150,150,0,0},55);
//		dev.setDesiredJointAxisValue(0,100,20);
		Slic3r.setExecutableLocation("/usr/local/Slic3r/bin/slic3r");
		NRPrinter printer = new NRPrinter(dev);
		printer.cancelPrint();
		//ThreadUtil.wait(5000);
//		StateBasedControllerConfiguration conf = printer.getStateBasedControllerConfiguration();
//		System.out.println(conf);
//		conf.setkP(.2);
//		
//		printer.setStateBasedControllerConfiguration(conf);
//		System.out.println(printer.getStateBasedControllerConfiguration());
//		
//		printer.homeAllLinks();
//		printer.setPausePrintState(true);
//		printer.setPausePrintState(false);
		
		try{
			File stl = new File("calibration_angle.stl");
			File gcode = new File(stl.getAbsoluteFile()+".gcode");
			if(!gcode.exists())
				printer.slice(stl, gcode);
			
			printer.print(CartesianTestSuit.class.getResourceAsStream("test.gcode"));
//			if(gcode.exists())
//				printer.print(new FileInputStream(gcode));
		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}
		
		
	}

}
