/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game.levels;

// My Imports 
import com.mygdx.game.Player;
import com.mygdx.game.GameCamera;
// Mundus Imports
import com.mbrlabs.mundus.runtime.*;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.*;
import com.mbrlabs.mundus.commons.scene3d.components.*;
// LibGDX Imports
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mygdx.game.PhysicsSystem;

/**
 *
 * @author wjohn
 */
public interface Level {
    
    public Object[] load(Mundus mundus, Player player, PhysicsSystem physicsSystem,GameCamera camera);
    // Call LevelHelper.autoload(all parameters) 
    // TODO, instead of object[] use custom data structure 
    // @return Object[] [mundus, player, physicsSystem, camera]
    
    public void checks(Scene scene);
    
    public void update();
}
