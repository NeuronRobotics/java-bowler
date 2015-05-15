package com.neuronrobotics.replicator.driver;

import java.io.File;
import java.util.Arrays;

public class Slic3r extends ExternalSlicer {
	
	private static String executableLocation=null;
	private double nozzle_diameter;
	private double[] printCenter = new double[2];
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
		this.setNozzle_diameter(args[0]);
		this.getPrintCenter()[0] = args[1];
		this.getPrintCenter()[1] =  args[2];
		this.setFilimentDiameter(args[3]);
		this.setExtrusionMultiplier(args[4]);
		this.setTempreture((int) args[5]);
		this.setBedTempreture((int) args[6]);
		this.setLayerHeight(args[7]);
		this.setWallThickness((int) args[8]);
		this.setUseSupportMaterial(args[9]!=0);
		this.setRetractLength(args[10]);
		this.setTravilSpeed((int) args[11]);
		this.setPerimeterSpeed((int) args[12]);
		this.setBridgeSpeed((int) args[13]);
		this.setGapFillSpeed((int) args[14]);
		this.setInfillSpeed((int) args[15]);
		this.setSupportMaterialSpeed((int) args[16]);
		this.setSmallPerimeterSpeedPercent((int) args[17]);
		this.setExternalPerimeterSpeedPercent((int) args[18]);
		this.setSolidInfillSpeedPercent((int) args[19]);
		this.setTopSolidInfillSpeedPercent((int) args[20]);
		this.setSupportMaterialInterfaceSpeedPercent((int) args[21]);
		this.setFirstLayerSpeedPercent((int) args[22]);
		makeCommandLine();
	}
	
	public double [] getPacketArguments(){
		if(args==null){
			args = new double[23];
		}
		
		args[0]  = this.getNozzle_diameter() ;
		args[1] = this.getPrintCenter()[0]  ;
		args[2]= this.getPrintCenter()[1] ;
		args[3] = this.getFilimentDiameter();
		args[4] = this.getExtrusionMultiplier()  ;
		args[5] = this.getTempreture()  ;
		args[6] = this.getBedTempreture() ;
		args[7] = this.getLayerHeight();
		args[8]= this.getWallThickness() ;
		args[9]= this.isUseSupportMaterial()?1:0;
		args[10]= this.getRetractLength() ;
		args[11]= this.getTravilSpeed() ;;
		args[12]= this.getPerimeterSpeed();
		args[13]= this.getBridgeSpeed();
		args[14]= this.getGapFillSpeed();
		args[15]= this.getInfillSpeed();
		args[16]= this.getSupportMaterialSpeed();
		args[17]= this.getSmallPerimeterSpeedPercent();
		args[18]= this.getExternalPerimeterSpeedPercent() ;
		args[19]= this.getSolidInfillSpeedPercent();
		args[20]= this.getTopSolidInfillSpeedPercent();
		args[21]= this.getSupportMaterialInterfaceSpeedPercent() ;
		args[22]= this.getFirstLayerSpeedPercent();
		
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
		
		this.setNozzle_diameter(nozzle_diameter);
		this.setPrintCenter(printCenter);
		this.setFilimentDiameter(filimentDiameter);
		this.setExtrusionMultiplier(extrusionMultiplier);
		this.setTempreture(tempreture);
		this.setBedTempreture(bedTempreture);
		this.setLayerHeight(layerHeight);
		this.setWallThickness(wallThickness);
		this.setUseSupportMaterial(useSupportMaterial);
		this.setRetractLength(retractLength);
		this.setTravilSpeed(travilSpeed);
		this.setPerimeterSpeed(perimeterSpeed);
		this.setBridgeSpeed(bridgeSpeed);
		this.setGapFillSpeed(gapFillSpeed);
		this.setInfillSpeed(infillSpeed);
		this.setSupportMaterialSpeed(supportMaterialSpeed);
		this.setSmallPerimeterSpeedPercent(smallPerimeterSpeedPercent);
		this.setExternalPerimeterSpeedPercent(externalPerimeterSpeedPercent);
		this.setSolidInfillSpeedPercent(solidInfillSpeedPercent);
		this.setTopSolidInfillSpeedPercent(topSolidInfillSpeedPercent);
		this.setSupportMaterialInterfaceSpeedPercent(supportMaterialInterfaceSpeedPercent);
		this.setFirstLayerSpeedPercent(firstLayerSpeedPercent);
		makeCommandLine();
		
	}

	private void makeCommandLine(){
		if(!new File(getExecutableLocation()).canExecute())
			throw new RuntimeException("Slicer binary must be executable. ");
		this.cmdline=Arrays.asList(getExecutableLocation(),
				"--nozzle-diameter="+getNozzle_diameter(),
				"--print-center=("+getPrintCenter()[0]+","+getPrintCenter()[1]+")",
				"--filament-diameter="+getFilimentDiameter(),
				"--extrusion-multiplier="+getExtrusionMultiplier(),
				"--temperature="+getTempreture(),
				"--bed-temperature="+getBedTempreture(),
				"--layer-height="+getLayerHeight(),
				"--perimeters="+getWallThickness(),
				"--avoid-crossing-perimeters",
				isUseSupportMaterial()?"--support-material":" ",
				"--retract-length="+getRetractLength(),
				//"--skirts=2",
				//"--repair",
				"--travel-speed="+getTravilSpeed(),
				"--perimeter-speed="+getPerimeterSpeed(),
				"--bridge-speed="+getBridgeSpeed(),
				"--gap-fill-speed="+getGapFillSpeed(),
				"--infill-speed="+getInfillSpeed(),
				"--support-material-speed="+getSupportMaterialSpeed(),
				
				"--small-perimeter-speed="+getSmallPerimeterSpeedPercent()+"%",
				"--external-perimeter-speed="+getExternalPerimeterSpeedPercent()+"%",
				"--solid-infill-speed="+getSolidInfillSpeedPercent()+"%",
				"--top-solid-infill-speed="+getTopSolidInfillSpeedPercent()+"%",
				"--support-material-interface-speed="+getSupportMaterialInterfaceSpeedPercent()+"%",
				"--first-layer-speed="+getFirstLayerSpeedPercent()+"%",
				"--notes=\"Generated by com.neuronrobotics.replicator.driver.Slic3r.java\""
			);
	}

	public static String getExecutableLocation() {
		return executableLocation;
	}


	public static void setExecutableLocation(String executableLocation) {
		Slic3r.executableLocation = executableLocation;
	}

	public double getNozzle_diameter() {
		return nozzle_diameter;
	}

	public void setNozzle_diameter(double nozzle_diameter) {
		this.nozzle_diameter = nozzle_diameter;
	}

	public double[] getPrintCenter() {
		return printCenter;
	}

	public void setPrintCenter(double[] printCenter) {
		this.printCenter = printCenter;
	}

	public double getFilimentDiameter() {
		return filimentDiameter;
	}

	public void setFilimentDiameter(double filimentDiameter) {
		this.filimentDiameter = filimentDiameter;
	}

	public double getExtrusionMultiplier() {
		return extrusionMultiplier;
	}

	public void setExtrusionMultiplier(double extrusionMultiplier) {
		this.extrusionMultiplier = extrusionMultiplier;
	}

	public int getTempreture() {
		return tempreture;
	}

	public void setTempreture(int tempreture) {
		this.tempreture = tempreture;
	}

	public int getBedTempreture() {
		return bedTempreture;
	}

	public void setBedTempreture(int bedTempreture) {
		this.bedTempreture = bedTempreture;
	}

	public double getLayerHeight() {
		return layerHeight;
	}

	public void setLayerHeight(double layerHeight) {
		this.layerHeight = layerHeight;
	}

	public int getWallThickness() {
		return wallThickness;
	}

	public void setWallThickness(int wallThickness) {
		this.wallThickness = wallThickness;
	}

	public boolean isUseSupportMaterial() {
		return useSupportMaterial;
	}

	public void setUseSupportMaterial(boolean useSupportMaterial) {
		this.useSupportMaterial = useSupportMaterial;
	}

	public double getRetractLength() {
		return retractLength;
	}

	public void setRetractLength(double retractLength) {
		this.retractLength = retractLength;
	}

	public int getTravilSpeed() {
		return travilSpeed;
	}

	public void setTravilSpeed(int travilSpeed) {
		this.travilSpeed = travilSpeed;
	}

	public int getPerimeterSpeed() {
		return perimeterSpeed;
	}

	public void setPerimeterSpeed(int perimeterSpeed) {
		this.perimeterSpeed = perimeterSpeed;
	}

	public int getBridgeSpeed() {
		return bridgeSpeed;
	}

	public void setBridgeSpeed(int bridgeSpeed) {
		this.bridgeSpeed = bridgeSpeed;
	}

	public int getGapFillSpeed() {
		return gapFillSpeed;
	}

	public void setGapFillSpeed(int gapFillSpeed) {
		this.gapFillSpeed = gapFillSpeed;
	}

	public int getInfillSpeed() {
		return infillSpeed;
	}

	public void setInfillSpeed(int infillSpeed) {
		this.infillSpeed = infillSpeed;
	}

	public int getSupportMaterialSpeed() {
		return supportMaterialSpeed;
	}

	public void setSupportMaterialSpeed(int supportMaterialSpeed) {
		this.supportMaterialSpeed = supportMaterialSpeed;
	}

	public int getSmallPerimeterSpeedPercent() {
		return smallPerimeterSpeedPercent;
	}

	public void setSmallPerimeterSpeedPercent(int smallPerimeterSpeedPercent) {
		this.smallPerimeterSpeedPercent = smallPerimeterSpeedPercent;
	}

	public int getExternalPerimeterSpeedPercent() {
		return externalPerimeterSpeedPercent;
	}

	public void setExternalPerimeterSpeedPercent(
			int externalPerimeterSpeedPercent) {
		this.externalPerimeterSpeedPercent = externalPerimeterSpeedPercent;
	}

	public int getSolidInfillSpeedPercent() {
		return solidInfillSpeedPercent;
	}

	public void setSolidInfillSpeedPercent(int solidInfillSpeedPercent) {
		this.solidInfillSpeedPercent = solidInfillSpeedPercent;
	}

	public int getTopSolidInfillSpeedPercent() {
		return topSolidInfillSpeedPercent;
	}

	public void setTopSolidInfillSpeedPercent(int topSolidInfillSpeedPercent) {
		this.topSolidInfillSpeedPercent = topSolidInfillSpeedPercent;
	}

	public int getSupportMaterialInterfaceSpeedPercent() {
		return supportMaterialInterfaceSpeedPercent;
	}

	public void setSupportMaterialInterfaceSpeedPercent(
			int supportMaterialInterfaceSpeedPercent) {
		this.supportMaterialInterfaceSpeedPercent = supportMaterialInterfaceSpeedPercent;
	}

	public int getFirstLayerSpeedPercent() {
		return firstLayerSpeedPercent;
	}

	public void setFirstLayerSpeedPercent(int firstLayerSpeedPercent) {
		this.firstLayerSpeedPercent = firstLayerSpeedPercent;
	}
	
//	public static void main(String args[]) throws Exception {
//		ExternalSlicer slicer=new Slic3r(new MiracleGrueMaterialData());
//		FileInputStream stlFile=new FileInputStream(args[0]);
//		FileOutputStream dumpFile=new FileOutputStream(args[0]+"-dump.gcode");
//		slicer.slice(stlFile, dumpFile);
//	}

}
