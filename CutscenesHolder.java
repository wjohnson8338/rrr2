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
public class CutscenesHolder {
    private Array<CutsceneData> cutscenes;
    private Player player;
    
    public CutscenesHolder(Player player) {
        this.cutscenes = new Array<CutsceneData>();
        this.player = player;
        // Create our test cutscene 
        
        Array<CutscenePoint> tutorialPoints = new Array<CutscenePoint>();
        tutorialPoints.add(new CutscenePoint(new Vector3(183f, 17f, 186f), player.getPlayerModelPosVector())); 
        tutorialPoints.add(new CutscenePoint(new Vector3(281f, 50f, 180f), player.getPlayerModelPosVector()));
        tutorialPoints.add(new CutscenePoint(new Vector3(106f, 15f, 116f)));
        
        this.cutscenes.add(new CutsceneData("test", tutorialPoints, 10f));
    }
    
    public CutsceneData getCutsceneByName(String name) {
        for (int i = 0; i < this.cutscenes.size; i++) {
            if (this.cutscenes.get(i).getCutsceneName() == name) {
                return this.cutscenes.get(i);
            }
        }
        // If we cannot find the name, just return the first one.
        return this.cutscenes.get(0);
    }
    
}
