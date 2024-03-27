/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
/**
 *
 * @author wjohn
 */
public class ValueBar {
    
    public static final Texture BarContainerTexture = new Texture(Gdx.files.internal("emptyGlass.png")); 
    
    private String name;
    
    private Vector2 position; // X and Y of scren location, this is for UI
    private float value;  // Amount of whatever, health, mana, stamina, etc
    private float minValue;
    private float maxValue;
    
    private Actor drawable; // Could be a button, image, etc 
    private Actor border; // We're going to use the same border for all ValueBars.
    private float drawableWidth;
    private float drawableHeight;
    
    public ValueBar(String name, Vector2 screenPosition, float scale, float amount, float minAmount, float maxAmount, Actor drawableBar) {
        /**
         * Creates a value bar that can be updated and drawn to screen. Automatically positions from the center.
         * 
         * PRE REQUISITES:// 
         * 1. drawableBar must be the same size as the border bar, assets are located in the assets folder.
         * 
         * POST REQUISITES://
         * 1. Call this.addToStage and pass the stage object you wish to have this ValueBar in 
         * 2. In the uiManager or render, call updateRenderedBar so we know how much to show 
         * 
         * @param name Used to find this UI component through uiManager when searching
         * @param screenPosition The Vector2 X and Y coordinates of where this appears on the screen
         * @param scale Amount to scale drawable by 
         * @param amount Starting amount inside the bar 
         * @param minAmount Amount cannot go lower than this, this will typically be 0
         * @param maxAmount Amount cannot go higher than this
         * @param drawableBar The actor to draw, this will probably be the red health texture for example, or green stamina texture.
         */
        
        this.name = name;
        
        this.value = amount;
        this.minValue = minAmount;
        this.maxValue = maxAmount;
        
        // Setup drawable
        this.drawable = drawableBar;
        this.drawableWidth = drawableBar.getWidth();
        this.drawableHeight = drawableBar.getHeight();
        this.border = new Image(ValueBar.BarContainerTexture);
        
        // Scale and Center the Drawable
        this.setScale(scale);
        this.position = screenPosition;
        this.position.x -= drawableBar.getWidth()*scale/2;
        this.position.y -= drawableBar.getHeight()*scale/2;
        this.setPosition();
        
        // Ensure value is within specified min and max bounds.
        this.enforceValueBounds();
        
        
    }
    
    
    public void addToStage(Stage stage) {
        stage.addActor(this.drawable);
        stage.addActor(this.border);
    }
    
    public void updateRenderedBar() {
        this.drawable.setSize(this.drawableWidth, this.getValuePercentage() * this.drawableHeight);
    }
    
    public void incrementValue(float value) {
        /**
         * Takes passed in value and adds it to the value bar, will not go above bounds
         * 
         * @param value the value to add 
         */
        this.value += value;
        this.enforceValueBounds();
    }
    
    public void decrementValue(float value) {
        /**
         * Takes passed in value and subtracts it from the value bar, will not go below bounds
         * 
         * @param value the value to add 
         */
        this.value -= value;
        this.enforceValueBounds();
    }
    
    public boolean isEmpty() {
        /**
         * Checks if the value is lower or the same as min value bound
         */ 
        return this.minValue >= this.value;
    }
    
    private void enforceValueBounds() {
        /**
         * Ensures that amount doesn't go below, or above the min 
         */
        if (this.value < this.minValue) {
            this.value = this.minValue;
        }
        else if (this.value > this.maxValue) {
            this.value = this.maxValue; 
        }
    }
    private float getValuePercentage() {
        /**
         * Percentage of value filled to max 
         */
        System.out.println(this.value + " v ");
        System.out.println(maxValue);
        return (this.value / this.maxValue);
        
    }
    
    private void setPosition() {
        /**
         * Will change the location of the value bar to the currently set X Y coordinates.
         */
        this.drawable.setPosition(this.position.x, this.position.y);
        this.border.setPosition(this.position.x, this.position.y);
    }
    private void setPosition(Vector2 position) {
        /**
         * Will change the location of the value bar on screen.
         * 
         * @param position Vector2 containing the x and y of the location of the ValueBar
         */
        this.position = position;
        this.drawable.setPosition(this.position.x, this.position.y);
        this.border.setPosition(this.position.x, this.position.y);
    }
    
    private void setScale(float scale) {
        this.drawable.setScale(scale);
        this.border.setScale(scale);
    }
    
    public String getName() { return this.name; }
    
    public Vector2 getPosition() { return this.position; }
    
    public float getValue() { return this.value; }
}
