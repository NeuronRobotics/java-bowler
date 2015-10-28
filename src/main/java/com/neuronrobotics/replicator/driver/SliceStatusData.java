package com.neuronrobotics.replicator.driver;


// TODO: Auto-generated Javadoc
/**
 * The Class SliceStatusData.
 */
public class SliceStatusData {
	
	
	/**
	 * This enum should be used to report on state of the slicer
	 * SLICING indicates the slicer is working and there is no problem
	 * ERROR indicates the slicer has had a problem and should terminate the slice
	 * WARNING_SLICING indicates a possible problem detected but the slice has not been terminated
	 * WARNING_DONE indicates the slice has terminated with a warning
	 * SUCCESS indicates slice has terminated with no warnings or errors.
	 */
	public enum SlicerState {
		
		/** The slicing. */
		SLICING, 
 /** The error. */
 ERROR, 
 /** The warning done. */
 WARNING_DONE,
/** The warning slicing. */
WARNING_SLICING, 
 /** The success. */
 SUCCESS;
	};

	/** The layers. */
	private int layers;
	
	/** The slice progress. */
	private int sliceProgress;
	
	/** The message. */
	private String message;
	
	/** The current slicer state. */
	private SlicerState currentSlicerState;
	
	/**
	 * Constructor that leaves state message blank.
	 *
	 * @param layers the layers
	 * @param sliceProgress the slice progress
	 * @param currentSlicerState the current slicer state
	 */
	public SliceStatusData(int layers, int sliceProgress, SlicerState currentSlicerState){
		this.layers = layers;
		this.sliceProgress = sliceProgress;
		this.currentSlicerState = currentSlicerState;
		this.message = "";
	}
	
	/**
	 * Constructor with ability to set state message.
	 *
	 * @param layers the layers
	 * @param sliceProgress the slice progress
	 * @param currentSlicerState the current slicer state
	 * @param stateMessage the state message
	 */
	public SliceStatusData(int layers, int sliceProgress, SlicerState currentSlicerState, String stateMessage){
		this.layers = layers;
		this.sliceProgress = sliceProgress;
		this.currentSlicerState = currentSlicerState;
		this.message = stateMessage; 
		//this.currentSlicerState.setMessage(stateMessage);
	}
	
	/**
	 * This should return the amount of layers needed to print the current object
	 * May or may not be useful/accurate depending on whether slice has completed .
	 *
	 * @return current calculated amount of layers
	 */
	public int getLayers(){
		return layers;
	}
	
	/**
	 * sliceProgress should be an integer between 0 and 100 specifying how far
	 * along in the slice we are.
	 *
	 * @return sliceProgress
	 */
	public int getSliceProgress(){
		return sliceProgress;
	}
	
	/**
	 * If there is a message to send, it will be stored in message.
	 * Recommended for errors and warnings
	 *  @return message string 
	 */
	public String getMessage(){
		return message;
	}
	
	/**
	 * Returns the current state of the slicer.
	 *
	 * @return currentSlicerState
	 */
	public SlicerState getCurrentSlicerState(){
		return currentSlicerState;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return currentSlicerState+":"+message;
	}
}
