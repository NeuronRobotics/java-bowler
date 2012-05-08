package com.neuronrobotics.sdk.common;

public class Tracer {
	public static String calledFrom() {
		try
	      {
	         throw new Exception("Who called me?");
	      }
	      catch( Exception e )
	      {
	    	 return e.getStackTrace()[2].getClassName()+":"+e.getStackTrace()[2].getMethodName(); 
	      }
	}
}
