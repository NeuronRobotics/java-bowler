package com.neuronrobotics.sdk.util;

public class OsInfoUtil {
	public static boolean is64Bit() {
		// //System.out.println("Arch: "+getOsArch());
		return getOsArch().startsWith("x86_64")
				|| getOsArch().startsWith("amd64");
	}

	public static boolean isARM() {
		return getOsArch().startsWith("arm");
	}

	public static boolean isPPC() {
		return getOsArch().toLowerCase().contains("ppc");
	}

	public static boolean isCortexA8() {
		if (isARM()) {
			// TODO check for cortex a8 vs arm9 generic
			return true;
		}
		return false;
	}

	public static boolean isWindows() {
		// //System.out.println("OS name: "+getOsName());
		return getOsName().toLowerCase().startsWith("windows")
				|| getOsName().toLowerCase().startsWith("microsoft")
				|| getOsName().toLowerCase().startsWith("ms");
	}

	public static boolean isLinux() {
		return getOsName().toLowerCase().startsWith("linux");
	}

	public static boolean isOSX() {
		return getOsName().toLowerCase().startsWith("mac");
	}

	public static String getExtension() {
		if (isWindows()) {
			return ".zip";
		}
		if (isLinux()) {
			return ".zip";
		}
		if (isOSX()) {
			return ".dmg";
		}
		return "";
	}

	public static String getOsName() {
		return System.getProperty("os.name");
	}

	public static String getOsArch() {
		return System.getProperty("os.arch");
	}

	@SuppressWarnings("unused")
	public static String getIdentifier() {
		return getOsName() + " : " + getOsArch();
	}
}
