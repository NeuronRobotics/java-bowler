package com.neuronrobotics.replicator.driver.interpreter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.neuronrobotics.sdk.common.Log;

// TODO: Auto-generated Javadoc
/**
 * An extensible G-code interpreter. Parses a stream containing G-code commands,
 * stores register values, and executes handlers. The default handler set
 * converts position axes to mm if neccessary, converts relative positioning to
 * absolute, and prints a message for rapid and interpolated linear motion. You
 * can register additional handlers using addGHandler and addMHandler, which
 * compose with, rather than replacing, the previous implementations.
 * {@link GCodeInterpreter#addDefaultHandlers}
 * 
 * To use, bind handlers to at least G00 and G01, and feed in a stream of
 * G-code. An example can be found in 
 * 
 * @author Jonathan D.K. Gibbons
 * @version 1
 */
public class GCodeInterpreter {


	/**
	 * The list of currently active M-codes. This defines what handlers will be
	 * called for M codes, and must be kept in a useful order.
	 */
	ArrayList<Integer> mcodes = new ArrayList<Integer>();
	/**
	 * The list of currently active G-codes. This defines what handlers will be
	 * called for G codes, and must be kept in a useful order.
	 */
	ArrayList<Integer> gcodes = new ArrayList<Integer>(); // Keep this is an
															// order that works
															// properly. Code
															// implementations
															// are allowed to
															// modify the line.
	
	/** The error handler. */
															private CodeHandler errorHandler=null;
	/**
	 * The list of handlers for G codes. gHandlers[0] is the list of handlers
	 * for G0. The handlers must be called from last to first, to preserve
	 * composition semantics.
	 */
	List<CodeHandler> gHandlers[];

	/**
	 * Exclusive codes, implemented as a list of codes to clear before setting a
	 * given code.
	 */
	List<Integer> gClearOnSet[];

	/**
	 * The list of one-shot G codes. These are codes such as cycle times or
	 * setting offsets, which happen exactly once when they are in the stream.
	 */
	List<Integer> gOneShot;

	/**
	 * The list of handlers for M codes. This is stored and handled the same as
	 * for G codes.
	 */
	List<CodeHandler> mHandlers[];

	/**
	 * A comparator to define G code ordering. This gives the order in which G
	 * codes must be interpreted in a line - units conversion happens before
	 * conversion to absolute, which happens before motion modes.
	 */
	Comparator<Integer> gCodeOrdering;
	/**
	 * What axes are motion - this mostly defines which axes get
	 * relative/absolute and units conversion.
	 */
	char motion_axes[] = { 'X', 'Y', 'Z' };

	/**
	 * Stores the previous position and other data for the previous line of G
	 * code.
	 */
	GCodeLineData lastLine;
	/**
	 * Stores the currently in-progress position and other data for this line of
	 * G code.
	 */
	GCodeLineData nextLine;

	/**
	 * The thread that is currently parsing G-code; for sending interruptions to
	 * cancel prints.
	 */

	Thread interpretingThread;

	/** The executing lock. */
	ReentrantLock executingLock;
	
	/** The line number. */
	private int lineNumber=0;
	/**
	 * Default Constructor. This builds an interpreter and adds the default set
	 * of handlers and configuration to it. @see
	 * GCodeInterpreter#addDefaultHandlers
	 */
	@SuppressWarnings("unchecked")
	public GCodeInterpreter() {
		gHandlers = (List<CodeHandler>[]) new ArrayList<?>[310];
		mHandlers = (List<CodeHandler>[]) new ArrayList<?>[310];
		gClearOnSet = (List<Integer>[]) new List<?>[310]; // Maybe not the best
															// storage
															// mechanism. Good
															// in C...
		nextLine = new GCodeLineData();
		lastLine = new GCodeLineData();
		addDefaultHandlers();
		executingLock = new ReentrantLock();
	}
	
	
	/**
	 * Process single gcode line.
	 *
	 * @param line the line
	 * @throws Exception the exception
	 */
	public void processSingleGCODELine(String line) throws Exception{
		String delims;
		String[] tokens;
		
		delims = "[ ]+";
		tokens = line.split(delims);
		nextLine.storeWord('G', 0);
		nextLine.storeWord('M', 0);
		nextLine.storeWord('P', lineNumber);
		System.out.println("GCODE: "+line);
		
		for(int i=0;i<tokens.length;i++){
			tokens[i] = tokens[i].trim();
			if(!tokens[i].isEmpty()){
				double val = Double.parseDouble(tokens[i].substring(1));
				char code = tokens[i].charAt(0);
				if(code == 'M'){
					mcodes.add((int) val);
				}
				if(code == 'G'){
					int theCode = (int) val;
					if (gClearOnSet[theCode] != null)
						gcodes.removeAll(gClearOnSet[theCode]);
					gcodes.add(theCode);
				}
				Log.debug("Code Token: "+tokens[i]+" "+code+" "+val);
				nextLine.storeWord(code, val);
			}
		}
		//System.out.println(nextLine);
		executeLine(line);
	}

