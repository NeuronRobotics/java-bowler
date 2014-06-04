package junit.test.neuronrobotics.cartesian;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.replicator.driver.DeltaForgeDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.serial.SerialConnection;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class CartesianTestSuit {

	@Test
	public void test() {
		//Log.enableDebugPrint();
		DeltaForgeDevice dev = new DeltaForgeDevice();
		dev.setConnection(new SerialConnection("/dev/BowlerDevice.74F726000000"));
		dev.getConnection().setSynchronusPacketTimeoutTime(2000);
		assertTrue(dev.connect());
		System.out.println("Connection ok");
		
		dev.sendLinearSection(new TransformNR(180, 0, 0, new RotationNR()), 0.0, 0);
		
		ThreadUtil.wait(5000);
		
		dev.sendLinearSection(new TransformNR(300, 300, 10, new RotationNR()), 0.0, 0);
		
		ThreadUtil.wait(5000);
		
		dev.sendLinearSection(new TransformNR(180, -300, 10, new RotationNR()), 0.0, 0);
		
		ThreadUtil.wait(5000);
		dev.sendLinearSection(new TransformNR(180, 0, 0, new RotationNR()), 0.0, 0);

//		NRPrinter printer = new NRPrinter(dev);
//		printer.cancelPrint();
//		ThreadUtil.wait(5000);
//		try{
//			printer.print(CartesianTestSuit.class.getResourceAsStream("test.gcode"));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			fail();
//		}
		
		
	}

}
