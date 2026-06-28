package io.github.andreytondo.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.EnemySpawner;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.entity.RangedEnemy;
import io.github.andreytondo.entity.TomatoEnemy;
import io.github.andreytondo.map.MapGenerator;
import io.github.andreytondo.map.Room;
import io.github.andreytondo.utils.Assets;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {

    private static final float MIN_ZOOM    = 0.35f;
    private static final float MAX_ZOOM    = 1.5f;
    private static final float CAMERA_LERP = 5f;

    private final Game game;
    private final AssetManager assets;
    private final OrthographicCamera camera;
    private final GameRenderer gameRenderer;
    private final Texture floorTex;
    private final Texture wallTex;
    private final Player player;
    private final Music music;

    private final List<BaseActor> enemies = new ArrayList<>();
    private final EnemySpawner spawner;
    private final List<Room> rooms;
    private int currentRoomIndex = 0;

    private final Vector3 tmpVec = new Vector3();

    public GameScreen(Game game, AssetManager assets) {
        this.game = game;
        this.assets = assets;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.ROOM_WIDTH, Constants.ROOM_HEIGHT);
        this.gameRenderer = new GameRenderer();

        Texture playerTexture = assets.get(Assets.PLAYER,      Texture.class);
        Texture tomatoWalkTex = assets.get(Assets.TOMATO_WALK, Texture.class);
        this.floorTex         = assets.get(Assets.TILE_FLOOR,  Texture.class);
        this.wallTex          = assets.get(Assets.TILE_WALL,   Texture.class);

        Sound dashSound  = assets.get(Assets.SFX_DASH,  Sound.class);
        Sound hitSound   = assets.get(Assets.SFX_HIT,   Sound.class);
        Sound deathSound = assets.get(Assets.SFX_DEATH, Sound.class);
        this.music       = assets.get(Assets.MUSIC,     Music.class);

        float cx = Constants.ROOM_WIDTH  / 2f;
        float cy = Constants.ROOM_HEIGHT / 2f;
        this.player = new Player(cx, cy, playerTexture, dashSound, hitSound);
        camera.position.set(cx, cy, 0);

        Pool<TomatoEnemy> tomatoPool = new Pool<TomatoEnemy>() {
            @Override
            protected TomatoEnemy newObject() {
                return new TomatoEnemy(0, 0, player, tomatoWalkTex, deathSound);
            }
        };

        Pool<RangedEnemy> rangedPool = new Pool<RangedEnemy>() {
            @Override
            protected RangedEnemy newObject() {
                return new RangedEnemy(0, 0, player, tomatoWalkTex, deathSound);
            }
        };

        spawner = new EnemySpawner(tomatoPool, rangedPool, enemies);
        rooms   = new MapGenerator().generate(4, new Random());
        spawner.spawnRoom(rooms.get(0), player);
    }

    @Override
    public void show() {
        music.setLooping(true);
        music.setVolume(0.4f);
        music.play();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(player.getInputProcessor());
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                handleZoom(amountY);
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        gameRenderer.getBatch().setProjectionMatrix(camera.combined);
        gameRenderer.getBatch().begin();
        drawRoom(gameRenderer);
        if (player.isActive()) player.render(gameRenderer);
        for (BaseActor enemy : enemies) {
            if (enemy.isActive()) ((io.github.andreytondo.contract.Renderable) enemy).render(gameRenderer);
        }
        gameRenderer.getBatch().end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        ShapeRenderer sr = gameRenderer.getShapeRenderer();
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (player.isActive()) drawHealthBar(sr, player, Color.GREEN);
        for (BaseActor enemy : enemies) {
            if (enemy.isActive()) drawHealthBar(sr, enemy, Color.RED);
        }
        sr.end();
    }

    @Override
    public void resize(int width, int height) {
        Vector3 savedPos = camera.position.cpy();
        float savedZoom = camera.zoom;
        camera.setToOrtho(false, Constants.ROOM_WIDTH, Constants.ROOM_HEIGHT);
        camera.position.set(savedPos);
        camera.zoom = savedZoom;
    }

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
    }

    private void update(float delta) {
        player.update(delta);
        if (!player.isActive()) {
            game.setScreen(new GameOverScreen(game, assets));
            return;
        }
        for (int i = 0; i < enemies.size(); i++) {
            BaseActor enemy = enemies.get(i);
            if (enemy.isActive()) {
                enemy.update(delta);
                player.tryAttack(enemy);
            }
        }
        resolveCollisions();
        checkRoomComplete();
        checkDoorTransition();
        updateCamera(delta);
    }

    private void checkRoomComplete() {
        if (rooms.get(currentRoomIndex).isCompleted()) return;
        for (BaseActor e : enemies) {
            if (e.isActive()) return;
        }
        rooms.get(currentRoomIndex).markCompleted();
    }

    private void checkDoorTransition() {
        Room room = rooms.get(currentRoomIndex);
        if (!room.isExitOpen()) return;
        float px = player.getX() + player.getWidth()  / 2f;
        float py = player.getY() + player.getHeight() / 2f;
        if (!room.getExitDoor().contains(px, py)) return;

        currentRoomIndex++;
        if (currentRoomIndex >= rooms.size()) {
            game.setScreen(new GameOverScreen(game, assets));
            return;
        }
        // Place player at west entry of next room
        player.getPosition().set(Constants.TILE_SIZE * 1.5f, Constants.ROOM_HEIGHT / 2f - Constants.PLAYER_SIZE / 2f);
        spawner.spawnRoom(rooms.get(currentRoomIndex), player);
    }

    private void updateCamera(float delta) {
        float targetX = player.getX() + player.getWidth()  / 2f;
        float targetY = player.getY() + player.getHeight() / 2f;

        tmpVec.set(targetX, targetY, 0);
        camera.project(tmpVec);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float deadZoneX = screenW * 0.25f;
        float deadZoneY = screenH * 0.25f;

        boolean outsideX = tmpVec.x < deadZoneX || tmpVec.x > screenW - deadZoneX;
        boolean outsideY = tmpVec.y < deadZoneY || tmpVec.y > screenH - deadZoneY;

        if (outsideX || outsideY) {
            camera.position.x += (targetX - camera.position.x) * CAMERA_LERP * delta;
            camera.position.y += (targetY - camera.position.y) * CAMERA_LERP * delta;
        }

        clampCameraToWorld();
        camera.update();
    }

    private void clampCameraToWorld() {
        float halfW = camera.viewportWidth  * camera.zoom * 0.5f;
        float halfH = camera.viewportHeight * camera.zoom * 0.5f;
        camera.position.x = MathUtils.clamp(camera.position.x,
            Math.min(halfW, Constants.ROOM_WIDTH  - halfW),
            Math.max(halfW, Constants.ROOM_WIDTH  - halfW));
        camera.position.y = MathUtils.clamp(camera.position.y,
            Math.min(halfH, Constants.ROOM_HEIGHT - halfH),
            Math.max(halfH, Constants.ROOM_HEIGHT - halfH));
    }

    private void handleZoom(float scrollAmount) {
        int mouseScreenX = Gdx.input.getX();
        int mouseScreenY = Gdx.input.getY();

        tmpVec.set(mouseScreenX, mouseScreenY, 0);
        camera.unproject(tmpVec);
        float worldX = tmpVec.x;
        float worldY = tmpVec.y;

        camera.zoom = MathUtils.clamp(camera.zoom + scrollAmount * 0.1f, MIN_ZOOM, MAX_ZOOM);
        camera.update();

        tmpVec.set(mouseScreenX, mouseScreenY, 0);
        camera.unproject(tmpVec);

        camera.position.x += worldX - tmpVec.x;
        camera.position.y += worldY - tmpVec.y;

        clampCameraToWorld();
    }

    private void drawRoom(GameRenderer renderer) {
        Room room = rooms.get(currentRoomIndex);
        float t = Constants.TILE_SIZE;
        for (int c = 0; c < Constants.ROOM_COLS; c++) {
            for (int r = 0; r < Constants.ROOM_ROWS; r++) {
                Texture tex = room.isWall(c, r) ? wallTex : floorTex;
                renderer.getBatch().draw(tex, c * t, r * t, t, t);
            }
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

    private void resolveCollisions() {
        for (int i = 0; i < enemies.size(); i++) {
            BaseActor enemy = enemies.get(i);
            if (player.isActive() && enemy.isActive()) {
                pushApart(player, enemy);
            }
        }
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                BaseActor a = enemies.get(i);
                BaseActor b = enemies.get(j);
                if (a.isActive() && b.isActive()) {
                    pushApart(a, b);
                }
            }
        }
        resolveWallCollisions();
    }

    private void resolveWallCollisions() {
        List<Rectangle> walls = rooms.get(currentRoomIndex).getWallRects();
        pushActorOutOfWalls(player, walls);
        for (BaseActor e : enemies) {
            if (e.isActive()) pushActorOutOfWalls(e, walls);
        }
    }

    private static void pushActorOutOfWalls(BaseActor actor, List<Rectangle> walls) {
        float ax = actor.getX();
        float ay = actor.getY();
        float aw = actor.getWidth();
        float ah = actor.getHeight();

        for (Rectangle wall : walls) {
            float overlapX = Math.min(ax + aw, wall.x + wall.width)  - Math.max(ax, wall.x);
            float overlapY = Math.min(ay + ah, wall.y + wall.height) - Math.max(ay, wall.y);

            if (overlapX <= 0 || overlapY <= 0) continue;

            if (overlapX < overlapY) {
                float actorCX = ax + aw / 2f;
                float wallCX  = wall.x + wall.width / 2f;
                if (actorCX < wallCX) actor.getPosition().x -= overlapX;
                else                  actor.getPosition().x += overlapX;
            } else {
                float actorCY = ay + ah / 2f;
                float wallCY  = wall.y + wall.height / 2f;
                if (actorCY < wallCY) actor.getPosition().y -= overlapY;
                else                  actor.getPosition().y += overlapY;
            }

            // Re-read position after push for next wall test
            ax = actor.getX();
            ay = actor.getY();
        }
    }

    private static void pushApart(BaseActor a, BaseActor b) {
        float ax = a.getX() + a.getWidth()  / 2f;
        float ay = a.getY() + a.getHeight() / 2f;
        float bx = b.getX() + b.getWidth()  / 2f;
        float by = b.getY() + b.getHeight() / 2f;

        float overlapX = (a.getWidth()  / 2f + b.getWidth()  / 2f) - Math.abs(ax - bx);
        float overlapY = (a.getHeight() / 2f + b.getHeight() / 2f) - Math.abs(ay - by);

        if (overlapX <= 0 || overlapY <= 0) return;

        if (overlapX < overlapY) {
            if (ax < bx) b.getPosition().x += overlapX;
            else         b.getPosition().x -= overlapX;
        } else {
            if (ay < by) b.getPosition().y += overlapY;
            else         b.getPosition().y -= overlapY;
        }
    }
}
