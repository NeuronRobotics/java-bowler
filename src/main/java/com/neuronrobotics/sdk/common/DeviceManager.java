package com.neuronrobotics.sdk.common;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import com.neuronrobotics.sdk.bootloader.NRBootLoader;
import com.neuronrobotics.sdk.bowlercam.device.BowlerCamDevice;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceManager.
 */
public class DeviceManager {

	/** The Constant devices. */
	private static final ArrayList<BowlerAbstractDevice> devices = new ArrayList<BowlerAbstractDevice>();

	/** The Constant deviceAddedListener. */
	private static final ArrayList<IDeviceAddedListener> deviceAddedListener = new ArrayList<IDeviceAddedListener>();

	/**
	 * Adds the connection.
	 *
	 * @param newDevice
	 *            the new device
	 * @param name
	 *            the name
	 */
	public static void addConnection(final Object newDevice, String name) {
		
		if (BowlerAbstractDevice.class.isInstance(newDevice)) {
			addConnectionBAD((BowlerAbstractDevice) newDevice, name);
		} else if (DMDevice.wrappable(newDevice)) {
			try {
				addConnectionBAD(new DMDevice(newDevice), name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("This object can not behave as a device");
		}
	}

	/**
	 * Adds the connection.
	 *
	 * @param newDevice
	 *            the new device
	 * @param name
	 *            the name
	 */
	private static void addConnectionBAD(final BowlerAbstractDevice newDevice, String name) {
		if (DeviceManager.getSpecificDevice(name) == newDevice) {
			System.out.println("Device " + name + " is already in the manager");
			return;
		}
		if ( DMDevice.class.isInstance(newDevice)) {
			DMDevice incoming = (DMDevice) newDevice;
			for(String s:listConnectedDevice() ){
				Object sDev = DeviceManager.getSpecificDevice(s);
				if(DMDevice.class.isInstance(sDev)) {
					DMDevice inside = (DMDevice) sDev;
					if (inside.getWrapped() == incoming.getWrapped()) {
						System.out.println("Wrapped Device " + name + " is already in the manager");
						return;
					}
				}
			}

		}
		
		if (!newDevice.isAvailable())
			newDevice.connect();
		if (!newDevice.isAvailable()) {
			throw new BowlerRuntimeException(
					"Device " + name + " of type " + newDevice.getClass().getSimpleName() + " is not availible");
		}
		if (devices.contains(newDevice)) {
			Log.warning("Device is already added " + newDevice.getScriptingName());
		}
		int numOfThisDeviceType = 0;

		for (int i = 0; i < devices.size(); i++) {
			if (newDevice.getClass().isInstance(devices.get(i))
					&& devices.get(i).getScriptingName().contentEquals(name))
				numOfThisDeviceType++;
		}
		if (numOfThisDeviceType > 0)
			name = name + numOfThisDeviceType;
		newDevice.setScriptingName(name);
		devices.add(newDevice);
		newDevice.addConnectionEventListener(new IDeviceConnectionEventListener() {
			@Override
			public void onDisconnect(BowlerAbstractDevice source) {
				if (source == newDevice && source != null)
					DeviceManager.remove(newDevice);
			}

			@Override
			public void onConnect(BowlerAbstractDevice source) {
			}
		});
		
		for (int i=0;i< deviceAddedListener.size();i++) {
			IDeviceAddedListener l = deviceAddedListener.get(i);
			l.onNewDeviceAdded(newDevice);
		}
	}

	/**
	 * Adds the connection.
	 *
	 * @param connection
	 *            the connection
	 */
	public static void addConnection(BowlerAbstractConnection connection) {
		if (connection == null) {
			return;
		}

		GenericDevice gen = new GenericDevice(connection);
		try {
			if (!gen.connect()) {
				throw new InvalidConnectionException("Connection is invalid");
			}
			if (!gen.ping(true)) {
				throw new InvalidConnectionException("Communication failed");
			}
		} catch (Exception e) {
			// connection.disconnect();
			ThreadUtil.wait(1000);
			BowlerDatagram.setUseBowlerV4(false);
			if (!gen.connect()) {
				throw new InvalidConnectionException("Connection is invalid");
			}
			if (!gen.ping()) {
				connection = null;
				throw new InvalidConnectionException("Communication failed");
			}
			throw new RuntimeException(e);
		}
		if (gen.hasNamespace("neuronrobotics.dyio.*")) {
			DyIO dyio = new DyIO(gen.getConnection());
			dyio.connect();
			String name = "dyio";

			addConnection(dyio, name);

		}else if (gen.hasNamespace("bcs.pid.*")) {
			GenericPIDDevice delt = new GenericPIDDevice();
			delt.setConnection(gen.getConnection());
			delt.connect();
			String name = "pid";

			addConnection(delt, name);
		} else if (gen.hasNamespace("bcs.bootloader.*") || gen.hasNamespace("neuronrobotics.bootloader.*")) {
			NRBootLoader delt = new NRBootLoader(gen.getConnection());
			String name = "bootloader";

			addConnection(delt, name);
		} else if (gen.hasNamespace("neuronrobotics.bowlercam.*")) {
			BowlerCamDevice delt = new BowlerCamDevice();
			delt.setConnection(gen.getConnection());
			delt.connect();
			String name = "bowlercam";
			addConnection(delt, name);
		} else {
			addConnection(gen, "device");
		}

	}

	/**
	 * Adds the connection.
	 */
	public static void addConnection() {
		new Thread() {
			public void run() {
				setName("Connection Dialog displayer thread");
				try {
					BowlerDatagram.setUseBowlerV4(true);
					addConnection(ConnectionDialog.promptConnection());
				} catch (BowlerRuntimeException ex) {
					// try one more time is it fails to connect
					BowlerDatagram.setUseBowlerV4(true);
					addConnection(ConnectionDialog.promptConnection());
				}
			}
		}.start();
	}

	/**
	 * Removes the.
	 *
	 * @param newDevice
	 *            the new device
	 */
	public static void remove(BowlerAbstractDevice newDevice) {
		if (devices.contains(newDevice) && newDevice != null) {
			devices.remove(newDevice);
			for (IDeviceAddedListener l : deviceAddedListener) {
				l.onDeviceRemoved(newDevice);
			}
		}
	}

	/**
	 * Adds the device added listener.
	 *
	 * @param l
	 *            the l
	 */
	public static void addDeviceAddedListener(IDeviceAddedListener l) {
		if (!deviceAddedListener.contains(l))
			deviceAddedListener.add(l);
	}

	/**
	 * Removes the device added listener.
	 *
	 * @param l
	 *            the l
	 */
	public static void removeDeviceAddedListener(IDeviceAddedListener l) {
		if (deviceAddedListener.contains(l))
			deviceAddedListener.remove(l);
	}

	/**
	 * Gets the specific device.
	 *
	 * @param name
	 *            the name
	 * @return the specific device
	 */
	public static Object getSpecificDevice(String name, IDeviceProvider provider) {
		if(name.contains("*")) {
			name = name.split("\\*")[0];
		}
		for (int i = 0; i < devices.size(); i++) {
			if (devices.get(i).getScriptingName().contains(name)) {
				if(DMDevice.class.isInstance(devices.get(i))) {
					return ((DMDevice)devices.get(i)).getWrapped();
				}
				return devices.get(i);
			}
		}
		// device doesn't exist already so we use the call back to build a new one on
		// the fly
		Object newDev = provider.call();
		addConnection(newDev, name);
		Object dev= getSpecificDevice(name);
		
		if(DMDevice.class.isInstance(dev)) {
			return ((DMDevice)dev).getWrapped();
		}
		return dev;
	}

	/**
	 * Gets the specific device.
	 *
	 * @param name
	 *            the name
	 * @return the specific device
	 */
	public static Object getSpecificDevice(String name) {
		if(name.contains("*")) {
			name = name.split("\\*")[0];
		}
		for (int i = 0; i < devices.size(); i++) {
			String devname = devices.get(i).getScriptingName();
			if (devname.contains(name)) {
				BowlerAbstractDevice dev = devices.get(i);
				if(DMDevice.class.isInstance(dev)) {
					return ((DMDevice)dev).getWrapped();
				}
				return dev;
			}
		}
		return null;
	}

	/**
	 * Gets the specific device.
	 *
	 * @param class1
	 *            the class1
	 * @param name
	 *            the name
	 * @return the specific device
	 */
	public static Object getSpecificDevice(Class<?> class1, String name) {
		if(name.contains("*")) {
			name = name.split("\\*")[0];
		}
		if (class1 == null)
			return getSpecificDevice(name);
		List<String> devs = listConnectedDevice(class1);
		if (devs.size() == 0)
			return null;
		else
			for (String d : devs) {
				// if the string is null it just returns the first of its kind
				if (name == null || d.contentEquals(name)) {
					for (int i = 0; i < devices.size(); i++) {
						if (devices.get(i).getScriptingName().contains(d))
							return devices.get(i);
					}
				}

			}
		return null;
	}

	/**
	 * List connected device.
	 *
	 * @return the list
	 */
	public static List<String> listConnectedDevice() {
		List<String> choices = new ArrayList<String>();
		for (int i = 0; i < devices.size(); i++) {
			choices.add(devices.get(i).getScriptingName());
		}
		return choices;

	}

	/**
	 * List connected device.
	 *
	 * @param class1
	 *            the class1
	 * @return the list
	 */
	public static List<String> listConnectedDevice(Class<?> class1) {
		List<String> choices = new ArrayList<String>();
		for (int i = 0; i < devices.size(); i++) {
			if (class1 == null)
				choices.add(devices.get(i).getScriptingName());
			else if (class1.isInstance(devices.get(i))) {
				choices.add(devices.get(i).getScriptingName());
			}
		}
		return choices;

	}
}
