package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

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
	private Transform3D baseLocation = null;
	BranchGroup rootBranchGroup= new BranchGroup();
	BranchGroup base= new BranchGroup();
	ArrayList<TransformHolder> transforms = new ArrayList<TransformHolder> ();
 	public SimpleTransformViewer() {
		super( SimpleUniverse.getPreferredConfiguration());
		createDefaultSceneGraph();
	}
	private void createDefaultSceneGraph() {
		
		if(simpleU!=null){
			simpleU.removeAllLocales();
			simpleU.cleanup();
		}else
			simpleU = new SimpleUniverse(this);
    	// Create the root of the branch graph
		rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		rootBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		base.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		base.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		base.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		base.setCapability(BranchGroup.ALLOW_DETACH);
		
		
		rootBranchGroup.addChild(new Axis());
		rootBranchGroup.addChild(base);
    	
    	
        BoundingSphere mouseBounds = null;

        TransformGroup  vpTrans = simpleU.getViewingPlatform().getViewPlatformTransform();

        mouseBounds = new BoundingSphere(new Point3d(), 100.0);

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(new TransformGroup(getBaseLocation()));
        myMouseRotate.setSchedulingBounds(mouseBounds);

        MouseTranslate myMouseTranslate = new MouseTranslate(MouseBehavior.INVERT_INPUT);
        myMouseTranslate.setTransformGroup(vpTrans);
        myMouseTranslate.setSchedulingBounds(mouseBounds);
        rootBranchGroup.addChild(myMouseTranslate);
        //base.addChild(myMouseTranslate);

        MouseWheelZoom myMouseZoom = new MouseWheelZoom(MouseBehavior.INVERT_INPUT);
        myMouseZoom.setTransformGroup(vpTrans);
        myMouseZoom.setSchedulingBounds(mouseBounds);
        rootBranchGroup.addChild(myMouseZoom);
        
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 10000);
        DirectionalLight lightD = new DirectionalLight();
        lightD.setInfluencingBounds(bounds);
        lightD.setDirection(new Vector3f(0.0f, 0.0f, -1.0f));
        lightD.setColor(new Color3f(Color.white));
        rootBranchGroup.addChild(lightD);

        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bounds);
        rootBranchGroup.addChild(lightA);

    	// Let Java 3D perform optimizations on this scene graph.
    	rootBranchGroup.compile();
    	simpleU.addBranchGraph(rootBranchGroup);
    	resetView();
    	clearTransforms();
	} // end of CreateSceneGraph method of HelloJava3Db
	
	public TransformGroup addTransform(TransformNR tr,String label, Color color) {
		Transform3D trans = TransformFactory.getTransform(tr);
		return addTransform(trans,label,color);
	}
	public synchronized TransformGroup addTransform(Transform3D tr,String label, Color color) {
		for(TransformHolder h:transforms){
			if(h.getLabel().equals(label)){
				h.getTransform().setTransform(tr);
				return h.getTransform();
			}
		}
		System.out.println("Adding a transform "+label);
		TransformGroup tmp = TransformFactory.getLabledAxis(tr, label,color);
		transforms.add(new TransformHolder(tmp, label));
		BranchGroup child = new BranchGroup();
		child.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		child.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		child.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		child.setCapability(BranchGroup.ALLOW_DETACH);
		child.addChild(tmp);
		base.addChild(child);
		return tmp;
	}
	
	public void resetView(){
		//System.out.println("Resetting view");
        TransformGroup viewTransform = simpleU.getViewingPlatform().getViewPlatformTransform();
        
        Transform3D t3d = new Transform3D(); 

        t3d.lookAt(new Point3d(20,20,20),//Position of camera
        		new Point3d(0,0,0), //position of base frame
        		new Vector3d(0,0,1)); // orentation of camera
        t3d.invert();
        
        viewTransform.setTransform(t3d);
	}
	public Transform3D getBaseLocation() {
		if(baseLocation == null) {
			baseLocation = new Transform3D();
		}
		return baseLocation;
	}
	public void clearTransforms() {
		base.removeAllChildren();
		addTransform(getBaseLocation() , "Base",Color.cyan);
	}
	
	private class TransformHolder{
		private final TransformGroup mine;
		private final String label;

		public TransformHolder(TransformGroup mine,String label){
			this.mine = mine;
			this.label = label;
			
		}

		public String getLabel() {
			return label;
		}

		public TransformGroup getTransform() {
			return mine;
		}
	}

}
