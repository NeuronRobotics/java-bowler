package com.neuronrobotics.sdk.dyio.dypid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;

public class DyPIDConfiguration {
	private int group;
	private int inputChannel;
	private DyIOChannelMode inputMode;
	private int outputChannel;
	private DyIOChannelMode outputMode;
	public DyPIDConfiguration(int group,int inputChannel,DyIOChannelMode inputMode,int outputChannel,DyIOChannelMode outputMode){
		setGroup(group);
		setInputChannel(inputChannel);
		setInputMode(inputMode);
		setOutputChannel(outputChannel);
		setOutputMode(outputMode);
	}
	public DyPIDConfiguration(BowlerDatagram conf) {
		setGroup(conf.getData().getUnsigned(0));
		setInputChannel(conf.getData().getUnsigned(1));
		setInputMode(DyIOChannelMode.get(conf.getData().get(2)));
		setOutputChannel(conf.getData().getUnsigned(3));
		setOutputMode(DyIOChannelMode.get(conf.getData().get(4)));
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getGroup() {
		return group;
	}
	public void setInputChannel(int inputChannel) {
		this.inputChannel = inputChannel;
	}
	public int getInputChannel() {
		return inputChannel;
	}
	public void setInputMode(DyIOChannelMode inputMode) {
		this.inputMode = inputMode;
	}
	public DyIOChannelMode getInputMode() {
		return inputMode;
	}
	public void setOutputChannel(int outputChannel) {
		this.outputChannel = outputChannel;
	}
	public int getOutputChannel() {
		return outputChannel;
	}
	public void setOutputMode(DyIOChannelMode outputMode) {
		this.outputMode = outputMode;
	}
	public DyIOChannelMode getOutputMode() {
		return outputMode;
	}
}
