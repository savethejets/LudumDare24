package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;
import com.ludumdare.evolution.domain.screens.GameScreen;

public class Goal extends Key {
    private String nextLevel;

    public Goal(MobiGenetics mobiGenetics, World world) {
        super(mobiGenetics, world);
    }

    public Goal(World world) {
        super(world);
    }

    @Override
    protected Color getFilledInColour() {
        return Color.GREEN;
    }

    @Override
    protected void doOnUnlock(World world, GameScreen gameScreen) {

        System.out.println("WIN!");

        gameScreen.goToNextLevel(nextLevel);

    }

    public void setNextLevelString(String theKey) {
        this.nextLevel = theKey;
    }
}
