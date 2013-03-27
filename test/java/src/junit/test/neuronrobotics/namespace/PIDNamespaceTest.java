package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PIDNamespaceTest {
	protected GenericPIDDevice pid = null;
	@Before
	public void setup(){
		if(pid == null){
			pid = new GenericPIDDevice();
			System.out.println("Creating PID device");
			if(!ConnectionDialog.getBowlerDevice(pid)){
				fail("Device not availible");
			}
		}
	}
	@Test
	public void PIDTestNs() {
		try{
			assertTrue(pid.hasNamespace("bcs.pid.*"));	
		}catch (Exception e){
			e.printStackTrace();
			assert false;
		}
	}

	@Test public void setPid(){
		try{
			assertTrue(pid.SetPIDSetPoint(0, 100, 1000));	
			int [] values = pid.GetAllPIDPosition();
			assertTrue(pid.SetAllPIDSetPoint(values, 1000));	
		}catch (Exception e){
			e.printStackTrace();
			assert false;
		}
	}

}
