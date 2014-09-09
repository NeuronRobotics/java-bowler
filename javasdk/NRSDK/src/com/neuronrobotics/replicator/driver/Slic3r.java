package com.neuronrobotics.replicator.driver;

import java.io.File;
import java.util.Arrays;

public class Slic3r extends ExternalSlicer {
	
	private static String executableLocation=null;
	private double nozzle_diameter;
	private double[] printCenter;
	private double filimentDiameter;
	private double extrusionMultiplier;
	private int tempreture;
	private int bedTempreture;
	private double layerHeight;
	private int wallThickness;
	private boolean useSupportMaterial;
	private double retractLength;
	private int travilSpeed;
	private int perimeterSpeed;
	private int bridgeSpeed;
	private int gapFillSpeed;
	private int infillSpeed;
	private int supportMaterialSpeed;
	private int smallPerimeterSpeedPercent;
	private int externalPerimeterSpeedPercent;
	private int solidInfillSpeedPercent;
	private int topSolidInfillSpeedPercent;
	private int supportMaterialInterfaceSpeedPercent;
	private int firstLayerSpeedPercent;
	private double[] args;
	
	public Slic3r(double [] args) {
		this.args = args;
		this.nozzle_diameter =args[0];
		this.printCenter[0] = args[1];
		this.printCenter[1] =  args[2];
		this.filimentDiameter =  args[3];
		this.extrusionMultiplier = args[4];
		this.tempreture = (int) args[5];
		this.bedTempreture = (int) args[6];
		this.layerHeight = args[7];
		this.wallThickness = (int) args[8];
		this.useSupportMaterial = args[9]!=0;
		this.retractLength = args[10];
		this.travilSpeed = (int) args[11];
		this.perimeterSpeed = (int) args[12];
		this.bridgeSpeed = (int) args[13];
		this.gapFillSpeed = (int) args[14];
		this.infillSpeed = (int) args[15];
		this.supportMaterialSpeed = (int) args[16];
		this.smallPerimeterSpeedPercent = (int) args[17];
		this.externalPerimeterSpeedPercent = (int) args[18];
		this.solidInfillSpeedPercent = (int) args[19];
		this.topSolidInfillSpeedPercent = (int) args[20];
		this.supportMaterialInterfaceSpeedPercent = (int) args[21];
		this.firstLayerSpeedPercent = (int) args[22];
		makeCommandLine();
	}
	
	public double [] getPacketArguments(){
		if(args==null){
			args = new double[23];
		}
		
		args[0]  = this.nozzle_diameter ;
		args[1] = this.printCenter[0]  ;
		args[2]= this.printCenter[1] ;
		args[3] = this.filimentDiameter;
		args[4] = this.extrusionMultiplier  ;
		args[5] = this.tempreture  ;
		args[6] = this.bedTempreture ;
		args[7] = this.layerHeight;
		args[8]= this.wallThickness ;
		args[9]= this.useSupportMaterial?1:0;
		args[10]= this.retractLength ;
		args[11]= this.travilSpeed ;;
		args[12]= this.perimeterSpeed;
		args[13]= this.bridgeSpeed;
		args[14]= this.gapFillSpeed;
		args[15]= this.infillSpeed;
		args[16]= this.supportMaterialSpeed;
		args[17]= this.smallPerimeterSpeedPercent;
		args[18]= this.externalPerimeterSpeedPercent ;
		args[19]= this.solidInfillSpeedPercent;
		args[20]= this.topSolidInfillSpeedPercent;
		args[21]= this.supportMaterialInterfaceSpeedPercent ;
		args[22]= this.firstLayerSpeedPercent;
		
		return args;
	}

