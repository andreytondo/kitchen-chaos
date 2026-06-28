package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.MeleeChaseAI;
import io.github.andreytondo.utils.Constants;

public class TomatoEnemy extends EnemyBase {

    public TomatoEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound) {
        super(x, y,
              Constants.TOMATO_SIZE, Constants.TOMATO_SIZE,
              Constants.TOMATO_SPEED, Constants.TOMATO_HEALTH,
              walkSheet, deathSound,
              new MeleeChaseAI(new Attack(10f, Constants.TOMATO_SIZE, 1.0f)), target,
              0.12f, new Color(1f, 0.3f, 0.3f, 1f), Color.WHITE);
    }
}
