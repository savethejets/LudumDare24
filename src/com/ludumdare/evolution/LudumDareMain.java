package com.ludumdare.evolution;

import com.badlogic.gdx.Game;
import com.ludumdare.evolution.domain.controllers.GameController;
import com.ludumdare.evolution.domain.screens.SplashScreen;

public class LudumDareMain extends Game {
    @Override
    public void create() {
        GameController.getInstance().initMusic();
        setScreen(new SplashScreen(this));
    }
}
