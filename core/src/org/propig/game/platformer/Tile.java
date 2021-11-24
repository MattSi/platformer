package org.propig.game.platformer;

public class Tile {
    public enum TileCollision{
        Passable,
        Impassable,
        Platform
    }

    public static final int width = 40;
    public static final int height = 32;
    public TileCollision collision;

    public Tile(TileCollision collision){
        this.collision = collision;
    }
}
