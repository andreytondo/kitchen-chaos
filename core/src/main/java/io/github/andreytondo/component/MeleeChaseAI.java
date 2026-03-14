package io.github.andreytondo.component;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.entity.BaseActor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeleeChaseAI implements EnemyBehavior {
    private final Attack attack;

    @Override
    public void execute(BaseActor target, BaseActor self, float delta) {
        attack.update(delta);
        Vector2 dir = target.getPosition().cpy().sub(self.getPosition()).nor();
        attack.tryAttack(target, self);
        self.move(dir, delta);
    }
}
