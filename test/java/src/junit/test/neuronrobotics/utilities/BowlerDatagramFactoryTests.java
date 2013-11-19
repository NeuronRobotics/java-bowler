package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class BowlerDatagramFactoryTests {

	@Test
	public void test() {
		int testBifferSize = 10*BowlerDatagramFactory.getDefaultPoolSize();//Must be a factor of default pool size
		
		ArrayList<BowlerDatagram> myList = new ArrayList<BowlerDatagram>();
		//verify initial state
		if(BowlerDatagramFactory.getCurrentPoolSize() != BowlerDatagramFactory.getDefaultPoolSize()){
			fail();
		}
		
		for(int i=0;i<testBifferSize;i++){
			myList.add(BowlerDatagramFactory.build(new MACAddress(), new PingCommand()));
		}
		//verify that the pool has been increased
		if(BowlerDatagramFactory.getCurrentPoolSize() != testBifferSize){
			fail();
		}
		
		for(BowlerDatagram b:myList){
			if(b.isFree())
				fail();//if any packets not marked as allocated
		}
		ThreadUtil.wait((int) ((double)BowlerDatagramFactory.getPacketTimeout()*1.1));//wait for packets to timeout
		for(BowlerDatagram b:myList){
			if(!b.isFree())
				fail();//any packets that failed to timeout
		}
		//refill the array
		myList.clear();
		for(int i=0;i<testBifferSize;i++){
			myList.add(BowlerDatagramFactory.build(new MACAddress(), new PingCommand()));
		}
		for(BowlerDatagram b:myList){
			if(b.isFree())
				fail();//if any packets not marked as before freeing
			BowlerDatagramFactory.freePacket(b);
			if(!b.isFree())
				fail();//if any packets not marked as free after freeing it
		}
		
		
	}

}
