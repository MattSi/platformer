package org.propig.game.platformer;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlatformerGame extends BaseGame implements ControllerListener {

    public SpriteBatch batch;
    @Override
    public void create() {
        batch = new SpriteBatch();
        super.create();
        Controllers.clearListeners();
        Controllers.addListener(this);
        if(Controllers.getControllers().size > 0){
            controller = Controllers.getControllers().get(0);
        }
        setActiveScreen(new LevelScreen(this));
    }


    @Override
    public void connected(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void disconnected(Controller controller) {
        this.controller = null;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }
}
