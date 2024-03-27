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
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.*;

/**
 *
 * @author wjohn
 */
public class GameInputProcessor implements InputProcessor {
    
    private GameCamera camera;
    private Player player;
    private PhysicsSystem physicsSystem;
    
    public GameInputProcessor(GameCamera camera, Player player, PhysicsSystem physicsSystem) {
        this.camera = camera;
        this.player = player;
        this.physicsSystem = physicsSystem;
    }
    
    public void processInput() { // Called in Main.java 
        this.camera.reduceAngleAroundPlayer(Gdx.input.getDeltaX() * GameCamera.CAM_SENSITIVITY);
        this.camera.reducePitchOnPlayer(Gdx.input.getDeltaY() * GameCamera.CAM_SENSITIVITY);
        
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
                player.movePlayer(Player.MOVE_FORWARDLEFT);
            }
            else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
                player.movePlayer(Player.MOVE_FORWARDRIGHT);
            }
            else {
                player.movePlayer(Player.MOVE_FORWARD);
            }
//            System.out.println("Moved Forward");
        }
               
        else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
                player.movePlayer(Player.MOVE_BACKLEFT);
            }
            else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
                player.movePlayer(Player.MOVE_BACKRIGHT);
            }
            else {
                player.movePlayer(Player.MOVE_BACK);
            }
        }
        
        else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            player.movePlayer(Player.MOVE_LEFT);
        }

        else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            player.movePlayer(Player.MOVE_RIGHT);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_LEFT)) {
            player.enableSprint();
        }
        
        else {
            player.disableSprint();
        }
        
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            player.jump();
        }
        
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SLASH)) {
            if (physicsSystem.isDebugModeCheck()) {
                physicsSystem.disableDebug();
            }
            else {
                physicsSystem.enableDebug();
            }
            System.out.println(physicsSystem.isDebugModeCheck());
        }
    }
    
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.changeDistanceFromPlayer(amountY);
        return false;
    }
    
}
