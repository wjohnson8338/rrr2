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

// GLTF Imports

// GDX Native 3D Imports
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
// GDX Native Imports 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;


public class GameCamera {
    
    public static final float CAM_SENSITIVITY = 0.2f; // Camera Sensitivity
    public static final float CAM_START_PITCH = -40f;
    public static final float CAM_MIN_PITCH = GameCamera.CAM_START_PITCH - 85f;
    public static final float CAM_MAX_PITCH = GameCamera.CAM_START_PITCH + 15f;
    public static final float CAM_NORM_FOV = 80f; // FOV for NOT sprinting 
    public static final float CAM_SPRINT_FOV = 92f; // FOV for sprinting 
    public static final float CAM_FOV_SPEED = 75f;
    public static final float CAM_DISTANCE_CHANGE_SPEED = 1;
    public static final float CAM_MIN_DISTANCE_FROM_PLAYER = 3f;
    public static final float CAM_MAX_DISTANCE_FROM_PLAYER = 8f; 
    
    private Player playerReference;
    private PerspectiveCamera camera;
    private float angleAroundPlayer;
    private float distanceFromPlayer;
    private float pitch;
    private boolean invert; 
    
    private boolean isSprintPOV;
    
    public GameCamera(PerspectiveCamera camera, Player playerReference) {
        this.playerReference = playerReference;
        this.camera = camera;
        this.angleAroundPlayer = 0f;
        this.distanceFromPlayer = 5f;
        this.pitch = GameCamera.CAM_START_PITCH;
        this.isSprintPOV = false;
        camera.far = 500f;
    }
    
    public void updateCamera(float deltaTime) {  
        /**
         * Updates the camera to be fixed upon the player 
         * Inspired off of https://github.com/JamesTKhan/gdx-gltf-starter/blob/video_4_third_person_cam/core/src/com/mygdx/game/MyGdxGame.java
         */
        //         
        
        // Get both distances from camera to player in X and Y
        float distance_x = (float) (this.distanceFromPlayer * Math.sin(Math.toRadians(this.pitch)));
        float distance_y = (float) (this.distanceFromPlayer * Math.cos(Math.toRadians(this.pitch)));
        
        // Get our offsets
        float offsetX = (float) (distance_x * Math.sin(Math.toRadians(this.angleAroundPlayer)));
        float offsetZ = (float) (distance_x * Math.cos(Math.toRadians(this.angleAroundPlayer)));
        
//        this.camera.position.x = playerReference.getPlayerModelX() - offsetX;
//        this.camera.position.y = playerReference.getPlayerModelY() + distance_y;
//        this.camera.position.z = playerReference.getPlayerModelZ() - offsetZ;
        
        this.camera.position.x = playerReference.getPlayerBodyX() - offsetX;
        this.camera.position.y = playerReference.getPlayerBodyY() + distance_y;
        this.camera.position.z = playerReference.getPlayerBodyZ() - offsetZ;
        //
        this.camera.up.set(Vector3.Y);
        this.camera.lookAt(playerReference.getPlayerBodyX(), playerReference.getPlayerBodyY()+0.5f, playerReference.getPlayerBodyZ());
//        this.camera.lookAt(playerReference.getPlayerModelX(), playerReference.getPlayerModelY()+1.5f, playerReference.getPlayerModelZ());

        if (this.isSprintPOV && !this.playerReference.isStaminaDepleted()) {
            this.camera.fieldOfView += GameCamera.CAM_FOV_SPEED * deltaTime;
        }
        else {
            this.camera.fieldOfView -= GameCamera.CAM_FOV_SPEED * deltaTime;
        }
        
        if (this.camera.fieldOfView > GameCamera.CAM_SPRINT_FOV) {
            this.camera.fieldOfView = GameCamera.CAM_SPRINT_FOV;
        }
        else if (this.camera.fieldOfView < GameCamera.CAM_NORM_FOV) {
            this.camera.fieldOfView = GameCamera.CAM_NORM_FOV;
        }
        this.camera.update();
    }
    
    
    // PlayerModel x Camera 
     
    public void reduceAngleAroundPlayer(float amount) { 
        /**
         * Subtracts the specified amount from camera's angleAroundPlayer
         * Called in GameInputProcessor.processInput()
         * A number negative or positive will be passed through, remember that a negative minus a negative will be positive
         * 
         * @param amount Amount to reduce in camera angle, calculated properly in GameInputProcessor 
         */
        
            this.angleAroundPlayer -= amount;
        
        
        // Ensure that our angle doesn't go over the 360 ranges to conserve memory 
        if (this.angleAroundPlayer < -180f) {
            this.angleAroundPlayer = 180f;
            this.playerReference.setPlayerFacingAngle(180f);
        }
        else if (this.angleAroundPlayer > 180f) {
            this.angleAroundPlayer = -180f;
            this.playerReference.setPlayerFacingAngle(-180f);
        }
//        if ((this.angleAroundPlayer > 360f) || (this.angleAroundPlayer < -360f)) {
//            this.angleAroundPlayer = 0f;
//        }
    }
    
    public void reducePitchOnPlayer(float amount) { 
        /**
         * Subtract the specified amount on the camera's pitch
         * Called in GameInputProcessor.processInput()
         * 
         * @param amount Amount to reduce in pitch, calculated properly in GameInputProcessor 
         */
        this.pitch -= -amount;
        
        // Ensure our pitch doesn't go over maximum/min height 
        if (this.pitch < GameCamera.CAM_MIN_PITCH) {
            this.pitch = GameCamera.CAM_MIN_PITCH;
        }
        else if (this.pitch > GameCamera.CAM_MAX_PITCH) {
            this.pitch = CAM_MAX_PITCH;
        }
    }
        
    public void changeDistanceFromPlayer(float amount) {
        /**
         * Increases or Decreases distance from player, respects min bounds and max bounds
         * 
         * @param amount Typically a deltaY in the scrollwheel passed from an input processor 
         * 
         */
        this.distanceFromPlayer += amount * GameCamera.CAM_DISTANCE_CHANGE_SPEED;
        if (this.distanceFromPlayer < GameCamera.CAM_MIN_DISTANCE_FROM_PLAYER) {
            this.distanceFromPlayer = GameCamera.CAM_MIN_DISTANCE_FROM_PLAYER;
        }
        else if (this.distanceFromPlayer > GameCamera.CAM_MAX_DISTANCE_FROM_PLAYER) {
            this.distanceFromPlayer = GameCamera.CAM_MAX_DISTANCE_FROM_PLAYER;
        }
    }
    public void enableSprintFOV() {
        this.isSprintPOV = true;
    }
    
    public void disableSprintFOV() {
        this.isSprintPOV = false;
    }
    public float getAngleAroundPlayer() {
        /**
         * @return float Return camera's angle around player 
         */
        return this.angleAroundPlayer;
    }
    
    public Vector3 getDirection() {
        /** 
         * @return Vector3 Return a vector the camera direction is facing in
         */
        return this.camera.direction;
    }
    
    public float getPositionX() { return this.camera.position.x; }
    
    public float getPositionY() {return this.camera.position.y; }
    
    public float getPositionZ() { return this.camera.position.z; } 
    
    public PerspectiveCamera getCamera() { 
        return this.camera;
    }
    

}
