package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;

//  Auto-generated Javadoc
/**
 * The Class ByteListTest.
 */
public class ByteListTest {

	/**
	 * Test.
	 */
	@Test
	public void test() {

		Log.enableDebugPrint();

		ByteList.setUseStaticBuffer(true);

		ByteList tester = new ByteList();

		assertTrue(tester != null);

		tester.setStaticBufferSize(5);
		byte testAray[] = new byte[(int) (tester.getStaticBufferSize() * 3)];
		for (int i = 0; i < testAray.length; i++) {
			testAray[i] = (byte) (Math.random() * 255);
		}

		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < testAray.length; i++) {
				tester.add(testAray[i]);
			}
			com.neuronrobotics.sdk.common.Log.error("Read test");
			for (int i = 0; i < testAray.length; i++) {
				assertTrue(tester.getByte(i) == testAray[i]);
			}

			com.neuronrobotics.sdk.common.Log.error("Iterator test");
			int k = 0;
			for (Byte b : tester) {
				// com.neuronrobotics.sdk.common.Log.error("Expecting "+testAray[k]+" Got "+b );
				assertTrue(b == testAray[k++]);
			}

			com.neuronrobotics.sdk.common.Log.error("Pop test");
			for (int i = 0; i < testAray.length; i++) {
				assertTrue(tester.pop() == testAray[i]);
			}
			com.neuronrobotics.sdk.common.Log.error("Looped through index " + j);
		}

		// fail("Not yet implemented");
	}

}
