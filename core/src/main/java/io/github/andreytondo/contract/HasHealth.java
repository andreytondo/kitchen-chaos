package io.github.andreytondo.contract;

import io.github.andreytondo.component.Health;

public interface HasHealth {
    Health getHealth();

    default void takeDamage(float amount) {
        getHealth().takeDamage(amount);
    }

    default boolean isDead() {
        return getHealth().isDead();
    }
}
