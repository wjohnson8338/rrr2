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
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;

// GLTF Imports

// Bullet Imports
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;

// GDX Native 3D Imports
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
// GDX Native Imports 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Quaternion;


/**
 *
 * @author wjohn
 */
public class Player {
    // Determine if we are moving left, right, down, up 
    public static int MOVE_FORWARD = 0;
    public static int MOVE_BACK = 1;
    public static int MOVE_LEFT = 2;
    public static int MOVE_RIGHT = 3;
    public static int MOVE_FORWARDLEFT = 4;
    public static int MOVE_FORWARDRIGHT = 5;
    public static int MOVE_BACKLEFT = 6;
    public static int MOVE_BACKRIGHT = 7;
    // Determines character model rotation
    public static int ANGLE_LEFT = 0;
    public static int ANGLE_RIGHT = 1;
    // Model Fields
    private GameObject playerModel; // Mundus GameObject
    private ModelInstance playerInstance; // Taken from Mundus GameObject, created from updatePlayerModel
    private PhysicsSystem physicsSystemReference;
    // Animation Fields 
    private AnimationController animationController;
    // Physics Fields
    private final float mass = 2f;
    private final Vector3 inertia = new Vector3();
//    private final Vector3 collisionDimensions = new Vector3(0.35f, 1.25f, 0.23f);
    private final Vector3 playerGravity = new Vector3(0f, 150f, 0f);
    private btRigidBody playerRigidBody;
    PlayerMotionState playerMotionState;
    // Raycast Fields
    private ClosestNotMeRayResultCallback raycastCallback;
    // Location/Rotation Fields
    private Vector3 playerModelPosition;  // Rendered Model Body 
    private Vector3 playerBodyPosition; // Physics Body
    private float playerAngleFacingProspect = 0f; // Used to determine where the player is FACING
    private float playerAngleFacingCurrent = 0f; // Active Angle
    private float playerAngleFacingSensitivity = 750f; // Speed of Player Rotation Angle Change 
    private GameCamera camera; 
    // Movement Physics Fields
    private boolean isReadyForMovementAudio = false;
    private boolean isMoving = false;
    private boolean isSprinting = false;
    private boolean isStaminaDepleted = false;
    private boolean isGrounded = true;
    private final float movementSpeed = 550f;
    private final float sprintMovementSpeed = 900f;
    private final float jumpForce = 2000f;
    private final float doubleJumpForce = 750f;
    private int playerAngleDirection = Player.ANGLE_LEFT;
    // Core Game Fields (health, stanima, etc) 
    private boolean doubleJumpReady = true;
    private ValueBar staminaValueBar;
    private ValueBar healthValueBar;
    // Core Game Field Factors 
    public static float STAMINA_LOSS_FACTOR = 15f;
    public static float STAMINA_GAIN_FACTOR = 50f;
    public static float STAMINA_REGAIN_DURATION = 3f; // After this amount of time, stamina will go back up. 
    private float staminaDuration = 0f;
    
    public Player(ValueBar healthBar, ValueBar staminaBar) {
        /**
         * PRE REQUISITES://
         * UserInterfaceManager class must be instantiated and properly setup,
         * the initHealthBar and initStaminaBar must also be called and setup properly.
         * POST REQUISITES:// 
         *  1. Update player model instance with this.updatePlayerModel(scene.sceneGraph.findByName("playerModelName")  From Mundus
         *  2. create GameCamera and set playerCamera with this.setCamera(GameCamera camera)
         *  3. If first time creating, then construct GameInputProcessor and pass in THIS player object
         *  
         */
        // Be aware that we should update our player model instance as soon as this class is instantiated 
        this.healthValueBar = healthBar;
        this.staminaValueBar = staminaBar;
    }
    
