package io.github.andreytondo.component;

import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.system.ProjectileSystem;
import io.github.andreytondo.utils.Constants;

public class BossAI implements EnemyBehavior {

    private static final float PROJ_DAMAGE       = 20f;
    private static final float PROJ_SPEED        = 600f;
    private static final float PHASE1_CD         = 2.5f;
    private static final float PHASE2_CD         = 1.2f;

    private final Attack          meleeAttack;
    private final ProjectileSystem projectileSystem;

    private float projCooldown = 2f; // initial delay
    private final Vector2 dir  = new Vector2();

    public BossAI(Attack meleeAttack, ProjectileSystem ps) {
        this.meleeAttack      = meleeAttack;
        this.projectileSystem = ps;
    }

    @Override
    public void execute(BaseActor target, BaseActor self, float delta) {
        meleeAttack.update(delta);
        dir.set(target.getPosition()).sub(self.getPosition()).nor();
        meleeAttack.tryAttack(target, self);
        self.move(dir, delta);

        boolean phase2 = self.getHealth().getPercent() < 0.5f;
        projCooldown -= delta;
        if (projCooldown <= 0) {
            projCooldown = phase2 ? PHASE2_CD : PHASE1_CD;
            int   count  = phase2 ? 3 : 1;
            float spread = phase2 ? 25f : 0f;
            float cx = self.getX() + self.getWidth()  / 2f;
            float cy = self.getY() + self.getHeight() / 2f;
            float halfSpread = spread * (count - 1) / 2f;
            for (int i = 0; i < count; i++) {
                Vector2 d = dir.cpy().rotateDeg(-halfSpread + i * spread);
                projectileSystem.fire(cx, cy, d.x, d.y, PROJ_SPEED, PROJ_DAMAGE,
                                      Constants.ROOM_WIDTH, false);
            }
        }
    }

    @Override
    public void reset() {
        meleeAttack.reset();
        projCooldown = 2f;
    }
}
