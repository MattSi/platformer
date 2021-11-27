package org.propig.game.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
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
    private Animation jumpAnimation;
    private Animation celebrateAnimation;
    private Animation dieAnimation;


    private Sound killedSound;
    private Sound jumpSound;
    private Sound fallSound;

    private boolean isAlive;
    private Vector2 position;
    private float previousBottom;
    private Vector2 acceleration;


    public Vector2 lanternTop, lanternBottom, lanternFront;

    public PlayerStatus playerStatus;
    private float movement;
    private boolean isJumping;
    public FaceDirection direction = FaceDirection.Front;
    private static final float moveAcceleration = 13000f;
    private static final float maxMoveSpeed = 1000f;
    private static final float gravityAcceleration = 1000f;
    private static final float maxFallSpeed = 550f;
    private static final float groundDragFactor = 0.48f;
    private static final float jumpVelocity = 550f;


    public Player( Vector2 position, Stage s) {
        super(position.x, position.y, s);
        actorType = ActorType.Player;
        lanternBottom = new Vector2(0,0);
        lanternTop = new Vector2(0,0);
        lanternFront = new Vector2(0,0);
        loadContent();
        setAnimation(idleAnimation);
        playerStatus = PlayerStatus.Idle;
        movement = 0.0f;
        isJumping = false;
        Controllers.clearListeners();
        Controllers.addListener(this);

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
            lanternFront = new Vector2(getX() + localBounds.width /2, getY() );
        } else {
            lanternFront = new Vector2(getX() + getWidth() - localBounds.width/2, getY() );
        }
        lanternTop = new Vector2(getX(), getY() + getHeight() + 5);


        if(playerStatus == PlayerStatus.Jumping || playerStatus == PlayerStatus.Falling){
            setAnimation(jumpAnimation);
        } else {
            if(MathUtils.round(Math.abs(velocityVec.x)) >2){
                setAnimation(runAnimation);
            } else {
                velocityVec.x=0f;
                setAnimation(idleAnimation);
            }
        }

        /*
        1. 处理连续输入
         */
        getInput();


        float speedX = movement * moveAcceleration * dt;
        float speedY = -gravityAcceleration * dt;
        velocityVec.add( speedX, speedY);

        velocityVec.x *= groundDragFactor;
        velocityVec.x = MathUtils.clamp(velocityVec.x, -maxMoveSpeed, maxMoveSpeed);
        velocityVec.y = MathUtils.clamp(velocityVec.y, -maxMoveSpeed, maxMoveSpeed);



        moveBy(velocityVec.x * dt, velocityVec.y * dt);

        if(onSolid(lanternBottom)){
            if(velocityVec.x == 0){
                setAnimation(idleAnimation);
            } else {
                setAnimation(runAnimation);
            }
        } else {
            setAnimation(jumpAnimation);
            if( velocityVec.y > 0) {
                playerStatus = PlayerStatus.Jumping;
            } else {
                playerStatus = PlayerStatus.Falling;
            }
        }


        if(velocityVec.x < 0){
            setScaleX(1);
        } else {
            setScaleX(-1);
        }
        // setX( x);
        // setY(y);


        boundToWorld();
        movement = 0.0f;
        isJumping = false;
      // velocityVec.set(0,0);

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
        playerStatus = PlayerStatus.Jumping;
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

//
//    public void applyPhysics(float dt){
//        Vector2 previousPosition = position;
//        velocityVec.x += movement*moveAcceleration*dt;
//        velocityVec.y = MathUtils.clamp(velocityVec.y - gravityAcceleration*dt, -maxFallSpeed, maxFallSpeed);
//    }

    private void loadContent(){
        runAnimation = loadAnimationFromAssetManager("Sprites/Player/Run.png",1,10,0.06f,true,false);
        idleAnimation = loadAnimationFromAssetManager("Sprites/Player/Idle.png",1,1,1f,true,false);
        jumpAnimation = loadAnimationFromAssetManager("Sprites/Player/Jump.png",1,11,0.1f,false,false);
        celebrateAnimation = loadAnimationFromAssetManager("Sprites/Player/Celebrate.png",1,11,0.1f,false,false);
        dieAnimation = loadAnimationFromAssetManager("Sprites/Player/Die.png",1,12,0.1f,false,false);


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


    public enum PlayerStatus{
        Idle,
        Running,
        Jumping,
        Falling
    }


}
