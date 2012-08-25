
package com.ludumdare.evolution.domain.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.ludumdare.evolution.LudumDareMain;
import com.ludumdare.evolution.app.Constants;
import com.ludumdare.evolution.domain.controllers.GameController;
import com.ludumdare.evolution.domain.entities.Mobi;
import com.ludumdare.evolution.domain.scene2d.AbstractScreen;

import java.util.List;

public class GameScreen extends AbstractScreen {

    private static final String COLLISION_OBJECT_GROUP = "COLLISION";
    private static final String PLAYER_START_OBJECT_GROUP = "PLAYER_START";

    private World world;

    private Box2DDebugRenderer renderer;
    private OrthographicCamera cam;
    private TileMapRenderer tileMapRenderer;

    private long lastGroundTime = 0;
    private float stillTime = 0;
    private boolean jump;
    private Vector3 point = new Vector3();
    private Vector3 tmp = new Vector3();

    @Override
    public void dispose() {
        world.dispose();
        renderer.dispose();
        batch.dispose();

        super.dispose();
    }

    public GameScreen(LudumDareMain game) {
        super(game);

        world = new World(new Vector2(0, -40), true);

        renderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(this);

        cam = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
//        cam.zoom = 20.f;

        Mobi mobi = new Mobi(world);

        GameController.getInstance().setCurrentMobi(mobi);

        createTiledMap("level1");


    }

    private void createTiledMap(String level) {
        TiledMap map = TiledLoader.createMap(Gdx.files.internal("tiledmap/"+ level +".tmx"));

        SimpleTileAtlas simpleTileAtlas = new SimpleTileAtlas(map, Gdx.files.internal("tiledmap/"));

        tileMapRenderer = new TileMapRenderer(map, simpleTileAtlas, 24, 24);

        // create collision
        for (TiledObjectGroup objectGroup : map.objectGroups) {
            if (COLLISION_OBJECT_GROUP.equals(objectGroup.name)) {
                for (TiledObject object : objectGroup.objects) {
                    createStaticBoxFromObject(object);
                }
            }
            if (PLAYER_START_OBJECT_GROUP.equals(objectGroup.name)) {
                TiledObject object = objectGroup.objects.get(0);

                GameController.getInstance().getCurrentMobi().setPosition(object.x, object.y);
            }
        }
    }

    private void createStaticBoxFromObject(TiledObject object) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        Body box = world.createBody(def);

        PolygonShape shape = new PolygonShape();

        float width = object.width / Constants.BOX2D_SCALE_FACTOR;
        float height = object.height / Constants.BOX2D_SCALE_FACTOR;

        shape.setAsBox(width, height,
                new Vector2(
                        (object.x / Constants.BOX2D_SCALE_FACTOR) - (width / 2),
                        (object.y / Constants.BOX2D_SCALE_FACTOR) - (height / 2)), 0);

        box.createFixture(shape, 0);
        shape.dispose();

    }

    @Override
    public void render(float delta) {

        Mobi currentMobi = GameController.getInstance().getCurrentMobi();

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        cam.update();

        Vector2 vel = currentMobi.getLinearVelocity();
        Vector2 pos = currentMobi.getBox2dPosition();

        boolean grounded = currentMobi.isPlayerGrounded(Gdx.graphics.getDeltaTime(), world);

        if (grounded) {
            lastGroundTime = TimeUtils.nanoTime();
        } else {
            if (TimeUtils.nanoTime() - lastGroundTime < 100000000) {
                grounded = true;
            }
        }

        // cap max velocity on x
        if (Math.abs(vel.x) > currentMobi.getMaxVelocity()) {

            vel.x = Math.signum(vel.x) * currentMobi.getMaxVelocity();
            currentMobi.setLinearVelocity(vel.x, vel.y);

        }

        // calculate stilltime & damp
        if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
            stillTime += Gdx.graphics.getDeltaTime();
            currentMobi.setLinearVelocity(vel.x * 0.9f, vel.y);
        } else {
            stillTime = 0;
        }

        // disable friction while jumping
        if (!grounded) {
            currentMobi.setFriction(0.0f);
        } else {
            if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D) && stillTime > 0.2) {
                currentMobi.setFriction(1000f);
            } else {
                currentMobi.setFriction(0.2f);
            }

            // dampen sudden changes in x/y of a MovingPlatform a little bit, otherwise
            // character hops :)
            Object groundedPlatform = currentMobi.getGroundedPlatform();

//            if (groundedPlatform != null && groundedPlatform instanceof MovingPlatform
//                    && ((MovingPlatform) groundedPlatform).dist == 0) {
//                currentMobi.applyLinearImpulse(0, -24, pos.x, pos.y);
//            }
        }

        // since Box2D 2.2 we need to reset the friction of any existing contacts
        List<Contact> contacts = world.getContactList();
        for (int i = 0; i < world.getContactCount(); i++) {
            Contact contact = contacts.get(i);
            contact.resetFriction();
        }

        // apply left impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -currentMobi.getMaxVelocity()) {
            currentMobi.applyLinearImpulse(-2f, 0, pos.x, pos.y);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < currentMobi.getMaxVelocity()) {
            currentMobi.applyLinearImpulse(2f, 0, pos.x, pos.y);
        }

        // jump, but only when grounded
        if (jump) {
            jump = false;
            if (grounded) {
                currentMobi.setLinearVelocity(vel.x, 0);

                System.out.println("vel before: " + vel.x);
                System.out.println("jump before: " + currentMobi.getLinearVelocity());

                currentMobi.setTransform(pos.x, pos.y + 0.01f, 0);

                currentMobi.applyLinearImpulse(0, currentMobi.getJumpVelocity(), pos.x, pos.y);

                System.out.println("jump, " + currentMobi.getLinearVelocity());
            }
        }

//        // update platforms
//        for (int i = 0; i < platforms.size; i++) {
//            Platform platform = platforms.get(i);
//            platform.update(Math.max(1 / 30.0f, Gdx.graphics.getDeltaTime()));
//        }

        world.step(Gdx.graphics.getDeltaTime(), 4, 4);

        currentMobi.setAwake(true);

        tileMapRenderer.getProjectionMatrix().set(cam.combined);

        Vector3 tmp = new Vector3();
        tmp.set(0, 0, 0);
        cam.unproject(tmp);

        tileMapRenderer.render((int) tmp.x, (int) tmp.y,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.setProjectionMatrix(cam.combined);

        batch.begin();

        currentMobi.draw(batch, 1.0f);

        batch.end();

        renderer.render(world, cam.combined.scale(
                Constants.BOX2D_SCALE_FACTOR,
                Constants.BOX2D_SCALE_FACTOR,
                Constants.BOX2D_SCALE_FACTOR));

        cam.position.set(currentMobi.getPosition().x, currentMobi.getPosition().y, 0);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W) jump = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.W) jump = false;
        return false;
    }
}
