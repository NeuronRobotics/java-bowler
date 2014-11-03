package junit.test.neuronrobotics.namespace;

import org.junit.Test;

import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class RPCTest {

	@Test
	public void test() {
		GenericDevice dev = new GenericDevice(ConnectionDialog.promptConnection());
		dev.connect();

		dev.send(	"bcs.io.*;0.3;;",
				BowlerMethod.POST,
				"schv",
				new Object[]{0,2,1000});
		dev.disconnect();
	}

}
