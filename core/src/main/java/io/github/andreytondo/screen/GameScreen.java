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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.entity.TomatoEnemy;
import io.github.andreytondo.utils.Assets;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private static final int   WAVE_BASE_COUNT = 4;
    private static final float SPAWN_OFFSET    = 300f;
    private static final float MIN_ZOOM        = 0.35f;
    private static final float MAX_ZOOM        = 1.5f;
    private static final float CAMERA_LERP     = 5f;

    private final Game game;
    private final AssetManager assets;
    private final OrthographicCamera camera;
    private final GameRenderer gameRenderer;
    private final Texture floorTex;
    private final Player player;
    private final Music music;

    private final Pool<TomatoEnemy> enemyPool;
    private final List<TomatoEnemy> enemies = new ArrayList<>();

    private int wave = 0;

    private final Vector3 tmpVec = new Vector3();

    public GameScreen(Game game, AssetManager assets) {
        this.game = game;
        this.assets = assets;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        this.gameRenderer = new GameRenderer();

        Texture playerTexture = assets.get(Assets.PLAYER,      Texture.class);
        Texture tomatoWalkTex = assets.get(Assets.TOMATO_WALK,  Texture.class);
        this.floorTex         = assets.get(Assets.TILE_FLOOR,   Texture.class);

        Sound dashSound  = assets.get(Assets.SFX_DASH,  Sound.class);
        Sound hitSound   = assets.get(Assets.SFX_HIT,   Sound.class);
        Sound deathSound = assets.get(Assets.SFX_DEATH, Sound.class);
        this.music       = assets.get(Assets.MUSIC,     Music.class);

        float cx = Constants.WORLD_WIDTH  / 2f;
        float cy = Constants.WORLD_HEIGHT / 2f;
        this.player = new Player(cx, cy, playerTexture, dashSound, hitSound);

        camera.position.set(cx, cy, 0);

        enemyPool = new Pool<TomatoEnemy>() {
            @Override
            protected TomatoEnemy newObject() {
                return new TomatoEnemy(0, 0, player, tomatoWalkTex, deathSound);
            }
        };

        spawnWave();
    }

    // -------------------------------------------------------------------------
    // Wave management
    // -------------------------------------------------------------------------

    private void spawnWave() {
        wave++;
        float cx = Constants.WORLD_WIDTH  / 2f;
        float cy = Constants.WORLD_HEIGHT / 2f;

        int count = WAVE_BASE_COUNT + (wave - 1);
        for (int i = 0; i < count; i++) {
            float angle  = (float)(2 * Math.PI * i / count);
            float radius = SPAWN_OFFSET + (wave - 1) * 80f;
            float ex     = cx + (float)Math.cos(angle) * radius;
            float ey     = cy + (float)Math.sin(angle) * radius;

            TomatoEnemy e = enemyPool.obtain();
            e.init(ex, ey);
            enemies.add(e);
        }
    }

    private void checkWaveComplete() {
        for (TomatoEnemy e : enemies) {
            if (e.isActive()) return;
        }
        for (TomatoEnemy e : enemies) enemyPool.free(e);
        enemies.clear();
        spawnWave();
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
        drawFloor(gameRenderer);
        if (player.isActive()) player.render(gameRenderer);
        for (TomatoEnemy enemy : enemies) {
            if (enemy.isActive()) enemy.render(gameRenderer);
        }
        gameRenderer.getBatch().end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        ShapeRenderer sr = gameRenderer.getShapeRenderer();
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (player.isActive()) drawHealthBar(sr, player, Color.GREEN);
        for (TomatoEnemy enemy : enemies) {
            if (enemy.isActive()) drawHealthBar(sr, enemy, Color.RED);
        }
        sr.end();
    }

    @Override
    public void resize(int width, int height) {
        Vector3 savedPos = camera.position.cpy();
        float savedZoom = camera.zoom;
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
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
        for (TomatoEnemy enemy : enemies) {
            if (enemy.isActive()) {
                enemy.update(delta);
                player.tryAttack(enemy);
            }
        }
        resolveCollisions();
        checkWaveComplete();
        updateCamera(delta);
    }

    private void updateCamera(float delta) {
        float targetX = player.getX() + player.getWidth()  / 2f;
        float targetY = player.getY() + player.getHeight() / 2f;

        tmpVec.set(targetX, targetY, 0);
        camera.project(tmpVec);   // tmpVec now holds screen coordinates

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
            Math.min(halfW, Constants.WORLD_WIDTH  - halfW),
            Math.max(halfW, Constants.WORLD_WIDTH  - halfW));
        camera.position.y = MathUtils.clamp(camera.position.y,
            Math.min(halfH, Constants.WORLD_HEIGHT - halfH),
            Math.max(halfH, Constants.WORLD_HEIGHT - halfH));
    }

    private void handleZoom(float scrollAmount) {
        int mouseScreenX = Gdx.input.getX();
        int mouseScreenY = Gdx.input.getY();

        tmpVec.set(mouseScreenX, mouseScreenY, 0);
        camera.unproject(tmpVec);   // tmpVec now holds world coords
        float worldX = tmpVec.x;
        float worldY = tmpVec.y;

        camera.zoom = MathUtils.clamp(camera.zoom + scrollAmount * 0.1f, MIN_ZOOM, MAX_ZOOM);
        camera.update();

        tmpVec.set(mouseScreenX, mouseScreenY, 0);
        camera.unproject(tmpVec);   // tmpVec now holds new world coords for same screen point

        camera.position.x += worldX - tmpVec.x;
        camera.position.y += worldY - tmpVec.y;

        clampCameraToWorld();
    }

    private void drawFloor(GameRenderer renderer) {
        float t = Constants.TILE_SIZE;
        for (float x = 0; x < Constants.WORLD_WIDTH; x += t) {
            for (float y = 0; y < Constants.WORLD_HEIGHT; y += t) {
                renderer.getBatch().draw(floorTex, x, y, t, t);
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
        for (TomatoEnemy enemy : enemies) {
            if (player.isActive() && enemy.isActive()) {
                pushApart(player, enemy);
            }
        }
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                TomatoEnemy a = enemies.get(i);
                TomatoEnemy b = enemies.get(j);
                if (a.isActive() && b.isActive()) {
                    pushApart(a, b);
                }
            }
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
