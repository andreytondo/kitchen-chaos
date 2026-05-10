package io.github.andreytondo.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.MeleeChaseAI;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

public class TomatoEnemy extends BaseActor implements Renderable, Pool.Poolable {

    private static final int   FRAME_COUNT    = 4;
    private static final int   FRAME_PX       = 64;
    private static final float FRAME_DURATION = 0.12f;

    private final EnemyBehavior   behavior;
    private final BaseActor       target;
    private final TextureRegion[] frames;
    private float animTime = 0f;

    public TomatoEnemy(float x, float y, BaseActor target, Texture walkSheet) {
        super(x, y, Constants.TOMATO_SIZE, Constants.TOMATO_SIZE, Constants.TOMATO_SPEED, Constants.TOMATO_HEALTH);
        this.behavior = new MeleeChaseAI(new Attack(10f, Constants.TOMATO_SIZE, 2f));
        this.target = target;

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
    }

    @Override
    public void update(float delta) {
        if (isDead()) {
            setActive(false);
            return;
        }

        animTime += delta;
        behavior.execute(target, this, delta);
        clampToWorld();
    }

    @Override
    public void render(GameRenderer renderer) {
        int frame = (int)(animTime / FRAME_DURATION) % FRAME_COUNT;
        renderer.getBatch().draw(frames[frame], getX(), getY(), getWidth(), getHeight());
    }
}
