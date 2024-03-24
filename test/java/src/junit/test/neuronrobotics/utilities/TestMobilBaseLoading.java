package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.MobileBase;
import com.neuronrobotics.sdk.addons.kinematics.VitaminLocation;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class TestMobilBaseLoading {

	@Test
	public void test() throws IOException {
		File file = new File("src/main/resources/com/neuronrobotics/sdk/addons/kinematics/xml/NASASuspensionTest.xml");
		
		String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		MobileBase base = new MobileBase(new FileInputStream(file));
//		base.getAllDHChains()
//				.get(0)
//				.getLinkConfiguration(0)
//				.setVitamin(new VitaminLocation("test1", "hobbyServo", "mg92b", new TransformNR(0, 1, 4)));
//		base.setVitamin(new VitaminLocation("test1", "hobbyServo", "mg92b", new TransformNR(0, 1, 4)));
//		base.setVitamin(new VitaminLocation("test2", "hobbyServo", "mg92b", new TransformNR(0, 1, 4)));
//		base.setVitamin(new VitaminLocation("test1", "hobbyServo", "mg92b", new TransformNR(0, 1, 4)));

		if(Math.abs(0.1-base.getMassKg())>0.0001) {
			fail("Base mass failed to load! expected "+0.1+" got "+base.getMassKg());
		}
		String read = base.getXml();
		if(!content.contentEquals(read)) {
			File out = new File("src/main/resources/com/neuronrobotics/sdk/addons/kinematics/xml/NASASuspensionTestOUTPUT.xml");
			Files.write( Paths.get(out.getAbsolutePath()), read.getBytes());
			System.out.println("diff "+file.getAbsolutePath()+" "+out.getAbsolutePath());
			fail("What was loaded failed to match the source");
		}
	}

}