    public void initAnimationController(AnimationController animController) {
        /**
         * Create a reference for anim controller, required to manage animations. 
         * 
         * @param animController The animation controller to pass in from main 
         * 
         */
        this.animationController = animController;
    }
    public void initPhysicsSystemReference(PhysicsSystem physicsSystem) {
        /**
         * Provide a reference for terrain so our jump mechanics will work (checking collision) 
         *
         */
        this.physicsSystemReference = physicsSystem;
    }
    public void initPlayerModel(GameObject playerModel) {
        /**
         * Should be called after creating player class, 
         * updates GameObject and ModelInstance for player class, 
         * also updates the position 
         * 
         * @param playerModel Updates the player model from scene.sceneGraph.findByName(String mundusObjectName) 
         * 
         */
        this.playerModel = playerModel;
        ModelComponent tmpModelComponent = (ModelComponent) this.playerModel.findComponentByType(Component.Type.MODEL);
        this.playerInstance = tmpModelComponent.getModelInstance();
        this.updatePlayerModelPosition();
    }
    
    public btRigidBody initPlayerEntity() {
        /**
         * Creates a brand new rigidBody with a collision shape that aligns with the player 
         * 
         * POST REQUISITES://
         * 1. With the returned btRigidBody, immediately add it to our physics simulation
         * 
         * @return btRigidBody We return this so that we can add the rigid body to our physics simulation 
         */
//        btBoxShape box = new btBoxShape(this.collisionDimensions);
        btCapsuleShape capsule = new btCapsuleShape(0.5f, 1.3f);
//        btCollisionShape box = Bullet.obtainStaticNodeShape(this.getModelInstance().nodes);
        capsule.calculateLocalInertia(this.mass, this.inertia);
        this.playerMotionState = new PlayerMotionState(this.playerModel, 1.4f);
        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(this.mass, this.playerMotionState, capsule, this.inertia);
        this.playerRigidBody = new btRigidBody(info);
        this.playerRigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        this.playerRigidBody.setAngularFactor(new Vector3(0f, 0f, 0f));
        this.playerRigidBody.setCollisionFlags(playerRigidBody.getCollisionFlags() | btRigidBody.CollisionFlags.CF_CHARACTER_OBJECT);
        this.playerRigidBody.setDamping(0f, 100f);
        this.playerRigidBody.setGravity(this.playerGravity);
        this.playerRigidBody.setRestitution(0f);
        this.updatePlayerBodyPosition();
        
        // INIT RAYCASTING
        this.raycastCallback = new ClosestNotMeRayResultCallback(this.playerRigidBody);
        return this.playerRigidBody;
    }   
    
//    public btRigidBody initPlayerEntityFeet() {
//        /**
//         * Creates a fresh set of feet for our player rigid body, using a non-collidable rigidbody
//         * PRE REQUISITES://
//         * 1. this.initPlayerEntity() must be called in main with all post requisites of that method completed> 
//         * 
//         * POST REQUISITES://
//         * 1. the btRigidBody returned from this must be added to the physics world as soon as possible.
//         * 
//         * @return btRigidBody the rigidbody representing the feet is returned 
//         */
//        // Creating feet to check if touching the ground 
//        btBoxShape box = new btBoxShape(new Vector3(0.2f, 0.2f, 0.2f));
//        box.calculateLocalInertia(this.mass, this.inertia); 
//        FeetMotionState feetMotionState = new FeetMotionState(this.playerModel, 0.2f);
//        btRigidBody.btRigidBodyConstructionInfo feetInfo = new btRigidBody.btRigidBodyConstructionInfo(this.mass, feetMotionState, box, this.inertia);
//        this.playerRigidBodyFeet = new btRigidBody(feetInfo);
//        this.playerRigidBodyFeet.setActivationState(Collision.DISABLE_DEACTIVATION);
//        // Make our feet non collidable so it doesn't affect physics world.
//        this.playerRigidBodyFeet.setCollisionFlags(this.playerRigidBodyFeet.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
//        this.playerRigidBodyFeet.setDamping(0f, 100f);
//        this.playerRigidBodyFeet.setGravity(this.playerGravity);
//        this.playerRigidBodyFeet.setRestitution(0f);
//        
//        return this.playerRigidBodyFeet;
//    }

           
    // Methods regarding model position 
    public void updatePlayerModelPosition() {
        this.playerModelPosition = this.getPlayerVector();
    }
    
