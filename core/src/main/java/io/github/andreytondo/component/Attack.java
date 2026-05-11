package io.github.andreytondo.component;

import com.badlogic.gdx.audio.Sound;
import io.github.andreytondo.contract.HasPosition;
import io.github.andreytondo.entity.BaseActor;
import lombok.Getter;

@Getter
public class Attack {
    private final float damage;
    private final float range;
    private final Timer cooldownTimer;
    private final Sound hitSound;

    public Attack(float damage, float range, float cooldown, Sound hitSound) {
        this.damage = damage;
        this.range = range;
        this.cooldownTimer = new Timer(cooldown);
        this.hitSound = hitSound;
    }

    public Attack(float damage, float range, float cooldown) {
        this(damage, range, cooldown, null);
    }

    public void update(float delta) {
        cooldownTimer.update(delta);
    }

    public boolean tryAttack(BaseActor target, HasPosition self) {
        if (cooldownTimer.isRunning()) return false;
        float dist = target.getPosition().dst(self.getPosition());
        if (dist > range) return false;
        target.takeDamage(damage);
        if (hitSound != null) hitSound.play(0.6f);
        cooldownTimer.start();
        return true;
    }

    public boolean isReady() { return !cooldownTimer.isRunning(); }

    public void reset() { cooldownTimer.reset(); }
}
