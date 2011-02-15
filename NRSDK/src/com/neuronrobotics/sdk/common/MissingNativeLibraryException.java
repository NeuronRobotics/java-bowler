package com.neuronrobotics.sdk.common;

public class MissingNativeLibraryException extends RuntimeException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The message. */
	private String message;
	
	/**
	 * Instantiates a new connection unavailable exception.
	 *
	 * @param message the message
	 */
	public MissingNativeLibraryException(String message) {
		this.message = message;
		Log.warning(message);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return message;
	}

}