    public void updatePlayerBodyPosition() {
        Vector3 tmpVector = new Vector3();
        this.playerRigidBody.getWorldTransform().getTranslation(tmpVector);
        this.playerBodyPosition = tmpVector;
    }
    
    public void updateStaminaValue(float deltaTime) {
        this.isStaminaDepleted = staminaValueBar.isEmpty();
        if (this.isSprinting) {
            staminaValueBar.decrementValue(deltaTime * Player.STAMINA_LOSS_FACTOR);
            staminaDuration = Player.STAMINA_REGAIN_DURATION;
        }
        else {
            if (this.staminaDuration > 0) {
                this.staminaDuration -= deltaTime;
            }
            else {
                staminaValueBar.incrementValue(deltaTime * Player.STAMINA_GAIN_FACTOR);
            }
        }
    }
    // Player Movement
    public void movePlayer(int direction) {
        /**
         * Moves player forward
         * 
         * @note Design Decision - Instead of having four functions (move left, forward right, back), use static variables to and "case" to determine where to move
         */
        
//        this.alignPlayerWithCamera(); // Align player rotation with camera 
        Vector3 moveDirection = this.camera.getDirection(); // Grab direction of camera, ie where it's facing 
        
        float deltaTime = Gdx.graphics.getDeltaTime();
        // Apply Movement Speed to RigidBody and maintain the Y value for gravity 
        
        switch (direction) {
            case 0: // forward
//                this.alignPlayerModelRotation(0); // Align player rotation with camera 
                this.beginPlayerFacingAngle(0f);
                break;
            case 1: // back 
                // To rotate our camera "direction", we can just use the .rotate method in Vector3.
                moveDirection.rotate(Vector3.Y, 180);
                this.beginPlayerFacingAngle(180);
//                this.alignPlayerModelRotation(180); 
                break;
            case 2: // left 
                moveDirection.rotate(Vector3.Y, 90);
                this.beginPlayerFacingAngle(90);
//                this.alignPlayerModelRotation(90);
                break;
            case 3: // right 
                moveDirection.rotate(Vector3.Y, -90);
                this.beginPlayerFacingAngle(-90);
//                this.alignPlayerModelRotation(-90);
                break;
            case 4: // forward left 
                moveDirection.rotate(Vector3.Y, 45);
                this.beginPlayerFacingAngle(45);
//                this.alignPlayerModelRotation(45);
                break;
            case 5: // forward right 
                moveDirection.rotate(Vector3.Y,-45);
                this.beginPlayerFacingAngle(-45);
//                this.alignPlayerModelRotation(-45);
                break;
            case 6: // back left -  A + S 
                moveDirection.rotate(Vector3.Y, 135);
                this.beginPlayerFacingAngle(135);
//                this.alignPlayerModelRotation(135);
                break;
            case 7: // back right 
                moveDirection.rotate(Vector3.Y, -135);
                this.beginPlayerFacingAngle(-135);
//                this.alignPlayerModelRotation(-135);
                break;
        }
        if (this.isSprinting && !this.isStaminaDepleted) {
            this.playerRigidBody.setLinearVelocity(new Vector3((moveDirection.x * this.sprintMovementSpeed) * deltaTime, this.playerRigidBody.getLinearVelocity().y, (moveDirection.z * this.sprintMovementSpeed) * deltaTime));
        }
        else {
            this.playerRigidBody.setLinearVelocity(new Vector3((moveDirection.x * this.movementSpeed) * deltaTime, this.playerRigidBody.getLinearVelocity().y, (moveDirection.z * this.movementSpeed) * deltaTime));
        }
        this.isMoving = true;
        this.updatePlayerModelPosition();
    }
    
