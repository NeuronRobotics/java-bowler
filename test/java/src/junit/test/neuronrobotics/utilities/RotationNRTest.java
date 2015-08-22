package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;

public class RotationNRTest {

	@Test
	public void test() {
		double rotx= Math.toRadians(22);
		double roty= Math.toRadians(33);
		double rotz=Math.toRadians(44);
		RotationNR rotTest = new RotationNR( Math.toDegrees(rotx),Math.toDegrees(roty), Math.toDegrees(rotz));
		if((int)Math.toDegrees(rotTest.getRotationX())!= (int)Math.toDegrees(rotx))
			fail("Rotation x is not consistant. expected "+ Math.toDegrees(rotx)+" got " +Math.toDegrees(rotTest.getRotationX()));
		if((int)Math.toDegrees(rotTest.getRotationY())!= (int)Math.toDegrees(roty))
			fail("Rotation y is not consistant. expected "+ Math.toDegrees(roty)+" got " +Math.toDegrees(rotTest.getRotationY()));
		if((int)Math.toDegrees(rotTest.getRotationZ())!= (int)Math.toDegrees(rotz))
			fail("Rotation z is not consistant. expected "+Math.toDegrees( rotz)+" got " +Math.toDegrees(rotTest.getRotationZ()));
		
		
		
	}

}
