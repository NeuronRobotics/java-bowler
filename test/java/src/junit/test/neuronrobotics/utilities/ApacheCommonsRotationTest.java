package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;

public class ApacheCommonsRotationTest {

	@Test
	public void test() {
		int failCount = 0;
		int iterations = 10;
		RotationOrder[] list = { RotationOrder.XYZ
				
		};
		RotationConvention[] conventions = { RotationConvention.FRAME_TRANSFORM, RotationConvention.VECTOR_OPERATOR };
		for (RotationConvention convention : conventions) {
			System.out.println("\n\nUsing convention " + convention.toString());
			for (RotationOrder order : list) {
				System.out.println("\n\nUsing rotationOrder " + order.toString());
				
				double tilt = Math.toRadians((Math.random() * 359) - 179.5);
				double elevation = Math.toRadians((Math.random() * 180) -90);
				double azumus = Math.toRadians((Math.random() * 359) - 179.5);
				
				Rotation tester = new Rotation(order, convention, azumus, elevation, tilt);
				
				double [] vals = tester.getAngles(order, convention);
				
				double tiltNew = vals[2];
				double elevationNew = vals[1];
				double azumusNew = vals[0];
				
				assertEquals(tilt, tiltNew, 0.001);
				assertEquals(elevation, elevationNew, 0.001);
				assertEquals(azumus, azumusNew, 0.001);

				
			}
		}
	}

}
