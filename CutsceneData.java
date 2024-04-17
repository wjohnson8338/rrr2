/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector3;
/**
 *
 * @author wjohn
 */
public class CutsceneData {
    private static float THRESHOLD = 4f;
    private String cutsceneName;
    private Array<CutscenePoint> cutscenePoints; 
    private CutscenePoint activePoint;
    private float cutsceneSpeed;
    private boolean isCameraMoved;
    
    public CutsceneData(String cutsceneName, Array<CutscenePoint> cutscenePoints, float cutsceneSpeed) {
        if (cutscenePoints.size < 2) {
            System.out.println("**WARNING** YOU ARE CREATING A CUTSCENE WITH LESS THAN 2 POINTS, THIS WILL RESULT IN ERROR.");
        }
        this.cutsceneName = cutsceneName;
        this.cutscenePoints = cutscenePoints;
        this.cutsceneSpeed = cutsceneSpeed;
        this.activePoint = new CutscenePoint(Vector3.Zero);
        this.isCameraMoved = false; 
    }
    
    public void startCutscene(GameCamera camera) {
        /**
         * Call this each time you BEGIN a cutscene
         * 
         * @param camera used to change the position immediately on cutscene start. 
         */
        
        // Get first point 
        CutscenePoint cp = this.popCutscenePoint();
        // Start our cutscene by teleporting our camera to the desired Vector3.
        camera.setPosition(cp.getPoint());
        if (cp.hasLookAtPoint()) {
            camera.lookAt(cp.getlookAt());
        }
        activePoint = this.popCutscenePoint();
    }
    
    public void stepCutscene(GameCamera camera, float dt) {
        /**
         * So long as our cutscene isn't finished, step through
         * the cutscene process and modify the location's Vector 
         * 
         * @param camera GameCamera object containing position Vector 
         * @param dt DeltaTime to ensure consistency in the translation of the camera 
         */
        this.isCameraMoved = false;
        Vector3 camera_pos = camera.getPosition();
        Vector3 point_pos = this.activePoint.getPoint();
        
        // Move Camera's X
        // TODO Remove Redundant Code 
        if (camera_pos.x > point_pos.x + CutsceneData.THRESHOLD) {
            camera.addPositionX(-(this.cutsceneSpeed * dt)); 
            this.isCameraMoved = true;
        }
        else if (camera_pos.x < point_pos.x - CutsceneData.THRESHOLD) {
            camera.addPositionX(this.cutsceneSpeed * dt);
            this.isCameraMoved = true; 
        }
        
        // Move Camera's Y
        if (camera_pos.y > point_pos.y + CutsceneData.THRESHOLD) {
            camera.addPositionY(-(dt * this.cutsceneSpeed));
            this.isCameraMoved = true;
        }
        else if (camera_pos.y < point_pos.y - CutsceneData.THRESHOLD) {
            camera.addPositionY(dt * this.cutsceneSpeed);
            this.isCameraMoved = true;
        }
        
        // Move Camera's Z 
        if (camera_pos.z > point_pos.z + CutsceneData.THRESHOLD) {
            camera.addPositionZ(-(dt * this.cutsceneSpeed));
            this.isCameraMoved = true;
        }
        else if (camera_pos.z < point_pos.z - CutsceneData.THRESHOLD) {
            camera.addPositionZ(dt * this.cutsceneSpeed);
            this.isCameraMoved = true; 
        }
        
        // Change look direction 
        if (this.activePoint.hasLookAtPoint())  {camera.lookAt(activePoint.getlookAt()); }
        
        camera.getCamera().up.set(Vector3.Y);  // Ensure our camera stays upright 
        
        // We are not done with our cutscene, continue popping CutscenePoint(s)
        if (!this.isCameraMoved && !this.isCutsceneFinished()) { this.activePoint = this.popCutscenePoint(); }       
    }
    
    
    public CutscenePoint popCutscenePoint() {
        /**
         * Returns the first CutscenePoint object in the Array 
         * 
         * PRE-REQUISITES:// 
         * 1. You cannot pop from an empty array, avoid this error by calling this.isCutsceneFinished() (is it empty)
         */
        CutscenePoint cp = this.cutscenePoints.first();
        this.cutscenePoints.removeIndex(0);
        return cp;
    }
    
    public boolean isCutsceneFinished() {
        return this.cutscenePoints.isEmpty();
    }
    
    public String getCutsceneName() {
        return this.cutsceneName;
    }
    
    
}
