package org.propig.game.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LevelScreen extends BaseScreen implements ControllerListener {

    Player player;
    Dialog dialog;
    Enemy enemy;
    List<Gem> gems;
    int levelIndex;
    int numberOfLevels;
    Music bgm;
    Sound sndCollect;
    Sound sndDie;
    ExitSign exitSign;
    Label timeLabel;
    Label scoreLabel;
    private Tile[][] tiles;
    private static int levelWidth;
    private static int levelHeight;
    private Random random;
    private float timeRemaining;
    private float warningSeconds;
    private int score;

    private float playerX, playerY;

    public LevelScreen(PlatformerGame game) {
        super(game);
        Controllers.clearListeners();
        Controllers.addListener(this);
    }

    @Override
    public void initialize() {
        warningSeconds=10;
        score = 0;
        game.assetManager.finishLoading();
        gems = new ArrayList<>();
        levelIndex = -1;
        numberOfLevels = 3;
        random = new Random(354668);
        loadNextLevel();
        bgm = game.assetManager.get("Sounds/Music.mp3", Music.class);
        bgm.setLooping(true);
        bgm.setVolume(0.8f);
        bgm.play();

        sndCollect = game.assetManager.get("Sounds/GemCollected.wav", Sound.class);
        sndDie = game.assetManager.get("Sounds/PlayerKilled.wav", Sound.class);

        timeLabel = new Label(" ", BaseGame.labelStyle);
        timeLabel.setColor(Color.YELLOW);
        scoreLabel = new Label("SCORE: 0", BaseGame.labelStyle);
        scoreLabel.setColor(Color.YELLOW);

        uiTable.left().top();
        uiTable.add(timeLabel).padLeft(10).width(50);
        uiTable.row();
        uiTable.add(scoreLabel).padLeft(10).width(50);

    }

    @Override
    public void update(float dt) {
        timeLabel.setText("TIME: " + MathUtils.round(timeRemaining));
        scoreLabel.setText("SCORE: " + score);

        Rectangle r = new Rectangle(0, 0, 0, 0);
        for (BaseActor a : BaseActor.getList(mainStage, ActorType.Solid)) {
            r.set(a.getX(), a.getY(), a.getWidth(), a.getHeight());
            if (r.contains(player.lanternBottom) && player.velocityVec.y <= 0f) {
                player.velocityVec.y = 0;
                player.setY(a.getY() + a.getHeight());
            }

            if (r.contains((player.lanternTop))) {
                player.velocityVec.y = 0;
                player.setY(a.getY() - player.getHeight());
            }

            if(r.contains(player.lanternFront)){
                if(player.direction == FaceDirection.Left || player.direction == FaceDirection.Front){
                    player.setX(a.getX() + player.getWidth()/5);
                } else {
                    player.setX(a.getX() - player.getWidth() * 0.7f);
                }
                player.velocityVec.x = 0;
            }
        }

        for (BaseActor a : BaseActor.getList(mainStage, ActorType.Platform)) {
            r.set(a.getX(), a.getY(), a.getWidth(), a.getHeight());
            if (r.contains(player.lanternBottom) && player.velocityVec.y <= 0f) {
                player.velocityVec.y = 0;
                player.setY(a.getY() + a.getHeight());
            }

//            if(r.contains(player.lanternFront)){
//                player.velocityVec.x = 0;
//            }
        }

        for(BaseActor a : BaseActor.getList(mainStage, ActorType.Gem)){
            Gem gem = (Gem)a;
            Rectangle rec = player.getBoundaryRectangle();
            if(Intersector.overlaps(gem.getBoundingCircle(), rec)){
                score += 30;
                gem.remove();
                sndCollect.play();
            }
        }


        for(BaseActor a : BaseActor.getList(mainStage, ActorType.Enemy)){
            Enemy enemyItem = (Enemy) a;
            if(player.isAlive &&
                    Intersector.overlaps(enemyItem.getBoundingRectagle(), player.getBoundaryRectangle())){
                player.killed();
                dialog.setVisible(true);
                dialog.setGameStatus(Dialog.GameStatus.PlayerDied);
            }
        }

        r.set(exitSign.getX() + 5, exitSign.getY(), 32, 32);
        if(player.isAlive &&
                Intersector.overlaps(r, player.getBoundaryRectangle())){
            player.succeed();
            dialog.setVisible(true);
            dialog.setGameStatus(Dialog.GameStatus.PlayerSuccess);
        }

        timeRemaining -= dt;
        if(timeRemaining <= 0f){
            timeRemaining = 0;
            dialog.setVisible(true);
            dialog.setGameStatus(Dialog.GameStatus.PlayerLose);
            player.lose();
        }

        if(timeRemaining < warningSeconds ) {
            if (MathUtils.round(timeRemaining) % 2 == 0){
                timeLabel.setColor(Color.RED);
            } else {
                timeLabel.setColor(Color.YELLOW);
            }
        }

    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }



    private void loadCurrentLevel(){
        levelIndex = (levelIndex - 1)%numberOfLevels;
        loadNextLevel();
    }

    private void loadNextLevel(){
        // 下一关
        levelIndex = (levelIndex + 1) % numberOfLevels;
        loadLevel();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.N){
            loadNextLevel();
        }
        if(keycode == Input.Keys.M) {
            float scalex = enemy.getScaleX();
            enemy.direction = FaceDirection.getDirection(enemy.direction.value * -1);
            enemy.setScaleX(scalex*-1);
        }
        if(keycode == Input.Keys.SPACE && player.velocityVec.y == 0){
            if(player.onSolid(player.lanternBottom) ){
                player.jump();
            }
        }
        return false;
    }

    public void loadLevel() {
        mainStage.clear();
        timeRemaining = 60;
        warningSeconds = 10;
        FileHandle handle = Gdx.files.internal("Levels/" + levelIndex + ".txt");
        String data = handle.readString();
        String[] wordArray = data.split("\\r?\\n");
        levelHeight = wordArray.length;
        levelWidth = wordArray[0].length();
        tiles = new Tile[levelWidth][levelHeight];
        BaseActor.tiles = tiles;
        BaseActor.setWorldBounds(levelWidth * Tile.width, levelHeight * Tile.height);
        new Background(0, 0, mainStage, 3);
        player = new Player(new Vector2(playerX, playerY), null);
        dialog = new Dialog(0,0,null);
        enemy = null;
        for (Gem item : gems) {
            item.dispose();
        }
        gems.clear();
        for (int j = 0; j < wordArray.length; j++) {
            for (int i = 0; i < wordArray[j].length(); i++) {
                String word = wordArray[j];
                char tileType = word.charAt(i);
                tiles[i][levelHeight - j - 1] = loadTile(tileType, i, levelHeight - j - 1);
            }
        }

        if(enemy != null) {
            mainStage.addActor(enemy);
        }
        mainStage.addActor(player);
        mainStage.addActor(dialog);
    }

    private Tile loadEnemyTile(int x, int y, String spriteSet){
        Vector2 start = Utils.getBottomCenter(getBounds(x, y));
        enemy = new Enemy(start.x, start.y, spriteSet, null);
        return new Tile( Tile.TileCollision.Passable);
    }

    private Tile loadExitTile(int x, int y){
        Rectangle bound = getBounds(x, y);
        exitSign = new ExitSign(bound.x, bound.y, mainStage);
        return new Tile( Tile.TileCollision.Passable);
    }

    private Tile loadStartTile(int x, int y){
        Vector2 start = Utils.getBottomCenter(getBounds(x, y));
        player.setPosition(start.x, start.y);
        return  new Tile(Tile.TileCollision.Passable);
    }

    private Tile loadVarietyTile(String baseName, int variationCount, Tile.TileCollision collision, int x, int y){
        int index = random.nextInt(variationCount);
        Rectangle rec = getBounds(x, y);
        new Solid("Tiles/"+baseName+index+".png", rec.x, rec.y, mainStage);
        return new Tile(collision);
    }

    private Tile loadPlatformTile(String name, Tile.TileCollision collision, int x, int y){
        Rectangle rec = getBounds(x, y);
        new Platfrom("Tiles/"+name+".png", rec.x, rec.y, mainStage);
        return new Tile(collision);
    }

    public Rectangle getBounds(int x, int y){
        return new Rectangle(x * Tile.width, y*Tile.height, Tile.width, Tile.height);
    }

    public Tile loadGemTile(int x, int y){
        Rectangle rec = getBounds(x, y);
        gems.add(new Gem(rec.x , rec.y , mainStage));
        return new Tile( Tile.TileCollision.Passable);
    }

    private Tile loadTile(char tileType, int x, int y){
        switch (tileType){
            // Blank space
            case '.':
                return new Tile(Tile.TileCollision.Passable);
            // Exit
            case 'X':
                return loadExitTile(x, y);
            // Gem
            case 'G':
                return loadGemTile(x, y);
            case '-':
                return loadPlatformTile("Platform", Tile.TileCollision.Platform, x, y);
            case 'A':
                return loadEnemyTile(x, y, "MonsterA");
            case 'B':
                return loadEnemyTile(x, y, "MonsterB");
            case 'C':
                return loadEnemyTile(x, y, "MonsterC");
            case 'D':
                return loadEnemyTile(x, y, "MonsterD");

            case '~':
                return loadVarietyTile("BlockB", 2, Tile.TileCollision.Platform, x, y);
            case ':':
                return loadVarietyTile("BlockB", 2, Tile.TileCollision.Passable, x, y);

            case '1':
                return loadStartTile(x, y);

            case '#':
                return loadVarietyTile("BlockA", 7, Tile.TileCollision.Impassable, x, y);

            default:
                throw new RuntimeException("Unsupported tile type character");
        }
    }


    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }


    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if(controller.getButton(controller.getMapping().buttonA)){
            if(!player.isAlive){
                if(dialog.gameStatus == Dialog.GameStatus.PlayerLose || dialog.gameStatus == Dialog.GameStatus.PlayerDied){
                    loadCurrentLevel();
                } else {
                    loadNextLevel();
                }
            } else {
                if(player.onSolid(player.lanternBottom) && player.velocityVec.y==0f ){
                    player.jump();
                }
            }
        }
        return false;
    }
}
