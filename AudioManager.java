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
    // Footsteps
    public static Music footsteps_grass_normal = Gdx.audio.newMusic(Gdx.files.internal("grass_footsteps_normal.wav"));
    public static Music footsteps_grass_sprint = Gdx.audio.newMusic(Gdx.files.internal("grass_footsteps_sprint.wav"));
    
    public static Music footsteps_cave_normal = Gdx.audio.newMusic(Gdx.files.internal("cave_footsteps_normal.mp3"));
    public static Music footsteps_cave_sprint = Gdx.audio.newMusic(Gdx.files.internal("cave_footsteps_sprint.mp3"));
    // Ambiences
    public static Music ambience_cave = Gdx.audio.newMusic(Gdx.files.internal("caveAmbience.wav"));
    public static Music ambience_grass = Gdx.audio.newMusic(Gdx.files.internal("grassAmbience.wav"));

    // Stored Data to Play
    private Music active_footsteps_normal;
    private Music active_footsteps_sprint;
    private Music active_ambience;

    
        
    
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
        
        // Ambience Volume/Loop Edits
        this.ambience_grass.setVolume(0.1f);
        this.ambience_grass.setLooping(true);
        
        this.ambience_cave.setVolume(1.1f);
        this.ambience_cave.setLooping(true);
        // Footstep Volume/Loop Edits
        this.footsteps_grass_normal.setLooping(true);
        this.footsteps_grass_sprint.setLooping(true);
        //
        
        
        // Defaults
        //Ambience
        this.active_ambience = AudioManager.ambience_grass;
        this.active_ambience.play();
        this.playerReference = playerReference;
        //Footsteps
        this.active_footsteps_normal = footsteps_grass_normal;
        this.active_footsteps_sprint = footsteps_grass_sprint;
    }
      
    public void stepAudio() {
        // Player is Sprinting and Sound is not being played 
        if (this.playerReference.isSprinting() && !this.active_footsteps_sprint.isPlaying()) { 
            this.active_footsteps_sprint.play();
            this.active_footsteps_normal.stop();
        } 
        // Player is moving, but not sprinting, and footsteps isn't playing
        else if (this.playerReference.isMoving() && !this.active_footsteps_normal.isPlaying() && !this.playerReference.isSprinting()) { 
            this.active_footsteps_normal.play();
            this.active_footsteps_sprint.stop();
        }
        // Player is not moving, immediately stop all footsteps sounds 
        else if (!this.playerReference.isMoving()) {
            this.active_footsteps_normal.stop();
            this.active_footsteps_normal.stop();
        }
    }
 
    public void setAmbienceAudio(Music ambienceAudio) {
        /**
         * Changes the ambience audio to the passed parameter 
         * Will also stop audio for the currently playing ambience audio
         * 
         * @param ambienceAudio a Music object for the audio manager to play 
         */
        this.active_ambience.stop();
        this.active_ambience = ambienceAudio;
    }
    
    public void setNormalRunAudio(Music normalRunAudio) {
        /**
         * Changes non-sprint movement audio to passed parameter
         * 
         * @param normalRunAudio a Music object to play when character moves (non-sprinting)
         */
//        this.active_footsteps_normal.stop();
        this.active_footsteps_normal.stop();
        this.active_footsteps_normal = normalRunAudio;
    }
    
    public void setSprintRunAudio(Music sprintRunAudio) {
        /**
         * Changes sprint movement audio to passed parameter
         * 
         * @param normalRunAudio a Music object to play when character moves (non-sprinting)
         */
//        this.active_footsteps_sprint.stop();
        this.active_footsteps_sprint.stop();
        this.active_footsteps_sprint = sprintRunAudio;
    }
    public void playAmbienceAudio() {
        /**
         * Play Audio Manager's Active Ambience Audio 
         * 
         * PRE REQUISITES:// 
         * 1. setAmbienceAudio(music Music) must have been called at least once. 
         */
        this.active_ambience.play();
    }
    
}
