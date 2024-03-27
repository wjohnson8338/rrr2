/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

// Mundus Imports
import com.mbrlabs.mundus.runtime.*;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.*;
import com.mbrlabs.mundus.commons.scene3d.components.*;

// Bullet Import
import com.badlogic.gdx.physics.bullet.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.collision.btHeightfieldTerrainShape;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.ebtDispatcherQueryType;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.collision.RayResultCallback;
import com.mbrlabs.mundus.commons.terrain.Terrain;
// GLTF Imports
// GDX Native 3D Imports
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;
// GDX Native Imports 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.kotcrab.vis.ui.VisUI;
import com.badlogic.gdx.utils.Disposable;
// Java Imports
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author wjohn
 */
public class PhysicsSystem implements Disposable {

    public static final float fixedTimeStep = 1/60f; // Refer to this.update()
    
    // Determines terrain physics simulation "resolution" 
//    public static int terrainPhysicsResolutionWidth = 64;
//    public static int terrainPhysicsResolutionLength = 64;
    // Scale heightfieldData values if not in float format, typically at 1.0
    
    // Static Settings Fields
    public static float terrainScale = 1.0f; 
    public static float terrainMinHeight = 0.0f;
    public static float terrainMaxHeight = 100.0f;
    public static int terrainAxis = 1; //  = 0=X, 1=Y, 2=Z -- Sets the axis of elevation.
    public static boolean isFlipQuadEdges = false;
    
    // Core Bullet Fields
    private final btDynamicsWorld dynamicsWorld; // Stores btCollision Objects
    private final btCollisionConfiguration collisionConfig; // Bullet Configuration such as Stack Allocator Size, Default Collision Algorithms, Persistent Manifold Pool Size
    private final btDispatcher dispatcher; // Dispatcher iterates over pairs of objects, searching for collisions
    private final btBroadphaseInterface broadphase; // Acceleration structure to quickly reject pairs of objects, based on axis aligned bounding box (AABB)
    private final btConstraintSolver constraintSolver; 
    
    // Debug Fields
    private Vector3 debugGroundStartRay = new Vector3();
    private Vector3 debugGroundEndRay = new Vector3();
    
    private Vector3 debugDirectionStartRay = new Vector3();
    private Vector3 debugDirectionEndRay = new Vector3();
   
    private final DebugDrawer debugDrawer;
    private boolean isDebugMode;
    
