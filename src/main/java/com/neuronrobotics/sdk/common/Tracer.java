package com.neuronrobotics.sdk.common;

// TODO: Auto-generated Javadoc
/**
 * The Class Tracer.
 */
public class Tracer {
	
	/**
	 * Called from.
	 *
	 * @return the string
	 */
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
