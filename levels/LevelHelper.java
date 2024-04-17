/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game.levels;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.runtime.Mundus;
import com.mygdx.game.GameCamera;
import com.mygdx.game.PhysicsSystem;
import com.mygdx.game.Player;

/**
 *
 * @author wjohn
 */
public class LevelHelper {
    public static Object[] autoload(String sceneName, String terrainName, Mundus mundus, Player player, PhysicsSystem physicsSystem, GameCamera camera) {
        /** Things to load checklist
         * 1. Load the mundus scene mundus.loadScene(string name) into a Scene object
         * 2. Grab our TerrainObject  with  scene.sceneGraph.findByName("terrainName")
         * 3. Load TerrainComponent and CAST as well, will look like this  tComponent = (TerrainComponent) terrainObject.findComponentByType(Component.Type.TERRAIN)
         * 4. Re-Init player model with player.initPlayerModel(scene.sceneGraph.findByName("rrrAnimed.gltf")
         * 5. Re Create Animation Controller animController = new AnimationController(player.getModelInstance())
         * 6. Re-Init player.initAnimationController(animController)
         * 7.  Add Physics Body
         * 
         * @return AnimationController The only class we are unable to re-create.
         */
        Scene scene = mundus.loadScene(sceneName);
        GameObject  terrainObject = scene.sceneGraph.findByName(terrainName);
        TerrainComponent terrainComponent = (TerrainComponent) terrainObject.findComponentByType(Component.Type.TERRAIN);
        
        player.initPlayerModel(scene.sceneGraph.findByName("rrrAnimed.gltf"));
        AnimationController animController = new AnimationController(player.getModelInstance());
        player.initAnimationController(animController);
        
        
        scene.cam = camera.getCamera();
      
        physicsSystem.clear();
        physicsSystem.addTerrain(terrainObject, terrainComponent);
        physicsSystem.addBody(player.initPlayerEntity());
        Object[] returnables = {scene, animController};
        // We must return a new Animation Controller. 
        
        return returnables;
    }
}
