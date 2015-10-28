package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class PacketValidationTest.
 */
public class PacketValidationTest {

	/**
	 * Packet test.
	 */
	@Test
	public void packetTest() {
		Log.enableInfoPrint();
		BowlerDatagram bd = BowlerDatagramFactory.build(new MACAddress(), new PingCommand());
		System.out.println(bd);
		
		ByteList data = new ByteList(bd.getBytes());
		System.out.println(data);
		
		BowlerDatagram back = BowlerDatagramFactory.build(data);
		if (back == null)
			fail();
		System.out.println(back);
	}

}
