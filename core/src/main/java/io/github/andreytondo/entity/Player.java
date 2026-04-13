package io.github.andreytondo.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.component.Attack;
import io.github.andreytondo.component.Dash;
import io.github.andreytondo.component.PlayerInput;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

public class Player extends BaseActor implements Renderable {

    private final PlayerInput input;
    private final Dash dash;
    private final Attack attack;
    private final Texture texture;

    public Player(float x, float y, Texture texture) {
        super(x, y, Constants.PLAYER_SIZE, Constants.PLAYER_SIZE, Constants.PLAYER_SPEED, Constants.PLAYER_HEALTH);
        this.input = new PlayerInput();
        this.dash = new Dash(Constants.PLAYER_DASH_DURATION, Constants.PLAYER_DASH_COOLDOWN, Constants.PLAYER_DASH_MULTIPLIER);
        this.attack = new Attack(Constants.PLAYER_ATTACK_DAMAGE, Constants.PLAYER_ATTACK_RANGE, Constants.PLAYER_ATTACK_COOLDOWN);
        this.texture = texture;
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

    @Override
    public void render(GameRenderer renderer) {
        renderer.getBatch().draw(texture, getX(), getY(), getWidth(), getHeight());
    }

}
