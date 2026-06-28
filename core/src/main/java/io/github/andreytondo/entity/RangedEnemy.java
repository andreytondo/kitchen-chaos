package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.RangedKiteAI;
import io.github.andreytondo.component.Timer;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

public class RangedEnemy extends BaseActor implements Renderable, Pool.Poolable {

    private static final int   FRAME_COUNT    = 4;
    private static final int   FRAME_PX       = 64;
    private static final float FRAME_DURATION = 0.18f;
    private static final float HURT_DURATION  = 0.15f;

    private final EnemyBehavior   behavior;
    private final BaseActor       target;
    private final TextureRegion[] frames;
    private final Sound           deathSound;
    private final Timer           hurtFlashTimer = new Timer(HURT_DURATION);
    private float animTime = 0f;

    public RangedEnemy(float x, float y, BaseActor target, Texture walkSheet, Sound deathSound) {
        super(x, y, Constants.RANGED_SIZE, Constants.RANGED_SIZE, Constants.RANGED_SPEED, Constants.RANGED_HEALTH);
        Attack attack = new Attack(8f, Constants.RANGED_PREFERRED_DIST * 0.9f, 2.0f);
        this.behavior = new RangedKiteAI(attack, Constants.RANGED_PREFERRED_DIST);
        this.target = target;
        this.deathSound = deathSound;

        frames = new TextureRegion[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = new TextureRegion(walkSheet, i * FRAME_PX, 0, FRAME_PX, FRAME_PX);
        }
    }

    public void init(float x, float y) {
        position.set(x, y);
        reset();
    }

    @Override
    public void reset() {
        health.reset();
        animTime = 0f;
        facingDirection.set(1f, 0f);
        setActive(true);
        behavior.reset();
        hurtFlashTimer.reset();
    }

    @Override
    public void takeDamage(float amount) {
        boolean wasAlive = !isDead();
        getHealth().takeDamage(amount);
        if (wasAlive) {
            hurtFlashTimer.start();
            if (isDead()) deathSound.play(0.7f);
        }
    }

    @Override
    public void update(float delta) {
        if (isDead()) {
            setActive(false);
            return;
        }

        animTime += delta;
        hurtFlashTimer.update(delta);
        behavior.execute(target, this, delta);
    }

    @Override
    public void render(GameRenderer renderer) {
        if (hurtFlashTimer.isRunning()) {
            renderer.getBatch().setColor(0.3f, 1f, 0.3f, 1f);
        }
        int frame = (int)(animTime / FRAME_DURATION) % FRAME_COUNT;
        renderer.getBatch().draw(frames[frame], getX(), getY(), getWidth(), getHeight());
        renderer.getBatch().setColor(1f, 1f, 1f, 1f);
    }
}
