/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author John Tran
 */
public class Level {
    
    //Create flat plane for now. There are better ways to abstract out level making but for now I was just focusing on functionality.
     public static void createDumbLevel(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace) {
        Box floor = new Box(20, 0.25f, 20); //make a floor
        Geometry floorGeom = new Geometry("floor", floor); //make it physical
        Material mat = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray); //make it gray
        floorGeom.setMaterial(mat);
        floorGeom.setLocalTranslation(0, -10, 0); //put it under
        floorGeom.addControl(new RigidBodyControl(0)); //put 0 here and it'll assume the correct shape + 0 mass for box/sphere
        rootNode.attachChild(floorGeom); //attach it to the world
        physicsSpace.add(floorGeom);    //attach it to the physics logic
        
        //COPY PASTE BUT WITH SPHERES
        Sphere sphere = new Sphere(8, 8, 1); //the first two parameters are for quality, the last parameter is radius
        Geometry sphereGeom = new Geometry("sphere", sphere);
        Material mat2 = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        sphereGeom.setMaterial(mat2);
        sphereGeom.setLocalTranslation(5, -9, 5);
        sphereGeom.addControl(new RigidBodyControl(0));
        rootNode.attachChild(sphereGeom);
        physicsSpace.add(sphereGeom);        
     }
}
