package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.junit.Test;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.INewLinkProvider;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.MobileBase;
import com.neuronrobotics.sdk.addons.kinematics.PidRotoryLink;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

public class ExternalLinkProviderTest {

	@Test
	public void test() throws Exception {
		// main(null);
	}
	static VirtualGenericPIDDevice virtual = new VirtualGenericPIDDevice("TestDevice");

	private static class myLinkImplementation extends PidRotoryLink {
		public myLinkImplementation(LinkConfiguration conf) {
			super(virtual.getPIDChannel(conf.getHardwareIndex()), conf, true);
			com.neuronrobotics.sdk.common.Log.error("Loading MY link");
		}
	}

	public static void main(String[] args) throws Exception {

		File f = new File("unknownLink.xml");
		if (f.exists()) {

			String typeTag = "myUserType";

			INewLinkProvider provider = new INewLinkProvider() {

				@Override
				public AbstractLink generate(LinkConfiguration conf) {
					com.neuronrobotics.sdk.common.Log.error("Loading my type link factory call");
					return new myLinkImplementation(conf);
				}
			};

			LinkFactory.addLinkProvider(typeTag, provider);

			MobileBase pArm = new MobileBase(new FileInputStream(f));
			// com.neuronrobotics.sdk.common.Log.error(pArm.getXml());

			try {
				String xmlParsed = pArm.getXml();
				BufferedWriter writer = null;

				writer = new BufferedWriter(new FileWriter("unknownLink2.xml"));
				writer.write(xmlParsed);

				if (writer != null)
					writer.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			pArm.disconnect();
			System.exit(0);
		} else
			com.neuronrobotics.sdk.common.Log.error("No config file");

	}

}
