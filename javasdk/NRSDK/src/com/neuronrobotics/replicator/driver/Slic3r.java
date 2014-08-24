package com.neuronrobotics.replicator.driver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class Slic3r extends ExternalSlicer {

	public Slic3r(MaterialData data) {
		this.cmdline=Arrays.asList("/home/hephaestus/bin/Slic3r/bin/slic3r");
	}
	
//	public static void main(String args[]) throws Exception {
//		ExternalSlicer slicer=new Slic3r(new MiracleGrueMaterialData());
//		FileInputStream stlFile=new FileInputStream(args[0]);
//		FileOutputStream dumpFile=new FileOutputStream(args[0]+"-dump.gcode");
//		slicer.slice(stlFile, dumpFile);
//	}

}
