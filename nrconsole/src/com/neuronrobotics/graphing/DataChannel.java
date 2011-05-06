package com.neuronrobotics.graphing;

import org.jfree.data.xy.XYSeries;

public class DataChannel {
	private String title;
	private XYSeries series;
	private static long startTime = System.currentTimeMillis();
	
	public DataChannel(String title) {
		this.title = title;
		series = new XYSeries(toString());
	}
	
	public String toString() {
		return title;
	}
	
	public void graphValue(int value) {
		long time = System.currentTimeMillis() - startTime ;
		series.add((double) time/1000, value);
	}

	public void graphValue(double value) {
		long time = System.currentTimeMillis() - startTime ;
		series.add((double) time/1000, value);
	}
	
	public XYSeries getSeries() {
		return series;
	}

	public static void restart() {
		startTime = System.currentTimeMillis();
	}
	
	public void clear() {
		series.clear();
	}
}
