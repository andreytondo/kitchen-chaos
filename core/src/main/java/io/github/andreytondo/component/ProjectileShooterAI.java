package io.github.andreytondo.component;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.system.ProjectileSystem;
import io.github.andreytondo.utils.Constants;

public class ProjectileShooterAI implements EnemyBehavior {

    private final ProjectileSystem projectileSystem;
    private final float damage;
    private final float speed;
    private final float cooldown;
    private final int   count;
    private final float spreadDeg;
    private final boolean moves;

    private float cooldownLeft = 1f; // initial delay so first shot isn't instant
    private final Vector2 dir = new Vector2();

    public ProjectileShooterAI(ProjectileSystem ps, float damage, float speed,
                                float cooldown, int count, float spreadDeg, boolean moves) {
        this.projectileSystem = ps;
        this.damage    = damage;
        this.speed     = speed;
        this.cooldown  = cooldown;
        this.count     = count;
        this.spreadDeg = spreadDeg;
        this.moves     = moves;
    }

    @Override
    public void execute(BaseActor target, BaseActor self, float delta) {
        cooldownLeft -= delta;

        dir.set(target.getPosition()).sub(self.getPosition()).nor();

        if (moves) self.move(dir, delta);

        if (cooldownLeft > 0) return;
        cooldownLeft = cooldown;

        float cx = self.getX() + self.getWidth()  / 2f;
        float cy = self.getY() + self.getHeight() / 2f;

        float halfSpread = spreadDeg * (count - 1) / 2f;
        for (int i = 0; i < count; i++) {
            Vector2 d = dir.cpy().rotateDeg(-halfSpread + i * spreadDeg);
            projectileSystem.fire(cx, cy, d.x, d.y, speed, damage, Constants.ROOM_WIDTH * 0.8f, false);
        }
    }

    @Override
    public void reset() {
        cooldownLeft = 1f;
    }
}
