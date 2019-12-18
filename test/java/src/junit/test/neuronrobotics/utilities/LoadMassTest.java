package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.MobileBase;

public class LoadMassTest {

	@Test
	public void test() throws FileNotFoundException {

		File f = new File("carlRobot.xml");
		if (f.exists()) {
			MobileBase pArm = new MobileBase(new FileInputStream(f));
			System.out.println("Mass = "+pArm.getMassKg());
			assertEquals(99, pArm.getMassKg(),0.1);
		}
	}

}
