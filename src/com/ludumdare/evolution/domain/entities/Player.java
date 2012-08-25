package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ludumdare.evolution.app.Constants;

import java.util.List;

public class Player extends Actor {

    private static final float MAX_VELOCITY = 14.0f;

    private Body body;
    private Fixture playerPhysicsFixture;
    private Fixture playerSensorFixture;

    private Object groundedPlatform;

    public Player(World world) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(0.45f, 1.4f);
        playerPhysicsFixture = body.createFixture(poly, 1);
        poly.dispose();

        CircleShape circle = new CircleShape();
        circle.setRadius(0.45f);
        circle.setPosition(new Vector2(0, -1.4f));
        playerSensorFixture = body.createFixture(circle, 0);
        circle.dispose();

        body.setBullet(true);
        body.setFixedRotation(true);

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        x = body.getPosition().x * Constants.BOX2D_SCALE_FACTOR;
        y = body.getPosition().y * Constants.BOX2D_SCALE_FACTOR;

    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {

    }

    @Override
    public Actor hit(float x, float y) {
        return null;
    }

    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
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
                    below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
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
}
