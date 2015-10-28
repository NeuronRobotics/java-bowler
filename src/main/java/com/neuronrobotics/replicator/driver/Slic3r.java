package com.neuronrobotics.replicator.driver;

import java.io.File;
import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class Slic3r.
 */
public class Slic3r extends ExternalSlicer {
	
	/** The executable location. */
	private static String executableLocation=null;
	
	/** The nozzle_diameter. */
	private double nozzle_diameter;
	
	/** The print center. */
	private double[] printCenter = new double[2];
	
	/** The filiment diameter. */
	private double filimentDiameter;
	
	/** The extrusion multiplier. */
	private double extrusionMultiplier;
	
	/** The tempreture. */
	private int tempreture;
	
	/** The bed tempreture. */
	private int bedTempreture;
	
	/** The layer height. */
	private double layerHeight;
	
	/** The wall thickness. */
	private int wallThickness;
	
	/** The use support material. */
	private boolean useSupportMaterial;
	
	/** The retract length. */
	private double retractLength;
	
	/** The travil speed. */
	private int travilSpeed;
	
	/** The perimeter speed. */
	private int perimeterSpeed;
	
	/** The bridge speed. */
	private int bridgeSpeed;
	
	/** The gap fill speed. */
	private int gapFillSpeed;
	
	/** The infill speed. */
	private int infillSpeed;
	
	/** The support material speed. */
	private int supportMaterialSpeed;
	
	/** The small perimeter speed percent. */
	private int smallPerimeterSpeedPercent;
	
	/** The external perimeter speed percent. */
	private int externalPerimeterSpeedPercent;
	
	/** The solid infill speed percent. */
	private int solidInfillSpeedPercent;
	
	/** The top solid infill speed percent. */
	private int topSolidInfillSpeedPercent;
	
	/** The support material interface speed percent. */
	private int supportMaterialInterfaceSpeedPercent;
	
	/** The first layer speed percent. */
	private int firstLayerSpeedPercent;
	
	/** The args. */
	private double[] args;
	
	/**
	 * Instantiates a new slic3r.
	 *
	 * @param args the args
	 */
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
	
	/**
	 * Gets the packet arguments.
	 *
	 * @return the packet arguments
	 */
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

	/**
	 * Instantiates a new slic3r.
	 *
	 * @param nozzle_diameter the nozzle_diameter
	 * @param printCenter the print center
	 * @param filimentDiameter the filiment diameter
	 * @param extrusionMultiplier the extrusion multiplier
	 * @param tempreture the tempreture
	 * @param bedTempreture the bed tempreture
	 * @param layerHeight the layer height
	 * @param wallThickness the wall thickness
	 * @param useSupportMaterial the use support material
	 * @param retractLength the retract length
	 * @param travilSpeed the travil speed
	 * @param perimeterSpeed the perimeter speed
	 * @param bridgeSpeed the bridge speed
	 * @param gapFillSpeed the gap fill speed
	 * @param infillSpeed the infill speed
	 * @param supportMaterialSpeed the support material speed
	 * @param smallPerimeterSpeedPercent the small perimeter speed percent
	 * @param externalPerimeterSpeedPercent the external perimeter speed percent
	 * @param solidInfillSpeedPercent the solid infill speed percent
	 * @param topSolidInfillSpeedPercent the top solid infill speed percent
	 * @param supportMaterialInterfaceSpeedPercent the support material interface speed percent
	 * @param firstLayerSpeedPercent the first layer speed percent
	 */
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

	/**
	 * Make command line.
	 */
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

	/**
	 * Gets the executable location.
	 *
	 * @return the executable location
	 */
	public static String getExecutableLocation() {
		return executableLocation;
	}


	/**
	 * Sets the executable location.
	 *
	 * @param executableLocation the new executable location
	 */
	public static void setExecutableLocation(String executableLocation) {
		Slic3r.executableLocation = executableLocation;
	}

	/**
	 * Gets the nozzle_diameter.
	 *
	 * @return the nozzle_diameter
	 */
	public double getNozzle_diameter() {
		return nozzle_diameter;
	}

	/**
	 * Sets the nozzle_diameter.
	 *
	 * @param nozzle_diameter the new nozzle_diameter
	 */
	public void setNozzle_diameter(double nozzle_diameter) {
		this.nozzle_diameter = nozzle_diameter;
	}

	/**
	 * Gets the prints the center.
	 *
	 * @return the prints the center
	 */
	public double[] getPrintCenter() {
		return printCenter;
	}

