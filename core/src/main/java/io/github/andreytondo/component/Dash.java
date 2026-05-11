package io.github.andreytondo.component;

import com.badlogic.gdx.audio.Sound;

public class Dash {
    private final Timer activeTimer;
    private final Timer cooldownTimer;
    private final float speedMultiplier;
    private final Sound sound;

    public Dash(float duration, float cooldown, float speedMultiplier, Sound sound) {
        this.activeTimer = new Timer(duration);
        this.cooldownTimer = new Timer(cooldown);
        this.speedMultiplier = speedMultiplier;
        this.sound = sound;
    }

    public void update(float delta) {
        boolean wasActive = activeTimer.isRunning();
        activeTimer.update(delta);
        if (wasActive && !activeTimer.isRunning()) {
            cooldownTimer.start();
        }
        cooldownTimer.update(delta);
    }

    public void tryActivate() {
        if (!activeTimer.isRunning() && !cooldownTimer.isRunning()) {
            activeTimer.start();
            sound.play(0.8f);
        }
    }

    public boolean isActive() { return activeTimer.isRunning(); }

    public float applyTo(float baseSpeed) {
        return isActive() ? baseSpeed * speedMultiplier : baseSpeed;
    }
}
