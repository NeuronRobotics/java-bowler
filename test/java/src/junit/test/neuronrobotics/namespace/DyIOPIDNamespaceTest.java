package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DyIOPIDNamespaceTest extends PIDNamespaceTest {

	@Before
	public void setUp() throws Exception {
		if(!DyIORegestry.get().isAvailable()){
			Log.enableDebugPrint(true);
			if(!ConnectionDialog.getBowlerDevice(DyIORegestry.get())){
				fail("No device availible");
			}
		}
		pid = DyIORegestry.get().getPid();
	}

	@Test
	public void DyPIDTest() {
		assertTrue(DyIORegestry.get().hasNamespace("bcs.pid.dypid.*"));
	}

}
