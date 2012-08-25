package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ludumdare.evolution.app.Constants;

import java.util.List;

public class Mobi extends Actor {

    private float MAX_VELOCITY = 14.0f;
    private float JUMP_VELOCITY = 20.0f;

    private Body body;
    private Fixture playerPhysicsFixture;

    private Fixture playerSensorFixture;

    private Object groundedPlatform;
    private Texture texture;

    public Mobi(World world) {

        texture = new Texture("mobi-test.png");

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(texture.getWidth() / (2 * Constants.BOX2D_SCALE_FACTOR), texture.getHeight() / (2 * Constants.BOX2D_SCALE_FACTOR));
        playerPhysicsFixture = body.createFixture(poly, 1);
        poly.dispose();

        CircleShape circle = new CircleShape();
        circle.setRadius((texture.getWidth() / 2) / Constants.BOX2D_SCALE_FACTOR);
        circle.setPosition(new Vector2(0, -(texture.getHeight() / (2 * Constants.BOX2D_SCALE_FACTOR))));
        playerSensorFixture = body.createFixture(circle, 0);
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
//                    Object userData = body.getUserData();
//                    if (userData instanceof Mobi) {
//                        Mobi mobi = (Mobi) userData;
                        below &= (manifold.getPoints()[j].y < pos.y);
//                    }
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
        playerPhysicsFixture.setFriction(friction);
        playerSensorFixture.setFriction(friction);
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
        body.setTransform(Constants.convertToBox2d(x), Constants.convertToBox2d(y), 0);
    }

    public float getBox2dHeight() {
        return Constants.convertToBox2d(texture.getHeight());
    }

    public float getJumpVelocity() {
        return JUMP_VELOCITY;
    }
}
