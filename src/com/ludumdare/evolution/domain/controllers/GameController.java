package com.ludumdare.evolution.domain.controllers;

import com.ludumdare.evolution.domain.entities.Player;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static GameController ourInstance = new GameController();

    private Player currentPlayer;

    private List<Player> otherPlayers;

    public static GameController getInstance() {
        return ourInstance;
    }

    private GameController() {
        otherPlayers = new ArrayList<Player>();
    }

    public void initGame(/*Level level*/) {

    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
