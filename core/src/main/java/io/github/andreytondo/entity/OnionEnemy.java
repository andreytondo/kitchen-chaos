package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.ExplosionAI;
import io.github.andreytondo.utils.Constants;

/** Cebola Explosiva — fast chaser that self-destructs on contact dealing AoE damage. */
public class OnionEnemy extends EnemyBase {

    public OnionEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound) {
        super(x, y,
              Constants.ONION_SIZE, Constants.ONION_SIZE,
              Constants.ONION_SPEED, Constants.ONION_HEALTH,
              walkSheet, deathSound,
              new ExplosionAI(Constants.ONION_EXPLOSION_RADIUS, Constants.ONION_EXPLOSION_DAMAGE),
              target,
              0.10f,
              new Color(1f, 0.4f, 0.8f, 1f),   // hurtColor — bright pink flash
              new Color(0.8f, 0.35f, 1f, 1f));  // baseColor — purple
    }
}