    private void alignPlayerModelRotation(float deltaTime) {
        // Rotating the player MODEL to face where the camera faces
        Quaternion tmpQuaternion = new Quaternion();
        this.playerModel.getRotation(tmpQuaternion); // Grab the Quaternion from the player, output to one we created 
        
        // Compute shortest Distance to Angle  TODO 
        // This is needed to fix the errors
        
        
//        if (this.playerAngleFacingCurrent-10f > this.playerAngleFacingProspect) {
//            this.playerAngleFacingCurrent -= deltaTime * this.playerAngleFacingSensitivity;
//        }
//        else if (this.playerAngleFacingCurrent+10f < this.playerAngleFacingProspect) {
//            this.playerAngleFacingCurrent += deltaTime * this.playerAngleFacingSensitivity;
//        }
        
        
        // This is our best way to avoid 360 rotations 
        if (this.playerAngleFacingProspect <= 180f & this.playerAngleFacingProspect >= 170f) {
            this.playerAngleFacingCurrent = this.playerAngleFacingProspect;
        }
        else if (this.playerAngleFacingProspect >= -180f && this.playerAngleFacingProspect <= -170f) {
            this.playerAngleFacingCurrent = this.playerAngleFacingProspect;
        }
        else if (this.playerAngleFacingProspect >= 0 && this.playerAngleFacingProspect <= 10f) {
            this.playerAngleFacingCurrent = this.playerAngleFacingProspect;
        }
        else if (this.playerAngleFacingProspect <= 0 && this.playerAngleFacingProspect >= -10f) {
            this.playerAngleFacingCurrent = this.playerAngleFacingProspect;
        }

        
        
        
        if (this.playerAngleFacingProspect-10f < this.playerAngleFacingCurrent && this.playerAngleFacingCurrent < this.playerAngleFacingProspect + 10f) {
        }
        else if (this.playerAngleDirection == Player.ANGLE_LEFT) {
            this.playerAngleFacingCurrent -= deltaTime * this.playerAngleFacingSensitivity;
        }
        else if (this.playerAngleDirection == Player.ANGLE_RIGHT){
            this.playerAngleFacingCurrent += deltaTime* this.playerAngleFacingSensitivity;
        }
        
        tmpQuaternion.setEulerAngles(this.playerAngleFacingCurrent, tmpQuaternion.x, tmpQuaternion.z); // Modify Quaternion with Angle, -180 to make it face opposite direction
        this.playerMotionState.updateCharacterRotation(tmpQuaternion);
    }
    
