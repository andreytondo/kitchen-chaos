package io.github.andreytondo.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.component.PlayerInput;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

public class Player extends BaseActor implements Renderable {

    private final PlayerInput input;

    public Player(float x, float y) {
        super(x, y, Constants.PLAYER_SIZE, Constants.PLAYER_SIZE, Constants.PLAYER_SPEED);
        this.input = new PlayerInput();
    }

    @Override
    public void update(float delta) {
        Vector2 direction = input.getMovementDirection();
        move(direction, delta);

        clampToWorld();
    }

    private void clampToWorld() {
        position.x = MathUtils.clamp(position.x, 0f, Constants.WORLD_WIDTH - width);
        position.y = MathUtils.clamp(position.y, 0f, Constants.WORLD_HEIGHT - height);
    }

    @Override
    public void render(GameRenderer renderer) {
        renderer.getShapeRenderer().rect(getX(), getY(), getWidth(), getHeight());
    }
}
