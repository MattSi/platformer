package org.propig.game.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class Player extends BaseActor implements ControllerListener {
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> celebrateAnimation;
    private Animation<TextureRegion> dieAnimation;


    private Sound killedSound;
    private Sound jumpSound;
    private Sound fallSound;

    private boolean isAlive;
    private Vector2 position;
    private float previousBottom;
    private Vector2 acceleration;


    public Vector2 lanternTop, lanternBottom, lanternFront;
    private BaseActor sensorTop, sensorBottom, sensorFront;

    private float movement;
    private boolean isJumping;
    public FaceDirection direction = FaceDirection.Front;
    private static final float moveAcceleration = 13000f;
    private static final float maxMoveSpeed = 1000f;
    private static final float gravityAcceleration = 1500f;
    private static final float maxFallSpeed = 550f;
    private static final float groundDragFactor = 0.48f;
    private static final float jumpVelocity = 650f;


    public Player( Vector2 position, Stage s) {
        super(position.x, position.y, s);
        actorType = ActorType.Player;
        lanternBottom = new Vector2(0,0);
        lanternTop = new Vector2(0,0);
        lanternFront = new Vector2(0,0);
        loadContent();
        setAnimation(idleAnimation);
        movement = 0.0f;
        isJumping = false;
        Controllers.clearListeners();
        Controllers.addListener(this);

        sensorTop = new BaseActor(0,0,s);
        sensorTop.loadTexture("Sprites/White.png");
        sensorTop.setSize(localBounds.width, Tile.height/2);
        sensorTop.setBoundaryRectangle();
        sensorTop.setVisible(false);
        sensorTop.setColor(Color.GREEN);

        sensorFront = new BaseActor(0,0,s);
        sensorFront.loadTexture("Sprites/White.png");
        sensorFront.setSize(5, getHeight());
        sensorFront.setBoundaryRectangle();
        sensorFront.setVisible(false);
        sensorFront.setColor(Color.GREEN);

    }

    public void reset(Vector2 position){
        this.position = position;
        velocityVec = Vector2.Zero;
        isAlive = true;
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        /**
         * 1. 获取当前Player的状态，位置信息
         * 2. 更新各个方向灯笼探针的信息
         *
         * 3. 处理连续输入
         * 4. 在各个方向上应用物理规律
         * 5. 处理落点、碰到平台停下。
         */
        lanternBottom = new Vector2(getX() + getWidth()/2, getY() - Tile.height/4);
        if(direction == FaceDirection.Left || direction == FaceDirection.Front){
            lanternFront = new Vector2(getX() + getWidth()/5 , getY() + getHeight()/2);
        } else {
            lanternFront = new Vector2(getX() + getWidth() * 0.7f, getY() + getHeight()/2);
        }
        lanternTop = new Vector2(getX() + getWidth()/2, getY() +localBounds.height  );

        sensorTop.setPosition(getX()+getWidth()/2, getY()+ localBounds.height );
        sensorFront.setPosition(lanternFront.x, getY());



        /*
        1. 处理连续输入
         */
        getInput();


        float speedX = movement * moveAcceleration * dt;
        float speedY = -gravityAcceleration * dt;
        velocityVec.add( speedX, speedY);

        velocityVec.x *= groundDragFactor;
        velocityVec.x = MathUtils.round(MathUtils.clamp(velocityVec.x, -maxMoveSpeed, maxMoveSpeed));
        velocityVec.y = MathUtils.round(MathUtils.clamp(velocityVec.y, -maxMoveSpeed, maxMoveSpeed));


        moveBy(velocityVec.x * dt, velocityVec.y * dt);

        if(onSolid(lanternBottom)){
            if(velocityVec.x == 0){
                setAnimation(idleAnimation);
            } else {
                setAnimation(runAnimation);
            }
        } else {
            setAnimation(jumpAnimation);
        }


        if(velocityVec.x < 0){
            direction = FaceDirection.Left;
            setScaleX(1);
        } else {
            setScaleX(-1);
            direction = FaceDirection.Right;
        }

        boundToWorld();
        movement = 0.0f;
        isJumping = false;

    }

    private void getInput() {
        Controller controller = getController();
        if (controller != null) {
            float xAxis = controller.getAxis(controller.getMapping().axisLeftX) * 1.0f;
            if (Math.abs(xAxis) < 0.5f) {
                movement = 0.0f;
            } else {
                movement = xAxis;
            }

            if(controller.getButton(controller.getMapping().buttonDpadLeft)){
                movement = -1.0f;
            }

            if(controller.getButton(controller.getMapping().buttonDpadRight)){
                movement = 1.0f;
            }

        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement = -1.0f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement = 1.0f;
        }


    }
    
    public void jump(){
        velocityVec.y += jumpVelocity;
        jumpSound.play();
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if(controller.getButton(controller.getMapping().buttonA)){
            if(onSolid(lanternBottom) ){
                jump();
            }
        }
        return false;
    }

    private void loadContent(){
        runAnimation = loadAnimationFromAssetManager("Sprites/Player/Run.png",1,10,0.06f,true,false);
        idleAnimation = loadAnimationFromAssetManager("Sprites/Player/Idle.png",1,1,1f,true,false);
        jumpAnimation = loadAnimationFromAssetManager("Sprites/Player/Jump.png",1,11,0.1f,true,false);
        celebrateAnimation = loadAnimationFromAssetManager("Sprites/Player/Celebrate.png",1,11,0.1f,false,false);
        dieAnimation = loadAnimationFromAssetManager("Sprites/Player/Die.png",1,12,0.1f,false,false);

        jumpSound = getAssetManager().get("Sounds/PlayerJump.wav", Sound.class);



        Texture idle = getAssetManager().get("Sprites/Player/Idle.png", Texture.class);
        int boundWidth = (int) (idle.getWidth()/10 * 0.35);
        int boundLeft = (int)(idle.getWidth()/10 - boundWidth)/2;
        int boundHeight = (int)(idle.getHeight() * 0.7);
        int boundBottom = (int)(idle.getHeight() - boundHeight);
        localBounds = new Rectangle(boundLeft, boundBottom, boundWidth, boundHeight);
        setBoundaryRectangle(boundWidth, boundHeight);
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

 
    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }



}
