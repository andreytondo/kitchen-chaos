package io.github.andreytondo.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.HasPosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEntity implements HasPosition {
    protected final Vector2 position;
    protected float width;
    protected float height;
    protected boolean active;

    protected BaseEntity(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.active = true;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
}
