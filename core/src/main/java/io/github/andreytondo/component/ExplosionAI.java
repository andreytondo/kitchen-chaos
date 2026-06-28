package io.github.andreytondo.component;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.entity.BaseActor;

public class ExplosionAI implements EnemyBehavior {

    private final float triggerRadius;
    private final float damage;
    private final Vector2 dir = new Vector2();

    public ExplosionAI(float triggerRadius, float damage) {
        this.triggerRadius = triggerRadius;
        this.damage        = damage;
    }

    @Override
    public void execute(BaseActor target, BaseActor self, float delta) {
        dir.set(target.getPosition()).sub(self.getPosition());
        float dist = dir.len();

        if (dist < triggerRadius) {
            target.takeDamage(damage);
            self.takeDamage(99999f); // self-destruct
            return;
        }
        self.move(dir.nor(), delta);
    }

    @Override
    public void reset() {}
}
