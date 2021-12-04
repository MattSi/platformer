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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;


public class Player extends BaseActor {
    private HashMap<PlayerStatus, Animation<TextureRegion>> animationMap;
    private HashMap<PlayerStatus, Rectangle> boundaryMap;

    private PlayerStatus playerStatus;
    private Sound killedSound;
    private Sound jumpSound;

    public boolean isAlive;


    public Vector2 lanternTop, lanternBottom, lanternFront;
    private BaseActor sensorTop, sensorBottom, sensorFront;
    private BaseActor sensorTest;


    private float movement;
    private boolean isJumping;
    public FaceDirection direction = FaceDirection.Front;
    private static final float moveAcceleration = 13000f;
    private static final float maxMoveSpeed = 1000f;
    private static final float gravityAcceleration = 1500f;
    private static final float maxFallSpeed = 550f;
    private static final float groundDragFactor = 0.48f;
    private static final float airDragFactor = 0.58f;
    private static final float jumpVelocity = 650f;



    // Shaders
    String vertexShaderCode;
    String fragmentShaderCode;
    ShaderProgram shaderProgram;
    float time;


    public Player( Vector2 position, Stage s) {
        super(position.x, position.y, s);
        playerStatus = PlayerStatus.Idling;
        actorType = ActorType.Player;
        lanternBottom = new Vector2(0,0);
        lanternTop = new Vector2(0,0);
        lanternFront = new Vector2(0,0);
        loadContent();
        setAnimation(animationMap.get(playerStatus));
        movement = 0.0f;
        isJumping = false;
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

        sensorTest = new BaseActor(0,0,s);
        sensorTest.loadTexture("Sprites/White.png");
        sensorTest.setSize(450, 10);
        sensorTest.setBoundaryRectangle();
        sensorTest.setVisible(true);
        sensorTest.setColor(Color.GREEN);


        vertexShaderCode = Gdx.files.internal("Shaders/default.vs").readString();
        fragmentShaderCode = Gdx.files.internal("Shaders/border.fs").readString();
        shaderProgram = new ShaderProgram(vertexShaderCode, fragmentShaderCode);
        time = 0;
        if(!shaderProgram.isCompiled()){
            System.out.printf("Shader compile error: %s\n", shaderProgram.getLog());
        }

       // addActor(sensorTest);
        isAlive = true;
    }

    public void reset(Vector2 position){
        velocityVec = Vector2.Zero;
        isAlive = true;
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        time += dt;
        if(!isAlive){
            setScaleX(1);
            return;
        }
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
        sensorTest.setPosition(lanternFront.x, lanternFront.y);

        /*
        1. 处理连续输入
         */
        getInput();
        float speedX = movement * moveAcceleration * dt;
        float speedY = -gravityAcceleration * dt;
        velocityVec.add( speedX, speedY);

        if(onSolid(lanternBottom)){
            if(velocityVec.x == 0){
                playerStatus = PlayerStatus.Idling;
            } else {
                playerStatus = PlayerStatus.Running;
                velocityVec.x *= groundDragFactor;
            }
        } else {
            playerStatus = PlayerStatus.Jumping;
            velocityVec.x *= airDragFactor;
        }

        velocityVec.x = MathUtils.round(MathUtils.clamp(velocityVec.x, -maxMoveSpeed, maxMoveSpeed));
        velocityVec.y = MathUtils.round(MathUtils.clamp(velocityVec.y, -maxMoveSpeed, maxMoveSpeed));

        moveBy(velocityVec.x * dt, velocityVec.y * dt);
        setAnimation(animationMap.get(playerStatus));


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



    public void killed(){
        isAlive = false;
        playerStatus = PlayerStatus.Dying;
        killedSound.play();
        setAnimation(animationMap.get(playerStatus));
    }

    private void loadContent(){

        if(animationMap == null){
            animationMap = new HashMap<>();
        }

        if(boundaryMap == null){
            boundaryMap = new HashMap<>();
        }
        animationMap.put(PlayerStatus.Running, loadAnimationFromAssetManager("Sprites/Player/Run.png",1,10,0.06f,true,false));
        boundaryMap.put(PlayerStatus.Running, new Rectangle(0,0,32, 64*0.8f));

        animationMap.put(PlayerStatus.Idling, loadAnimationFromAssetManager("Sprites/Player/Idle.png",1,1,1f,true,false));
        boundaryMap.put(PlayerStatus.Idling, new Rectangle(0,0,28, 64*0.8f));

        animationMap.put(PlayerStatus.Jumping, loadAnimationFromAssetManager("Sprites/Player/Jump.png",1,11,0.1f,true,false));
        boundaryMap.put(PlayerStatus.Jumping, new Rectangle(0,0,32, 60));

        animationMap.put(PlayerStatus.Celebrating, loadAnimationFromAssetManager("Sprites/Player/Celebrate.png",1,11,0.1f,false,false));
        boundaryMap.put(PlayerStatus.Celebrating, new Rectangle(0,0,32, 60));

        animationMap.put(PlayerStatus.Dying, loadAnimationFromAssetManager("Sprites/Player/Die.png",1,12,0.1f,false,false));
        boundaryMap.put(PlayerStatus.Dying, new Rectangle(0,0,32, 60));

        jumpSound = getAssetManager().get("Sounds/PlayerJump.wav", Sound.class);
        killedSound = getAssetManager().get("Sounds/PlayerKilled.wav", Sound.class);



        Texture idle = getAssetManager().get("Sprites/Player/Idle.png", Texture.class);
        int boundWidth = (int) (idle.getWidth()/10 * 0.35);
        int boundLeft = (int)(idle.getWidth()/10 - boundWidth)/2;
        int boundHeight = (int)(idle.getHeight() * 0.7);
        int boundBottom = (int)(idle.getHeight() - boundHeight);
        localBounds = new Rectangle(boundLeft, boundBottom, boundWidth, boundHeight);
        setBoundaryRectangle(boundWidth, boundHeight);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
//        batch.setShader(shaderProgram);
//        shaderProgram.setUniformf("u_imageSize", new Vector2(getWidth(), getHeight()));
//        shaderProgram.setUniformf("u_borderColor", Color.BLACK);
//        shaderProgram.setUniformf("u_borderSize", 3);
        super.draw(batch, parentAlpha);
//        batch.setShader(null);
    }

    public Rectangle getBoundaryRectangle(){
        Rectangle rec = boundaryMap.get(playerStatus);
        rec.x =  getX() + getWidth()/4;
        rec.y =  getY();
        return  rec;
    }


}
