package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;

public class RotationNRTest {

	@Test
	public void test() {
		double rotx= Math.toRadians(-31);
		double roty= Math.toRadians(0);
		double rotz=Math.toRadians(-125);
		RotationNR rotTest = new RotationNR( Math.toDegrees(rotx),Math.toDegrees(roty), Math.toDegrees(rotz));
		if(!RotationNR.bound(rotx-.001, rotx+.001, rotTest.getRotationBank()))
			fail("Rotation Bank is not consistant. expected "+ Math.toDegrees(rotx)+" got " +Math.toDegrees(rotTest.getRotationBank()));
		if(!RotationNR.bound(roty-.001, roty+.001, rotTest.getRotationAttitude()))
			fail("Rotation Attitude is not consistant. expected "+ Math.toDegrees(roty)+" got " +Math.toDegrees(rotTest.getRotationAttitude()));
		if(!RotationNR.bound(rotz-.001, rotz+.001, rotTest.getRotationHeading()))
			fail("Rotation Heading is not consistant. expected "+Math.toDegrees( rotz)+" got " +Math.toDegrees(rotTest.getRotationHeading()));
		
		
		
	}

}
