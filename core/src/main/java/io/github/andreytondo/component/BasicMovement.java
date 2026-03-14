package io.github.andreytondo.component;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.Movement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BasicMovement implements Movement {
    private float speed;

    @Override
    public void move(Vector2 position, Vector2 direction, float delta) {
        if (direction == null || direction.isZero()) {
            return;
        }

        Vector2 normalizedDirection = new Vector2(direction).nor();
        position.mulAdd(normalizedDirection, speed * delta);
    }
}
