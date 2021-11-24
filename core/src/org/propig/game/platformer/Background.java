package org.propig.game.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.LinkedList;
import java.util.List;

public class Background extends BaseActor{
    List<Texture> layers;
    static int levelIndex=0;
    public Background(float x, float y, Stage s, int numberOfLevels) {
        super(x, y, s);
        // 下一关
        levelIndex = (levelIndex + 1) % numberOfLevels;
        layers = new LinkedList<>();
        actorType = ActorType.Background;

        for(int i=0; i<numberOfLevels; i++){
            int segmentIndex = levelIndex;
            layers.add( getAssetManager().get("Backgrounds/Layer" + i + "_" + segmentIndex + ".png", Texture.class));
        }
    }


    @Override
    public void act(float dt) {
        super.act(dt);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for(int i=0; i<layers.size(); i++) {
            batch.draw(layers.get(i), 0, 0);
        }
        super.draw(batch, parentAlpha);
    }
}
