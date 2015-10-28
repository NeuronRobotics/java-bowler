package com.neuronrobotics.sdk.dyio.dypid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;

// TODO: Auto-generated Javadoc
/**
 * The Class DyPIDConfiguration.
 */
public class DyPIDConfiguration {
	
	/** The group. */
	private int group;
	
	/** The input channel. */
	private int inputChannel;
	
	/** The input mode. */
	private DyIOChannelMode inputMode=DyIOChannelMode.DIGITAL_IN;
	
	/** The output channel. */
	private int outputChannel;
	
	/** The output mode. */
	private DyIOChannelMode outputMode=DyIOChannelMode.DIGITAL_OUT;
	
	/**
	 * Instantiates a new dy pid configuration.
	 *
	 * @param group the group
	 */
	public DyPIDConfiguration(int group){
		setGroup(group);
		//disabled
		setInputChannel(0xff);
		setOutputChannel(0xff);
	}
	
	/**
	 * Instantiates a new dy pid configuration.
	 *
	 * @param group the group
	 * @param inputChannel the input channel
	 * @param inputMode the input mode
	 * @param outputChannel the output channel
	 * @param outputMode the output mode
	 */
	public DyPIDConfiguration(int group,int inputChannel,DyIOChannelMode inputMode,int outputChannel,DyIOChannelMode outputMode){
		setGroup(group);
		setInputChannel(inputChannel);
		setInputMode(inputMode);
		setOutputChannel(outputChannel);
		setOutputMode(outputMode);
	}
	
	/**
	 * Instantiates a new dy pid configuration.
	 *
	 * @param conf the conf
	 */
	public DyPIDConfiguration(BowlerDatagram conf) {
		setGroup(conf.getData().getUnsigned(0));
		setInputChannel(conf.getData().getUnsigned(1));
		setInputMode(DyIOChannelMode.get(conf.getData().get(2)));
		setOutputChannel(conf.getData().getUnsigned(3));
		setOutputMode(DyIOChannelMode.get(conf.getData().get(4)));
	}
	
	/**
	 * Sets the group.
	 *
	 * @param group the new group
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	
	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}
	
	/**
	 * Sets the input channel.
	 *
	 * @param inputChannel the new input channel
	 */
	public void setInputChannel(int inputChannel) {
		this.inputChannel = inputChannel;
	}
	
	/**
	 * Gets the input channel.
	 *
	 * @return the input channel
	 */
	public int getInputChannel() {
		return inputChannel;
	}
	
	/**
	 * Sets the input mode.
	 *
	 * @param inputMode the new input mode
	 */
	public void setInputMode(DyIOChannelMode inputMode) {
		this.inputMode = inputMode;
	}
	
	/**
	 * Gets the input mode.
	 *
	 * @return the input mode
	 */
	public DyIOChannelMode getInputMode() {
		return inputMode;
	}
	
	/**
	 * Sets the output channel.
	 *
	 * @param outputChannel the new output channel
	 */
	public void setOutputChannel(int outputChannel) {
		this.outputChannel = outputChannel;
	}
	
	/**
	 * Gets the output channel.
	 *
	 * @return the output channel
	 */
	public int getOutputChannel() {
		return outputChannel;
	}
	
	/**
	 * Sets the output mode.
	 *
	 * @param outputMode the new output mode
	 */
	public void setOutputMode(DyIOChannelMode outputMode) {
		this.outputMode = outputMode;
	}
	
	/**
	 * Gets the output mode.
	 *
	 * @return the output mode
	 */
	public DyIOChannelMode getOutputMode() {
		return outputMode;
	}
}
