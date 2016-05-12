package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

import java.io.File;

public interface IGcodeExecuter {
	/**
	 * Execute a single line of gcode
	 * @param line
	 * @return
	 */
	public String runLine(String line);
	/**
	 * Run all the lines in a file
	 * @param gcode
	 */
	public void runFile(File gcode);

}
