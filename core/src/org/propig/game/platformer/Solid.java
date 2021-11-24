package org.propig.game.platformer;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Solid extends BaseActor{
    protected String resName;
    public Solid(String resName, float x, float y, Stage s) {
        super(x, y, s);
        actorType = ActorType.Solid;
        this.resName = resName;

        loadAnimationFromAssetManager(resName, 1,1,1,false, true);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
    }
}
