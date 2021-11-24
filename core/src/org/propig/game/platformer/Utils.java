package org.propig.game.platformer;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Utils {
    public static Vector2 getBottomCenter(Rectangle rect){
        return new Vector2(rect.x + rect.width/2.0f, rect.y);
    }


}
