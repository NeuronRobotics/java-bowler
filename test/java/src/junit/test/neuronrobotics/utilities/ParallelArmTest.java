package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.MobileBase;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.parallel.ParallelGroup;
import com.neuronrobotics.sdk.common.Log;

public class ParallelArmTest {

	@Test
	public void test() throws Exception {
		//main(null);
	}

	public static void main(String[] args) throws Exception {
		
		File f = new File("paralleloutput.xml");
		if (f.exists()) {
			MobileBase pArm = new MobileBase(new FileInputStream(f));
			pArm.setGlobalToFiducialTransform(new TransformNR());
			try{
				String xmlParsed = pArm.getXml();
				BufferedWriter writer = null;
	
				writer = new BufferedWriter(new FileWriter("paralleloutput2.xml"));
				writer.write(xmlParsed);
	
				if (writer != null)
					writer.close();
	
				ParallelGroup group = pArm.getParallelGroup("ParallelArmGroup");
				
				Log.enableInfoPrint();
				//TransformNR Tip = group.getCurrentTaskSpaceTransform();
				TransformNR Tip = new TransformNR(87,12,25,new RotationNR());
				
				for(DHParameterKinematics kin:pArm.getAppendages()){
					kin.setDesiredJointSpaceVector(new double[]{0,0,0}, 0);
					kin.setDesiredTaskSpaceTransform(Tip, 0);
					
					System.out.println("Arm "+kin.getScriptingName()+"setting to : "+Tip);
				}
				assertEquals(Tip.getX(), group.getCurrentTaskSpaceTransform().getX(), 1);
				group.setDesiredTaskSpaceTransform(Tip.copy(), 0);
				for (DHParameterKinematics limb : group.getConstituantLimbs()) {
					TransformNR TipOffset = group.getTipOffset().get(limb);
					TransformNR newTip = limb.getCurrentTaskSpaceTransform().times(TipOffset);
	
					System.out.println("Expected tip to be " + Tip.getX() + " and got: " + newTip.getX());
					assertTrue(!Double.isNaN(Tip.getX()));
					assertEquals(Tip.getX(), newTip.getX(), 1);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			pArm.disconnect();
		}
		
	}

}
