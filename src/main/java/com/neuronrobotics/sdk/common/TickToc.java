package com.neuronrobotics.sdk.common;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TickToc {
	private static final DecimalFormat df = new DecimalFormat(".000");
	private static boolean enabled=false;
	
	public static class Pair {
		public long integer;

		public String message;
		public Pair(long timestamp,String message) {
			integer=timestamp;
			this.message=message;
		}
		public void print(Pair start,Pair previous) {
			double tookms = integer-start.integer;
			String m=" took "+df.format(tookms/1000.0)+" seconds ";
			if(previous!=null) {
				double diffms = integer-previous.integer;
				m=m+" from last event "+df.format(diffms/1000.0)+" seconds ";
			}
			m=m+" "+message;
			System.out.println(m);
		}
	}

	private static ArrayList<Pair> events = new ArrayList<>();

	public static void tic(String message) {
		if(!isEnabled())
			return;
		events.add(new Pair(System.currentTimeMillis(), message));
	}
	
	
	public static void clear() {
		events.clear();
	}
	public static void toc() {
		if(!isEnabled())
			return;
		events.add(new Pair(System.currentTimeMillis(), "Toc end event"));
		Pair start = events.remove(0);
		Pair previous=null;
		System.out.println("\n\n");
		for (int i = 0; i < events.size(); i++) {
			Pair p = events.get(i);
			p.print(start,previous);
			previous=p;
		}
		clear();
	}


	public static boolean isEnabled() {
		return enabled;
	}


	public static void setEnabled(boolean enabled) {
		TickToc.enabled = enabled;
		if(!enabled)
			clear();
		else {
			System.out.println("Start TickToc");
			tic("Tick Tock start");
		}
	}
}
