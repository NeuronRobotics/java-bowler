package junit.test.neuronrobotics.cartesian;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.replicator.driver.DeltaForgeDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class CartesianTestSuit {

	@Test
	public void test() {
		DeltaForgeDevice dev = new DeltaForgeDevice();
		assertTrue(ConnectionDialog.getBowlerDevice(dev));
		
		
	}

}
