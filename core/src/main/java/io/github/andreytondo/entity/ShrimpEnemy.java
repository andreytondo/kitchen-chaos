package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.MeleeChaseAI;
import io.github.andreytondo.utils.Constants;

/** Camarão Veloz — tiny, extremely fast swarm unit with low HP. */
public class ShrimpEnemy extends EnemyBase {

    public ShrimpEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound) {
        super(x, y,
              Constants.SHRIMP_SIZE, Constants.SHRIMP_SIZE,
              Constants.SHRIMP_SPEED, Constants.SHRIMP_HEALTH,
              walkSheet, deathSound,
              new MeleeChaseAI(new Attack(5f, Constants.SHRIMP_SIZE, 0.6f)), target,
              0.07f,
              new Color(1f, 0.7f, 0.2f, 1f),   // hurtColor — bright orange flash
              new Color(1f, 0.55f, 0.1f, 1f));  // baseColor — deep orange
    }
}
