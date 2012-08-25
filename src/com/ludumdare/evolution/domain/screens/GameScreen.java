package com.ludumdare.evolution.domain.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.ludumdare.evolution.LudumDareMain;
import com.ludumdare.evolution.domain.controllers.GameController;
import com.ludumdare.evolution.domain.entities.Player;
import com.ludumdare.evolution.domain.scene2d.AbstractScreen;

import java.util.List;

public class GameScreen extends AbstractScreen {

    private World world;

    private Box2DDebugRenderer renderer;
    private OrthographicCamera cam;

    private long lastGroundTime = 0;
    private float stillTime = 0;

    private boolean jump;
    private Vector3 point = new Vector3();

    @Override
    public void dispose () {
        world.dispose();
        renderer.dispose();
        batch.dispose();

        super.dispose();
    }

    public GameScreen(LudumDareMain game) {
        super(game);

        world = new World(new Vector2(0, -40), true);

        float y1 = -1; // (float)Math.random() * 0.1f + 1;
        float y2 = y1;
        for (int i = 0; i < 50; i++) {
            Body ground = createEdge(BodyDef.BodyType.StaticBody, -50 + i * 2, y1, -50 + i * 2 + 2, y2, 0);
            y1 = y2;
            y2 = -1; // (float)Math.random() + 1;
        }

        renderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(this);

        cam = new OrthographicCamera(28, 20);

        Player player = new Player(world);

        GameController.getInstance().setCurrentPlayer(player);
    }

    private Body createEdge (BodyDef.BodyType type, float x1, float y1, float x2, float y2, float density) {
        BodyDef def = new BodyDef();
        def.type = type;
        Body box = world.createBody(def);

        EdgeShape poly = new EdgeShape();
        poly.set(new Vector2(0, 0), new Vector2(x2 - x1, y2 - y1));
        box.createFixture(poly, density);
        box.setTransform(x1, y1, 0);
        poly.dispose();

        return box;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        Player currentPlayer = GameController.getInstance().getCurrentPlayer();

        cam.position.set(currentPlayer.x, currentPlayer.y, 0);

        cam.update();

        renderer.render(world, cam.combined);

        Vector2 vel = currentPlayer.getLinearVelocity();
        Vector2 pos = currentPlayer.getBox2dPosition();

        boolean grounded = currentPlayer.isPlayerGrounded(Gdx.graphics.getDeltaTime(), world);

        if (grounded) {
            lastGroundTime = TimeUtils.nanoTime();
        } else {
            if (TimeUtils.nanoTime() - lastGroundTime < 100000000) {
                grounded = true;
            }
        }

        // cap max velocity on x
        if (Math.abs(vel.x) > currentPlayer.getMaxVelocity()) {
            currentPlayer.limitLinearVelocity(vel);
        }

        // calculate stilltime & damp
        if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
            stillTime += Gdx.graphics.getDeltaTime();
            currentPlayer.setLinearVelocity(vel.x * 0.9f, vel.y);
        } else {
            stillTime = 0;
        }

        // disable friction while jumping
        if (!grounded) {
            currentPlayer.setFriction(0.0f);
        } else {
            if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D) && stillTime > 0.2) {
                currentPlayer.setFriction(1000f);
            } else {
                currentPlayer.setFriction(0.2f);
            }

            // dampen sudden changes in x/y of a MovingPlatform a little bit, otherwise
            // character hops :)
            Object groundedPlatform = currentPlayer.getGroundedPlatform();

//            if (groundedPlatform != null && groundedPlatform instanceof MovingPlatform
//                    && ((MovingPlatform) groundedPlatform).dist == 0) {
//                currentPlayer.applyLinearImpulse(0, -24, pos.x, pos.y);
//            }
        }

        // since Box2D 2.2 we need to reset the friction of any existing contacts
        List<Contact> contacts = world.getContactList();
        for (int i = 0; i < world.getContactCount(); i++) {
            Contact contact = contacts.get(i);
            contact.resetFriction();
        }

        // apply left impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -currentPlayer.getMaxVelocity()) {
            currentPlayer.applyLinearImpulse(-2f, 0, pos.x, pos.y);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < currentPlayer.getMaxVelocity()) {
            currentPlayer.applyLinearImpulse(2f, 0, pos.x, pos.y);
        }

        // jump, but only when grounded
        if (jump) {
            jump = false;
            if (grounded) {
                currentPlayer.setLinearVelocity(vel.x, 0);

                System.out.println("jump before: " + currentPlayer.getLinearVelocity());

                currentPlayer.setTransform(pos.x, pos.y + 0.01f, 0);

                currentPlayer.applyLinearImpulse(0, 40, pos.x, pos.y);

                System.out.println("jump, " + currentPlayer.getLinearVelocity());
            }
        }

//        // update platforms
//        for (int i = 0; i < platforms.size; i++) {
//            Platform platform = platforms.get(i);
//            platform.update(Math.max(1 / 30.0f, Gdx.graphics.getDeltaTime()));
//        }

        world.step(delta, 4, 4);

        currentPlayer.setAwake(true);

        cam.project(point.set(pos.x, pos.y, 0));

    }

    @Override
    public boolean keyDown (int keycode) {
        if (keycode == Input.Keys.W) jump = true;
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        if (keycode == Input.Keys.W) jump = false;
        return false;
    }
}
