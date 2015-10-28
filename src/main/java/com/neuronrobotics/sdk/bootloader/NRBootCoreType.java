package com.neuronrobotics.sdk.bootloader;

// TODO: Auto-generated Javadoc
/**
 * The Enum NRBootCoreType.
 */
public enum NRBootCoreType {
	
	/** The AV rxx4p. */
	AVRxx4p		(2, "avr_atmegaXX4p"),
	
	/** The PI c32. */
	PIC32       (4, "pic32mx440f128h");
	
	/** The bytes per word. */
	private int bytesPerWord;
	
	/** The readable name. */
	private String readableName;
	
	/**
	 * Instantiates a new NR boot core type.
	 *
	 * @param bytesPerWord the bytes per word
	 * @param name the name
	 */
	private NRBootCoreType(int bytesPerWord, String name) {
		this.setBytesPerWord(bytesPerWord);
		setReadableName(name);
	}

	/**
	 * Sets the bytes per word.
	 *
	 * @param bytesPerWord the new bytes per word
	 */
	public void setBytesPerWord(int bytesPerWord) {
		this.bytesPerWord = bytesPerWord;
	}

	/**
	 * Gets the bytes per word.
	 *
	 * @return the bytes per word
	 */
	public int getBytesPerWord() {
		return bytesPerWord;
	}

	/**
	 * Sets the readable name.
	 *
	 * @param readableName the new readable name
	 */
	public void setReadableName(String readableName) {
		this.readableName = readableName;
	}

	/**
	 * Gets the readable name.
	 *
	 * @return the readable name
	 */
	public String getReadableName() {
		return readableName;
	}

	/**
	 * Find.
	 *
	 * @param tagValue the tag value
	 * @return the NR boot core type
	 */
	public static NRBootCoreType find(String tagValue) {
		if (NRBootCoreType.AVRxx4p.getReadableName().toLowerCase().contentEquals(tagValue.toLowerCase())){
			return NRBootCoreType.AVRxx4p;
		}
		if (NRBootCoreType.PIC32.getReadableName().toLowerCase().contentEquals(tagValue.toLowerCase())){
			return NRBootCoreType.PIC32;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getReadableName();
	}
}
