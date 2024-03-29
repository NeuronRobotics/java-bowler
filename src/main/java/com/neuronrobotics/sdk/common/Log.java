/*******************************************************************************
 * Copyright 2010 Neuron Robotics, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.neuronrobotics.sdk.common;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.neuronrobotics.sdk.config.SDKBuildInfo;
// TODO: Auto-generated Javadoc
/**
 * This class is the Logging Class for the NRsdk.
 * @author rbreznak
 *
 */
public class Log {
	
	
	
	/** The Constant LOG. */
	public static final int LOG =-1;
	
	/** The Constant INFO. */
	public static final int INFO = 0;
	
	/** The Constant DEBUG. */
	public static final int DEBUG = 1;
	
	/** The Constant WARNING. */
	public static final int WARNING = 2;
	
	/** The Constant ERROR. */
	public static final int ERROR = 3;
	
	/** The instance. */
	private static Log instance;
	
	/** The messages. */
	private Message m;
	//private ArrayList<Message> messages = new ArrayList<Message>();
	
	/** The date format. */
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SS");
	
	/** The minprintlevel. */
	private int minprintlevel = WARNING;
	
	/** The systemprint. */
	private boolean systemprint = false;
	
	/** The debugprint. */
	private boolean debugprint = false;
	
	/** The out stream. */
	private static PrintStream outStream = System.out;
	
	/** The err stream. */
	private static PrintStream errStream = System.err;
	
	/** The use colored prints. */
	private boolean useColoredPrints=false;
	

	/**
	 * Instantiates a new log.
	 */
	private Log() {
		// private for singleton pattern
		add(SDKBuildInfo.getSDKVersionString(), INFO);
	}

	/**
	 * Log an error message.
	 *
	 * @param message the message to log as an error
	 */
	public static void error(String message) {
		instance().add(message, ERROR);
	}
	
	/**
	 * Log an warning message.
	 *
	 * @param message the message to log as a warning
	 */
	public static void warning(String message) {
		instance().add(message, WARNING);
	}
	
	/**
	 * Log a info message.
	 *
	 * @param message the message to log as a piece of information.
	 */
	public static void info(String message) {
		instance().add(message, INFO);
	}
	
	/**
	 * Log a string.
	 *
	 * @param message The string to log.
	 */
	public static void log(String message) {
		instance().add(message, LOG);	
	}
	
	/**
	 * Log a debug message.
	 *
	 * @param message The debug message to log
	 */
	public static void debug(String message) {
		instance().add(message, DEBUG);
	}
	
	/**
	 * Add a line to the log.
	 *
	 * @param message The line to add
	 */
	public static void add(String message) {
		instance().add(message, LOG);	
	}
	
	/**
	 * Add a string to the log with a specific importance.
	 *
	 * @param message the message to add
	 * @param importance the importance to log it as.
	 */
	private void add(String message, int importance) {
		
		if( importance < minprintlevel) {
			return;
		}
		if(m==null)
			m = new Message(message, importance);
		else{
			m.init(message, importance);
		}
		//messages.add(m);
		
		if(isPrinting() && importance >= minprintlevel && systemprint) {
			errStream.println(m);
			if(errStream != System.err)
				 System.err.println(m);
		}
		
		if(debugprint&& systemprint) {
			outStream.println("# " + message);
			if(outStream != System.out)
				 System.out.println(m);
		}
		
		
	}
	
	/**
	 * Enable printing of output to standard out.
	 *
	 * @param systemprint the systemprint
	 */
	public static void enableSystemPrint(boolean systemprint) {
		Log.instance().systemprint = systemprint;
	}
	
	/**
	 * Enable printing of debug output.
	 */
	
	public static void enableDebugPrint() {
		Log.enableSystemPrint(true);
		Log.setMinimumPrintLevel(DEBUG);
	}
	
	/**
	 * Enable printing of debug output.
	 *
	 * @param flag the flag
	 */
	
	public static void enableDebugPrint(boolean flag) {
		Log.enableSystemPrint(flag);
		Log.setMinimumPrintLevel(DEBUG);
	}
	
	/**
	 * Enable printing of debug output.
	 */
	
	public static void enableInfoPrint() {
		Log.enableSystemPrint(true);
		Log.setMinimumPrintLevel(INFO);
	}
	
	/**
	 * Enable printing of debug output.
	 */
	
	public static void enableWarningPrint() {
		Log.enableSystemPrint(true);
		Log.setMinimumPrintLevel(WARNING);
	}
	
	/**
	 * Enable printing of debug output.
	 */
	
