/*******************************************************************************
 * Copyright 2010 Neuron Robotics, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.neuronrobotics.sdk.common;

import com.neuronrobotics.sdk.config.SDKBuildInfo;
// TODO: Auto-generated Javadoc

/**
 * This class contains information about the SDK.
 *
 * @author rbreznak
 */
public class SDKInfo {
	
	/** The Constant NAME. */
	public static final String NAME = "Neuron Robotics SDK " + SDKBuildInfo.getMajorVersion() + "." + SDKBuildInfo.getMinorVersion() + "(" + SDKBuildInfo.getBuild() + ")";
	
	/** The Constant isVM64bit. */
	//public static final boolean isVM64bit = (System.getProperty("sun.arch.data.model").indexOf("64") != -1);
	
	/** The Constant isOS64bit. */
	public static final boolean isOS64bit = (System.getProperty("os.arch").indexOf("x86_64") != -1);
	
	/** The Constant isLinux. */
	public static final boolean isLinux = (System.getProperty("os.name").toLowerCase().indexOf("linux")!=-1);
	
	/** The Constant isWindows. */
	public static final boolean isWindows = (System.getProperty("os.name").toLowerCase().indexOf("win")!=-1);
	
	/** The Constant isMac. */
	public static final boolean isMac = (System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1);
	
	/** The is unix. */
	public static boolean isUnix =(isLinux || isMac);


	
}
