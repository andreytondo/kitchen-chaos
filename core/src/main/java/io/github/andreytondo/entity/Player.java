package io.github.andreytondo.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.Dash;
import io.github.andreytondo.component.PlayerInput;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;
import lombok.Getter;

public class Player extends BaseActor implements Renderable {

    private final PlayerInput input;
    private final Dash dash;
    private final Attack attack;

    public Player(float x, float y) {
        super(x, y, Constants.PLAYER_SIZE, Constants.PLAYER_SIZE, Constants.PLAYER_SPEED, Constants.PLAYER_HEALTH);
        this.input = new PlayerInput();
        this.dash = new Dash(Constants.PLAYER_DASH_DURATION, Constants.PLAYER_DASH_COOLDOWN, Constants.PLAYER_DASH_MULTIPLIER);
        this.attack = new Attack(Constants.PLAYER_ATTACK_DAMAGE, Constants.PLAYER_ATTACK_RANGE, Constants.PLAYER_ATTACK_COOLDOWN);
    }

    @Override
    public void update(float delta) {
        if (isDead()) {
            setActive(false);
            return;
        }

        if (input.isDashPressed()) {
            dash.tryActivate();
        }
        dash.update(delta);
        attack.update(delta);

        Vector2 direction = input.getMovementDirection();
        move(direction, dash.applyTo(speed), delta);

        clampToWorld();
    }

    public boolean tryAttack(BaseActor target) {
        if (!input.isAttackPressed()) return false;
        return attack.tryAttack(target, this);
    }

    private void clampToWorld() {
        position.x = MathUtils.clamp(position.x, 0f, Constants.WORLD_WIDTH - width);
        position.y = MathUtils.clamp(position.y, 0f, Constants.WORLD_HEIGHT - height);
    }

    @Override
    public void render(GameRenderer renderer) {
        renderer.getShapeRenderer().rect(getX(), getY(), getWidth(), getHeight());
    }
}
