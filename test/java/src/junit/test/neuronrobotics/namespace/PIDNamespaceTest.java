package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class PIDNamespaceTest {
	private static GenericPIDDevice pid = null;
	@Before
	public void setup(){
		//Log.enableInfoPrint();
		getPid();
	}
	@Test
	public void PIDTestNs() {
		try{
			assertTrue(getPid().hasNamespace("bcs.pid.*"));	
		}catch (Exception e){
			e.printStackTrace();
			fail();
		}
	}


	@Test public void getAllPidNsTest(){
		try{
			int [] values = getPid().GetAllPIDPosition();
			assertTrue(getPid().SetAllPIDSetPoint(values, 1));	
		}catch (Exception e){
			e.printStackTrace();
			fail();
		}
	}
	@Test public void configurePidNsTest(){
		try{
			PIDConfiguration conf = getPid().getPIDConfiguration(0);	
			conf.setKP(.15);
			conf.setKI(.01);
			conf.setKD(1);
			
			getPid().ConfigurePIDController(conf);
			
			PIDConfiguration tmp = getPid().getPIDConfiguration(0);	
			
			assertTrue(conf.getKP() == tmp.getKP());
			assertTrue(conf.getKI() == tmp.getKI());
			assertTrue(conf.getKD() == tmp.getKD());
			
		}catch (Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test public void setPidNsTest(){
		try{
			int position = getPid().GetPIDPosition(0);
			int newPos  = position- 0x0fff;
			getPid().SetPIDSetPoint(0, newPos, 1.0);
			ThreadUtil.wait(3000);
			int currentPos = getPid().GetPIDPosition(0);
			System.out.println("Set to "+newPos+" got "+currentPos);
			getPid().SetPIDSetPoint(0, position, 0);
			ThreadUtil.wait(1200);
			assertTrue((newPos < currentPos+100) &&(newPos > currentPos-100) );
		}catch (Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	
	
	public static GenericPIDDevice getPid() {
		if( DyIORegestry.get().isAvailable() == false&& 
				PIDNamespaceTest.pid == null ){
			setPid(new GenericPIDDevice());
			System.out.println("Creating PID device");
			if(!ConnectionDialog.getBowlerDevice(PIDNamespaceTest.pid)){
				fail("Device not availible");
			}
		}
		return PIDNamespaceTest.pid;
	}
	public static void setPid(GenericPIDDevice pid) {
		PIDNamespaceTest.pid = pid;
	}

}
