package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ludumdare.evolution.app.Constants;

public class Door extends Actor {

    private Body body;
    private Color colour;
    private ShapeRenderer renderer = new ShapeRenderer();
    private int width;
    private int height;

    public Door(Body body, int width, int height) {
        this.body = body;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.setColor(colour);
        renderer.filledRect(
                Constants.convertFromBox2d(body.getPosition().x) - width / 2,
                Constants.convertFromBox2d(body.getPosition().y) - height / 2,
                width,
                height);
        renderer.end();
    }

    @Override
    public Actor hit(float x, float y) {
        return null;
    }

    public void destroy(World world) {
        world.destroyBody(body);
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }
}