	/**
	 * Parses the line.
	 *
	 * @param r the r
	 * @throws Exception the exception
	 */
	private void parseLine(InputStream r) throws Exception { 
		BufferedReader br = new BufferedReader(new InputStreamReader(r));
		String line;
		boolean inCommentSection = false;
		lineNumber=0;
		while ((line = br.readLine()) != null) {
			lineNumber++;// lines in the file
			String delims;
			String[] tokens;
			if(line.indexOf(';')>-1){
				//this line contains a comment
				if(line.indexOf(';') ==0){
					// this is just a comment
					line = null;
				}else{
					delims = "[;]+";
					tokens = line.split(delims);
					//strip the comment and place the rest of the line 
					// in the to-be-parsed variable
					line = tokens[0];
				}
			}else{
				// no comment on this line
			}
			//Check for the block comment case
			if(line != null){
				if(line.indexOf('(')>-1){
					// block comment section
					inCommentSection = true;
					line = null;
				}
				
			}
			if(line != null){
				if(line.indexOf(')')>-1){
					// end block comment section
					inCommentSection = false;
					line = null;
				}
			}
			if(inCommentSection)
				line = null;
			
			// Check for the empty line case
			if(line != null){
				if (line.trim().isEmpty()){
					//empty line detect
					line = null;
				}
			}
			// OK, now we have a valid line
			if(line !=null){
				
				
				processSingleGCODELine( line);
			}
			
		}
		br.close();
	}


	/**
	 * Execute the action(s) specified by the already built-up line of G-code.
	 *
	 * @param rawLine the raw line
	 * @throws Exception the exception
	 */
	private void executeLine(String rawLine) throws Exception {
		
		Log.debug("Next Gcode Line " + nextLine);
		Log.debug("Active Gcodes: " + gcodes);
		Log.debug("Active Mcodes: " + mcodes);
		for (int m : mcodes)
			if (mHandlers[m] != null) {
				for (CodeHandler handler : mHandlers[m]) {
					handler.execute(lastLine, nextLine);
				}
			} else {
				// Log.debug("No implementation found for M"+m);
				if(getErrorHandler() ==null)
					throw new RuntimeException("No implementation found for M" + m);
				else{
					getErrorHandler().execute(lastLine, nextLine);
				}
			}
		for (int g : gcodes)
			if (gHandlers[g] != null) {
				for (CodeHandler handler : gHandlers[g]) {
					handler.execute(lastLine, nextLine);
				}
			} else {
				if(getErrorHandler() ==null)
					throw new RuntimeException("No implementation found for G" + g);
				else{
					getErrorHandler().execute(lastLine, nextLine);
				}
			}

		lastLine = nextLine;
		nextLine = new GCodeLineData(lastLine);
		gcodes.removeAll(gOneShot);
		mcodes.clear();
	}

	/**
	 * Main entry point; take an InputStream of G code and run the sequence of
	 * actions it describes.
	 *
	 * @param in the in
	 * @throws Exception the exception
	 */
	public void interpretStream(InputStream in) throws Exception {
		executingLock.lock();

		interpretingThread = Thread.currentThread();
		
		parseLine(in);

		interpretingThread = null;
		executingLock.unlock();
		
	}

	/**
	 * Nonblocking version of interpretStream(); fails rather than waiting.
	 *
	 * @param in the in
	 * @throws Exception the exception
	 */

	public void tryInterpretStream(InputStream in) throws Exception {
		if (executingLock.tryLock()) {
			try {
				interpretStream(in);
			} finally {
				executingLock.unlock();
			}
		} else {
			throw(new RuntimeException("Printer not ready"));
		}
	}

