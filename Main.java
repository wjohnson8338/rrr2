package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

// My Imports 
import com.mygdx.game.levels.Level;
import com.mygdx.game.levels.TestLevel;
// Scene2D Imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
// Mundus Imports
import com.mbrlabs.mundus.runtime.*;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.*;
import com.mbrlabs.mundus.commons.scene3d.components.*;

// Bullet Import
import com.badlogic.gdx.physics.bullet.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
// GLTF Imports
// GDX Native 3D Imports
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;
// GDX Native Imports 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.kotcrab.vis.ui.VisUI;


public class Main extends ApplicationAdapter {
    // Scene 2D and UI  // TODO All of this information should be moved to a class.
    private Stage stage;
    private Image image;
    private Image imageTwo;
    private UserInterfaceManager uiManager;
    // Scene Building Information
    private Mundus mundus;
    private Scene scene;   
    private GameObject terrainObject;
    private TerrainComponent terrainComponent;
    
    // Camera Information
    private GameCamera camera;
    // Player Information
    private Player player;
    private AnimationController animController; // Debug 
    // Bullet Physics Handling
    PhysicsSystem physicsSystem;
    private DebugDrawer debugDrawer;
    private btRigidBody playerRigidBody;
    private btRigidBody terrainBody;
//    private btCollisionConfiguration bulletConfig; // Configure bullet collision, and algorithms associated 
//    private btDispatcher bulletDispatcher; // Iterates over pairs looking for collisions. // trash 
    // Input Handling 
    private GameInputProcessor inputProcessor; 
    // Cutscene Management
    private CutscenesHolder cutscenesHolder;
    // Audio Management
    private AudioManager audioManager;
    // Level Management
    private Level debugLevel;
    

    @Override
    public void create () {
        // Scene2d // TODO Eventually move this stuff to a class.
        uiManager = new UserInterfaceManager();
        uiManager.initHealthBar("health", new Vector2(Gdx.graphics.getWidth() * 0.98f, Gdx.graphics.getHeight() * 0.5f), 1.5f, 100f, 0f, 100f, new Image(new Texture(Gdx.files.internal("healthBar.png"))));
        uiManager.initStaminaBar("stamina", new Vector2(Gdx.graphics.getWidth() * 0.955f, Gdx.graphics.getHeight() * 0.5f), 1f, 100f, 0f, 100f, new Image(new Texture(Gdx.files.internal("staminaBar.png"))));
        // Scene Information
        mundus = new Mundus(Gdx.files.internal("RRRProject"));
        scene = mundus.loadScene("areaOne.mundus");
        
        // Terrain Info
        terrainObject = scene.sceneGraph.findByName("terrainAreaOne");
        terrainComponent = (TerrainComponent) terrainObject.findComponentByType(Component.Type.TERRAIN);  
        
        // Player Information
        player = new Player(uiManager.getHealthValueBar(), uiManager.getStaminaValueBar());
        player.initPlayerModel(scene.sceneGraph.findByName("rrrAnimed.gltf"));
        // Camera Information
        camera = new GameCamera(new PerspectiveCamera(80f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), player);
        player.setCamera(camera);
        // Physics Handling  
        Bullet.init(); // Required before making any further calls to Bullet
        physicsSystem = new PhysicsSystem(true);
        player.initPhysicsSystemReference(physicsSystem);
        // Input Handling 
        inputProcessor = new GameInputProcessor(camera, player, physicsSystem);
        Gdx.input.setInputProcessor(inputProcessor);
        Gdx.input.setCursorCatched(true);
        // Animation Handling  
        animController = new AnimationController(player.getModelInstance());
        player.initAnimationController(animController);
        // Cutscene Management
        cutscenesHolder = new CutscenesHolder(player);
        // Audio Manager
        audioManager = new AudioManager(player);
        // Set camera for scene
        scene.cam = camera.getCamera();        
        
        // Now add our Physics Body
        terrainBody = physicsSystem.addTerrain(terrainObject, terrainComponent);
        physicsSystem.addBody(player.initPlayerEntity());
        
        // CUTSCENE TESTING
        camera.playCutscene(cutscenesHolder.getCutsceneByName("testing"));
        camera.getCamera().far = 240f;
        
        // Level Testing
        debugLevel = new TestLevel();
        
        
        
    }
    
    @Override
    public void resize(int width, int height) {
        // This block of code will handle resizing and camera viewport.
    }

    @Override
    public void render () {
        ScreenUtils.clear(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float dt = Gdx.graphics.getDeltaTime();
        
        physicsSystem.update(Gdx.graphics.getDeltaTime()); 
        animController.update(Gdx.graphics.getDeltaTime());
        inputProcessor.processInput();
        player.update(dt);
        camera.updateCamera(dt);
        scene.sceneGraph.update();
        scene.render();
        physicsSystem.render(camera, player); // Rendering Debug Lines
        uiManager.render();
        audioManager.play();
        
        // Debugger
        if (Gdx.input.isKeyPressed(Keys.X)) {
            Object[] returnedItems = debugLevel.load(mundus, player, physicsSystem, camera);
            // [scene, animationController]
            scene = (Scene) returnedItems[0];
            animController = (AnimationController) returnedItems[1];
        }



    }

    @Override
    public void dispose () {
        mundus.dispose();
        scene.dispose();
        physicsSystem.dispose();
        stage.dispose();
        
    }
}
