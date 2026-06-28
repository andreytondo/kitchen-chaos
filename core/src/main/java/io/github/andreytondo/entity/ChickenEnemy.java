package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.MeleeChaseAI;
import io.github.andreytondo.utils.Constants;

/** Frango Zumbi — slow tank with high HP. */
public class ChickenEnemy extends EnemyBase {

    public ChickenEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound) {
        super(x, y,
              Constants.CHICKEN_SIZE, Constants.CHICKEN_SIZE,
              Constants.CHICKEN_SPEED, Constants.CHICKEN_HEALTH,
              walkSheet, deathSound,
              new MeleeChaseAI(new Attack(15f, Constants.CHICKEN_SIZE, 1.5f)), target,
              0.22f,
              new Color(0.3f, 0.7f, 1f, 1f),   // hurtColor — bright blue flash
              new Color(0.5f, 0.8f, 1f, 1f));   // baseColor — light blue
    }
}