	/**
	 * Cancel a run of a G-code stream. This interrupts the thread that is
	 * currently parsing a G-code stream, canceling its execution.
	 *
	 * @return true, if successful
	 */
	public boolean cancel() {
		if (interpretingThread != null) {
			interpretingThread.interrupt();
			return true;
		}
		return false;
	}

	/**
	 * Add a handler for a G code. The new handler executes before any
	 * previously installed handler for the code, to permit composition; for
	 * instance, where the first-installed G01 handler specifies motion and
	 * flushes the commands to device, a handler installed later could handle
	 * tool behavior without flushing the device, and provide coordinated
	 * behavior.
	 * 
	 * @param code
	 *            the G code to bind this handler to.
	 * @param handler
	 *            the handler implementation.
	 */
	public void addGHandler(int code, CodeHandler handler) { // Should really do
																// boundschecking.
																// New code
																// happens
																// before old.
		if (gHandlers[code] == null)
			gHandlers[code] = new ArrayList<CodeHandler>();
		gHandlers[code].add(handler);
	}

	/**
	 * Clear any previous handlers for a code, and install a new one. This is
	 * usually unneccessary, but it allows you to clear a handler set and start
	 * from scratch.
	 * 
	 * @param code
	 *            the G code to bind this handler to.
	 * @param handler
	 *            the handler implementation.
	 */
	public void setGHandler(int code, CodeHandler handler) { // For overriding
																// all default
																// behavior of a
																// code.
		handler.setSubHandlers(gHandlers[code]);
		gHandlers[code] = new ArrayList<CodeHandler>();
		gHandlers[code].add(handler);
	}

	/**
	 * Add a handler for an M code. The new handler executes before any
	 * previously installed handler for the code, to permit composition; for
	 * instance, where the first-installed G01 handler specifies motion and
	 * flushes the commands to device, a handler installed later could handle
	 * tool behavior without flushing the device, and provide coordinated
	 * behavior.
	 * 
	 * @param code
	 *            the G code to bind this handler to.
	 * @param handler
	 *            the handler implementation.
	 */
	public void addMHandler(int code, CodeHandler handler) { // Should really do
																// boundschecking.
																// New code
																// happens
																// before old.
		if (mHandlers[code] == null)
			mHandlers[code] = new ArrayList<CodeHandler>();
		mHandlers[code].add(handler);
	}

	/**
	 * Clear any previous handlers for a code, and install a new one. This is
	 * usually unneccessary, but it allows you to clear a handler set and start
	 * from scratch.
	 * 
	 * @param code
	 *            the G code to bind this handler to.
	 * @param handler
	 *            the handler implementation.
	 */
	public void setMHandler(int code, CodeHandler handler) { // For overriding
																// all default
																// behavior of a
																// code.
		handler.setSubHandlers(mHandlers[code]);
		mHandlers[code] = new ArrayList<CodeHandler>();
		mHandlers[code].add(handler);
	}

	/**
	 * Add rules for how to sort G codes before executing them. The new and old
	 * Comparators are combined; whenever the new comparator returns equal, the
	 * old comparator is consulted, permitting straightforward overridability.
	 * 
	 * For instance, to add a rule stating that the handlers for code G07 should
	 * always execute after those for G09:
	 * 
	 * <pre>
	 * interp.addGSorting(new Comparator&lt;Integer&gt;() {
	 * 	public int compare(Integer c1, Integer c2) {
	 * 		if (c1 == 7 &amp;&amp; c2 == 9)
	 * 			return -1;
	 * 		if (c1 == 9 &amp;&amp; c2 == 7)
	 * 			return 1;
	 * 		return 0;
	 * 	}
	 * });
	 * </pre>
	 * 
	 * Note: this does <em>not</em> implement any sort of transitivity, that is
	 * left to the new comparator, and is required for the contract of a
	 * comparator.
	 * 
	 * @param c
	 *            the comparator implementing the new ordering rules.
	 */
	public void addGSorting(final Comparator<Integer> c) {
		if (gCodeOrdering == null) {
			gCodeOrdering = c;
			return;
		}
		// ...perhaps I've had too much haskell to write java-style code easily.
		// I do believe this is a combinator.
		final Comparator<Integer> oldComparator = gCodeOrdering; // Make a
																	// constant
																	// reference
																	// to the
																	// old one,
																	// for the
																	// inner
																	// class.
		gCodeOrdering = new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				int r1 = c.compare(o1, o2);
				if (r1 == 0)
					return oldComparator.compare(o1, o2);
				return r1;
			}

