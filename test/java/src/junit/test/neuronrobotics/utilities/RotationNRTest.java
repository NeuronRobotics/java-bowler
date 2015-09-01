package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;

public class RotationNRTest {

	@Test
	public void test() {
		double tilt= Math.toRadians(-31);
		double elevation= Math.toRadians(0);
		double azumus=Math.toRadians(-125);
		RotationNR rotTest = new RotationNR( Math.toDegrees(tilt),Math.toDegrees(elevation), Math.toDegrees(azumus));
		if(!RotationNR.bound(tilt-.001, tilt+.001, rotTest.getRotationTilt()))
			fail("Rotation Tilt is not consistant. expected "+ Math.toDegrees(tilt)+" got " +Math.toDegrees(rotTest.getRotationTilt()));
		if(!RotationNR.bound(elevation-.001, elevation+.001, rotTest.getRotationElevation()))
			fail("Rotation Elevation is not consistant. expected "+ Math.toDegrees(elevation)+" got " +Math.toDegrees(rotTest.getRotationElevation()));
		if(!RotationNR.bound(azumus-.001, azumus+.001, rotTest.getRotationAzimuth()))
			fail("Rotation Tilt is not consistant. expected "+Math.toDegrees( azumus)+" got " +Math.toDegrees(rotTest.getRotationAzimuth()));
		
		tilt= Math.toRadians(-31);
		elevation= Math.toRadians(0);
		azumus=Math.toRadians(125);
		rotTest = new RotationNR( Math.toDegrees(tilt),Math.toDegrees(elevation), Math.toDegrees(azumus));
		if(!RotationNR.bound(tilt-.001, tilt+.001, rotTest.getRotationTilt()))
			fail("Rotation Tilt is not consistant. expected "+ Math.toDegrees(tilt)+" got " +Math.toDegrees(rotTest.getRotationTilt()));
		if(!RotationNR.bound(elevation-.001, elevation+.001, rotTest.getRotationElevation()))
			fail("Rotation Elevation is not consistant. expected "+ Math.toDegrees(elevation)+" got " +Math.toDegrees(rotTest.getRotationElevation()));
		if(!RotationNR.bound(azumus-.001, azumus+.001, rotTest.getRotationAzimuth()))
			fail("Rotation Tilt is not consistant. expected "+Math.toDegrees( azumus)+" got " +Math.toDegrees(rotTest.getRotationAzimuth()));
		
		
	}

}
