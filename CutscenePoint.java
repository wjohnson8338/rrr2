/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;
/**
 *
 * @author wjohn
 */
public class CutscenePoint {
    /**
     * Class that contains a point in 3D space, as well as a location for the camera to look at,
     * typically used in CutsceneData
     * 
     * lookAt can be null, which means don't point the camera anywhere else.
     */
    private final Vector3 point;
    private Vector3 lookAt; 
    
    
    public CutscenePoint(Vector3 point) {
        this.point = point;
    }
    
    public CutscenePoint(Vector3 point, Vector3 lookAt) {
        this.point = point;
        this.lookAt = lookAt;
    }
    
    
    
    public Vector3 getPoint() {
        /**
         * Returns a Vector3 to determine where the camera should travel next.
         * 
         * @return Vector3 Point in 3D space.
         */
        return this.point;
    }
    
    public boolean hasLookAtPoint() {
        return this.lookAt != null;
    }
    
    public Vector3 getlookAt() {
        /**
         * Returns a Vector3 to determine where the camera should point at (look)
         * 
         * PRE REQUISITES://
         * 1. Has lookAt point instantiated in constructor
         * 
         * @return Vector3 look direction
         */
        return this.lookAt;
    }
}
