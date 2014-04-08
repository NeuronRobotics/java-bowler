package com.neuronrobotics.test.nrdk.network;

import com.neuronrobotics.sdk.util.IThreadedNsTimerListener;
import com.neuronrobotics.sdk.util.ThreadedNsTimer;

public class SpedTest implements IThreadedNsTimerListener{
	
	boolean fired=false;
	long nstime;
	long nsDiff;
	int diffindex=0;
	long diffs[] = new long[1000];
	ThreadedNsTimer timer;
	long tmerTarget = 1000000;
	public SpedTest(){
		nstime = System.nanoTime();
		timer = new ThreadedNsTimer(this, tmerTarget,false );
		timer.start();
		nstime = timer.getStartTime();

	}

	public static void main(String [] args){
		
		new SpedTest();
	}

	@Override
	public void onTimerInterval(long index) {
		
		nsDiff = System.nanoTime()- (nstime);

		diffs[diffindex++]=nsDiff;
		if(diffindex==diffs.length){
			timer.setRunning(false);
			int fails=0;
			for(int i=1;i<diffs.length;i++){
				double val  = (double)(diffs[i]-diffs[i-1]-tmerTarget)/1000.0;
				if(val>10){
					fails++;
					System.out.println("Time diff at "+i+" is "+val/1000);
				}
			}
			if(fails>0)
				System.err.println("Failed "+fails+" times");
			System.exit(0);
		}
	}
}
