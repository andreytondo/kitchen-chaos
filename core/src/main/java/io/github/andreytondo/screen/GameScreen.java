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
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.EnemySpawner;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.entity.RangedEnemy;
import io.github.andreytondo.entity.TomatoEnemy;
import io.github.andreytondo.map.MapGenerator;
import io.github.andreytondo.map.Room;
import io.github.andreytondo.system.CameraController;
import io.github.andreytondo.system.CollisionSystem;
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

    private final List<BaseActor>      enemies    = new ArrayList<>();
    private final CameraController     camera;
    private final CollisionSystem      collision  = new CollisionSystem();
    private final RoomProgressionSystem rooms;

    public GameScreen(Game game, AssetManager assets) {
        this.game   = game;
        this.assets = assets;

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

        EnemySpawner spawner = new EnemySpawner(tomatoPool, rangedPool, enemies);
        List<Room>   roomList = new MapGenerator().generate(4, new Random());
        this.rooms = new RoomProgressionSystem(roomList, spawner, game, assets);
        spawner.spawnRoom(roomList.get(0), player);
    }

    @Override
    public void show() {
        music.setLooping(true);
        music.setVolume(0.4f);
        music.play();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(player.getInputProcessor());
        multiplexer.addProcessor(new InputAdapter() {
            @Override public boolean scrolled(float amountX, float amountY) {
                camera.handleZoom(amountY);
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

        camera.getCamera().update();
        gameRenderer.getBatch().setProjectionMatrix(camera.getCamera().combined);
        gameRenderer.getBatch().begin();
        drawRoom();
        if (player.isActive()) player.render(gameRenderer);
        for (BaseActor enemy : enemies) {
            if (enemy.isActive()) ((io.github.andreytondo.contract.Renderable) enemy).render(gameRenderer);
        }
        gameRenderer.getBatch().end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        ShapeRenderer sr = gameRenderer.getShapeRenderer();
        sr.setProjectionMatrix(camera.getCamera().combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (player.isActive()) drawHealthBar(sr, player, Color.GREEN);
        for (BaseActor enemy : enemies) {
            if (enemy.isActive()) drawHealthBar(sr, enemy, Color.RED);
        }
        sr.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize();
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
        for (BaseActor enemy : enemies) {
            if (enemy.isActive()) {
                enemy.update(delta);
                player.tryAttack(enemy);
            }
        }
        collision.resolve(player, enemies, rooms.currentRoom().getWallRects());
        rooms.checkRoomComplete(enemies);
        rooms.checkDoorTransition(player);
        camera.update(delta, player);
    }

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
