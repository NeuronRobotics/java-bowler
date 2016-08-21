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

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.INewLinkProvider;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.MobileBase;
import com.neuronrobotics.sdk.addons.kinematics.PidRotoryLink;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.parallel.ParallelGroup;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

public class ExternalLinkProviderTest {

	@Test
	public void test() throws Exception {
		main(null);
	}
	
	private static class myLinkImplementation extends PidRotoryLink{
		static VirtualGenericPIDDevice virtual=new VirtualGenericPIDDevice();
		public myLinkImplementation( LinkConfiguration conf) {
			super(virtual.getPIDChannel(conf.getHardwareIndex()), conf);
			System.out.println("Loading MY link");
		}
	}

	public static void main(String[] args) throws Exception {
		
		File f = new File("unknownLink.xml");
		if (f.exists()) {
			
			String typeTag = "myUserType";
			
			INewLinkProvider provider = new INewLinkProvider() {
				
				@Override
				public AbstractLink generate(LinkConfiguration conf) {
					System.out.println("Loading my type link factory call");
					return new myLinkImplementation(conf);
				}
			};
			
			LinkFactory.addLinkProvider(typeTag, provider );
			
			
			MobileBase pArm = new MobileBase(new FileInputStream(f));
			//System.out.println(pArm.getXml());
			
			try{
				String xmlParsed = pArm.getXml();
				BufferedWriter writer = null;
	
				writer = new BufferedWriter(new FileWriter("unknownLink2.xml"));
				writer.write(xmlParsed);
	
				if (writer != null)
					writer.close();
	
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			pArm.disconnect();
			System.exit(0);
		}else
			System.err.println("No config file");
		
	}

}
