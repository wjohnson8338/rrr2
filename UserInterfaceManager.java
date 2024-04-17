/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
/**
 *
 * @author wjohn
 */
public class UserInterfaceManager {
    
    private final Stage stage; 
    private ValueBar healthBar; // This will hold our health value for the player 
    private ValueBar staminaBar; // This will hold our stamina value for the player 
    
    public UserInterfaceManager() {
        /**
         * Only creates the UIM and Stage for it. 
         * 
         * POST REQUISITES:// 
         * Begin called init on value bars, methods are inside the class.
         */
        this.stage = new Stage();
    }
    
    public void initHealthBar(String name, Vector2 screenPosition, float scale,float amount, float minAmount, float maxAmount, Actor drawableBar) {
        /**
         * Creates a health bar on screen 
         * 
         * PRE REQUISITES:// 
         * 1. For safety all core classes must have been properly setup, this mainly includes Player, GameInputProcessor, etc. 
         * 
         * NOTE:// 
         * I do not put this in the constructor due to leaving myself with the option of changing this in the future, I am aiming for flexibility.
         * @param screenPosition The Vector2 X and Y coordinates of where this appears on the screen
         * @param amount Starting amount inside the bar 
         * @param minAmount Amount cannot go lower than this
         * @param maxAmount Amount cannot go higher than this
         * @param drawableBar The actor to draw, this will probably be the red health texture for example, or green stamina texture.         
         */
        this.healthBar = new ValueBar(name, screenPosition, scale, amount, minAmount, maxAmount, drawableBar);
        this.healthBar.addToStage(this.stage); // Once again, I leave this out of ValueBar's constructor for future flexibility.
    }
    
    public void initStaminaBar(String name, Vector2 screenPosition, float scale,float amount, float minAmount, float maxAmount, Actor drawableBar) {
        /**
         * Creates a stamina  bar on screen 
         * 
         * PRE REQUISITES:// 
         * 1. For safety all core classes must have been properly setup, this mainly includes Player, GameInputProcessor, etc. 
         * 
         * NOTE:// 
         * I do not put this in the constructor due to leaving myself with the option of changing this in the future, I am aiming for flexibility.
         * @param screenPosition The Vector2 X and Y coordinates of where this appears on the screen
         * @param amount Starting amount inside the bar 
         * @param minAmount Amount cannot go lower than this
         * @param maxAmount Amount cannot go higher than this
         * @param drawableBar The actor to draw, this will probably be the red health texture for example, or green stamina texture.         
         */
        this.staminaBar = new ValueBar(name, screenPosition, scale, amount, minAmount, maxAmount, drawableBar);
        this.staminaBar.addToStage(this.stage);
    }
  
    public Vector2 getHealthBarPosition() {
        /**
         * Returns the health bar's position
         * 
         * PRE REQUISITRES://
         * 1. this.initHealthBar must be successfully called and setup.
         * 
         * @return Vector2 the coordinates of the health bar
         */
        return this.healthBar.getPosition();
    }
    
    public ValueBar getHealthValueBar() {
        /**
         * Returns the ValueBar class for what manages health. 
         * 
         * PRE REQUISITES://
         * 1. this.initHealthBar must be successfully called and setup.
         * 
         * @return ValueBar the health value bar
         */
        return this.healthBar;
    }
    
    public ValueBar getStaminaValueBar() {
        /**
         * Returns the ValueBar class for what manages stamina. 
         * 
         * PRE REQUISITES://
         * 1. this.initStaminaBar must be successfully called and setup
         * 
         * @return ValueBar the stamina value bar 
         */
        return this.staminaBar;
    }
    public void render() {
        this.stage.act();
        this.healthBar.updateRenderedBar();
        this.staminaBar.updateRenderedBar();
        this.stage.draw();
    }
    
    public void dispose() {
        this.stage.dispose();
    }
}
