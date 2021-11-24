package org.propig.game.platformer;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class ExitSign extends BaseActor{
    public ExitSign(float x, float y, Stage s) {
        super(x, y, s);

        loadAnimationFromAssetManager("Tiles/Exit.png", 1,1,1, false, true);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
    }
}
