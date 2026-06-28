package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.andreytondo.component.Dash;
import io.github.andreytondo.component.PlayerInput;
import io.github.andreytondo.component.WeaponType;
import io.github.andreytondo.contract.Renderable;
import io.github.andreytondo.utils.Constants;
import io.github.andreytondo.utils.GameRenderer;

public class Player extends BaseActor implements Renderable {

    private final PlayerInput input;
    private final Dash        dash;
    private final Texture     texture;
    private final Sound       hitSound;

    private WeaponType    currentWeapon  = WeaponType.FRYING_PAN;
    private float         attackCooldown = 0f;
    private final Vector2 aimDirection   = new Vector2(1f, 0f);

    public Player(float x, float y, Texture texture, Sound dashSound, Sound hitSound) {
        super(x, y, Constants.PLAYER_SIZE, Constants.PLAYER_SIZE,
              Constants.PLAYER_SPEED, Constants.PLAYER_HEALTH);
        this.input   = new PlayerInput();
        this.dash    = new Dash(Constants.PLAYER_DASH_DURATION, Constants.PLAYER_DASH_COOLDOWN,
                                 Constants.PLAYER_DASH_MULTIPLIER, dashSound);
        this.texture = texture;
        this.hitSound = hitSound;
    }

    @Override
    public void update(float delta) {
        if (isDead()) { setActive(false); return; }

        if (input.isDashPressed()) dash.tryActivate();
        dash.update(delta);

        if (attackCooldown > 0f) attackCooldown = Math.max(0f, attackCooldown - delta);

        Vector2 dir = input.getMovementDirection();
        move(dir, dash.applyTo(speed), delta);
    }

    // ── Aim ──────────────────────────────────────────────────────────────────

    /** Updates aim direction toward the given world-space point each frame. */
    public void setAimWorldPos(float wx, float wy) {
        float cx = getX() + getWidth()  / 2f;
        float cy = getY() + getHeight() / 2f;
        float dx = wx - cx, dy = wy - cy;
        if (dx != 0f || dy != 0f) {
            aimDirection.set(dx, dy).nor();
            facingDirection.set(aimDirection);
        }
    }

    public Vector2 getAimDirection() { return aimDirection; }

    // ── Attack ────────────────────────────────────────────────────────────────

    public boolean wantsToAttack()      { return input.isMouseAttackPressed(); }
    public boolean canAttack()          { return attackCooldown <= 0f; }
    public void triggerAttackCooldown() { attackCooldown = currentWeapon.cooldown; }

    /** Centre-to-centre distance check for melee weapons. */
    public boolean isInMeleeRange(BaseActor target, float range) {
        float px = getX() + getWidth()  / 2f;
        float py = getY() + getHeight() / 2f;
        float tx = target.getX() + target.getWidth()  / 2f;
        float ty = target.getY() + target.getHeight() / 2f;
        float dx = px - tx, dy = py - ty;
        return (dx * dx + dy * dy) <= (range * range);
    }

    public void playHitSound() { if (hitSound != null) hitSound.play(0.6f); }

    // ── Weapon ───────────────────────────────────────────────────────────────

    public WeaponType getCurrentWeapon() { return currentWeapon; }

    /** Positive delta = scroll down (next weapon); negative = scroll up (prev). */
    public void cycleWeapon(int delta) {
        if      (delta > 0) currentWeapon = currentWeapon.next();
        else if (delta < 0) currentWeapon = currentWeapon.prev();
    }

    // ── Misc ─────────────────────────────────────────────────────────────────

    public PlayerInput getInputProcessor() { return input; }

    @Override
    public void render(GameRenderer renderer) {
        renderer.getBatch().draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
