package com.ludumdare.evolution.domain.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.actions.Delay;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.ludumdare.evolution.LudumDareMain;
import com.ludumdare.evolution.domain.controllers.GameController;
import com.ludumdare.evolution.domain.scene2d.AbstractScreen;

public class SplashScreen extends AbstractScreen {

    private Texture codeHerdLabel;

    public SplashScreen(LudumDareMain game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        // load the texture with the splash image
        codeHerdLabel = new Texture("SplashScreenMain.png");

        // set the linear texture filter to improve the image stretching
        codeHerdLabel.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        stage.clear();

        TextureRegion splashRegion = new TextureRegion(codeHerdLabel);

        Image splashImage = new Image(splashRegion, Scaling.stretch, Align.BOTTOM | Align.LEFT);
        splashImage.width = width;
        splashImage.height = height;

        splashImage.color.a = 0f;

        Sequence actions = Sequence.$(FadeIn.$(0.75f), Delay.$(FadeOut.$(0.75f), 1.75f));
        actions.setCompletionListener(new OnActionCompleted() {
            @Override
            public void completed(Action action) {
                GameController.getInstance();
                game.setScreen(new GameScreen(game, "level1"));
            }
        });
        splashImage.action(actions);

        stage.addActor(splashImage);
    }

    @Override
    public void dispose() {
        super.dispose();
        codeHerdLabel.dispose();
    }
}