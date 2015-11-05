package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.imageprovider.AbstractImageProvider;

public class CameraLink extends AbstractLink {

	private AbstractImageProvider img;

	public CameraLink(LinkConfiguration conf, AbstractImageProvider img) {
		super(conf);
		// TODO Auto-generated constructor stub
		this.setImageProvider(img);
	}
	
	
	
	

	@Override
	public void cacheTargetValue() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush(double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushAll(double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 0;
	}



	public AbstractImageProvider getImageProvider() {
		return img;
	}



	public void setImageProvider(AbstractImageProvider img) {
		this.img = img;
	}

}
