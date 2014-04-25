package junit.test.neuronrobotics.cartesian;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.replicator.driver.DeltaForgeDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.serial.SerialConnection;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class CartesianTestSuit {

	@Test
	public void test() {
		Log.enableDebugPrint();
		DeltaForgeDevice dev = new DeltaForgeDevice();
		dev.setConnection(new SerialConnection("/dev/BowlerDevice.74F726000000"));
		assertTrue(dev.connect());
		System.out.println("Connection ok");
		
		NRPrinter printer = new NRPrinter(dev);
		
		
		
	}

}
