package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ludumdare.evolution.app.Constants;
import com.ludumdare.evolution.domain.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class Key extends Actor {

    private List<Door> doors = new ArrayList<Door>();
    private String stringKey;

    protected MobiGenetics genetics;

    protected Body body;

    private Fixture playerPhysicsFixture;
    private Fixture playerSensorFixture;

    private Object groundedPlatform;

    private Texture texture;

    private Pixmap pixmap;
    private ShapeRenderer renderer = new ShapeRenderer();

    public void addDoor(Door door) {

        doors.add(door);

        door.setColour(getFilledInColour());

    }

    public Key(MobiGenetics mobiGenetics, World world) {
        genetics = mobiGenetics;

        initTexture();

        genetics.createTexture(pixmap);

        pixmap.dispose();

        initBoxPhysics(world);
    }

    public Key(World world) {

        genetics = new MobiGenetics(MobiGeneticsTypes.line);

        initTexture();

        genetics.createTexture(pixmap);

        pixmap.dispose();

        initBoxPhysics(world);
    }

    private void initTexture() {

        pixmap = new Pixmap(64, 64, Pixmap.Format.RGB888);

        texture = new Texture(pixmap);

    }

    protected void initBoxPhysics(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData(this);


        float widthInc = pixmap.getWidth() / 3;
        float heightInc = pixmap.getHeight() / 3;

        float posYStart = getPosition().y - texture.getHeight() + heightInc/2;
        for (int i = 0; i < 3; i++) {
            char[] chars = genetics.geneticMap[i];
            float posXStart = getPosition().x - texture.getWidth() / 2 + widthInc / 2;
            for (int j = 0; j < chars.length; j++) {
                char aChar = chars[j];
                if (aChar == 1) {
                    PolygonShape poly = new PolygonShape();
                    poly.setAsBox(widthInc/ 2 / Constants.BOX2D_SCALE_FACTOR, heightInc/ 2 / Constants.BOX2D_SCALE_FACTOR,
                            new Vector2(posXStart / Constants.BOX2D_SCALE_FACTOR, posYStart / Constants.BOX2D_SCALE_FACTOR), 0);
                    playerPhysicsFixture = body.createFixture(poly, 1);

                    Filter filter = new Filter();

                    filter.categoryBits = 0x0002;
                    filter.maskBits = 0x0002;

                    playerPhysicsFixture.setFilterData(filter);

                    poly.dispose();
                }
                posXStart += widthInc;
            }
            posYStart += heightInc;
        }

        CircleShape circle = new CircleShape();
        circle.setRadius((texture.getWidth() /3 / 2) / Constants.BOX2D_SCALE_FACTOR);
        circle.setPosition(new Vector2(0, -(texture.getHeight() / (Constants.BOX2D_SCALE_FACTOR * 1.02f))));

        playerSensorFixture = body.createFixture(circle, 50);

        Filter filter = new Filter();

        filter.groupIndex = -2;

        playerSensorFixture.setFilterData(filter);

        circle.dispose();

        body.setBullet(true);
        body.setFixedRotation(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        renderer.setProjectionMatrix(batch.getProjectionMatrix());

        float posYStart = getPosition().y - texture.getHeight();
        float widthInc = pixmap.getWidth() / 3;
        float heightInc = pixmap.getHeight() / 3;

        for (int i = 0; i < 3; i++) {
            char[] chars = genetics.geneticMap[i];
            float posXStart = getPosition().x - texture.getWidth() / 2;
            for (int j = 0; j < chars.length; j++) {
                char aChar = chars[j];
                if (aChar == 1) {
                    renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
                    renderer.setColor(getFilledInColour());
                    renderer.filledRect(posXStart, posYStart, widthInc, heightInc);
                    renderer.end();
                } else {
                    renderer.begin(ShapeRenderer.ShapeType.Rectangle);
                    Color gray = Color.GRAY;
                    gray.a = 0.01f;
                    renderer.setColor(gray);
                    renderer.rect(posXStart, posYStart, widthInc, heightInc);
                    renderer.end();
                }
                posXStart += widthInc;
            }
            posYStart += heightInc;
        }


//        batch.draw(texture, getPosition().x - texture.getWidth() / 2, getPosition().y - texture.getHeight() / 2);
//        batch.draw(pixmapTexture, getPosition().x - pixmapTexture.getWidth() / 2, getPosition().y - pixmapTexture.getHeight() / 2);
    }

    protected Color getFilledInColour() {
        return Color.CYAN;
    }

    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    public Vector2 getPosition() {
        return body.getPosition().mul(Constants.BOX2D_SCALE_FACTOR);
    }

    public Vector2 getBox2dPosition() {
        return body.getPosition();
    }

    public void setPosition(int x, int y) {

        float aWidth = Constants.convertToBox2d(texture.getWidth());
        float aHeight = Constants.convertToBox2d(texture.getHeight());

        body.setTransform(Constants.convertToBox2d(x + aWidth), Constants.convertToBox2d(y + aHeight), 0);
    }

    public float getBox2dHeight() {
        return Constants.convertToBox2d(texture.getHeight());
    }

    public float getTextureHeight() {
        return texture.getHeight();
    }

    public float getTextureWidth() {
        return texture.getWidth();
    }

    public void destroy(World world) {
        world.destroyBody(body);
    }

    public void unlock(Mobi userData, World world, GameScreen gameScreen) {

        char[][] combinedMap = userData.genetics.combineAll(this.genetics);

        if (MobiGenetics.matchesCompleteMap(combinedMap)) {

            doOnUnlock(world, gameScreen);

        }

    }

    protected void doOnUnlock(World world, GameScreen gameScreen) {
        destroy(world);
        gameScreen.getKeys().remove(this);

        for (Door door : doors) {
            door.destroy(world);
            gameScreen.getDoors().remove(door);
        }
    }

    @Override
    public Actor hit(float x, float y) {
        return null;
    }

    public String getStringKey() {
        return stringKey;
    }

    public void setStringKey(String stringKey) {
        this.stringKey = stringKey;
    }
}
