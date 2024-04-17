/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game.levels;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.runtime.Mundus;
import com.mygdx.game.GameCamera;
import com.mygdx.game.PhysicsSystem;
import com.mygdx.game.Player;

/**
 *
 * @author wjohn
 */
public class TestLevel implements Level {

    public TestLevel(){};
    
    @Override
    public Object[] load(Mundus mundus, Player player, PhysicsSystem physicsSystem, GameCamera camera) {
        Object[] returnables = LevelHelper.autoload("TestingSite.mundus", "Terrain", mundus, player, physicsSystem,camera);
        return returnables;
    }

    @Override
    public void checks(Scene scene) {
    }

    @Override
    public void update() {
    }
    
}
