package com.ludumdare.evolution.domain.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ludumdare.evolution.LudumDareMain;
import com.ludumdare.evolution.app.Constants;

public abstract class AbstractScreen implements Screen {

    protected final LudumDareMain game;
    protected final BitmapFont font;
    protected final SpriteBatch batch;
    protected final Stage stage;

    public AbstractScreen(LudumDareMain game) {
        this.game = game;
        this.font = new BitmapFont();
        this.batch = new SpriteBatch();
        this.stage = new Stage(0, 0, true);
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    // Screen implementation

    @Override
    public void show() {
        Gdx.app.log(Constants.LOG, "Showing screen: " + getName());
    }

    @Override
    public void resize(int width,int height) {
        Gdx.app.log(Constants.LOG, "Resizing screen: " + getName() + " to: " + width + " x " + height);

        // resize the stage
        stage.setViewport(width, height, true);
    }

    @Override
    public void render(float delta) {
        // the following code clears the screen with the given RGB color (black)
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update and draw the stage actors
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        Gdx.app.log(Constants.LOG, "Hiding screen: " + getName());
    }

    @Override
    public void pause() {
        Gdx.app.log(Constants.LOG, "Pausing screen: " + getName());
    }

    @Override
    public void resume() {
        Gdx.app.log(Constants.LOG, "Resuming screen: " + getName());
    }

    @Override
    public void dispose() {
        Gdx.app.log(Constants.LOG, "Disposing screen: " + getName());

        // dispose the collaborators
        stage.dispose();
        batch.dispose();
        font.dispose();

    }
}