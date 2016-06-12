package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.MobileBase;

public class ParallelArmTest {

	@Test
	public void test() {
		main(null);
	}
	
	public static void main(String [] args){
		File f = new File("/home/hephaestus/bowler-workspace/gistcache/gist.github.com/33f2c10ab3adc5bd91f0a58ea7f24d14/ParalellArm.xml");
		 
		 try {
			MobileBase pArm=new MobileBase(new FileInputStream(f));
			String xmlParsed  =  pArm.getXml();
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("paralleloutput.xml"));
				writer.write(xmlParsed);

			} catch (IOException e) {
			} finally {
				try {
					if (writer != null)
						writer.close();
				} catch (IOException e) {
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
