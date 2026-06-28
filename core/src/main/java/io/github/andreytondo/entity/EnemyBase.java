package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.component.Timer;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.GameRenderer;

public abstract class EnemyBase extends BaseActor implements Renderable, Pool.Poolable {

    private static final int   FRAME_COUNT   = 4;
    private static final int   FRAME_PX      = 64;
    private static final float HURT_DURATION = 0.15f;

    private final EnemyBehavior   behavior;
    private final BaseActor       target;
    private final TextureRegion[] frames;
    private final Sound           deathSound;
    private final Timer           hurtFlashTimer = new Timer(HURT_DURATION);
    private final Color           hurtColor;
    private final Color           baseColor;
    private final float           frameDuration;
    private float animTime = 0f;

    protected EnemyBase(float x, float y, float width, float height, float speed, float maxHealth,
                        Texture walkSheet, Sound deathSound,
                        EnemyBehavior behavior, BaseActor target,
                        float frameDuration, Color hurtColor, Color baseColor) {
        super(x, y, width, height, speed, maxHealth);
        this.behavior      = behavior;
        this.target        = target;
        this.deathSound    = deathSound;
        this.frameDuration = frameDuration;
        this.hurtColor     = hurtColor;
        this.baseColor     = baseColor;

        frames = new TextureRegion[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = new TextureRegion(walkSheet, i * FRAME_PX, 0, FRAME_PX, FRAME_PX);
        }
        // Sprite frames have empty padding; shrink hitbox to match visible character
        hitboxScale = 0.6f;
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
        renderer.getBatch().setColor(hurtFlashTimer.isRunning() ? hurtColor : baseColor);
        int frame = (int)(animTime / frameDuration) % FRAME_COUNT;
        renderer.getBatch().draw(frames[frame], getX(), getY(), getWidth(), getHeight());
        renderer.getBatch().setColor(1f, 1f, 1f, 1f);
    }
}