			@SuppressWarnings("unused")
			public boolean equals(Integer o1, Integer o2) {
				return this.compare(o1, o2) == 0;
			}
		};
	}

	/**
	 * Override the default sort for G codes. Not recommended unless all G codes
	 * are being written by the user; this can break dependencies between
	 * handlers.
	 * 
	 * @param c
	 *            the new comparator.
	 */
	@SuppressWarnings("unchecked")
	public void setGSorting(@SuppressWarnings("rawtypes") Comparator c) {
		gCodeOrdering = c;
	}

	/**
	 * Add the default set of handlers to this interpreter.
	 * 
	 * <!-- describe the full set of handlers here -->
	 */
	public void addDefaultHandlers() {
		final double curOffset[] = new double[26]; // Yes, we'll let them offset
													// on any axis they feel
													// like.
		// G0 - no handler.
		addGHandler(0, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) {
				Log.debug("Rapid move to " + next.getWord('X') + ", "
						+ next.getWord('Y') + ", " + next.getWord('Z'));
			}
		});
		// G1 - no handler.
		addGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) {
				if (next.getWord('F') == 0.0)
					Log.error("Zero feedrate; action will never complete.");
				Log.debug("Feed move to " + next.getWord('X') + ", "
						+ next.getWord('Y') + ", " + next.getWord('Z')
						+ " at feed " + next.getWord('F'));
			}
		});
		// G21 - program in mm - present but nullary.
		addGHandler(21, new EmptyCodeHandler());
		// G22 - program in in - convert to mm. Only on xyz for now.
		addGHandler(22, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) {
				char axes[] = { 'X', 'Y', 'Z' };
				for (char c : axes) {
					next.storeWord(c, next.getWord(c) * 25.4);
				}
			}
		});
		// G90 - absolute positioning - default, so empty.
		addGHandler(90, new EmptyCodeHandler());
		// G91 - relative positioning - convert to absolute. Only do this for
		// xyz by default.
		addGHandler(91, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) {
				char axes[] = { 'X', 'Y', 'Z' };
				for (char c : axes) {
					next.storeWord(c, next.getWord(c) + prev.getWord(c));
				}
			}
		});
		// G92 - set position ( reprap usage ) - messy. This probably goes in
		// the printer class.
		addGHandler(92, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) {
				Log.debug("G92 is not complete");
				char axes[] = { 'X', 'Y', 'Z' };
				for (char c : axes) {
					next.storeWord(c, next.getWord(c) + curOffset[c - 'A']);
				}
				// And copy the last line to the current line, because we don't
				// want to change actual positions.
			}
		});
		// GROUPS:
		// 21,22 , 90,91, 0-5
		setGSorting(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				int a = o1.intValue();
				int b = o2.intValue();
				if (a >= 21 && a <= 22)
					return -1; // Unit conversions default to first.
				if (a >= 0 && a < 6)
					return 1; // Movements are last.
				if (b >= 21 && b <= 22)
					return 1;
				if (b >= 0 && b < 6)
					return -1;
				if (a >= 90 && a <= 91)
					return -1; // Relative/absolute are immediately after unit
								// conversion unless otherwise specified.
				if (b >= 90 && b <= 91)
					return 1;
				return 0;
			}
		});

		@SuppressWarnings("unchecked")
		List<Integer>[] exclGroups = (List<Integer>[]) new List<?>[] {
				Arrays.asList(0, 1, 4, 28), // All of these might need to change
											// to be mutable later.
				Arrays.asList(20, 21), Arrays.asList(90, 91) };
		for (List<Integer> group : exclGroups) {
			for (int code : group) {
				gClearOnSet[code] = group;
			}
		}
		gClearOnSet[91] = Arrays.asList(90, 91, 92); // Incremental and setting
														// positions makes no
														// sense.
		gOneShot = Arrays.asList(28, 92);
	}

	/**
	 * Default executable; runs as a pipe, parsing standard input with the
	 * default handlers.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String args[]) throws Exception {
		GCodeInterpreter interp = new GCodeInterpreter();
		interp.interpretStream(System.in);
	}

	/**
	 * Gets the error handler.
	 *
	 * @return the error handler
	 */
	public CodeHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * Sets the error handler.
	 *
	 * @param errorHandler the new error handler
	 */
	public void setErrorHandler(CodeHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
}
