package io.github.andreytondo.component;

import lombok.Getter;

@Getter
public class Health {
    private final float maxHealth;
    private float currentHealth;

    public Health(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public void takeDamage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public float getPercent() {
        return currentHealth / maxHealth;
    }
}

