package io.github.andreytondo.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.component.BasicMovement;
import io.github.andreytondo.component.Health;
import io.github.andreytondo.contract.HasHealth;
import io.github.andreytondo.contract.Movement;
import io.github.andreytondo.contract.Updatable;
import io.github.andreytondo.utils.Constants;
import lombok.Getter;

@Getter
public abstract class BaseActor extends BaseEntity implements Updatable, HasHealth {
    protected final float speed;
    protected final Vector2 facingDirection;
    protected final Movement movementComponent;
    protected final Health health;

    protected BaseActor(float x, float y, float width, float height, float speed, float maxHealth) {
        super(x, y, width, height);
        this.speed = speed;
        this.facingDirection = new Vector2(1f, 0f);
        this.movementComponent = new BasicMovement();
        this.health = new Health(maxHealth);
    }

    public void move(Vector2 direction, float delta) {
        move(direction, speed, delta);
    }

    protected void move(Vector2 direction, float effectiveSpeed, float delta) {
        if (direction != null && !direction.isZero()) {
            facingDirection.set(direction).nor();
        }

        movementComponent.move(position, direction, effectiveSpeed, delta);
    }

    protected void clampToWorld() {
        position.x = MathUtils.clamp(position.x, 0f, Constants.WORLD_WIDTH - width);
        position.y = MathUtils.clamp(position.y, 0f, Constants.WORLD_HEIGHT - height);
    }
}
