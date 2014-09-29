package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.neuronrobotics.sdk.common.IthreadedTimoutListener;
import com.neuronrobotics.sdk.common.ThreadedTimeout;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class TestTimer {

	private int timerTimedOut;

	@Test
	public void test() {
		ArrayList<ThreadedTimeout> timers= new ArrayList<ThreadedTimeout>();
		
		for(int j=0;j<5;j++){
			for(int i=0;i<10;i++){
				timers.add(new ThreadedTimeout());
			}
			int i=0;
			timerTimedOut = 0;
			for(ThreadedTimeout t : timers){
				t.initialize(500+(i++), new IthreadedTimoutListener() {
					@Override
					public void onTimeout(String message) {
						System.out.println(message);
						timerTimedOut++;
					}
				});
			}
			
			for(ThreadedTimeout t : timers){
				if(t.isTimedOut())
					fail();
			}
			
			ThreadUtil.wait(1000);
			
			if(timerTimedOut != timers.size())
				fail("One or more timers failed to time out, expected="+timers.size()+" got="+timerTimedOut);
			
			for(ThreadedTimeout t : timers){
				if(!t.isTimedOut())
					fail("Timer failed to time out");
			}
			timers.clear();
		}
		
		
	}

}
