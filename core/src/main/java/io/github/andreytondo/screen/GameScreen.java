package io.github.andreytondo.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.entity.TomatoEnemy;
import io.github.andreytondo.utils.Assets;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

import java.util.List;

public class GameScreen implements Screen {

    private final OrthographicCamera camera;
    private final GameRenderer gameRenderer;
    private final Player player;
    private final List<TomatoEnemy> enemies;

    public GameScreen(AssetManager assets) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        this.gameRenderer = new GameRenderer();

        Texture playerTexture = assets.get(Assets.PLAYER, Texture.class);
        Texture tomatoTexture = assets.get(Assets.TOMATO, Texture.class);

        float cx = Constants.WORLD_WIDTH / 2f;
        float cy = Constants.WORLD_HEIGHT / 2f;
        this.player = new Player(cx, cy, playerTexture);

        float offset = 300f;
        this.enemies = List.of(
            new TomatoEnemy(cx - offset, cy - offset, player, tomatoTexture),
            new TomatoEnemy(cx + offset, cy - offset, player, tomatoTexture),
            new TomatoEnemy(cx - offset, cy + offset, player, tomatoTexture),
            new TomatoEnemy(cx + offset, cy + offset, player, tomatoTexture)
        );
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        gameRenderer.getBatch().setProjectionMatrix(camera.combined);
        gameRenderer.getBatch().begin();
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

    private static final float BAR_HEIGHT = 8f;
    private static final float BAR_GAP = 4f;

    private void drawHealthBar(ShapeRenderer sr, BaseActor actor, Color fillColor) {
        float x = actor.getX();
        float y = actor.getY() + actor.getHeight() + BAR_GAP;
        float w = actor.getWidth();
        float filled = w * actor.getHealth().getPercent();

        sr.setColor(0.2f, 0.2f, 0.2f, 1f);
        sr.rect(x, y, w, BAR_HEIGHT);
        sr.setColor(fillColor);
        sr.rect(x, y, filled, BAR_HEIGHT);
    }

    private void update(float delta) {
        player.update(delta);
        if (!player.isActive()) return;
        for (TomatoEnemy enemy : enemies) {
            if (enemy.isActive()) {
                enemy.update(delta);
                player.tryAttack(enemy);
            }
        }
        resolveCollisions();
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
        float ax = a.getX() + a.getWidth() / 2f;
        float ay = a.getY() + a.getHeight() / 2f;
        float bx = b.getX() + b.getWidth() / 2f;
        float by = b.getY() + b.getHeight() / 2f;

        float overlapX = (a.getWidth() / 2f + b.getWidth() / 2f) - Math.abs(ax - bx);
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

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
    }
}
