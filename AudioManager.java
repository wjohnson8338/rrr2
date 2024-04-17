/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
/**
 *
 * @author wjohn
 */
public class AudioManager {
    private Music footsteps_grass_sprint; 
    private Music footsteps_grass_normal;
    
    private Music ambience_grass;
        
    
    private final Player playerReference;
    
    public AudioManager(Player playerReference) {
        /**
         * Handles the audio on our game. 
         * 
         * PRE-REQUISITES:// 
         * 1. Correctly init and setup Player in main 
         * 
         * @param playerReference Used to change audio based off of player state 
         */
        
        this.footsteps_grass_sprint = Gdx.audio.newMusic(Gdx.files.internal("grass_footsteps_sprint.wav"));
        this.footsteps_grass_sprint.setLooping(true);
        this.footsteps_grass_normal = Gdx.audio.newMusic(Gdx.files.internal("grass_footsteps_normal.wav"));
        this.footsteps_grass_normal.setLooping(true);
        
        this.ambience_grass = Gdx.audio.newMusic(Gdx.files.internal("grassAmbience.wav"));
        this.ambience_grass.setVolume(0.2f);
        this.ambience_grass.setLooping(true);
        this.ambience_grass.play();
        this.playerReference = playerReference;
    }
    
    public void play() {
        // Player is Sprinting and Sound is not being played 
        if (this.playerReference.isSprinting() && !this.footsteps_grass_sprint.isPlaying()) { 
            this.footsteps_grass_sprint.play();
            this.footsteps_grass_normal.stop();
        } 
        // Player is moving, but not sprinting, and footsteps isn't playing
        else if (this.playerReference.isMoving() && !this.footsteps_grass_normal.isPlaying() && !this.playerReference.isSprinting()) { 
            System.out.println("hit");
            this.footsteps_grass_normal.play();
            this.footsteps_grass_sprint.stop();
        }
        // Player is not moving, immediately stop all footsteps sounds 
        else if (!this.playerReference.isMoving()) {
            this.footsteps_grass_normal.stop();
            this.footsteps_grass_sprint.stop();
        }
    }
}
