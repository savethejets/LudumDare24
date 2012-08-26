package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.physics.box2d.World;
import com.ludumdare.evolution.domain.screens.GameScreen;

public class Goal extends Key {
    public Goal(MobiGenetics mobiGenetics, World world) {
        super(mobiGenetics, world);
    }

    public Goal(World world) {
        super(world);
    }

    @Override
    protected void doOnUnlock(World world, GameScreen gameScreen) {

        System.out.println("WIN!");

    }
}
