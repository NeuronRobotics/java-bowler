package com.neuronrobotics.replicator.driver.interpreter;

// TODO: Auto-generated Javadoc
/** 
 * An empty code handler, for noting that "do nothing" is the correct action.
 * Presently used for absolute positioning and programming in mm, because those are the internal representations.
 * 
 */
public class EmptyCodeHandler extends CodeHandler {
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.replicator.driver.interpreter.CodeHandler#execute(com.neuronrobotics.replicator.driver.interpreter.GCodeLineData, com.neuronrobotics.replicator.driver.interpreter.GCodeLineData)
	 */
	public void execute(GCodeLineData prev, GCodeLineData line) throws Exception {
		
		//throw new RuntimeException("No handler availible "+line);
	}
}

