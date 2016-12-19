package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class RotationNRTest.
 */
public class RotationNRTest {

	/**
	 * Test.
	 */
	@Test
	public void test() {
		int failCount = 0;
		int iterations = 100;
		for (int i = 0; i < iterations; i++) {
			double tilt = Math.toRadians((Math.random() *360.0) -180);
			double elevation = Math.toRadians((Math.random() * 360.0) -180 );
			double azumus = Math.toRadians((Math.random() * 360.0) -180 );
			RotationNR rotTest = new RotationNR(Math.toDegrees(tilt), Math.toDegrees(azumus),
					Math.toDegrees(elevation));
			System.out.println("\n\nTest #" + i);
			System.out.println("Testing Az=" + Math.toDegrees(azumus) + " El=" + Math.toDegrees(elevation) + " Tl="
					+ Math.toDegrees(tilt));
			System.out.println("Got     Az=" + Math.toDegrees(rotTest.getRotationAzimuth()) + " El="
					+ Math.toDegrees(rotTest.getRotationElevation()) + " Tl="
					+ Math.toDegrees(rotTest.getRotationTilt()));
	
			if (!RotationNR.bound(tilt - .01, tilt + .01, rotTest.getRotationTilt())) {
				failCount++;
				System.err.println("Rotation Tilt is not consistant. expected " + Math.toDegrees(tilt) + " got "
						+ Math.toDegrees(rotTest.getRotationTilt())+
						" \t\tOff By "+(Math.toDegrees(tilt) - Math.toDegrees(rotTest.getRotationTilt()) )
						);
			}
			if (!RotationNR.bound(elevation - .01, elevation + .01, rotTest.getRotationElevation())) {
				failCount++;
				System.err.println("Rotation Elevation is not consistant. expected " + Math.toDegrees(elevation)
						+ " got " + Math.toDegrees(rotTest.getRotationElevation())+
						" \t\tOff By "+(Math.toDegrees(elevation) + Math.toDegrees(rotTest.getRotationElevation()) )
						
						);
			}
			if (!RotationNR.bound(azumus - .01, azumus + .01, rotTest.getRotationAzimuth())) {
				failCount++;
				System.err.println("Rotation Tilt is not consistant. expected " + Math.toDegrees(azumus) + " got "
						+ Math.toDegrees(rotTest.getRotationAzimuth())+
						" \t\tOff By "+(Math.toDegrees(azumus) - Math.toDegrees(rotTest.getRotationAzimuth()) )
						);
			}
			ThreadUtil.wait(20);
		}

		if (failCount > 1) {
			fail("Rotation failed " + failCount + " times of "+iterations*3);

		}

	}

}
