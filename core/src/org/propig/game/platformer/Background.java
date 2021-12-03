package org.propig.game.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.LinkedList;
import java.util.List;

public class Background extends BaseActor{
    static int levelIndex=0;
    public Background(float x, float y, Stage s, int numberOfLevels) {
        super(x, y, s);
        // 下一关
        levelIndex = (levelIndex + 1) % numberOfLevels;
        actorType = ActorType.Background;

        for(int i=0; i<numberOfLevels; i++){
            int segmentIndex = levelIndex;
            BaseActor bg = new BaseActor(0,0,s);
            bg.loadAnimationFromAssetManager("Backgrounds/Layer" + i + "_" + segmentIndex + ".png",1, 1, 1, false, true);
            addActor(bg);
        }
    }

}
