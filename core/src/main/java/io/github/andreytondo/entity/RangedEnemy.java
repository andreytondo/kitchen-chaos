package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.RangedKiteAI;
import io.github.andreytondo.utils.Constants;

public class RangedEnemy extends EnemyBase {

    public RangedEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound) {
        super(x, y,
              Constants.RANGED_SIZE, Constants.RANGED_SIZE,
              Constants.RANGED_SPEED, Constants.RANGED_HEALTH,
              walkSheet, deathSound,
              new RangedKiteAI(new Attack(8f, Constants.RANGED_PREFERRED_DIST * 0.9f, 2.0f), Constants.RANGED_PREFERRED_DIST), target,
              0.18f, new Color(0.3f, 1f, 0.3f, 1f));
    }
}