	public static void enableErrorPrint() {
		Log.enableSystemPrint(true);
		Log.setMinimumPrintLevel(ERROR);
	}
	
	
	/**
	 * Set the minimum level of importance to dsplay.
	 * Messages below this wont be displayed.
	 * @param level	The minimu importance level
	 */
	public static void setMinimumPrintLevel(int level) {
		Log.instance().minprintlevel = level;
	}
	
	/**
	 * Set the minimum level of importance to dsplay.
	 * Messages below this wont be displayed.
	 *
	 * @return the minimum print level
	 */
	public static int getMinimumPrintLevel() {
		return Log.instance().minprintlevel;
	}
	
	/**
	 * Get the current log (singleton) instance.
	 *
	 * @return The log instance.
	 */
	public static Log instance() {
		if(instance == null) {
			instance = new Log();
		}
		return instance;
	}
	
	/**
	 * Get a string describing the given importance level.
	 *
	 * @param importance The given importance level.
	 * @return the importance
	 */
	public String getImportance(int importance) {
		switch(importance) {
		case INFO:
			return "Info";
		case WARNING:
			return "Warning";
		case ERROR:
			return "Error";
		case DEBUG:
			return "Debug";
		case LOG:
		default:
			return "Log";
		}
	}
	/**
	 * Get a string describing the given importance level.
	 *
	 * @param importance The given importance level.
	 * @return the importance
	 */
	public String getImportanceColor(int importance) {
		if(isUseColoredPrints()){
			switch(importance) {
			case INFO:
				return "\033[92m";// green
			case WARNING:
				return "\033[93m";// orange
			case ERROR:
				return "\033[31m";// red
			case DEBUG:
				return "\033[94m";// blue
			case LOG:
			default:
				return "\033[92m";// green
			}
		}
		return "";
	}
	
	/**
	 * get the current error PrintStream.
	 *
	 * @return the current Error PrintStream
	 */
	public static PrintStream getErrStream() {
		return errStream;
	}
	
	/**
	 * set the current error PrintStream.
	 *
	 * @param newerrStream the new err stream
	 */
	public static void setErrStream(PrintStream newerrStream) {
		errStream = newerrStream;
	}
	
	/**
	 * Get the current output PrintStream.
	 *
	 * @return The current output PrintStream
	 */
	public static PrintStream getOutStream() {
		return outStream;
	}
	
	/**
	 * Set the current output PrintStream.
	 *
	 * @param newoutStream the new out stream
	 */
	public static void setOutStream(PrintStream newoutStream) {
		outStream = newoutStream;
	}
	
	/**
	 * A log message.
	 *
	 * @author rbreznak
	 */
	private class Message {
		
		/** The message. */
		private String message;
		
		/** The importance. */
		private int importance;
		
		/** The datetime. */
		private Date datetime;
		
		/** The calling class. */
		private String callingClass;

		/**
		 * Instantiates a new message.
		 *
		 * @param message the message
		 * @param importance the importance
		 */
		public Message(String message, int importance) {
			init(message, importance);
		}
		
		/**
		 * Inits the.
		 *
		 * @param message the message
		 * @param importance the importance
		 */
		public void init(String message, int importance){
			this.message = message;
			this.importance = importance;
			datetime = new Date();
		      try
		      {
		         throw new Exception("Who called me?");
		      }
		      catch( Exception e )
		      {
		    	 callingClass= e.getStackTrace()[3].getClassName()+":"+e.getStackTrace()[3].getMethodName();
		      }
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			//return "\t\t\t\t[" + dateFormat.format(datetime) + "] " + " " + getImportance(importance) +" "+callingClass+ " :\n"+ message;
			return getImportanceColor(importance)+"\t\t\t\t[" + dateFormat.format(datetime) + "] " + " " + getImportance(importance) +" "+callingClass+ " :\n"+ message+getColorNormalizationCode();
		}
	}
	
	/**
	 * Gets the color normalization code.
	 *
	 * @return the color normalization code
	 */
	private String getColorNormalizationCode(){
		if(isUseColoredPrints())
			return "\033[39m";
		return "";
	}
	
	/**
	 * Checks if is use colored prints.
	 *
	 * @return true, if is use colored prints
	 */
	public static  boolean isUseColoredPrints() {
		return instance().useColoredPrints;
	}
	
	/**
	 * Sets the use colored prints.
	 *
	 * @param useColoredPrints the new use colored prints
	 */
	public static void setUseColoredPrints(boolean useColoredPrints) {
		instance().useColoredPrints = useColoredPrints;
	}

	/**
	 * Checks if is printing.
	 *
	 * @return true, if is printing
	 */
	public static boolean isPrinting() {
		// TODO Auto-generated method stub
		return instance().systemprint;
	}

	public static void error(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String sStackTrace = sw.toString(); // stack trace as a string
		Log.error(sStackTrace);
	}
	
}
