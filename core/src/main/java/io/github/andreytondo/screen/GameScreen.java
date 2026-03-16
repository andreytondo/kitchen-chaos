package io.github.andreytondo.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.entity.TomatoEnemy;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

import java.util.List;

public class GameScreen implements Screen {

    private final OrthographicCamera camera;
    private final GameRenderer gameRenderer;
    private final Player player;
    private final List<TomatoEnemy> enemies;

    public GameScreen() {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        this.gameRenderer = new GameRenderer();

        float cx = Constants.WORLD_WIDTH / 2f;
        float cy = Constants.WORLD_HEIGHT / 2f;
        this.player = new Player(cx, cy);

        float offset = 300f;
        this.enemies = List.of(
            new TomatoEnemy(cx - offset, cy - offset, player),
            new TomatoEnemy(cx + offset, cy - offset, player),
            new TomatoEnemy(cx - offset, cy + offset, player),
            new TomatoEnemy(cx + offset, cy + offset, player)
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
        player.dispose();
        enemies.forEach(TomatoEnemy::dispose);
    }
}
