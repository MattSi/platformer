package org.propig.game.platformer;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Gem extends BaseActor{

    Sound collectedSound;
    public final int pointValue = 30;
    private float bounce;

    public Gem(float x, float y, Stage s) {
        super(x, y, s);

        actorType = ActorType.Gem;
        loadAnimationFromAssetManager("Sprites/Gem.png",1,1,0, false, true);
        Texture texture = getAssetManager().get("Sprites/Gem.png", Texture.class);
        int deltax = (Tile.width - texture.getWidth())/2;
        setX(x + deltax);

        setColor(Color.YELLOW);
        collectedSound = getAssetManager().get("Sounds/GemCollected.wav", Sound.class);
    }

    public Circle getBoundingCircle(){
        return new Circle(getX(), getY(), Tile.width / 3.0f);
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        float bounceHeight = 0.005f;
        float bounceRate = 3.0f;
        float bounceSync = -0.75f;

        double t = (elapsedTime) * bounceRate + getX() * bounceSync;
        bounce = (float) Math.sin(t) * bounceHeight * getHeight();
        setY(getY() + bounce);
    }

    public void onCollected(Player player){
        collectedSound.play();
        remove();
    }

    public void dispose(){
        setVisible(false);
        remove();
    }
}
