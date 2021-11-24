package org.propig.game.platformer;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

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
        runAnimation = loadAnimationFromAssetManager("Sprites/Player/Run.png",1,10,0.06f,true,false);
        idleAnimation = loadAnimationFromAssetManager("Sprites/Player/Idle.png",1,10,0.15f,true,false);
        jumpAnimation = loadAnimationFromAssetManager("Sprites/Player/Jump.png",1,10,0.1f,false,false);
        celebrateAnimation = loadAnimationFromAssetManager("Sprites/Player/Celebrate.png",1,10,0.1f,false,false);
        dieAnimation = loadAnimationFromAssetManager("Sprites/Player/Die.png",1,10,0.1f,false,false);


    }


}
