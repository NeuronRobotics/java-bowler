package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.neuronrobotics.sdk.addons.kinematics.DHChain;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class SimpleTransformViewer extends Canvas3D {
	/**
	 * long 
	 */
	private static final long serialVersionUID = -5190028697311178389L;
	SimpleUniverse simpleU ;
	TransformGroup base;
	private Transform3D baseLocation = null;
	BranchGroup rootBranchGroup;
	public SimpleTransformViewer() {
		super( SimpleUniverse.getPreferredConfiguration());
        resetView();
	}
	public BranchGroup createDefaultSceneGraph(SimpleUniverse su) {
		//System.out.println("Current pose = "+tk.forwardKinematics(new  double [] {0,0,0,0,0,0}));
		//System.out.println("Current pose = "+tk.forwardKinematics(new  double [] {0,90,0,0,0,0}));
		simpleU = new SimpleUniverse(this);
    	// Create the root of the branch graph
		rootBranchGroup = new BranchGroup();
    	
    	base = TransformFactory.getLabledAxis(new TransformNR(), "Base");
    	//links.add(base);
    	base.setTransform(getBaseLocation());
    	rootBranchGroup.addChild(base); 
    	
//    	ArrayList<TransformNR> chain = tk.getChain(jointSpaceVector);
//    	for(TransformNR t:chain){
//    		TransformGroup tmp = TransformFactory.getLabledAxis(t, "Link #"+i++);
//    		links.add(tmp);
//    		base.addChild(tmp);
//    	}
    	
        BoundingSphere mouseBounds = null;

        TransformGroup  vpTrans = su.getViewingPlatform().getViewPlatformTransform();
        //vpTrans = base; 

        rootBranchGroup.addChild(new Axis());

        mouseBounds = new BoundingSphere(new Point3d(), 100.0);

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(base);
        myMouseRotate.setSchedulingBounds(mouseBounds);
        //rootBranchGroup.addChild(myMouseRotate);
        //base.addChild(myMouseRotate);

        MouseTranslate myMouseTranslate = new MouseTranslate(MouseBehavior.INVERT_INPUT);
        myMouseTranslate.setTransformGroup(vpTrans);
        myMouseTranslate.setSchedulingBounds(mouseBounds);
        rootBranchGroup.addChild(myMouseTranslate);
        //base.addChild(myMouseTranslate);

        MouseWheelZoom myMouseZoom = new MouseWheelZoom(MouseBehavior.INVERT_INPUT);
        myMouseZoom.setTransformGroup(vpTrans);
        myMouseZoom.setSchedulingBounds(mouseBounds);
        rootBranchGroup.addChild(myMouseZoom);
        //base.addChild(myMouseZoom);
    	// Let Java 3D perform optimizations on this scene graph.
    	rootBranchGroup.compile();
    	simpleU.addBranchGraph(rootBranchGroup);
    	return rootBranchGroup;
	} // end of CreateSceneGraph method of HelloJava3Db
	
	public void addTransform(TransformNR tr,String label) {
		System.out.println("Adding a transform "+label);
		TransformGroup tmp = TransformFactory.getLabledAxis(tr, label);
		base.addChild(tmp);
		rootBranchGroup.compile();
	}
	
	public void resetView(){
		createDefaultSceneGraph(simpleU);
		//System.out.println("Resetting view");
        TransformGroup viewTransform = simpleU.getViewingPlatform().getViewPlatformTransform();
        
        Transform3D t3d = new Transform3D();        
        t3d.lookAt(new Point3d(20,20,20),//Position of camera
        		new Point3d(0,0,0), //position of base frame
        		new Vector3d(0,1,0)); // orentation of camera
        t3d.invert();
        
        viewTransform.setTransform(t3d);
        base.setTransform(getBaseLocation());
	}
	public Transform3D getBaseLocation() {
		if(baseLocation == null) {
			baseLocation = new Transform3D();
			Transform3D x = new Transform3D();
			x.rotX(Math.toRadians(-90));
			Transform3D z = new Transform3D();
			z.rotZ(Math.toRadians(180));
			baseLocation.mul(x);
			baseLocation.mul(z);
		}
		return baseLocation;
	}

}
