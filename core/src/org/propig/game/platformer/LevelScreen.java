package org.propig.game.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LevelScreen extends BaseScreen{

    Player player;
    Enemy enemy;
    List<Gem> gems;
    int levelIndex;
    int numberOfLevels;
    Music bgm;
    private Tile[][] tiles;
    private static int levelWidth;
    private static int levelHeight;
    private Random random;

    public LevelScreen(PlatformerGame game) {
        super(game);
    }

    @Override
    public void initialize() {
        game.assetManager.finishLoading();
        gems = new ArrayList<>();
        levelIndex = -1;
        numberOfLevels = 3;
        random = new Random(354668);
        loadNextLevel();
        bgm = game.assetManager.get("Sounds/Music.mp3", Music.class);
        bgm.setLooping(true);
        bgm.setVolume(0.8f);
       // bgm.play();

    }

    @Override
    public void update(float dt) {
        Rectangle r = new Rectangle(0,0,0,0);
        for(BaseActor a : BaseActor.getList(mainStage, ActorType.Solid)){
            r.set(a.getX(), a.getY(), a.getWidth(), a.getHeight());
            if(r.contains(player.lanternBottom) && player.velocityVec.y<=0f){
                player.velocityVec.y=0;
                player.setY(a.getY()+a.getHeight());
            }

            if(r.contains((player.lanternTop))){
                  player.velocityVec.y = 0;
            }
        }

        for(BaseActor a : BaseActor.getList(mainStage, ActorType.Platform)){
            r.set(a.getX(), a.getY(), a.getWidth(), a.getHeight());
            if(r.contains(player.lanternBottom) && player.velocityVec.y <=0f){
                player.velocityVec.y=0;
                player.setY(a.getY()+a.getHeight());
            }
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
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

    public void loadLevel(){
        FileHandle handle = Gdx.files.internal("Levels/"+ levelIndex+".txt");
        String data = handle.readString();
        String[] wordArray = data.split("\\r?\\n");
        levelHeight = wordArray.length;
        levelWidth = wordArray[0].length();
        tiles = new Tile[levelWidth][levelHeight];
        BaseActor.tiles = tiles;
        BaseActor.setWorldBounds(levelWidth*Tile.width, levelHeight*Tile.height);
        new Background(0,0,mainStage,3);
        player = new Player(new Vector2(200, 200), mainStage);
        enemy = null;
        for(Gem item: gems){
            item.dispose();
        }
        gems.clear();
        for(int j = 0; j<wordArray.length; j++){
            for(int i=0; i<wordArray[j].length(); i++){
                String word = wordArray[j];
                char tileType = word.charAt(i);
                tiles[i][levelHeight-j-1] = loadTile(tileType, i, levelHeight-j-1);
            }
        }
    }

    private Tile loadEnemyTile(int x, int y, String spriteSet){
        Vector2 start = Utils.getBottomCenter(getBounds(x, y));
        enemy = new Enemy(start.x, start.y, spriteSet, mainStage);
        return new Tile( Tile.TileCollision.Passable);
    }

    private Tile loadExitTile(int x, int y){
        Rectangle bound = getBounds(x, y);
        new ExitSign(bound.x, bound.y, mainStage);
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
}
