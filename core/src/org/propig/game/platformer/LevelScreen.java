package org.propig.game.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.*;


public class LevelScreen extends BaseScreen{

    Player player;
    Enemy enemy;
    List<Gem> gems;
    int levelIndex;
    int numberOfLevels;
    List<Texture> layers;
    Music bgm;
    private static Tile[][] tiles;
    private static int levelWidth;
    private static int levelHeight;
    private Vector2 exit ;
    private Random random;

    public LevelScreen(PlatformerGame game) {
        super(game);
    }

    @Override
    public void initialize() {
        game.assetManager.finishLoading();
        player = new Player(new Vector2(200, 200), mainStage);
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

    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        for(int i=0; i<layers.size(); i++){
            game.batch.draw(layers.get(i), 0,0);
        }
        //player.draw(batch, 0.5f);
        for(int i = 0; i<levelWidth; i++){
            for(int j=0; j<levelHeight; j++){
                Tile tile = tiles[i][j];
                if(tile.texture == null)
                    continue;
                game.batch.draw(tile.texture, i*Tile.width, j*Tile.height);
            }
        }
        if(enemy !=null)
            enemy.draw(dt, game.batch);
        game.batch.end();
        super.render(dt);
    }

    private void loadNextLevel(){
        // 下一关
        levelIndex = (levelIndex + 1) % numberOfLevels;

        // 首先装入背景
        layers = new LinkedList<>();

        for(int i=0; i<numberOfLevels; i++){
            int segmentIndex = levelIndex;

            layers.add( game.assetManager.get("Backgrounds/Layer" + i + "_" + segmentIndex + ".png", Texture.class));
        }

        loadLevel();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.N){
            loadNextLevel();
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
        exit = new Vector2(-1, -1);
        enemy = null;
        for(Gem item: gems){
            item.dispose();
        }
        gems.clear();
        for(int j = 0; j<wordArray.length; j++){
            for(int i=0; i<wordArray[j].length(); i++){
                String word = wordArray[j];
                char tileType = word.charAt(i);
                tiles[i][Math.abs(j+1-levelHeight)] = loadTile(tileType, i, Math.abs(j-levelHeight));
            }
        }
    }


    public static Tile.TileCollision getCollision(int x, int y){
        if(x < 0 || x> levelWidth){
            return Tile.TileCollision.Impassable;
        }
        if(y<0 || y>levelHeight){
            return Tile.TileCollision.Passable;
        }

        return tiles[x][y].collision;
    }

    private Tile loadEnemyTile(int x, int y, String spriteSet){
        Vector2 start = Utils.getBottomCenter(getBounds(x, y));
        enemy = new Enemy(start.x, start.y, spriteSet, mainStage);
        return new Tile(null, Tile.TileCollision.Passable);
    }

    private Tile loadExitTile(int x, int y){
        Rectangle bound = getBounds(x, y);
        exit = bound.getCenter(new Vector2(bound.x, bound.y));
        return loadTile("Exit", Tile.TileCollision.Passable);
    }

    private Tile loadStartTile(int x, int y){
        Vector2 start = Utils.getBottomCenter(getBounds(x, y));
        player.setPosition(start.x, start.y);
        return  new Tile(null, Tile.TileCollision.Passable);
    }

    private Tile loadVarietyTile(String baseName, int variationCount, Tile.TileCollision collision){
        int index = random.nextInt(variationCount);
        return loadTile(baseName+index, collision);
    }
    private Tile loadTile(String name, Tile.TileCollision collision){
        Texture texture = game.assetManager.get("Tiles/"+name+".png", Texture.class);
        return new Tile(texture,collision);
    }

    public Rectangle getBounds(int x, int y){
        return new Rectangle(x * Tile.width, y*Tile.height, Tile.width, Tile.height);
    }

    public Tile loadGemTile(int x, int y){
        Rectangle bound = getBounds(x, y);
        gems.add(new Gem(bound.x, bound.y-Tile.height, mainStage));
        return new Tile(null, Tile.TileCollision.Passable);
    }

    private Tile loadTile(char tileType, int x, int y){
        switch (tileType){
            // Blank space
            case '.':
                return new Tile(null, Tile.TileCollision.Passable);

            // Exit
            case 'X':
                return loadExitTile(x, y);

            // Gem
            case 'G':
                return loadGemTile(x, y);
                //return new Tile(null, Tile.TileCollision.Passable);


            case '-':
                return loadTile("Platform", Tile.TileCollision.Platform);


            case 'A':
                return loadEnemyTile(x, y, "MonsterA");
            case 'B':
                return loadEnemyTile(x, y, "MonsterB");
            case 'C':
                return loadEnemyTile(x, y, "MonsterC");
            case 'D':
                return loadEnemyTile(x, y, "MonsterD");

            case '~':
                return loadVarietyTile("BlockB", 2, Tile.TileCollision.Platform);
            case ':':
                return loadVarietyTile("BlockB", 2, Tile.TileCollision.Passable);

            case '1':
                return loadStartTile(x, y);

            case '#':
                return loadVarietyTile("BlockA", 7, Tile.TileCollision.Impassable);

            default:
                throw new RuntimeException("Unsupported tile type character");
        }

    }
}
