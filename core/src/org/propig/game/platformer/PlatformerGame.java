package org.propig.game.platformer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlatformerGame extends BaseGame{

    public SpriteBatch batch;
    @Override
    public void create() {
        batch = new SpriteBatch();
        super.create();
        setActiveScreen(new LevelScreen(this));
    }



}
