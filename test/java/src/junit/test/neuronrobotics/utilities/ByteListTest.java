package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;

// TODO: Auto-generated Javadoc
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
		byte testAray[] = new byte[(int) (tester.getStaticBufferSize()*3)];
		for(int i= 0;i<testAray.length;i++){
			testAray[i] = (byte) (Math.random()*255);
		}
		
		for(int j=0;j<3;j++){
			for(int i= 0;i<testAray.length;i++){
				tester.add(testAray[i]);
			}
			System.out.println("Read test");
			for(int i= 0;i<testAray.length;i++){
				assertTrue(tester.getByte(i) == testAray[i]);
			}
			
			System.out.println("Iterator test");
			int k=0;
			for(Byte b: tester){
				//System.out.println("Expecting "+testAray[k]+" Got "+b );
				assertTrue(b == testAray[k++]);
			}
			
			System.out.println("Pop test");
			for(int i= 0;i<testAray.length;i++){
				assertTrue(tester.pop() == testAray[i]);
			}
			System.out.println("Looped through index "+j);
		}
		
		//fail("Not yet implemented");
	}

}
