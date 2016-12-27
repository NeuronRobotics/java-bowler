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
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void test() throws FileNotFoundException {
		int failCount = 0;
		int iterations = 100;
		RotationOrder[] list = {  RotationOrder.XYZ
									// RotationOrder.XZY,
									// RotationOrder.YXZ,
									// RotationOrder.YZX,
				//RotationOrder.ZXY, RotationOrder.ZYX, RotationOrder.XYX, RotationOrder.XZX, RotationOrder.YXY,
				//RotationOrder.YZY, RotationOrder.ZXZ, RotationOrder.ZYZ 
				};
		RotationConvention[] conventions = { RotationConvention.VECTOR_OPERATOR };
		for (RotationConvention conv : conventions) {
			// RotationNR.setConvention(conv);
			System.out.println("\n\nUsing convention " + conv.toString());
			for (RotationOrder ro : list) {
				// RotationNR.setOrder(ro);
				System.out.println("\n\nUsing rotationOrder " + ro.toString());
				failCount = 0;
				for (int i = 0; i < iterations; i++) {
					double tilt = Math.toRadians((Math.random() * 360) - 180);
					double elevation = Math.toRadians((Math.random() * 360) - 180);
					double azumus = Math.toRadians((Math.random() * 360) - 180);
					try {
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
							System.err.println("Rotation Tilt is not consistant. expected " + Math.toDegrees(tilt)
									+ " got " + Math.toDegrees(rotTest.getRotationTilt()) + " \t\tOff By "
									+ (Math.toDegrees(tilt) - Math.toDegrees(rotTest.getRotationTilt())));
						}
						if (!RotationNR.bound(elevation - .01, elevation + .01, rotTest.getRotationElevation())) {
							failCount++;
							System.err.println("Rotation Elevation is not consistant. expected "
									+ Math.toDegrees(elevation) + " got "
									+ Math.toDegrees(rotTest.getRotationElevation()) + " \t\tOff By "
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
					} catch (NumberFormatException ex) {
						if(elevation >=Math.PI/2 || elevation <=-Math.PI/2){
							System.out.println("Invalid numbers rejected ok");
						}
					}

				}
				if (failCount < 1) {
					System.out.println("Orentation " + ro.toString() + " worked in all cases");

				}
				// frame();
				// frame2();
				System.out.println("Frame test passed with " + ro);
				//return;
			}
		}

		File f = new File("carlRobot.xml");
		if (f.exists()) {
			MobileBase pArm = new MobileBase(new FileInputStream(f));
			try {
				String xmlParsed = pArm.getXml();
				BufferedWriter writer = null;

				writer = new BufferedWriter(new FileWriter("carlRobot2.xml"));
				writer.write(xmlParsed);

				if (writer != null)
					writer.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			pArm.disconnect();
		}
		if (failCount > 1) {
			fail("Rotation failed " + failCount + " times of " + ((iterations * 3 * list.length) - 0));

		}
	}

//	public void frame() {
//		double w = 0.25021580750394473;
//		double x = -0.5895228206035708;
//		double y = 0.12359002177935843;
//		double z = 0.758010817983053;
//		RotationNR knownAngles = new RotationNR(w, x, y, z);
//		assertArrayEquals(new double[] { w, x, y, z },
//				new double[] { knownAngles.getRotationMatrix2QuaturnionW(), knownAngles.getRotationMatrix2QuaturnionX(),
//						knownAngles.getRotationMatrix2QuaturnionY(), knownAngles.getRotationMatrix2QuaturnionZ() },
//				0.0000001);
//		double knownTilt = Math.toDegrees(knownAngles.getRotationTilt());
//		double knownAz = Math.toDegrees(knownAngles.getRotationAzimuth());
//		double knownel = Math.toDegrees(knownAngles.getRotationElevation());
//		System.out.println("Known angles are az=" + knownAz + " el=" + knownel + " tilt=" + knownTilt);
//
//		RotationNR doubleCheck = new RotationNR(knownTilt, knownAz, knownel);
//		assertArrayEquals(
//				new double[] { doubleCheck.getRotationMatrix2QuaturnionW(), doubleCheck.getRotationMatrix2QuaturnionX(),
//						doubleCheck.getRotationMatrix2QuaturnionY(), doubleCheck.getRotationMatrix2QuaturnionZ() },
//				new double[] { knownAngles.getRotationMatrix2QuaturnionW(), knownAngles.getRotationMatrix2QuaturnionX(),
//						knownAngles.getRotationMatrix2QuaturnionY(), knownAngles.getRotationMatrix2QuaturnionZ() },
//				0.0000001);
//
//		assertArrayEquals(new double[] { knownTilt, knownel, knownAz }, new double[] { -111.422, -72.858, 37.570 },
//				0.01);
//	}
//
//	public void frame2() {
//		double w = 0.29405190560732924;
//		double x = 0.5230342577988376;
//		double y = -0.32364491993997213;
//		double z = 0.7315890976323846;
//		RotationNR knownAngles = new RotationNR(w, x, y, z);
//		assertArrayEquals(new double[] { w, x, y, z },
//				new double[] { knownAngles.getRotationMatrix2QuaturnionW(), knownAngles.getRotationMatrix2QuaturnionX(),
//						knownAngles.getRotationMatrix2QuaturnionY(), knownAngles.getRotationMatrix2QuaturnionZ() },
//				0.0000001);
//		double knownTilt = Math.toDegrees(knownAngles.getRotationTilt());
//		double knownAz = Math.toDegrees(knownAngles.getRotationAzimuth());
//		double knownel = Math.toDegrees(knownAngles.getRotationElevation());
//		System.out.println("Known angles are az=" + knownAz + " el=" + knownel + " tilt=" + knownTilt);
//
//		RotationNR doubleCheck = new RotationNR(knownTilt, knownAz, knownel);
//		assertArrayEquals(
//				new double[] { doubleCheck.getRotationMatrix2QuaturnionW(), doubleCheck.getRotationMatrix2QuaturnionX(),
//						doubleCheck.getRotationMatrix2QuaturnionY(), doubleCheck.getRotationMatrix2QuaturnionZ() },
//				new double[] { knownAngles.getRotationMatrix2QuaturnionW(), knownAngles.getRotationMatrix2QuaturnionX(),
//						knownAngles.getRotationMatrix2QuaturnionY(), knownAngles.getRotationMatrix2QuaturnionZ() },
//				0.0000001);
//
//		assertArrayEquals(new double[] { knownTilt, knownel, knownAz }, new double[] { 55.711, 107.132, -108.137 },
//				0.01);
//	}
}