	/**
	 * Sets the prints the center.
	 *
	 * @param printCenter the new prints the center
	 */
	public void setPrintCenter(double[] printCenter) {
		this.printCenter = printCenter;
	}

	/**
	 * Gets the filiment diameter.
	 *
	 * @return the filiment diameter
	 */
	public double getFilimentDiameter() {
		return filimentDiameter;
	}

	/**
	 * Sets the filiment diameter.
	 *
	 * @param filimentDiameter the new filiment diameter
	 */
	public void setFilimentDiameter(double filimentDiameter) {
		this.filimentDiameter = filimentDiameter;
	}

	/**
	 * Gets the extrusion multiplier.
	 *
	 * @return the extrusion multiplier
	 */
	public double getExtrusionMultiplier() {
		return extrusionMultiplier;
	}

	/**
	 * Sets the extrusion multiplier.
	 *
	 * @param extrusionMultiplier the new extrusion multiplier
	 */
	public void setExtrusionMultiplier(double extrusionMultiplier) {
		this.extrusionMultiplier = extrusionMultiplier;
	}

	/**
	 * Gets the tempreture.
	 *
	 * @return the tempreture
	 */
	public int getTempreture() {
		return tempreture;
	}

	/**
	 * Sets the tempreture.
	 *
	 * @param tempreture the new tempreture
	 */
	public void setTempreture(int tempreture) {
		this.tempreture = tempreture;
	}

	/**
	 * Gets the bed tempreture.
	 *
	 * @return the bed tempreture
	 */
	public int getBedTempreture() {
		return bedTempreture;
	}

	/**
	 * Sets the bed tempreture.
	 *
	 * @param bedTempreture the new bed tempreture
	 */
	public void setBedTempreture(int bedTempreture) {
		this.bedTempreture = bedTempreture;
	}

	/**
	 * Gets the layer height.
	 *
	 * @return the layer height
	 */
	public double getLayerHeight() {
		return layerHeight;
	}

	/**
	 * Sets the layer height.
	 *
	 * @param layerHeight the new layer height
	 */
	public void setLayerHeight(double layerHeight) {
		this.layerHeight = layerHeight;
	}

	/**
	 * Gets the wall thickness.
	 *
	 * @return the wall thickness
	 */
	public int getWallThickness() {
		return wallThickness;
	}

	/**
	 * Sets the wall thickness.
	 *
	 * @param wallThickness the new wall thickness
	 */
	public void setWallThickness(int wallThickness) {
		this.wallThickness = wallThickness;
	}

	/**
	 * Checks if is use support material.
	 *
	 * @return true, if is use support material
	 */
	public boolean isUseSupportMaterial() {
		return useSupportMaterial;
	}

	/**
	 * Sets the use support material.
	 *
	 * @param useSupportMaterial the new use support material
	 */
	public void setUseSupportMaterial(boolean useSupportMaterial) {
		this.useSupportMaterial = useSupportMaterial;
	}

	/**
	 * Gets the retract length.
	 *
	 * @return the retract length
	 */
	public double getRetractLength() {
		return retractLength;
	}

	/**
	 * Sets the retract length.
	 *
	 * @param retractLength the new retract length
	 */
	public void setRetractLength(double retractLength) {
		this.retractLength = retractLength;
	}

	/**
	 * Gets the travil speed.
	 *
	 * @return the travil speed
	 */
	public int getTravilSpeed() {
		return travilSpeed;
	}

	/**
	 * Sets the travil speed.
	 *
	 * @param travilSpeed the new travil speed
	 */
	public void setTravilSpeed(int travilSpeed) {
		this.travilSpeed = travilSpeed;
	}

	/**
	 * Gets the perimeter speed.
	 *
	 * @return the perimeter speed
	 */
	public int getPerimeterSpeed() {
		return perimeterSpeed;
	}

	/**
	 * Sets the perimeter speed.
	 *
	 * @param perimeterSpeed the new perimeter speed
	 */
	public void setPerimeterSpeed(int perimeterSpeed) {
		this.perimeterSpeed = perimeterSpeed;
	}

	/**
	 * Gets the bridge speed.
	 *
	 * @return the bridge speed
	 */
	public int getBridgeSpeed() {
		return bridgeSpeed;
	}

	/**
	 * Sets the bridge speed.
	 *
	 * @param bridgeSpeed the new bridge speed
	 */
	public void setBridgeSpeed(int bridgeSpeed) {
		this.bridgeSpeed = bridgeSpeed;
	}

