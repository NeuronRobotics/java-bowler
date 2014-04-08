package com.neuronrobotics.test.nrdk.network;

public class SpedTest {
	 public static void sleepNanos (long nanoDuration){
		 long start=System.nanoTime();
        final long end = start + nanoDuration-550;
        long timeLeft = end - start;
        do {
            //java.util.concurrent.locks.LockSupport.parkNanos(1);
            timeLeft = end - System.nanoTime();
        } while (timeLeft > 0);
    }
	public static void main(String [] args){
		for (int i=0;i<100;i++){
			long start,end;
			start = System.nanoTime();

			//sleepNanos (0);

			end = System.nanoTime();
			System.out.println("Loop took: "+(end-start));
		}
	}
}
