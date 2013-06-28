package junit.test.neuronrobotics.namespace;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PIDNamespaceTest {
	private static GenericPIDDevice pid = null;
	@Before
	public void setup(){
		getPid();
	}
	@Test
	public void PIDTestNs() {
		try{
			assertTrue(getPid().hasNamespace("bcs.pid.*"));	
		}catch (Exception e){
			e.printStackTrace();
			assert false;
		}
	}

	@Test public void setPidNsTest(){
		try{
			assertTrue(getPid().SetPIDSetPoint(0, 100, 1000));	
		}catch (Exception e){
			e.printStackTrace();
			assert false;
		}
	}
	@Test public void getSetAllPidNsTest(){
		try{
			int [] values = getPid().GetAllPIDPosition();
			assertTrue(getPid().SetAllPIDSetPoint(values, 1000));	
		}catch (Exception e){
			e.printStackTrace();
			assert false;
		}
	}
	@Test public void configurePidNsTest(){
		try{
			PIDConfiguration conf = getPid().getPIDConfiguration(0);	
			conf.setKP(1.5);
			conf.setKI(.01);
			conf.setKD(1);
			
			getPid().ConfigurePIDController(conf);
			
			PIDConfiguration tmp = getPid().getPIDConfiguration(0);	
			
			assertTrue(conf.getKP() == tmp.getKP());
			assertTrue(conf.getKI() == tmp.getKI());
			assertTrue(conf.getKD() == tmp.getKD());
			
		}catch (Exception e){
			e.printStackTrace();
			assert false;
		}
	}
	
	
	
	public static GenericPIDDevice getPid() {
		if( DyIORegestry.get().isAvailable() == false||
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
