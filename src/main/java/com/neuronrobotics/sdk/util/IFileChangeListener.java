package com.neuronrobotics.sdk.util;

import java.io.File;
import java.nio.file.WatchEvent;

public interface IFileChangeListener {
	
	public void onFileChange(File fileThatChanged,WatchEvent event);

}
