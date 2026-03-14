package io.github.andreytondo.contract;

import com.badlogic.gdx.math.Vector2;

/**
 * Define como uma entidade irá se mover
 */
public interface Movement {
    void move(Vector2 position, Vector2 direction, float delta);
}
