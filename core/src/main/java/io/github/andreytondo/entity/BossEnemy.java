package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.BossAI;
import io.github.andreytondo.system.ProjectileSystem;
import io.github.andreytondo.utils.Constants;

/**
 * Chef Tomate Bravão — boss enemy.
 * Phase 1 (>50% HP): melee chase + single projectile every 4s.
 * Phase 2 (≤50% HP): faster melee + 3-way spread projectile every 2s.
 */
public class BossEnemy extends EnemyBase {

    public BossEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound,
                     ProjectileSystem ps) {
        super(x, y,
              Constants.BOSS_SIZE, Constants.BOSS_SIZE,
              Constants.BOSS_SPEED, Constants.BOSS_HEALTH,
              walkSheet, deathSound,
              new BossAI(new Attack(30f, Constants.BOSS_SIZE * 1.1f, 1.5f), ps), target,
              0.15f,
              new Color(1f, 0f, 0f, 1f),          // hurtColor — pure red flash
              new Color(0.85f, 0.1f, 0.1f, 1f));  // baseColor — dark red
    }
}
