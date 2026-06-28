package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.ProjectileShooterAI;
import io.github.andreytondo.system.ProjectileSystem;
import io.github.andreytondo.utils.Constants;

/** Brócolis Atirador — stationary enemy that fires spread of 3 projectiles. */
public class BroccoliEnemy extends EnemyBase {

    public BroccoliEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound,
                          ProjectileSystem ps) {
        super(x, y,
              Constants.BROCCOLI_SIZE, Constants.BROCCOLI_SIZE,
              0f, Constants.BROCCOLI_HEALTH,
              walkSheet, deathSound,
              new ProjectileShooterAI(ps, 10f, Constants.PROJECTILE_SPEED * 0.65f, 1.5f, 3, 22f, false),
              target,
              0.25f,
              new Color(0.1f, 0.9f, 0.1f, 1f),  // hurtColor — bright green flash
              new Color(0.2f, 0.75f, 0.2f, 1f)); // baseColor — dark green
    }
}