    public PhysicsSystem(boolean isDebug) {
        this.collisionConfig = new btDefaultCollisionConfiguration();
        this.dispatcher = new btCollisionDispatcher(this.collisionConfig);
        
        // Broadphase that adapts to dimensions of world.
        this.broadphase = new btDbvtBroadphase();
        this.constraintSolver = new btSequentialImpulseConstraintSolver();
        this.dynamicsWorld = new btDiscreteDynamicsWorld(this.dispatcher, this.broadphase, this.constraintSolver, this.collisionConfig);
        // Debug Drawer 
        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawAabb);
        this.dynamicsWorld.setDebugDrawer(debugDrawer);
        this.dynamicsWorld.setGravity(new Vector3(0, -60f, 0)); // -22f is original
        this.isDebugMode = isDebug;

    }
    
    public void update(float delta) {
        // Run through collision detections and physics simulationslo[mpmmol
        this.dynamicsWorld.stepSimulation(delta, 1, PhysicsSystem.fixedTimeStep);
    }
    
    public void render(GameCamera camera, Player player) {
        if (this.isDebugMode) {
            this.debugDrawer.begin(camera.getCamera());
            
            // Draw Collisions
            this.dynamicsWorld.debugDrawWorld();
            
            //isGrounded() Ray 
            debugDrawer.drawLine(this.debugGroundStartRay, this.debugGroundEndRay, new Vector3(1f, 0f, 1f));
            
            // Camera Direction Ray
            
//            debugDrawer.drawLine(player.getPlayerVector(), new Vector3(camera.getPositionX(), 2f, camera.getPositionZ()), new Vector3(1f, 0f, 1f));
            
            debugDrawer.draw3dText(player.getPlayerVector(), "CAM ANGLE: " + String.valueOf(camera.getAngleAroundPlayer()));
            debugDrawer.draw3dText(player.getPlayerVector().sub(0f, 0.4f, 0f), "CAM_POS_VEC: X- " + (int) camera.getPositionX() + " | Y- " + (int) camera.getPositionY() + " | Z- " + (int) camera.getPositionZ());
            debugDrawer.draw3dText(player.getPlayerVector().sub(0f, 0.8f, 0f), "CAM_DIR_VEC: X- " + camera.getDirection().x + " | Y- " + camera.getDirection().y + " | Z- " + camera.getDirection().z);

            debugDrawer.draw3dText(player.getPlayerVector().add(0f, 2.4f, 0f), "MODEL_X: " + (int)player.getPlayerModelX());
            debugDrawer.draw3dText(player.getPlayerVector().add(0f, 2.2f, 0f), "MODEL_Y: " + (int)player.getPlayerModelY());
            debugDrawer.draw3dText(player.getPlayerVector().add(0f, 2.0f, 0f), "MODEL_Z: " + (int)player.getPlayerModelZ());
            debugDrawer.draw3dText(player.getPlayerVector().add(0f, 3.0f, 0f), "LIN_VELOCITY_VEC: X- " + (int) player.getLinVelocity().x + " | Y- " + (int) player.getLinVelocity().y + " | Z- " + (int) player.getLinVelocity().z);
            debugDrawer.draw3dText(player.getPlayerVector().add(0f, 3.0f, 0f), "LIN_VELOCITY_VEC: X- " + (int) player.getLinVelocity().x + " | Y- " + (int) player.getLinVelocity().y + " | Z- " + (int) player.getLinVelocity().z);
            
            debugDrawer.draw3dText(player.getPlayerVector().add(0f, 3.5f, 0f), "ANGLE_PROSPECT: X- " + player.getPlayerAngleFacingProspect());
            debugDrawer.draw3dText(player.getPlayerVector().add(0f, 3.7f, 0f), "ANGLE_CURRENT: X- " + player.getPlayerAngleFacingCurrent());

//            debugDrawer.draw3dText(player.getPlayerVector().add(2f, 2.2f, 0f), "J READY: " + player.isGroundedCheck());

            this.debugDrawer.end();
        }
    }
    
    public void addBody(btRigidBody body) {
        this.dynamicsWorld.addRigidBody(body);
    }
    public btRigidBody addTerrain(GameObject terrainObject, TerrainComponent terrainComponent) {
        // Create collisionShape from Terrain 
        btCollisionShape terrainShape = Bullet.obtainStaticNodeShape(terrainComponent.getTerrainAsset().getTerrain().getModel().nodes);
        // Prepare btTerrainShape for STATIC rigid body construction.
        btRigidBody.btRigidBodyConstructionInfo terrainBodyConstructionInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, terrainShape, Vector3.Zero);
        btRigidBody rigidBody = new btRigidBody(terrainBodyConstructionInfo);  
        // Align our physics object with rigidbody
        rigidBody.setWorldTransform(terrainObject.getTransform());
        
        // Debug Prints 
//        System.out.println("Render Terrain Transform");
//        System.out.println(terrainObject.getTransform().toString());
//        System.out.println("Physics Terrain Transform");
//        System.out.println(rigidBody.getWorldTransform().toString());        
        
        
        // Finally, add our rigid body to the physics simulation
        this.addBody(rigidBody);
        return rigidBody;
    }
    
    public void raycast(Vector3 startPosition, Vector3 endPosition, RayResultCallback rayCallback) {
        Vector3 rayFrom = new Vector3(startPosition).sub(0f, 5f, 0f);
        
        // built in method for raytest, will update our rayCallback
        this.dynamicsWorld.rayTest(startPosition, endPosition, rayCallback);
        
        // It has a hit, and is the correct callback. This is like "upcasting"
        Vector3 rayTo = new Vector3();
        if (rayCallback.hasHit() && rayCallback instanceof ClosestRayResultCallback) {
            // Linear Interpolation -- Sourced from:  https://github.com/JamesTKhan/libgdx-bullet-tutorials/blob/master/core/src/com/jpcodes/physics/BulletPhysicsSystem.java -- Function Call at Line 113
            rayTo.set(startPosition).lerp(endPosition, rayCallback.getClosestHitFraction());     
        }
        else {
            rayTo.set(endPosition);
        }
        
        // Raycast Debugs for the Drawer 
        this.debugGroundStartRay.set(startPosition);
        this.debugGroundEndRay.set(endPosition);
    }
    
    public void setDebugDrawer(DebugDrawer drawer) { this.dynamicsWorld.setDebugDrawer(drawer); } 
    
    public void enableDebug() { this.isDebugMode = true; }
    
    public void disableDebug() { this.isDebugMode = false; }
    
    public boolean isDebugModeCheck() { return this.isDebugMode; }
    
    @Override
    public void dispose() {
        this.collisionConfig.dispose();
        this.dispatcher.dispose();
        this.broadphase.dispose();
        this.constraintSolver.dispose();
        this.dynamicsWorld.dispose();
    }
    
}
