package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PIDNamespaceTest {
	protected GenericPIDDevice pid = null;
	@Test
	public void PIDTest() {
		if(pid == null){
			pid = new GenericPIDDevice();
			System.out.println("Creating PID device");
			if(!ConnectionDialog.getBowlerDevice(pid)){
				fail("Device not availible");
			}
		}
		
		assertTrue(pid.hasNamespace("bcs.pid.*"));
		
	}


}
