package io.github.andreytondo.component;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.entity.BaseActor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RangedKiteAI implements EnemyBehavior {
    private final Attack attack;
    private final float preferredDistance;

    @Override
    public void execute(BaseActor target, BaseActor self, float delta) {
        attack.update(delta);
        float dist = target.getPosition().dst(self.getPosition());
        Vector2 toTarget = target.getPosition().cpy().sub(self.getPosition()).nor();

        if (dist < preferredDistance) {
            self.move(toTarget.scl(-1), delta);
        } else if (dist > preferredDistance + 50) {
            self.move(toTarget, delta);
        }

        attack.tryAttack(target, self);
    }
}
