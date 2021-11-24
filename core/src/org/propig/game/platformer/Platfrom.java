package org.propig.game.platformer;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Platfrom extends Solid{
    public Platfrom(String resName, float x, float y, Stage s) {
        super(resName, x, y, s);
        actorType=ActorType.Platform;
    }

    @Override
    public void act(float dt) {
        super.act(dt);
    }
}
