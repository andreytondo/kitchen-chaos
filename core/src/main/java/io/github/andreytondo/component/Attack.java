package io.github.andreytondo.component;

import io.github.andreytondo.contract.HasPosition;
import io.github.andreytondo.entity.BaseActor;
import lombok.Getter;

@Getter
public class Attack {
    private final float damage;
    private final float range;
    private final float cooldown;
    private float cooldownTimer = 0;

    public Attack(float damage, float range, float cooldown) {
        this.damage = damage;
        this.range = range;
        this.cooldown = cooldown;
    }

    public void update(float delta) {
        if (cooldownTimer > 0) cooldownTimer -= delta;
    }

    public boolean tryAttack(BaseActor target, HasPosition self) {
        if (cooldownTimer > 0) return false;
        float dist = target.getPosition().dst(self.getPosition());
        if (dist > range) return false;
        target.takeDamage(damage);
        cooldownTimer = cooldown;
        return true;
    }

    public boolean isReady() { return cooldownTimer <= 0; }
}
