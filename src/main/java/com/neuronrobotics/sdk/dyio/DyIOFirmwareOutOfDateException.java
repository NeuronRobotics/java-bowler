package com.neuronrobotics.sdk.dyio;

public class DyIOFirmwareOutOfDateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845181001884863523L;
	public DyIOFirmwareOutOfDateException(String message){
		super(message);
	}
}