    private void beginPlayerFacingAngle(float angleChange) {
        /**
         * 1. This method first takes the prospecting angle and converts it to -180 - 180 
         * 2. Then we find the shortest distance to the prospected angle with our current angle
         * 3. Using absolute values, whichever angle is the smallest, will be our shortest distance angle
         * 4. If the angle is negative, tell our next method to move LEFT
         * 5. Finally, set our prospected angle value and call this.alignPlayerModelRotation which begins rotating
         * 
         * @param angleChange with the NEW reference angles, turn this amount of degrees
         * 
         */
        if (this.playerAngleFacingProspect > 180f && this.playerAngleFacingProspect < 182f) {
            this.playerAngleFacingCurrent = this.playerAngleFacingProspect;
        }
        else if (this.playerAngleFacingCurrent < -180f && this.playerAngleFacingCurrent > -2f) {
            this.playerAngleFacingCurrent = this.playerAngleFacingProspect;
        }
        // Angle to test
        float prospectedAngle = this.camera.getAngleAroundPlayer() -180f  + angleChange;
        
        // These two if statements ensure the prospected angle aligns with our angle system 0 -> 180> -180 -> 0
        if (prospectedAngle <= -180f) { // Passed top right 
            prospectedAngle += 360f;
        }
        else if (prospectedAngle > 180f) { // Passed top lft 
            prospectedAngle -= 360f;
        }
        
        
        // TODO, Remake this Formula. These find three distances, not the correct way and most likely not efficient
        float a0 = prospectedAngle - this.playerAngleFacingCurrent; // normal unchanged angle
        float a1 = a0 + 360; // angle + 360
        float a2 = a0 - 360; // angle - 360 
        
        // Distances but with absolute values for comparison
        float tmpA0 = Math.abs(a0);
        float tmpA1 = Math.abs(a1);
        float tmpA2 = Math.abs(a2);
        
        float closestAngle = prospectedAngle; // Eventually, this will be our closest angle 
        
        // Pick Closest Angle 
        if (tmpA0 > tmpA1 && tmpA0 > tmpA2) {
            closestAngle = a0;
        }
        else if (tmpA1 > tmpA0 && tmpA1 > tmpA2) {
            closestAngle = a1;
        }
        else if (tmpA2 > tmpA0 && tmpA2 > tmpA1) {
            closestAngle = a2; 
        }
        
        // Get Direction to Turn in 
        if (closestAngle < 0f) {
            this.playerAngleDirection = Player.ANGLE_LEFT; // It's negative so move left 
        }
        else {
            this.playerAngleDirection = Player.ANGLE_RIGHT; 
        }
        
        this.playerAngleFacingProspect = prospectedAngle;        
        this.alignPlayerModelRotation(Gdx.graphics.getDeltaTime()); // TODO Eventually remove this and pass easily
    }
   
    
    public void setPlayerFacingAngle(float angle) {
        /**
         * To be used sparingly, this will hardcode the rotation of the player,
         * already called in beginPlayerFacingAngle() and alignPlayerModelRotation()
         * 
         * @param angle The angle to have the player face right away.
         */
        this.playerAngleFacingCurrent = angle;
    }
    public float getPlayerFacingAngle() {
        return this.playerAngleFacingCurrent;
    }
    private void updateAnimationStatus() {
        /**
         * Set the animation based off the action player is performing. 
         * 
         * @animations Usable animations -- "idle", "run.001", "sprint.001"
         */
        if (this.isMoving) {
            if (this.isSprinting & !this.isStaminaDepleted) {
                this.animationController.setAnimation("sprint.001", -1);
                return;
            }
            this.animationController.setAnimation("run.001", -1);
            return;
        }
        
        this.animationController.setAnimation("idle", -1);
        
    }
    private void isGroundedCheck() {
        /**
         * Checks if the user is touching the ground, this can allow jumping
         * 
         * PRE REQUISITES://
         * 1. this.initPlayerModel must be properly called and set up in main
         * 2. this.initPlayerEntity must be properly called and set up in main 
         * 3. this.initPhysicsSystemReference must be properly called, passing in the physics system reference, in main 
         * 
         * @return boolean whether or not the player is on the ground 
         */
        // Reusing Raycast Callback
        raycastCallback.setClosestHitFraction(1.0f);
        raycastCallback.setCollisionObject(null);
        
        Vector3 tmpPosition = new Vector3();
        
        // Vector3.sub means subtraction, same with .add .mul, etc 
        
        // Start the ray at the player's position (MODEL POSITION NOT PHYSICS BODY POSITION)
        // Then we are going to shoot the ray towards this temporary position below 
        tmpPosition.set(this.getPlayerVector()).sub(0f, 0.3f, 0f);
        
        
        this.physicsSystemReference.raycast(this.getPlayerVector().add(0, 0.5f, 0), tmpPosition, raycastCallback);
        // Immediately set our hasHit() to isGrounded
        this.isGrounded = raycastCallback.hasHit();
        // We can also enable double jump since it was hit 
        if (this.raycastCallback.hasHit()) {
            this.doubleJumpReady = true;  // TODO, possibly remove this if statement in the future, I'm sure there's a more efficient way of doing this
        }
    }
    
    
    // Physics Stuff 
    
