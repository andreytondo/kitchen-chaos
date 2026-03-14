package io.github.andreytondo.component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Dash {
    private final float duration;
    private final float cooldown;
    private final float speedMultiplier;

    private float activeTimer;
    private float cooldownTimer;

    public void update(float delta) {
        if (activeTimer > 0) {
            activeTimer -= delta;
            if (activeTimer <= 0) {
                cooldownTimer = cooldown;
            }
        } else if (cooldownTimer > 0) {
            cooldownTimer -= delta;
        }
    }

    public void tryActivate() {
        if (activeTimer <= 0 && cooldownTimer <= 0) {
            activeTimer = duration;
        }
    }

    public boolean isActive() {
        return activeTimer > 0;
    }

    public float applyTo(float baseSpeed) {
        return isActive() ? baseSpeed * speedMultiplier : baseSpeed;
    }
}
