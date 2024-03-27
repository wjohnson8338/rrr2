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
// GLTF Imports

// GDX Native 3D Imports
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;
// GDX Native Imports 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.kotcrab.vis.ui.VisUI;

/**
 *
 * @author wjohn
 */
public class Debugger {
    
    public static void printComponents(GameObject gameObject) {
        /**
         * Returns the class name of each component within a Mundus GameObject
         * 
         * @param gameObject GameObject to print components from 
         */
        Array<Component> componentArray = gameObject.getComponents();
        for (int i = 0; i < componentArray.size; i++) {
            System.out.println(componentArray.get(i).getClass());
        }
    }
}