    public void jump() {
        /**
         * Applies force so that the player rigid body "jumps"
         * 
         * PRE REQUISITES://
         * 
         * 
         */
        
        // If the player is not on the ground 
        if (!this.isGrounded && this.doubleJumpReady) {
            this.playerRigidBody.setLinearVelocity(new Vector3(this.playerRigidBody.getLinearVelocity().x, 0f, this.playerRigidBody.getLinearVelocity().z));
            this.playerRigidBody.applyCentralForce(new Vector3(0f, this.doubleJumpForce, 0f));
            this.doubleJumpReady = false;
        }
        else if (!this.isGrounded) {
            return;
        }
        this.playerRigidBody.applyCentralForce(new Vector3(0f, this.jumpForce, 0f));
    }
    
    public void enableSprint() { this.isSprinting = true; }
    public void disableSprint() { this.isSprinting = false; }
    
    // Must Haves
    
    public void update(float dt) {
        /**
         * Method called each frame in the Main file's update 
         * 
         * PRE REQUISITES://
         * 1. all init methods must be called and properly set up through main, high risk of error if not
         */
        this.updateStaminaValue(dt);
        this.updatePlayerBodyPosition();
        this.isGroundedCheck();
        this.updateAnimationStatus();
        this.camera.disableSprintFOV();
        if (this.isMoving) {
            // This frame we will not change the velocity, so change to false to prepare for next frame 
            this.isMoving = false;
            this.isReadyForMovementAudio = true;
            if (this.isSprinting) {
                this.camera.enableSprintFOV();
            }
        }
        else {
            // If the player isn't moving, immediately cancel our X and Z velocity, Y stays the same for gravity.
            this.playerRigidBody.setLinearVelocity(new Vector3(0f, this.playerRigidBody.getLinearVelocity().y, 0f));
            this.isReadyForMovementAudio = false;
        }
    }
    
    // Getters for Player Position
    public Vector3 getPlayerVector() { 
        /**
         * Gets the player's position in the form of a Vector3
         * 
         * @return Vector3 the player position
         */
        return this.playerModel.getPosition(Vector3.X); 
    }
    
    public boolean isStaminaDepleted() { return this.isStaminaDepleted; }
    
    public boolean isMoving() { return this.isReadyForMovementAudio; }
    
    public boolean isSprinting() { return (this.isSprinting && !this.isStaminaDepleted()); }
    
    public float getPlayerAngleFacingProspect() { return this.playerAngleFacingProspect; }
    public float getPlayerAngleFacingCurrent() { return this.playerAngleFacingCurrent; }
    
    public Quaternion getRotation() {
        Quaternion tmpQuaternion = new Quaternion(); // Really need to remove this in the future, TODO
        this.playerModel.getLocalRotation(tmpQuaternion);
        return tmpQuaternion;
    }
    
    
    public float getPlayerModelX() { return this.playerModelPosition.x; }
    
    public float getPlayerModelY() { return this.playerModelPosition.y; }
    
    public float getPlayerModelZ() { return this.playerModelPosition.z; }    
    
    public float getPlayerBodyX() { return this.playerBodyPosition.x; }
    
    public float getPlayerBodyY() { return this.playerBodyPosition.y; }
    
    public float getPlayerBodyZ() { return this.playerBodyPosition.z; }
    
    public Vector3 getLinVelocity() { return this.playerRigidBody.getLinearVelocity(); }
    // Getters for more primitive-like classes regarding Mundus
    public GameObject getGameObject() { return this.playerModel; }
    
    public ModelInstance getModelInstance() { return this.playerInstance; }
    // Setters
    
    public void setCamera(GameCamera camera) { this.camera = camera; }
}
