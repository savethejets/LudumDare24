package com.ludumdare.evolution;

import com.badlogic.gdx.Game;
import com.ludumdare.evolution.domain.screens.GameScreen;

public class LudumDareMain extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
