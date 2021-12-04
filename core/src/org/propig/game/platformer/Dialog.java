package org.propig.game.platformer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;

public class Dialog extends BaseActor{
    public enum GameStatus{
        PlayerDied,
        PlayerLose,
        PlayerSuccess
    }


    GameStatus gameStatus;
    HashMap<GameStatus, Animation<TextureRegion>> animationHashMap;
    public Dialog(float x, float y, Stage s) {
        super(x, y, s);

        animationHashMap = new HashMap<>();
        animationHashMap.put(GameStatus.PlayerDied,     loadAnimationFromAssetManager("Overlays/you_died.png",1,1,1f,false,false));
        animationHashMap.put(GameStatus.PlayerLose,     loadAnimationFromAssetManager("Overlays/you_lose.png",1,1,1f,false,false));
        animationHashMap.put(GameStatus.PlayerSuccess,  loadAnimationFromAssetManager("Overlays/you_win.png",1,1,1f,false,false));
        setVisible(false);
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        setAnimation(animationHashMap.get(gameStatus));
    }


}
