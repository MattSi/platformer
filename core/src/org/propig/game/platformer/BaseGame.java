package org.propig.game.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public abstract class BaseGame extends Game {

    /**
     * Stores reference to game; used when calling setActiveScreen method
     */
    private static BaseGame game;
    public static TextButton.TextButtonStyle textButtonStyle;
    public static Label.LabelStyle labelStyle;
    public static Label.LabelStyle debugLabelStyle;
    protected AssetManager assetManager;
    protected Controller controller;


    /**
     * Called when game is initialized
     */
    public BaseGame(){
        game = this;
        assetManager = new AssetManager();
    }

    @Override
    public void create() {
        // prepare for multiple classes/stages/actors to receive discrete input

        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);

        loadAsset(assetManager);
//
//        // parameters for generating a custom bitmap font
//        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("spacewar/OpenSans.ttf"));
//        //fontGenerator = new FreeTypeFontGenerator()
//        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter =
//                new FreeTypeFontGenerator.FreeTypeFontParameter();
//        fontParameter.size = 12;
//        fontParameter.color = Color.WHITE;
//
//        // TODO: fix font and Style;
//        BitmapFont custFont = new BitmapFont(Gdx.files.internal("spacewar/spacewar.fnt"),
//                Gdx.files.internal("spacewar/spacewar.png"),false);
//
//
//        Texture buttonTex = new Texture(Gdx.files.internal("spacewar/button.png"));
//        NinePatch buttonPatch =new NinePatch(buttonTex, 24, 24, 24, 24);
//
//        textButtonStyle = new TextButton.TextButtonStyle();
//        textButtonStyle.up = new NinePatchDrawable(buttonPatch);
//        textButtonStyle.font = custFont;
//
//        labelStyle = new Label.LabelStyle();
//        labelStyle.font = custFont;
//
//        debugLabelStyle = new Label.LabelStyle();
//        debugLabelStyle.font = fontGenerator.generateFont(fontParameter);


    }

    public static void setActiveScreen(BaseScreen s)
    {
        game.setScreen(s);
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

        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));


        FreeTypeFontLoaderParameter stdFont = new FreeTypeFontLoaderParameter();
        stdFont.fontFileName="Fonts/arialbd.ttf";
        stdFont.fontParameters.size=14;
        stdFont.fontParameters.spaceX=0;
        stdFont.fontParameters.spaceY=0;
        stdFont.fontParameters.kerning=true;
        assetManager.load("Fonts/arialbd.ttf",BitmapFont.class, stdFont);

        assetManager.finishLoading();
    }


}
