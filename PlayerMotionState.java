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

// Bullet Physics Imports
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
// GDX Native 3D Imports
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
// GDX Native Imports 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;


/**
 *
 * @author wjohn
 */
public class PlayerMotionState extends btMotionState {
    private static Vector3 tmpVector = new Vector3();
    private static Quaternion tmpQuat = new Quaternion();
    private static Matrix4 tmpMatrix = new Matrix4();
    private GameObject gameObject;
    private float bodyHeight;
    private Quaternion characterRotation = new Quaternion(0f, 0f, 0f, 1f);
    
    
    public PlayerMotionState(GameObject gameObject, float rigidBodyHeight) {
        this.gameObject = gameObject;
        this.bodyHeight = rigidBodyHeight;
    }
    
    public void updateCharacterRotation(Quaternion rotation) {
        /**
         * Assumes that Quaternion have already set Euler Angles, updates as so.
         * 
         * @param rotation A Quaternion with configured Euler Angles 
         *
         */
        
        this.characterRotation = rotation;
    }
    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(this.gameObject.getPosition(PlayerMotionState.tmpVector), this.gameObject.getRotation(PlayerMotionState.tmpQuat));
    }
    
    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        // Formula Taken from: https://github.com/JamesTKhan/Mundus/wiki/Mundus-jBullet-btMotionState 
        // Some of this code has been changed by me to fit the purposes of my game 
        Matrix4 worldToLocal = tmpMatrix.set(worldTrans).mulLeft(this.gameObject.getParent().getTransform().inv());
        worldToLocal.getTranslation(tmpVector);
        worldToLocal.getRotation(PlayerMotionState.tmpQuat, true);
        this.gameObject.setLocalPosition(PlayerMotionState.tmpVector.x, PlayerMotionState.tmpVector.y - (this.bodyHeight-0.24f), PlayerMotionState.tmpVector.z);
        this.gameObject.setLocalRotation(characterRotation.x, characterRotation.y, characterRotation.z, characterRotation.w);
//        this.gameObject.setLocalPosition(GameMotionState.tmpVector.x, GameMotionState.tmpVector.y, GameMotionState.tmpVector.z);
//        this.gameObject.setLocalRotation(GameMotionState.tmpQuat.x, GameMotionState.tmpQuat.y, GameMotionState.tmpQuat.z, GameMotionState.tmpQuat.z);
    }
}
