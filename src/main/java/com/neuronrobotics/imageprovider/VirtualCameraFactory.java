package com.neuronrobotics.imageprovider;

import java.net.MalformedURLException;
import java.net.URL;

public class VirtualCameraFactory {
	private static IVirtualCameraFactory factory = new IVirtualCameraFactory() {
		
		@Override
		public AbstractImageProvider getVirtualCamera() {
			// TODO Auto-generated method stub
			try {
				return new URLImageProvider(new URL("http://commonwealthrobotics.com/img/AndrewHarrington/2014-09-15-86.jpg"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);			
			}
		}
	}; 
	public static AbstractImageProvider getVirtualCamera(){
		return getFactory().getVirtualCamera();
	}
	public static IVirtualCameraFactory getFactory() {
		return factory;
	}
	public static void setFactory(IVirtualCameraFactory factory) {
		VirtualCameraFactory.factory = factory;
	}
}
