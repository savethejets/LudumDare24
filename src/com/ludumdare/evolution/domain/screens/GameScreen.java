package com.ludumdare.evolution.domain.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.ludumdare.evolution.LudumDareMain;
import com.ludumdare.evolution.app.Constants;
import com.ludumdare.evolution.domain.controllers.GameController;
import com.ludumdare.evolution.domain.entities.Mobi;
import com.ludumdare.evolution.domain.entities.MobiGenetics;
import com.ludumdare.evolution.domain.entities.MobiGeneticsTypes;
import com.ludumdare.evolution.domain.scene2d.AbstractScreen;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends AbstractScreen {

    private static final String COLLISION_OBJECT_GROUP = "COLLISION";
    private static final String PLAYER_START_OBJECT_GROUP = "PLAYER_START";
    private static final String MOBI_OBJECT_GROUP = "MOBI";

    private World world;

    private Box2DDebugRenderer renderer;
    private OrthographicCamera cam;
    private TileMapRenderer tileMapRenderer;
    private ShapeRenderer shapeRenderer;
    private ShapeRenderer currentMobiRenderer;

    private List<Mobi> mobis = new ArrayList<Mobi>();
    private long lastGroundTime = 0;
    private float stillTime = 0;

    private boolean jump;

    private long lastSexTime;

    @Override
    public void dispose() {
        world.dispose();
        renderer.dispose();
        batch.dispose();

        super.dispose();
    }

    public GameScreen(LudumDareMain game) {
        super(game);

        GameController.getInstance().setCurrentMobi(null);

        world = new World(new Vector2(0, -40), true);

        renderer = new Box2DDebugRenderer(true, true, true, true);

        Gdx.input.setInputProcessor(this);

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        createTiledMap("level1");

        shapeRenderer = new ShapeRenderer();
        currentMobiRenderer = new ShapeRenderer();
    }

    private void createTiledMap(String level) {
        TiledMap map = TiledLoader.createMap(Gdx.files.internal("tiledmap/" + level + ".tmx"));

        SimpleTileAtlas simpleTileAtlas = new SimpleTileAtlas(map, Gdx.files.internal("tiledmap/"));

        tileMapRenderer = new TileMapRenderer(map, simpleTileAtlas, 32, 32);

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
            if (MOBI_OBJECT_GROUP.equals(objectGroup.name)) {
                for (TiledObject object : objectGroup.objects) {

                    MobiGenetics mobiGenetics = new MobiGenetics(MobiGeneticsTypes.line);
                    if (object.properties.containsKey("type")) {
                        String type = object.properties.get("type");

                        if (type.equals("L")) {
                            mobiGenetics = new MobiGenetics(MobiGeneticsTypes.lShapeRight);
                        }
                    }
                    Mobi mobi = new Mobi(mobiGenetics, world);

                    mobi.setPosition(object.x, object.y);

                    mobis.add(mobi);

                    if (GameController.getInstance().getCurrentMobi() == null) {
                        GameController.getInstance().setCurrentMobi(mobi);
                    }
                }
            }
        }
    }

    private void createStaticBoxFromObject(TiledObject object) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        Body box = world.createBody(def);

        PolygonShape shape = new PolygonShape();

        float width = object.width / (Constants.BOX2D_SCALE_FACTOR * 2);
        float height = object.height / (Constants.BOX2D_SCALE_FACTOR * 2);

        shape.setAsBox(width, height);

        box.createFixture(shape, 0);
        shape.dispose();

        float totalWidth = tileMapRenderer.getMap().width * tileMapRenderer.getMap().tileWidth;

        box.setTransform(Constants.convertToBox2d(object.x + object.width / 2), Constants.convertToBox2d(totalWidth - (object.y + object.height / 2)), 0);
    }

    @Override
    public void render(float delta) {

        Mobi currentMobi = GameController.getInstance().getCurrentMobi();

        Vector2 vel = currentMobi.getLinearVelocity();
        Vector2 pos = currentMobi.getBox2dPosition();

        boolean touchingOnlyOneOtherMobi = currentMobi.isMobiTouchingOnlyOneOtherMobi(world);

        boolean grounded = currentMobi.isPlayerGrounded(Gdx.graphics.getDeltaTime(), world);

        if (grounded) {
            lastGroundTime = TimeUtils.nanoTime();
        } else {
            if (TimeUtils.nanoTime() - lastGroundTime < 100000000) {
                grounded = true;
            }
        }

        if (TimeUtils.millis() - lastSexTime < 2000) {
            touchingOnlyOneOtherMobi = false;
        }

        // cap max velocity on x
        if (Math.abs(vel.x) > currentMobi.getMaxVelocity()) {

            vel.x = Math.signum(vel.x) * currentMobi.getMaxVelocity();
            currentMobi.setLinearVelocity(vel.x, vel.y);

        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && touchingOnlyOneOtherMobi) {
            List<Mobi> mobisTouching = currentMobi.getMobisCollidingWith(world);

            if (mobisTouching.size() == 1) {
                Mobi otherMobi = mobisTouching.get(0);

                List<MobiGenetics> geneticsList = currentMobi.mate(otherMobi);

                int i = 0;
                for (MobiGenetics mobiGenetics : geneticsList) {
                    Mobi mobi = new Mobi(mobiGenetics, world);

                    mobi.setPosition((int) currentMobi.getPosition().x + i, (int) (currentMobi.getPosition().y + 2 * currentMobi.getTextureHeight()));

                    mobi.applyLinearImpulse(0, 2, mobi.getBox2dPosition().x, mobi.getBox2dPosition().y);

                    mobis.add(mobi);

                    i += mobi.getTextureWidth();
                }

                lastSexTime = TimeUtils.millis();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) && touchingOnlyOneOtherMobi) {

            List<Mobi> mobisTouching = currentMobi.getMobisCollidingWith(world);

            for (Mobi mobi : mobisTouching) {
                mobi.destroy(world);
                mobis.remove(mobi);
            }
        }

        // calculate stilltime & damp
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            stillTime += Gdx.graphics.getDeltaTime();
            currentMobi.setLinearVelocity(vel.x * 0.9f, vel.y);
        } else {
            stillTime = 0;
        }

        // disable friction while jumping
        if (!grounded) {
            currentMobi.setFriction(0.0f);
        } else {
            if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && stillTime > 0.2) {
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
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && vel.x > -currentMobi.getMaxVelocity()) {
            currentMobi.applyLinearImpulse(-2f, 0, pos.x, pos.y);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && vel.x < currentMobi.getMaxVelocity()) {
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

        world.step(Gdx.graphics.getDeltaTime(), 4, 4);

        currentMobi.setAwake(true);

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        /**
         * A nice(?), blue backdrop.
         */
        Gdx.gl.glClearColor(0, 0.5f, 0.9f, 0);

        cam.update();

        tileMapRenderer.render(cam);

        batch.setProjectionMatrix(cam.combined);

        cam.position.set(currentMobi.getPosition().x, currentMobi.getPosition().y, 0);

        batch.begin();

        for (Mobi mobi : mobis) {
            mobi.act(delta);
            mobi.draw(batch, 1.0f);
        }

        batch.end();

        currentMobiRenderer.setProjectionMatrix(cam.combined);
        currentMobiRenderer.begin(ShapeRenderer.ShapeType.Triangle);
        float triangleY = currentMobi.getPosition().y + Constants.convertFromBox2d(currentMobi.getBox2dHeight()) / 2 + 15;
        currentMobiRenderer.triangle(
                currentMobi.getPosition().x + 10, triangleY,
                currentMobi.getPosition().x - 10, triangleY,
                currentMobi.getPosition().x, triangleY - 10);
        currentMobiRenderer.end();

        renderer.render(world, cam.combined.scale(
                Constants.BOX2D_SCALE_FACTOR,
                Constants.BOX2D_SCALE_FACTOR,
                Constants.BOX2D_SCALE_FACTOR));

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W) jump = true;

        if (keycode == Input.Keys.T) {
            game.setScreen(new GameScreen(game));
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.W) jump = false;

        if (keycode == Input.Keys.E && !jump) {
            Mobi currentMobi = GameController.getInstance().getCurrentMobi();

            int i = mobis.indexOf(currentMobi);

            if (i < mobis.size() - 1) {
                i++;
            } else {
                i = 0;
            }

            currentMobi.setFriction(0.2f);

            GameController.getInstance().setCurrentMobi(mobis.get(i));
        }

        if (keycode == Input.Keys.Q && !jump) {
            Mobi currentMobi = GameController.getInstance().getCurrentMobi();

            int i = mobis.indexOf(currentMobi);

            if (i > 0) {
                i--;
            } else {
                i = mobis.size() - 1;
            }

            currentMobi.setFriction(0.2f);

            GameController.getInstance().setCurrentMobi(mobis.get(i));
        }
        return false;
    }
}
