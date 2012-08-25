package com.ludumdare.evolution.domain.controllers;

import com.ludumdare.evolution.domain.entities.Mobi;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static GameController ourInstance = new GameController();

    private Mobi currentMobi;

    private List<Mobi> otherMobis;

    public static GameController getInstance() {
        return ourInstance;
    }

    private GameController() {
        otherMobis = new ArrayList<Mobi>();
    }

    public void initGame(/*Level level*/) {

    }

    public Mobi getCurrentMobi() {
        return currentMobi;
    }

    public void setCurrentMobi(Mobi currentMobi) {
        this.currentMobi = currentMobi;
    }
}
