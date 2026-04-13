package io.github.andreytondo.entity;

import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.MeleeChaseAI;
import io.github.andreytondo.contract.EnemyBehavior;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

public class TomatoEnemy extends BaseActor implements Renderable {
    private final EnemyBehavior behavior;
    private final BaseActor target;
    private final Texture texture;

    public TomatoEnemy(float x, float y, BaseActor target, Texture texture) {
        super(x, y, Constants.TOMATO_SIZE, Constants.TOMATO_SIZE, Constants.TOMATO_SPEED, Constants.TOMATO_HEALTH);
        Attack attack = new Attack(10f, Constants.TOMATO_SIZE, 2f);
        this.behavior = new MeleeChaseAI(attack);
        this.target = target;
        this.texture = texture;
    }

    @Override
    public void update(float delta) {
        if (isDead()) {
            setActive(false);
            return;
        }

        behavior.execute(target, this, delta);
        clampToWorld();
    }

    @Override
    public void render(GameRenderer renderer) {
        renderer.getBatch().draw(texture, getX(), getY(), getWidth(), getHeight());
    }

}
