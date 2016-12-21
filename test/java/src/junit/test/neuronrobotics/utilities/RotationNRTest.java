package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.MobileBase;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.parallel.ParallelGroup;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class RotationNRTest.
 */
public class RotationNRTest {

	/**
	 * Test.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void test() throws FileNotFoundException {
		int failCount = 0;
		int iterations = 10;
		RotationOrder[] list = { RotationOrder.XYZ
				
		};
		RotationConvention[] conventions = {  RotationConvention.VECTOR_OPERATOR };
		for (RotationConvention conv : conventions) {
			RotationNR.setConvention(conv);
			System.out.println("\n\nUsing convention " + conv.toString());
			for (RotationOrder ro : list) {
				RotationNR.setOrder(ro);
				System.out.println("\n\nUsing rotationOrder " + ro.toString());
				failCount = 0;
				for (int i = 0; i < iterations; i++) {
					double tilt = Math.toRadians((Math.random() * 359) - 179.5);
					double elevation = Math.toRadians((Math.random() * 180) - 90);
					double azumus = Math.toRadians((Math.random() * 359) - 179.5);
					RotationNR rotTest = new RotationNR(Math.toDegrees(tilt), Math.toDegrees(azumus),
							Math.toDegrees(elevation));
					System.out.println("\n\nTest #" + i);
					System.out.println("Testing Az=" + Math.toDegrees(azumus) + " El=" + Math.toDegrees(elevation)
							+ " Tl=" + Math.toDegrees(tilt));
					System.out.println("Got     Az=" + Math.toDegrees(rotTest.getRotationAzimuth()) + " El="
							+ Math.toDegrees(rotTest.getRotationElevation()) + " Tl="
							+ Math.toDegrees(rotTest.getRotationTilt()));

					if (!RotationNR.bound(tilt - .01, tilt + .01, rotTest.getRotationTilt())) {
						failCount++;
						System.err.println("Rotation Tilt is not consistant. expected " + Math.toDegrees(tilt) + " got "
								+ Math.toDegrees(rotTest.getRotationTilt()) + " \t\tOff By "
								+ (Math.toDegrees(tilt) - Math.toDegrees(rotTest.getRotationTilt())));
					}
					if (!RotationNR.bound(elevation - .01, elevation + .01, rotTest.getRotationElevation())) {
						failCount++;
						System.err.println("Rotation Elevation is not consistant. expected " + Math.toDegrees(elevation)
								+ " got " + Math.toDegrees(rotTest.getRotationElevation()) + " \t\tOff By "
								+ (Math.toDegrees(elevation) + Math.toDegrees(rotTest.getRotationElevation()))

						);
					}
					if (!RotationNR.bound(azumus - .01, azumus + .01, rotTest.getRotationAzimuth())) {
						failCount++;
						System.err.println("Rotation azumus is not consistant. expected " + Math.toDegrees(azumus)
								+ " got " + Math.toDegrees(rotTest.getRotationAzimuth()) + " \t\tOff By "
								+ (Math.toDegrees(azumus) - Math.toDegrees(rotTest.getRotationAzimuth())));
					}
					ThreadUtil.wait(20);
				}
				if (failCount < 1) {
					System.out.println("Orentation " + ro.toString() + " worked ina all cases");

				}
			}
		}
		new RotationNR(0.38268343236509234, -1.2443977214448087E-17, 2.1758644300923683E-16, -0.9238795325112857);
		File f = new File("carlRobot.xml");
		if (f.exists()) {
			MobileBase pArm = new MobileBase(new FileInputStream(f));
			try{
				String xmlParsed = pArm.getXml();
				BufferedWriter writer = null;
	
				writer = new BufferedWriter(new FileWriter("carlRobot2.xml"));
				writer.write(xmlParsed);
	
				if (writer != null)
					writer.close();
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			pArm.disconnect();
			System.exit(0);
		}
		if (failCount > 1) {
			fail("Rotation failed " + failCount + " times of " + ((iterations * 3 * list.length) - 0));

		}
	}

}
