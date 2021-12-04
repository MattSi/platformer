package org.propig.game.platformer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Enemy extends BaseActor {
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> animation;
    public FaceDirection direction = FaceDirection.Left;
    private static final float maxWaitTime = 0.5f;
    private static final float moveSpeed = 64.0f;
    private Rectangle localBounds;
    private float waitTime;
    private BaseActor sensorTest;

    public Rectangle getBoundingRectagle(){

        float left = MathUtils.round(getX() ) + getWidth()/3;
        float bottom =  MathUtils.round(getY());

        return new Rectangle(left, bottom, localBounds.width, localBounds.height);
    }

    public Enemy(float x, float y, String spriteSet, Stage s) {
        super(x, y, s);
        actorType = ActorType.Enemy;
        waitTime=0f;
        loadContext(spriteSet);


        animation=runAnimation;
        setAnimation(runAnimation);


        sensorTest = new BaseActor(0,0,s);
        sensorTest.loadTexture("Sprites/White.png");
        sensorTest.setBoundaryRectangle();
        sensorTest.setVisible(true);
        sensorTest.setSize(localBounds.width, localBounds.height);
        sensorTest.setColor(Color.GREEN);

        addActor(sensorTest);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        Vector2 lantern;

        sensorTest.setPosition(getX() + localBounds.x, getY() );

        if(waitTime > 0){
            waitTime = Math.max(0.0f, waitTime-dt);
        } else {
            if(direction == FaceDirection.Left){
                lantern = new Vector2(getX() + localBounds.width * 0.8f, getY() - Tile.height/2);
            } else {
                lantern = new Vector2(getX() + getWidth() - localBounds.width * 0.8f, getY() - Tile.height/2);
            }

            if(onSolid(lantern)){
                float speed = direction.value * moveSpeed * dt;
                setX(getX() + speed);
                setAnimation(runAnimation);
            } else {
                direction = FaceDirection.getDirection(direction.value * -1);
                setScaleX(direction.value * -1);
                waitTime = maxWaitTime;
                setAnimation(idleAnimation);
            }
        }

    }



    private Tile.TileCollision getCollision(int x, int y){
        if (x < 0 || x >= 20)
            return Tile.TileCollision.Impassable;
        // Allow jumping past the level top and falling through the bottom.
        if (y < 0 || y >= 15)
            return Tile.TileCollision.Passable;

        return tiles[x][y].collision;

    }
    public void loadContext(String spriteSet){

        runAnimation = loadAnimationFromAssetManager("Sprites/"+spriteSet+"/Run.png",1,10,0.1f,true,false);
        idleAnimation = loadAnimationFromAssetManager("Sprites/"+spriteSet+"/Idle.png",1,11,0.15f,true,false);

        Texture idle = getAssetManager().get("Sprites/"+spriteSet+"/Idle.png", Texture.class);
        int boundWidth = (int) (idle.getWidth()/10 * 0.35);
        int boundLeft = (int)(idle.getWidth()/10 - boundWidth)/2;
        int boundHeight = (int)(idle.getHeight() * 0.7);
        int boundBottom = (int)(idle.getHeight() - boundHeight);
        localBounds = new Rectangle(boundLeft, boundBottom, boundWidth, boundHeight);

    }



}
