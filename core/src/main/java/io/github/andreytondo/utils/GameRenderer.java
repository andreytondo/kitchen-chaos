package io.github.andreytondo.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;

@Getter
public class GameRenderer {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    public GameRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
    }
}
