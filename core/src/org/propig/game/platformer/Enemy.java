package org.propig.game.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Enemy extends BaseActor {
    public enum FaceDirection{
        Left(-1),
        Right(1);

        private int value;
        FaceDirection(int value){
            this.value = value;
        }
        public static FaceDirection getDirection(int value){
            if(value == -1){
                return FaceDirection.Left;
            } else {
                return FaceDirection.Right;
            }

        }

    }

    private Rectangle localBounds;
    private float elapseTime;
    private float waitTime;

    public Rectangle getBoundingRectagle(){

        int left = (int) (MathUtils.round(getX() - getOriginX()) + localBounds.x);
        int top =  (int) (MathUtils.round(getY() - getOriginY()) + localBounds.y);

        return new Rectangle(left, top, localBounds.width, localBounds.height);
    }

    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> animation;
    private FaceDirection direction = FaceDirection.Left;
    private static final float maxWaitTime = 0.5f;
    private static final float moveSpeed = 6f;


    public Enemy(float x, float y, String spriteSet, Stage s) {
        super(x, y, s);
        this.elapseTime = 0f;
        actorType = ActorType.Enemy;
        waitTime=0f;
        loadContext(spriteSet);
    }



    @Override
    public void act(float dt) {
        super.act(dt);

        float posX = getX() + localBounds.width/2 * direction.value;
        int tileX = (int) MathUtils.floor(posX / Tile.width) - direction.value;
        int tileY = (int) MathUtils.floor(getY() / Tile.height);

        if(waitTime > 0){
            waitTime = Math.max(0.0f, waitTime - elapseTime);
            if(waitTime <= 0.0f){
                direction = FaceDirection.getDirection(direction.value * (-1));
            }
        } else {

            if( LevelScreen.getCollision(tileX + direction.value, tileY - 1) == Tile.TileCollision.Impassable ||
                    LevelScreen.getCollision(tileX + direction.value, tileY) == Tile.TileCollision.Passable){
                waitTime = maxWaitTime;
            } else {
                float speed = direction.value * moveSpeed * dt;
                setX(getX() + speed);
            }
        }
    }

    public void draw(float dt, SpriteBatch batch){

        elapseTime += dt;
//        if(waitTime > 0)
//            animation = idleAnimation;
//        else
//            animation = runAnimation;
        animation = runAnimation;
        TextureRegion frame = animation.getKeyFrame(elapseTime);
        batch.draw(frame, getX(), getY(), getOriginX(),getOriginY(),64, 64,  1.0f, 1.0f, 1);
    }

    public void loadContext(String spriteSet){
        int frameWidth, frameHeight;
        float frameDuration;
        Texture texture;
        TextureRegion[][] temp;
        Array<TextureRegion> textureRegionArray;
        spriteSet = "Sprites/"+spriteSet+"/";


        texture = getAssetManager().get(spriteSet +"Run.png", Texture.class);
        frameWidth = texture.getHeight();
        frameHeight = texture.getHeight();
        frameDuration = 0.1f;
        temp = TextureRegion.split(texture, frameWidth, frameHeight);
        textureRegionArray = new Array<>();
        for(int c = 0; c< texture.getWidth() / texture.getHeight(); c++){
            textureRegionArray.add(temp[0][c]);
        }
        runAnimation = new Animation(frameDuration, textureRegionArray, Animation.PlayMode.LOOP);


        texture = getAssetManager().get(spriteSet +"Idle.png", Texture.class);
        frameWidth = texture.getHeight();
        frameHeight = texture.getHeight();
        frameDuration = 0.15f;
        temp = TextureRegion.split(texture, frameWidth, frameHeight);
        textureRegionArray = new Array<>();
        for(int c = 0; c< texture.getWidth() / texture.getHeight(); c++){
            textureRegionArray.add(temp[0][c]);
        }
        idleAnimation = new Animation(frameDuration, textureRegionArray, Animation.PlayMode.LOOP);

        int width = (int)(frameWidth * 0.35f);
        int height = (int)(frameHeight * 0.7f);
        int left = (frameWidth - width)/2;
        int top = frameHeight - height;

        localBounds = new Rectangle(left, top, width, height);

    }

}
