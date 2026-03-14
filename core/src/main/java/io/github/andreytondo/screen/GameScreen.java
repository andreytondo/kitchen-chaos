package io.github.andreytondo.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

public class GameScreen implements Screen {

    private final OrthographicCamera camera;
    private final GameRenderer gameRenderer;
    private final Player player;

    public GameScreen() {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        this.gameRenderer = new GameRenderer();
        this.player = new Player(200f, 200f);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        gameRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        gameRenderer.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        player.render(gameRenderer);
        gameRenderer.getShapeRenderer().end();
    }

    private void update(float delta) {
        player.update(delta);
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
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
