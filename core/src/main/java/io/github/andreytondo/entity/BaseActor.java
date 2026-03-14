package io.github.andreytondo.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.component.BasicMovement;
import io.github.andreytondo.contract.Movement;
import io.github.andreytondo.contract.Updatable;
import lombok.Getter;

@Getter
public abstract class BaseActor extends BaseEntity implements Updatable {
    protected final Vector2 facingDirection;
    protected final Movement movementComponent;

    protected BaseActor(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        this.facingDirection = new Vector2(1f, 0f);
        this.movementComponent = new BasicMovement(speed);
    }

    protected void move(Vector2 direction, float delta) {
        if (direction != null && !direction.isZero()) {
            facingDirection.set(direction).nor();
        }

        movementComponent.move(position, direction, delta);
    }
}