	public Slic3r(	double nozzle_diameter,
					double [] printCenter,
					double filimentDiameter,
					double extrusionMultiplier,
					int tempreture,
					int bedTempreture,
					double layerHeight,
					int wallThickness,
					boolean useSupportMaterial,
					double retractLength,
					int travilSpeed,
					int perimeterSpeed,
					int bridgeSpeed,
					int gapFillSpeed,
					int infillSpeed,
					int supportMaterialSpeed,
					
					int smallPerimeterSpeedPercent,
					int externalPerimeterSpeedPercent,
					int solidInfillSpeedPercent,
					int topSolidInfillSpeedPercent,
					int supportMaterialInterfaceSpeedPercent,
					int firstLayerSpeedPercent
					) {
		
		this.nozzle_diameter = nozzle_diameter;
		this.printCenter = printCenter;
		this.filimentDiameter = filimentDiameter;
		this.extrusionMultiplier = extrusionMultiplier;
		this.tempreture = tempreture;
		this.bedTempreture = bedTempreture;
		this.layerHeight = layerHeight;
		this.wallThickness = wallThickness;
		this.useSupportMaterial = useSupportMaterial;
		this.retractLength = retractLength;
		this.travilSpeed = travilSpeed;
		this.perimeterSpeed = perimeterSpeed;
		this.bridgeSpeed = bridgeSpeed;
		this.gapFillSpeed = gapFillSpeed;
		this.infillSpeed = infillSpeed;
		this.supportMaterialSpeed = supportMaterialSpeed;
		this.smallPerimeterSpeedPercent = smallPerimeterSpeedPercent;
		this.externalPerimeterSpeedPercent = externalPerimeterSpeedPercent;
		this.solidInfillSpeedPercent = solidInfillSpeedPercent;
		this.topSolidInfillSpeedPercent = topSolidInfillSpeedPercent;
		this.supportMaterialInterfaceSpeedPercent = supportMaterialInterfaceSpeedPercent;
		this.firstLayerSpeedPercent = firstLayerSpeedPercent;
		makeCommandLine();
		
	}

	private void makeCommandLine(){
		if(!new File(getExecutableLocation()).canExecute())
			throw new RuntimeException("Slicer binary must be executable. ");
		this.cmdline=Arrays.asList(getExecutableLocation(),
				"--nozzle-diameter="+nozzle_diameter,
				"--print-center=("+printCenter[0]+","+printCenter[1]+")",
				"--filament-diameter="+filimentDiameter,
				"--extrusion-multiplier="+extrusionMultiplier,
				"--temperature="+tempreture,
				"--bed-temperature="+bedTempreture,
				"--layer-height="+layerHeight,
				"--perimeters="+wallThickness,
				"--avoid-crossing-perimeters",
				useSupportMaterial?"--support-material":" ",
				"--retract-length="+retractLength,
				//"--skirts=2",
				//"--repair",
				"--travel-speed="+travilSpeed,
				"--perimeter-speed="+perimeterSpeed,
				"--bridge-speed="+bridgeSpeed,
				"--gap-fill-speed="+gapFillSpeed,
				"--infill-speed="+infillSpeed,
				"--support-material-speed="+supportMaterialSpeed,
				
				"--small-perimeter-speed="+smallPerimeterSpeedPercent+"%",
				"--external-perimeter-speed="+externalPerimeterSpeedPercent+"%",
				"--solid-infill-speed="+solidInfillSpeedPercent+"%",
				"--top-solid-infill-speed="+topSolidInfillSpeedPercent+"%",
				"--support-material-interface-speed="+supportMaterialInterfaceSpeedPercent+"%",
				"--first-layer-speed="+firstLayerSpeedPercent+"%",
				"--notes=\"Generated by com.neuronrobotics.replicator.driver.Slic3r.java\""
			);
	}

	public static String getExecutableLocation() {
		return executableLocation;
	}


	public static void setExecutableLocation(String executableLocation) {
		Slic3r.executableLocation = executableLocation;
	}
	
//	public static void main(String args[]) throws Exception {
//		ExternalSlicer slicer=new Slic3r(new MiracleGrueMaterialData());
//		FileInputStream stlFile=new FileInputStream(args[0]);
//		FileOutputStream dumpFile=new FileOutputStream(args[0]+"-dump.gcode");
//		slicer.slice(stlFile, dumpFile);
//	}

}
