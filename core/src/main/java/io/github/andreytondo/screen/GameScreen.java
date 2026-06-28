package io.github.andreytondo.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.component.WeaponType;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.EnemySpawner;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.entity.RangedEnemy;
import io.github.andreytondo.entity.TomatoEnemy;
import io.github.andreytondo.map.MapGenerator;
import io.github.andreytondo.map.Room;
import io.github.andreytondo.map.RoomType;
import io.github.andreytondo.system.CameraController;
import io.github.andreytondo.system.CollisionSystem;
import io.github.andreytondo.system.DebugRenderer;
import io.github.andreytondo.system.ProjectileSystem;
import io.github.andreytondo.system.RoomProgressionSystem;
import io.github.andreytondo.utils.Assets;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {

    private final Game         game;
    private final AssetManager assets;
    private final GameRenderer gameRenderer;
    private final Texture      floorTex;
    private final Texture      wallTex;
    private final Player       player;
    private final Music        music;

    private final List<BaseActor>       enemies          = new ArrayList<>();
    private final CameraController      camera;
    private final CollisionSystem       collision        = new CollisionSystem();
    private final ProjectileSystem      projectileSystem = new ProjectileSystem();
    private final RoomProgressionSystem rooms;
    private final DebugRenderer         debugRenderer    = new DebugRenderer();

    private final Vector3 mouseWorld = new Vector3();

    public GameScreen(Game game, AssetManager assets) {
        this.game   = game;
        this.assets = assets;

        Texture playerTex     = assets.get(Assets.PLAYER,      Texture.class);
        Texture tomatoWalkTex = assets.get(Assets.TOMATO_WALK, Texture.class);
        this.floorTex         = assets.get(Assets.TILE_FLOOR,  Texture.class);
        this.wallTex          = assets.get(Assets.TILE_WALL,   Texture.class);

        Sound dashSound  = assets.get(Assets.SFX_DASH,  Sound.class);
        Sound hitSound   = assets.get(Assets.SFX_HIT,   Sound.class);
        Sound deathSound = assets.get(Assets.SFX_DEATH, Sound.class);
        this.music       = assets.get(Assets.MUSIC,     Music.class);

        float cx = Constants.ROOM_WIDTH  / 2f;
        float cy = Constants.ROOM_HEIGHT / 2f;
        this.player = new Player(cx, cy, playerTex, dashSound, hitSound);

        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(false, Constants.ROOM_WIDTH, Constants.ROOM_HEIGHT);
        cam.position.set(cx, cy, 0);
        this.camera = new CameraController(cam);

        this.gameRenderer = new GameRenderer();

        Pool<TomatoEnemy> tomatoPool = new Pool<TomatoEnemy>() {
            @Override protected TomatoEnemy newObject() {
                return new TomatoEnemy(0, 0, player, tomatoWalkTex, deathSound);
            }
        };
        Pool<RangedEnemy> rangedPool = new Pool<RangedEnemy>() {
            @Override protected RangedEnemy newObject() {
                return new RangedEnemy(0, 0, player, tomatoWalkTex, deathSound);
            }
        };

        EnemySpawner spawner = new EnemySpawner(
            tomatoPool, rangedPool, enemies,
            player, tomatoWalkTex, deathSound, projectileSystem
        );

        List<Room> roomList = new MapGenerator().generate(4, new Random());
        this.rooms = new RoomProgressionSystem(roomList, spawner, game, assets);
        spawner.spawnRoom(roomList.get(0));
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override
    public void show() {
        music.setLooping(true);
        music.setVolume(0.4f);
        music.play();

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(player.getInputProcessor());
        mux.addProcessor(new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.F3) { debugRenderer.toggle(); return true; }
                return false;
            }
        });
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.getCamera().update();
        ShapeRenderer sr = gameRenderer.getShapeRenderer();

        // ── Pass 1: Sprites (world space) ─────────────────────────────────
        gameRenderer.getBatch().setProjectionMatrix(camera.getCamera().combined);
        gameRenderer.getBatch().begin();
        drawRoom();
        if (player.isActive()) player.render(gameRenderer);
        for (BaseActor e : enemies) {
            if (e.isActive()) ((Renderable) e).render(gameRenderer);
        }
        gameRenderer.getBatch().end();

        // ── Pass 2: Filled shapes (health bars, projectiles, room overlay) ─
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.getCamera().combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (player.isActive()) drawHealthBar(sr, player, Color.GREEN);
        for (BaseActor e : enemies) {
            if (e.isActive()) drawHealthBar(sr, e, Color.RED);
        }
        projectileSystem.renderFilled(sr);
        drawRoomOverlay(sr);
        sr.end();

        // ── Pass 3: Line shapes (debug hitboxes) ──────────────────────────
        if (debugRenderer.isEnabled()) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.drawHitboxes(sr, player, enemies,
                                        rooms.currentRoom().getWallRects(),
                                        rooms.currentRoom());
            debugRenderer.drawProjectileHitboxes(sr, projectileSystem);
            sr.end();
        }

        // ── Pass 4: HUD text (screen space) ──────────────────────────────
        gameRenderer.getBatch().setProjectionMatrix(debugRenderer.getScreenMatrix());
        gameRenderer.getBatch().begin();
        debugRenderer.drawAlwaysHud(gameRenderer.getBatch(), player.getCurrentWeapon(), player);
        if (debugRenderer.isEnabled()) {
            debugRenderer.drawDebugHud(gameRenderer.getBatch(), player, enemies,
                                        projectileSystem, player.getCurrentWeapon(),
                                        rooms.currentRoom());
        }
        gameRenderer.getBatch().end();
        gameRenderer.getBatch().setProjectionMatrix(camera.getCamera().combined);
    }

    @Override public void resize(int width, int height) { camera.resize(); }
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void hide() {
        music.stop();
        player.getInputProcessor().reset();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        debugRenderer.dispose();
    }

    // ── Update ────────────────────────────────────────────────────────────────

    private void update(float delta) {
        // Mouse aim: unproject screen → world each frame
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.getCamera().unproject(mouseWorld);
        player.setAimWorldPos(mouseWorld.x, mouseWorld.y);

        // Weapon cycling via scroll wheel
        int scroll = player.getInputProcessor().consumeScrollDelta();
        if (scroll != 0) player.cycleWeapon(scroll);

        player.update(delta);
        if (!player.isActive()) {
            game.setScreen(new GameOverScreen(game, assets));
            return;
        }

        // Attack input
        if (player.wantsToAttack() && player.canAttack()) {
            player.triggerAttackCooldown();
            WeaponType w = player.getCurrentWeapon();
            if (w.isMelee) {
                boolean hitAny = false;
                for (BaseActor e : enemies) {
                    if (!e.isActive()) continue;
                    if (player.isInMeleeRange(e, w.meleeRange)) {
                        e.takeDamage(w.damage);
                        hitAny = true;
                    }
                }
                if (hitAny) player.playHitSound();
            } else {
                firePlayerProjectiles(w);
            }
        }

        // Enemy updates
        for (BaseActor e : enemies) {
            if (e.isActive()) e.update(delta);
        }

        // Projectile updates
        projectileSystem.update(delta, player, enemies, rooms.currentRoom().getWallRects());

        // Physics
        collision.resolve(player, enemies, rooms.currentRoom().getWallRects());

        // Room progression
        rooms.checkRoomComplete(enemies);
        rooms.checkDoorTransition(player);
        if (rooms.consumeTransition()) projectileSystem.clear();

        camera.update(delta, player);
    }

    private void firePlayerProjectiles(WeaponType w) {
        float cx  = player.getX() + player.getWidth()  / 2f;
        float cy  = player.getY() + player.getHeight() / 2f;
        Vector2 aim = player.getAimDirection();
        float halfSpread = w.spreadDegrees * (w.projectileCount - 1) / 2f;
        for (int i = 0; i < w.projectileCount; i++) {
            Vector2 d = aim.cpy().rotateDeg(-halfSpread + i * w.spreadDegrees);
            projectileSystem.fire(cx, cy, d.x, d.y,
                                  Constants.PROJECTILE_SPEED, w.damage,
                                  Constants.ROOM_WIDTH * 0.8f, true);
        }
    }

    // ── Drawing helpers ────────────────────────────────────────────────────────

    private void drawRoom() {
        Room  room = rooms.currentRoom();
        float t    = Constants.TILE_SIZE;
        for (int c = 0; c < Constants.ROOM_COLS; c++) {
            for (int r = 0; r < Constants.ROOM_ROWS; r++) {
                Texture tex = room.isWall(c, r) ? wallTex : floorTex;
                gameRenderer.getBatch().draw(tex, c * t, r * t, t, t);
            }
        }
    }

    /** Room-type-specific overlays drawn in the filled ShapeRenderer pass. */
    private void drawRoomOverlay(ShapeRenderer sr) {
        Room room = rooms.currentRoom();
        if (room.getType() == RoomType.HEALING) {
            float cx = Constants.ROOM_WIDTH  / 2f;
            float cy = Constants.ROOM_HEIGHT / 2f;
            sr.setColor(0.1f, 0.9f, 0.3f, 0.30f);
            sr.circle(cx, cy, Constants.TILE_SIZE * 1.5f, 32);
            sr.setColor(0.15f, 1f, 0.45f, 0.55f);
            sr.circle(cx, cy, Constants.TILE_SIZE * 0.55f, 20);
        } else if (room.getType() == RoomType.BOSS) {
            float w = Constants.ROOM_WIDTH, h = Constants.ROOM_HEIGHT;
            float b = Constants.TILE_SIZE * 0.5f;
            sr.setColor(0.65f, 0f, 0f, 0.18f);
            sr.rect(0,     0,     w, b);
            sr.rect(0,     h - b, w, b);
            sr.rect(0,     b,     b, h - 2 * b);
            sr.rect(w - b, b,     b, h - 2 * b);
        }
    }

    private static final float BAR_HEIGHT = 8f;
    private static final float BAR_GAP    = 4f;

    private void drawHealthBar(ShapeRenderer sr, BaseActor actor, Color fillColor) {
        float x      = actor.getX();
        float y      = actor.getY() + actor.getHeight() + BAR_GAP;
        float w      = actor.getWidth();
        float filled = w * actor.getHealth().getPercent();

        sr.setColor(0.2f, 0.2f, 0.2f, 1f);
        sr.rect(x, y, w, BAR_HEIGHT);
        sr.setColor(fillColor);
        sr.rect(x, y, filled, BAR_HEIGHT);
    }
}
