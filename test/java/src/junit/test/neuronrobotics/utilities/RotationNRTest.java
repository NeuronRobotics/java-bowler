package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;

public class RotationNRTest {

	@Test
	public void test() {
		
		for(int i=0;i<100;i++){
			double tilt= Math.toRadians(Math.random()*360.0-180);
			double elevation= Math.toRadians(Math.random()*360.0-180);
			double azumus=Math.toRadians(Math.random()*360.0-180);
			RotationNR rotTest = new RotationNR( Math.toDegrees(tilt),Math.toDegrees(elevation), Math.toDegrees(azumus));
			System.out.println("\n\nTest #"+i);
			System.out.println("Testing Az="+Math.toDegrees(azumus)+
					" El="+Math.toDegrees(elevation)+
					" Tl="+Math.toDegrees(tilt));
			System.out.println("Got Az="+Math.toDegrees(rotTest.getRotationAzimuth())+
					" El="+Math.toDegrees(rotTest.getRotationElevation())+
					" Tl="+Math.toDegrees(rotTest.getRotationTilt()));
			if(!RotationNR.bound(tilt-.001, tilt+.001, rotTest.getRotationTilt()))
				fail("Rotation Tilt is not consistant. expected "+ Math.toDegrees(tilt)+" got " +Math.toDegrees(rotTest.getRotationTilt()));
			if(!RotationNR.bound(elevation-.001, elevation+.001, rotTest.getRotationElevation()))
				fail("Rotation Elevation is not consistant. expected "+ Math.toDegrees(elevation)+" got " +Math.toDegrees(rotTest.getRotationElevation()));
			if(!RotationNR.bound(azumus-.001, azumus+.001, rotTest.getRotationAzimuth()))
				fail("Rotation Tilt is not consistant. expected "+Math.toDegrees( azumus)+" got " +Math.toDegrees(rotTest.getRotationAzimuth()));
			
		}

	}

}
