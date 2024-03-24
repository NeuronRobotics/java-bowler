package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.MobileBase;

public class TestMobilBaseLoading {

	@Test
	public void test() throws FileNotFoundException {
		MobileBase base = new MobileBase(new FileInputStream(new File("src/main/resources/com/neuronrobotics/sdk/addons/kinematics/xml/NASASuspensionTest.xml")));
		
		if(Math.abs(0.1-base.getMassKg())>0.0001) {
			fail("Base mass failed to load! expected "+0.1+" got "+base.getMassKg());
		}
	}

}
