package io.github.andreytondo.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.andreytondo.utils.Constants;

public class LoadingScreen implements Screen {

    private static final float BAR_WIDTH  = Constants.WORLD_WIDTH  * 0.6f;
    private static final float BAR_HEIGHT = 40f;
    private static final float BAR_X      = (Constants.WORLD_WIDTH  - BAR_WIDTH)  / 2f;
    private static final float BAR_Y      = (Constants.WORLD_HEIGHT - BAR_HEIGHT) / 2f;
    private static final Color BAR_BG     = new Color(0.15f, 0.15f, 0.15f, 1f);
    private static final Color BAR_FILL   = new Color(0.2f,  0.8f,  0.2f,  1f);
    private static final Color BAR_BORDER = new Color(0.5f,  0.5f,  0.5f,  1f);

    private final Game game;
    private final AssetManager assets;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;

    public LoadingScreen(Game game, AssetManager assets) {
        this.game = game;
        this.assets = assets;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        if (assets.update()) {
            game.setScreen(new MainMenuScreen(game, assets));
            return;
        }

        float progress = assets.getProgress();

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);

        // Background bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BG);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT);

        // Fill bar
        shapeRenderer.setColor(BAR_FILL);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH * progress, BAR_HEIGHT);
        shapeRenderer.end();

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(BAR_BORDER);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
    }

    @Override public void show()    {}
    @Override public void pause()   {}
    @Override public void resume()  {}
    @Override public void hide()    {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
