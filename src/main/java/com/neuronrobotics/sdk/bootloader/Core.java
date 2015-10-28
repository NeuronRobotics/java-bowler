package com.neuronrobotics.sdk.bootloader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Core.
 */
public class Core {
	
	/** The index. */
	private int index;
	
	/** The type. */
	private NRBootCoreType type;
	
	/** The lines. */
	private ArrayList<hexLine> lines = new ArrayList<hexLine>();
	
	/**
	 * Instantiates a new core.
	 *
	 * @param core the core
	 * @param lines the lines
	 * @param type the type
	 */
	public Core(int core,ArrayList<hexLine> lines,NRBootCoreType type){
		setIndex(core);
		this.setType(type);
		this.lines=lines;
	}
	
	/**
	 * Instantiates a new core.
	 *
	 * @param core the core
	 * @param file the file
	 * @param type the type
	 */
	public Core(int core,String file, NRBootCoreType type){
		setIndex(core);
		this.setType(type);
		try {
			ArrayList<hexLine> tmp = new ArrayList<hexLine>();
			 // Get the object of DataInputStream
		    DataInputStream in = new DataInputStream(new FileInputStream(file));
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null)   {
		        // Print the content on the console
		        try {
					tmp.add(new hexLine(strLine));
				} catch (Exception e) {
					System.err.println("This is not a valid hex file");
				}
		     }
		     //Close the input stream
		     in.close();
			setLines(tmp);
		}catch (Exception e) {
			////System.out.println("File not found!!");
		}
	}

	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(NRBootCoreType type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public NRBootCoreType getType() {
		return type;
	}

	/**
	 * Sets the lines.
	 *
	 * @param lines the new lines
	 */
	public void setLines(ArrayList<hexLine> lines) {
		this.lines = lines;
	}

	/**
	 * Gets the lines.
	 *
	 * @return the lines
	 */
	public ArrayList<hexLine> getLines() {
		return lines;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Core: "+index+" of type: "+type; 
	}
}
