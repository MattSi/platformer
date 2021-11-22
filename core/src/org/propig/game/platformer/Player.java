package org.propig.game.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Player extends BaseActor {
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
    private Vector2 velocity;
    private Vector2 acceleration;

    private float maxSpeed;
    private float elapseTime = 0.0f;
    private float delta = 0.0f;



    public Player( Vector2 position, Stage s) {
        super(position.x, position.y, s);
        actorType = ActorType.Player;
        loadContent();
    }

    public void reset(Vector2 position){
        this.position = position;
        velocity = Vector2.Zero;
        isAlive = true;

    }
    private void loadContent(){
        int frameWidth, frameHeight;
        float frameDuration;
        Texture texture = getAssetManager().get("Sprites/Player/Run.png", Texture.class);

        frameWidth = texture.getHeight();
        frameHeight = texture.getHeight();
        frameDuration = 0.06f;
        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);
        Array<TextureRegion> textureRegionArray = new Array<>();
        for(int c = 0; c< texture.getWidth() / texture.getHeight(); c++){
            textureRegionArray.add(temp[0][c]);
        }
        runAnimation = new Animation(frameDuration, textureRegionArray, Animation.PlayMode.LOOP);

    }

    public void draw(Batch batch, float alphaModulation) {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
        elapseTime += delta;
        if(runAnimation != null){
            TextureRegion frame = runAnimation.getKeyFrame(elapseTime);
            //frame.flip(true,false);
            batch.draw(frame, getX(), getY(), 10,10,64, 64, -1*1.0f, 1.0f, 1);
        }
    }
}
