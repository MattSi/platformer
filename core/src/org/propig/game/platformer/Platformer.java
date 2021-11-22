package org.propig.game.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.LinkedList;
import java.util.List;

public class Platformer extends ApplicationAdapter {
	Platformer instance;


	private AssetManager assetManager;
	SpriteBatch batch;
	Texture img;
	Texture winOverlay;
	Texture loseOverlay;
	Texture diedOverlay;
	List<Texture> layers;
	Music bgm;
	Player player;

	public Platformer() {
		assetManager = new AssetManager();
		instance = this;
	}


	@Override
	public void create () {
		loadAsset(assetManager);
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		winOverlay  = new Texture("Overlays/you_win.png");
		loseOverlay = new Texture("Overlays/you_lose.png");
		diedOverlay = new Texture("Overlays/you_died.png");

		layers = new LinkedList<>();
		layers.add(new Texture("Backgrounds/Layer0_0.png"));
		layers.add(new Texture("Backgrounds/Layer1_1.png"));
		layers.add(new Texture("Backgrounds/Layer2_2.png"));

		bgm = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Music.mp3"));
		bgm.setLooping(true);
		bgm.setVolume(0.8f);
		bgm.play();
		//player = new Player(null, new Vector2(100,100) );


	}


	private void loadAsset(AssetManager assetManager){
		assetManager.load("Backgrounds/Layer0_0.png", Texture.class);
		assetManager.load("Backgrounds/Layer0_1.png", Texture.class);
		assetManager.load("Backgrounds/Layer0_2.png", Texture.class);
		assetManager.load("Backgrounds/Layer1_0.png", Texture.class);
		assetManager.load("Backgrounds/Layer1_1.png", Texture.class);
		assetManager.load("Backgrounds/Layer1_2.png", Texture.class);
		assetManager.load("Backgrounds/Layer2_0.png", Texture.class);
		assetManager.load("Backgrounds/Layer2_1.png", Texture.class);
		assetManager.load("Backgrounds/Layer2_2.png", Texture.class);

		assetManager.load("Overlays/you_died.png", Texture.class);
		assetManager.load("Overlays/you_lose.png", Texture.class);
		assetManager.load("Overlays/you_win.png", Texture.class);

		assetManager.load("Sounds/ExitReached.wav", Sound.class);
		assetManager.load("Sounds/GemCollected.wav", Sound.class);
		assetManager.load("Sounds/MonsterKilled.wav", Sound.class);
		assetManager.load("Sounds/PlayerFall.wav", Sound.class);
		assetManager.load("Sounds/PlayerJump.wav", Sound.class);
		assetManager.load("Sounds/PlayerKilled.wav", Sound.class);
		assetManager.load("Sounds/Powerup.wav", Sound.class);
		assetManager.load("Sounds/Music.mp3", Music.class);

		assetManager.load("Sprites/MonsterA/Idle.png", Texture.class);
		assetManager.load("Sprites/MonsterA/Run.png", Texture.class);
		assetManager.load("Sprites/MonsterB/Idle.png", Texture.class);
		assetManager.load("Sprites/MonsterB/Run.png", Texture.class);
		assetManager.load("Sprites/MonsterC/Idle.png", Texture.class);
		assetManager.load("Sprites/MonsterC/Run.png", Texture.class);
		assetManager.load("Sprites/MonsterD/Idle.png", Texture.class);
		assetManager.load("Sprites/MonsterD/Run.png", Texture.class);

		assetManager.load("Sprites/Player/Celebrate.png", Texture.class);
		assetManager.load("Sprites/Player/Die.png", Texture.class);
		assetManager.load("Sprites/Player/Idle.png", Texture.class);
		assetManager.load("Sprites/Player/Jump.png", Texture.class);
		assetManager.load("Sprites/Player/Run.png", Texture.class);

		assetManager.load("Sprites/Gem.png", Texture.class);
		assetManager.load("Sprites/VirtualControlArrow.png", Texture.class);

		assetManager.load("Tiles/BlockA0.png", Texture.class);
		assetManager.load("Tiles/BlockA1.png", Texture.class);
		assetManager.load("Tiles/BlockA2.png", Texture.class);
		assetManager.load("Tiles/BlockA3.png", Texture.class);
		assetManager.load("Tiles/BlockA4.png", Texture.class);
		assetManager.load("Tiles/BlockA5.png", Texture.class);
		assetManager.load("Tiles/BlockA6.png", Texture.class);
		assetManager.load("Tiles/BlockB0.png", Texture.class);
		assetManager.load("Tiles/BlockB1.png", Texture.class);
		assetManager.load("Tiles/Exit.png", Texture.class);
		assetManager.load("Tiles/Platform.png", Texture.class);

		assetManager.finishLoading();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		for(int i=0; i<layers.size(); i++){
			batch.draw(layers.get(i), 0,0);
		}
		player.draw(batch, 0.5f);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}


}
