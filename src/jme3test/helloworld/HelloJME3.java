/*
 * Copyright (c) 2009-2012 jMonkeyEngine All rights reserved. <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. <p/> * Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. <p/> * Neither the name of
 * 'jMonkeyEngine' nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission. <p/> THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Sphere;

/**
 * A walking physical character followed by a 3rd person camera. (No animation.)
 * @author normenhansen, zathras
 */
public class HelloJME3 extends SimpleApplication implements ActionListener {

  private BulletAppState bulletAppState; //controls physics logic
  
  // try to abstract this out to a player class potentially
  private CharacterControl playerControl;
  private Node playerNode;
  private CameraNode camNode;
  
  boolean rotate = false;
  private Vector3f walkDirection = new Vector3f(0,0,0);
  private Vector3f viewDirection = new Vector3f(0,0,0);
  boolean left, right, forward, backward = false;
  boolean leftStrafe = false, rightStrafe = false, 
          leftRotate = false, rightRotate = false;

  public static void main(String[] args) {
    HelloJME3 app = new HelloJME3();
    app.start();
  }

    private void setupKeys() {
        inputManager.addMapping("moveLeft", 
                new KeyTrigger(KeyInput.KEY_A), 
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("moveRight", 
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("moveForward", 
                new KeyTrigger(KeyInput.KEY_W), 
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("moveBackward", 
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("jump", 
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("shoot", 
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, 
                                    "moveLeft", "moveRight", "moveForward",
                                    "moveBackward");
        inputManager.addListener(this, "jump", "shoot");
    }
  @Override
  public void simpleInitApp() {
    // activate physics
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);

    // make a level
    Level.createDumbLevel(rootNode, assetManager, bulletAppState.getPhysicsSpace());
    setupKeys();

    // initialize the player
    Sphere sphere = new Sphere(32, 32, 0.5f);
    Material mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    Geometry playerGeom = new Geometry("player", sphere);
    playerGeom.setMaterial(mat);
    playerGeom.addControl(new RigidBodyControl(1)); //make a sphere with mass 1 for physics
    playerControl = new CharacterControl(new SphereCollisionShape(0.5f), .1f);
    playerControl.setPhysicsLocation(new Vector3f(0, 0, 0));
    playerNode = new Node("playerNode");
    playerNode.addControl(playerControl);
    bulletAppState.getPhysicsSpace().add(playerControl);
    rootNode.attachChild(playerNode);
    playerNode.attachChild(playerGeom);

    // make camera
    camNode = new CameraNode("CamNode", cam);
    camNode.setControlDir(ControlDirection.SpatialToCamera);
    camNode.setLocalTranslation(new Vector3f(0, 1, -5));
    camNode.lookAt(playerGeom.getLocalTranslation(), Vector3f.UNIT_Y);
    playerNode.attachChild(camNode);
    flyCam.setEnabled(false);
  }

   @Override
    public void simpleUpdate(float tpf) {
        Vector3f camDir = cam.getDirection().mult(0.2f);
        Vector3f camLeft = cam.getLeft().mult(0.2f);
        camDir.y = 0;
        camLeft.y = 0;
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 0);
        
        if (left) {
            walkDirection.addLocal(camLeft);
        } else
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (forward) {
            walkDirection.addLocal(camDir);
        } else
        if (backward) {
            walkDirection.addLocal(camDir.negate());
        }
        playerControl.setWalkDirection(walkDirection);
        playerControl.setViewDirection(viewDirection);
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("moveLeft")) {
            if (value) {
                left = true;
            } else {
                left = false;
            }
        } else if (binding.equals("moveRight")) {
            if (value) {
                right = true;
            } else {
                right = false;
            }
        }  else if (binding.equals("moveForward")) {
            if (value) {
                forward = true;
            } else {
                forward = false;
            }
        } else if (binding.equals("moveBackward")) {
            if (value) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (binding.equals("jump")) {
            playerControl.jump();
        }
    }
}
