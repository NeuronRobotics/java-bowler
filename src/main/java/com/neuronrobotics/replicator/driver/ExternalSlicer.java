package com.neuronrobotics.replicator.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.neuronrobotics.replicator.driver.SliceStatusData.SlicerState;

// TODO: Auto-generated Javadoc
/**
 * The Class ExternalSlicer.
 */
public class ExternalSlicer extends StlSlicer {
	
	/** The cmdline. */
	List<String> cmdline;

	/**
	 * Instantiates a new external slicer.
	 */
	public ExternalSlicer() {
		super(new MaterialData());
	}

	/**
	 * Instantiates a new external slicer.
	 *
	 * @param data the data
	 */
	public ExternalSlicer(MaterialData data) {
		super(data);
		// Ignore the data for now.
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.replicator.driver.StlSlicer#slice(java.io.File, java.io.File)
	 */
	public boolean slice(File input, File gcode) {

		ProcessBuilder builder = new ProcessBuilder();

		List<String> thisCommand = new ArrayList<String>(cmdline);
		thisCommand.add(1, input.getAbsolutePath());
		thisCommand.add("--output=" + gcode.getAbsolutePath());

		// builder.redirectErrorStream(true);
		// builder.redirectOutput(Redirect.INHERIT);
		builder.command(thisCommand);

		try {
			Process p = builder.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			new Thread(new StreamDump(this, br)).start();
			p.waitFor();
			fireStatus(new SliceStatusData(0, 0, SlicerState.SUCCESS,
					"complete slice"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	// public static void main(String args[]) throws Exception {
	// // ExternalSlicer slicer=new Slic3r(null);
	// //// slicer.cmdline=Arrays.asList("skeinforge");
	// // FileInputStream stlFile=new FileInputStream(args[0]);
	// // FileOutputStream dumpFile=new FileOutputStream(args[0]+"-dump.gcode");
	// // slicer.slice(stlFile, dumpFile);
	// }
}

/**
 * @author hephaestus
 *
 */
class StreamDump implements Runnable {

	/**
	 * 
	 */
	private ExternalSlicer externalSlicer;

	/**
	 * 
	 */
	String line = "";

	/**
	 * 
	 */
	private BufferedReader br;

	/**
	 * @param externalSlicer instantiated object to implement slicing
	 * @param br reader containing the file handle
	 */
	StreamDump(ExternalSlicer externalSlicer, BufferedReader br) {
		this.externalSlicer = externalSlicer;
		this.br = br;

	}

	public void run() {
		try {
			while ((line = br.readLine()) != null) {
				externalSlicer.fireStatus(new SliceStatusData(0, 0,
						SlicerState.SLICING, new String(line)));
				line = "";
			}
		} catch (IOException e) {
			return;
		}
	}
}