	/**
	 * Gets the gap fill speed.
	 *
	 * @return the gap fill speed
	 */
	public int getGapFillSpeed() {
		return gapFillSpeed;
	}

	/**
	 * Sets the gap fill speed.
	 *
	 * @param gapFillSpeed the new gap fill speed
	 */
	public void setGapFillSpeed(int gapFillSpeed) {
		this.gapFillSpeed = gapFillSpeed;
	}

	/**
	 * Gets the infill speed.
	 *
	 * @return the infill speed
	 */
	public int getInfillSpeed() {
		return infillSpeed;
	}

	/**
	 * Sets the infill speed.
	 *
	 * @param infillSpeed the new infill speed
	 */
	public void setInfillSpeed(int infillSpeed) {
		this.infillSpeed = infillSpeed;
	}

	/**
	 * Gets the support material speed.
	 *
	 * @return the support material speed
	 */
	public int getSupportMaterialSpeed() {
		return supportMaterialSpeed;
	}

	/**
	 * Sets the support material speed.
	 *
	 * @param supportMaterialSpeed the new support material speed
	 */
	public void setSupportMaterialSpeed(int supportMaterialSpeed) {
		this.supportMaterialSpeed = supportMaterialSpeed;
	}

	/**
	 * Gets the small perimeter speed percent.
	 *
	 * @return the small perimeter speed percent
	 */
	public int getSmallPerimeterSpeedPercent() {
		return smallPerimeterSpeedPercent;
	}

	/**
	 * Sets the small perimeter speed percent.
	 *
	 * @param smallPerimeterSpeedPercent the new small perimeter speed percent
	 */
	public void setSmallPerimeterSpeedPercent(int smallPerimeterSpeedPercent) {
		this.smallPerimeterSpeedPercent = smallPerimeterSpeedPercent;
	}

	/**
	 * Gets the external perimeter speed percent.
	 *
	 * @return the external perimeter speed percent
	 */
	public int getExternalPerimeterSpeedPercent() {
		return externalPerimeterSpeedPercent;
	}

	/**
	 * Sets the external perimeter speed percent.
	 *
	 * @param externalPerimeterSpeedPercent the new external perimeter speed percent
	 */
	public void setExternalPerimeterSpeedPercent(
			int externalPerimeterSpeedPercent) {
		this.externalPerimeterSpeedPercent = externalPerimeterSpeedPercent;
	}

	/**
	 * Gets the solid infill speed percent.
	 *
	 * @return the solid infill speed percent
	 */
	public int getSolidInfillSpeedPercent() {
		return solidInfillSpeedPercent;
	}

	/**
	 * Sets the solid infill speed percent.
	 *
	 * @param solidInfillSpeedPercent the new solid infill speed percent
	 */
	public void setSolidInfillSpeedPercent(int solidInfillSpeedPercent) {
		this.solidInfillSpeedPercent = solidInfillSpeedPercent;
	}

	/**
	 * Gets the top solid infill speed percent.
	 *
	 * @return the top solid infill speed percent
	 */
	public int getTopSolidInfillSpeedPercent() {
		return topSolidInfillSpeedPercent;
	}

	/**
	 * Sets the top solid infill speed percent.
	 *
	 * @param topSolidInfillSpeedPercent the new top solid infill speed percent
	 */
	public void setTopSolidInfillSpeedPercent(int topSolidInfillSpeedPercent) {
		this.topSolidInfillSpeedPercent = topSolidInfillSpeedPercent;
	}

	/**
	 * Gets the support material interface speed percent.
	 *
	 * @return the support material interface speed percent
	 */
	public int getSupportMaterialInterfaceSpeedPercent() {
		return supportMaterialInterfaceSpeedPercent;
	}

	/**
	 * Sets the support material interface speed percent.
	 *
	 * @param supportMaterialInterfaceSpeedPercent the new support material interface speed percent
	 */
	public void setSupportMaterialInterfaceSpeedPercent(
			int supportMaterialInterfaceSpeedPercent) {
		this.supportMaterialInterfaceSpeedPercent = supportMaterialInterfaceSpeedPercent;
	}

	/**
	 * Gets the first layer speed percent.
	 *
	 * @return the first layer speed percent
	 */
	public int getFirstLayerSpeedPercent() {
		return firstLayerSpeedPercent;
	}

	/**
	 * Sets the first layer speed percent.
	 *
	 * @param firstLayerSpeedPercent the new first layer speed percent
	 */
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
