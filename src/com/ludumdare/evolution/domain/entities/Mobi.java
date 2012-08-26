package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ludumdare.evolution.app.Constants;

import java.util.ArrayList;
import java.util.List;

public class Mobi extends Actor {

    private float MAX_VELOCITY = 14.0f;
    private float JUMP_VELOCITY = 8.0f;

    private MobiGenetics genetics;

    private Body body;
    private Fixture playerPhysicsFixture;

    private Fixture playerSensorFixture;

    private Object groundedPlatform;
    private Texture texture;

    private Pixmap pixmap;
    private Texture pixmapTexture;

    public Mobi(MobiGenetics mobiGenetics, World world) {
        initTexture();

        genetics = mobiGenetics;

        initBoxPhysics(world);
    }

    public Mobi(World world) {

        initTexture();

        genetics = new MobiGenetics(MobiGeneticsTypes.line);

        initBoxPhysics(world);
    }

    private void initTexture() {
        texture = new Texture("mobi-test.png");
    }

    private void initBoxPhysics(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData(this);

        int posXStart;
        int posYStart = texture.getHeight() / 3 / 4;

        int widthInc = texture.getWidth() / 3 / 2;
        int heightInc = texture.getHeight() / 3 / 2 ;

        for (int i = 0; i < 3; i++) {
            char[] chars = genetics.geneticMap[i];
            posXStart = texture.getWidth() / 3 / 4;
            for (int j = 0; j < chars.length; j++) {
                char aChar = chars[j];
                if (aChar == 1) {
                    PolygonShape poly = new PolygonShape();
                    poly.setAsBox(widthInc / Constants.BOX2D_SCALE_FACTOR, heightInc / Constants.BOX2D_SCALE_FACTOR,
                            new Vector2(posXStart / Constants.BOX2D_SCALE_FACTOR, posYStart / Constants.BOX2D_SCALE_FACTOR), 0);

                    playerPhysicsFixture = body.createFixture(poly, 1);
                    poly.dispose();
                }
                posXStart += widthInc + (widthInc );
            }
            posYStart += heightInc + (heightInc );
        }

        CircleShape circle = new CircleShape();
        circle.setRadius((texture.getWidth() / 2) / Constants.BOX2D_SCALE_FACTOR);
        circle.setPosition(new Vector2(0, -(texture.getHeight() / (2 * Constants.BOX2D_SCALE_FACTOR))));
        playerSensorFixture = body.createFixture(circle, 0);
        Filter filter = new Filter();
        filter.groupIndex = -1;
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
        batch.draw(texture, getPosition().x - texture.getWidth() / 2, getPosition().y - texture.getHeight() / 2);
//        batch.draw(pixmapTexture, getPosition().x - pixmapTexture.getWidth() / 2, getPosition().y - pixmapTexture.getHeight() / 2);
    }

    @Override
    public Actor hit(float x, float y) {
        return null;
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

    public boolean isMobiTouchingOnlyOneOtherMobi(World world) {

        List<Contact> contactList = world.getContactList();

        List<Mobi> otherMobis = new ArrayList<Mobi>();

        for (Contact contact : contactList) {
            if (contact.isTouching() && contact.getFixtureA().getBody().getUserData() == this) {
                Object userData = contact.getFixtureB().getBody().getUserData();
                if (userData instanceof Mobi && !otherMobis.contains(userData)) {
                    otherMobis.add((Mobi) userData);
                }
            } else if (contact.getFixtureB().getBody().getUserData() == this) {
                Object userData = contact.getFixtureA().getBody().getUserData();
                if (userData instanceof Mobi && !otherMobis.contains(userData)) {
                    otherMobis.add((Mobi) userData);
                }
            }
        }

        return otherMobis.size() == 1;
    }

    public List<Mobi> getMobisCollidingWith(World world) {

        List<Contact> contactList = world.getContactList();

        List<Mobi> otherMobis = new ArrayList<Mobi>();

        for (Contact contact : contactList) {
            if (contact.isTouching() && contact.getFixtureA().getBody().getUserData() == this) {
                Object userData = contact.getFixtureB().getBody().getUserData();
                if (userData instanceof Mobi && !otherMobis.contains(userData)) {
                    otherMobis.add((Mobi) userData);
                }
            } else if (contact.getFixtureB().getBody().getUserData() == this) {
                Object userData = contact.getFixtureA().getBody().getUserData();
                if (userData instanceof Mobi && !otherMobis.contains(userData)) {
                    otherMobis.add((Mobi) userData);
                }
            }
        }

        return otherMobis;
    }

    public boolean isPlayerGrounded (float deltaTime, World world) {

        groundedPlatform = null;

        List<Contact> contactList = world.getContactList();

        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            if (contact.isTouching()
                    && (contact.getFixtureA() == playerSensorFixture || contact.getFixtureB() == playerSensorFixture)) {

                Vector2 pos = body.getPosition();
                WorldManifold manifold = contact.getWorldManifold();
                boolean below = true;
                for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                        below &= (manifold.getPoints()[j].y < pos.y);
                }

                if (below) {
                    //@todo this user data constant sucks.
                    if (contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("p")) {
                        groundedPlatform = contact.getFixtureA().getBody().getUserData();
                    }

                    if (contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("p")) {
                        groundedPlatform = contact.getFixtureB().getBody().getUserData();
                    }
                    return true;
                }

                return false;
            }
        }
        return false;
    }

    public Object getGroundedPlatform() {
        return groundedPlatform;
    }

    public float getMaxVelocity() {
        return MAX_VELOCITY;
    }

    public void limitLinearVelocity(Vector2 vel) {
        vel.x = Math.signum(vel.x) * MAX_VELOCITY;
        body.setLinearVelocity(vel.x, vel.y);
    }

    public void setLinearVelocity(float v, float y) {
        body.setLinearVelocity(v, y);
    }

    public void setFriction(float friction) {
        if (playerPhysicsFixture != null) {
            playerPhysicsFixture.setFriction(friction);
        }
        if (playerSensorFixture != null) {
            playerSensorFixture.setFriction(friction);
        }
    }

    public void applyLinearImpulse(float i, float i1, float x, float y) {
        body.applyLinearImpulse(i, i1, x, y);
    }

    public void setTransform(float x, float v, int i) {
        body.setTransform(x, v, i);
    }

    public void setAwake(boolean awake) {
        body.setAwake(awake);
    }

    public void setPosition(int x, int y) {

        float aWidth = Constants.convertToBox2d(texture.getWidth());
        float aHeight = Constants.convertToBox2d(texture.getHeight());

        body.setTransform(Constants.convertToBox2d(x + aWidth), Constants.convertToBox2d(y + aHeight), 0);
    }

    public float getBox2dHeight() {
        return Constants.convertToBox2d(texture.getHeight());
    }

    public float getJumpVelocity() {
        return JUMP_VELOCITY;
    }

    public float getTextureHeight() {
        return texture.getHeight();
    }

    public float getTextureWidth() {
        return texture.getWidth();
    }

    public List<MobiGenetics> mate(Mobi userData) {
        System.out.println("AWWW YEAH!");

        return genetics.mateWith(userData.genetics);
    }


    public void destroy(World world) {
        world.destroyBody(body);
    }
}
