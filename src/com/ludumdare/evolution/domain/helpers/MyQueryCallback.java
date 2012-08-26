package com.ludumdare.evolution.domain.helpers;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.ludumdare.evolution.domain.entities.Mobi;

import java.util.ArrayList;
import java.util.List;

public class MyQueryCallback implements QueryCallback{

    List<Fixture> fixtures = new ArrayList<Fixture>();

    @Override
    public boolean reportFixture(Fixture fixture) {
        if (fixture.getBody().getUserData() instanceof Mobi) {
            boolean containsMobi = false;

            for (Fixture fixture1 : fixtures) {
                if (fixture1.getBody().getUserData().equals(fixture.getBody().getUserData())) {
                    containsMobi = true;
                }
            }

            if (!containsMobi) {
                fixtures.add(fixture);
            }
        }
        return true;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }
}
