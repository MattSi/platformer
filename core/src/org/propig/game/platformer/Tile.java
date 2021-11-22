package org.propig.game.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Tile {

    public Texture texture;
    public TileCollision collision;

    public final static int width = 40;
    public final static int height = 32;

    public static final Vector2 size = new Vector2(width, height);

    public Tile(Texture texture, TileCollision collision){
        this.texture = texture;
        this.collision = collision;
    }


    public enum TileCollision {

        Passable,
        Impassable,
        Platform
    }
}
