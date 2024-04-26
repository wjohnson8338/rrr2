/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 *
 * @author wjohn
 */
public class GoalFrame {
   
    private Vector2 position;
    private String text;
    
    private Actor drawable;
    private float drawableWidth;
    private float drawableHeight;
    
    private Actor textLabel;
    
    public GoalFrame(Vector2 screenPosition, float scale) {
        // Init Frame and Width Height 
        this.drawable = new Image(new Texture(Gdx.files.internal("goalsFrame.png")));
        this.drawable.setColor(255f, 255f, 255f, 0.10f);
        this.drawable.scaleBy(scale);
        this.drawableWidth = this.drawable.getWidth() * + 1f + scale; // We add 1f because scaleBy increases the scale, not sets it 
        this.drawableHeight = this.drawable.getHeight() * + 1f + scale;
        
        // Update Position
        this.position = screenPosition;
        
        // Create Parameters and Generator for Text Label 
        FreeTypeFontGenerator bmfGenerator = new FreeTypeFontGenerator(Gdx.files.internal("times_new_roman_bold.ttf"));
        FreeTypeFontParameter bmfParameters = new FreeTypeFontParameter();
        // Give it some WHITE color
        bmfParameters.shadowColor = Color.WHITE;
        
        BitmapFont bmf = bmfGenerator.generateFont(bmfParameters);
        LabelStyle labelStyle = new LabelStyle(bmf, Color.BLACK);
        this.textLabel = new Label("Add Text Here", labelStyle);
        
        // Change the drawable position, and make sure label correlates
        this.drawable.setPosition(this.position.x, this.position.y);
        this.textLabel.setPosition( this.position.x + (this.drawableWidth * 0.1f), this.position.y + (this.drawableHeight * .95f));
        
    }
    
    public void addToStage(Stage stage) {
        stage.addActor(this.drawable);
        stage.addActor(this.textLabel);
    }
}
