package io.github.andreytondo.component;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.Movement;

public class BasicMovement implements Movement {

    private final Vector2 scratch = new Vector2();

    @Override
    public void move(Vector2 position, Vector2 direction, float speed, float delta) {
        if (direction == null || direction.isZero()) {
            return;
        }

        position.mulAdd(scratch.set(direction).nor(), speed * delta);
    }
}
