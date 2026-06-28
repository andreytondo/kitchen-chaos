package io.github.andreytondo.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile {

    public static final float SIZE = 18f;

    public final Vector2   position  = new Vector2();
    public final Vector2   direction = new Vector2();
    public float           speed;
    public float           damage;
    public float           maxRange;
    public boolean         fromPlayer;

    private float          traveled  = 0f;
    private boolean        active    = false;
    private final Rectangle bounds   = new Rectangle();

    public void init(float x, float y, float dirX, float dirY,
                     float speed, float damage, float maxRange, boolean fromPlayer) {
        position.set(x, y);
        direction.set(dirX, dirY).nor();
        this.speed      = speed;
        this.damage     = damage;
        this.maxRange   = maxRange;
        this.fromPlayer = fromPlayer;
        traveled        = 0f;
        active          = true;
    }

    public void update(float delta) {
        float dx = direction.x * speed * delta;
        float dy = direction.y * speed * delta;
        position.x += dx;
        position.y += dy;
        traveled += (float) Math.sqrt(dx * dx + dy * dy);
        if (traveled >= maxRange) active = false;
    }

    public boolean  isActive()   { return active; }
    public void     deactivate() { active = false; }

    public Rectangle getBounds() {
        float half = SIZE / 2f;
        return bounds.set(position.x - half, position.y - half, SIZE, SIZE);
    }
}
