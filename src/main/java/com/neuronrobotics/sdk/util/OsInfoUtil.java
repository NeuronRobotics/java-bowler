package com.neuronrobotics.sdk.util;

// TODO: Auto-generated Javadoc
/**
 * The Class OsInfoUtil.
 */
public class OsInfoUtil {
	
	/**
	 * Checks if is 64 bit.
	 *
	 * @return true, if is 64 bit
	 */
	public static boolean is64Bit() {
		// //System.out.println("Arch: "+getOsArch());
		return getOsArch().startsWith("x86_64")
				|| getOsArch().startsWith("amd64");
	}

	/**
	 * Checks if is arm.
	 *
	 * @return true, if is arm
	 */
	public static boolean isARM() {
		return getOsArch().startsWith("arm");
	}

	/**
	 * Checks if is ppc.
	 *
	 * @return true, if is ppc
	 */
	public static boolean isPPC() {
		return getOsArch().toLowerCase().contains("ppc");
	}

	/**
	 * Checks if is cortex a8.
	 *
	 * @return true, if is cortex a8
	 */
	public static boolean isCortexA8() {
		if (isARM()) {
			// TODO check for cortex a8 vs arm9 generic
			return true;
		}
		return false;
	}

	/**
	 * Checks if is windows.
	 *
	 * @return true, if is windows
	 */
	public static boolean isWindows() {
		// //System.out.println("OS name: "+getOsName());
		return getOsName().toLowerCase().startsWith("windows")
				|| getOsName().toLowerCase().startsWith("microsoft")
				|| getOsName().toLowerCase().startsWith("ms");
	}

	/**
	 * Checks if is linux.
	 *
	 * @return true, if is linux
	 */
	public static boolean isLinux() {
		return getOsName().toLowerCase().startsWith("linux");
	}

	/**
	 * Checks if is osx.
	 *
	 * @return true, if is osx
	 */
	public static boolean isOSX() {
		return getOsName().toLowerCase().startsWith("mac");
	}

	/**
	 * Gets the extension.
	 *
	 * @return the extension
	 */
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

	/**
	 * Gets the os name.
	 *
	 * @return the os name
	 */
	public static String getOsName() {
		return System.getProperty("os.name");
	}

	/**
	 * Gets the os arch.
	 *
	 * @return the os arch
	 */
	public static String getOsArch() {
		return System.getProperty("os.arch");
	}

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	@SuppressWarnings("unused")
	public static String getIdentifier() {
		return getOsName() + " : " + getOsArch();
	}
}
