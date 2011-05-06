package com.neuronrobotics.nrconsole.plugin.DyIO;

import com.neuronrobotics.graphing.DataChannel;

public class ChannelRecorder {

	private DataChannel dataChannel;
	
	public ChannelRecorder(ChannelManager channelManager) {
		 dataChannel = new DataChannel(channelManager.getChannel().toString());
	}

	public void setGraphing(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public DataChannel getDataChannel() {
		return dataChannel;
	}

	public void recordValue(int value) {
		dataChannel.graphValue(value);
	}

	public void recordValue(double value) {
		dataChannel.graphValue(value);
	}
}
