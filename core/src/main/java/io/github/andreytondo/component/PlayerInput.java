package io.github.andreytondo.component;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class PlayerInput extends InputAdapter {

    private boolean up, down, left, right;
    private boolean mouseAttack;
    private boolean dashPending;
    private int     scrollDelta;

    private final Vector2 direction = new Vector2();

    // ── Keyboard ─────────────────────────────────────────────────────────────

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W: case Input.Keys.UP:    up    = true;  break;
            case Input.Keys.S: case Input.Keys.DOWN:  down  = true;  break;
            case Input.Keys.A: case Input.Keys.LEFT:  left  = true;  break;
            case Input.Keys.D: case Input.Keys.RIGHT: right = true;  break;
            case Input.Keys.SHIFT_LEFT: case Input.Keys.SHIFT_RIGHT:
                dashPending = true; return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W: case Input.Keys.UP:    up    = false; break;
            case Input.Keys.S: case Input.Keys.DOWN:  down  = false; break;
            case Input.Keys.A: case Input.Keys.LEFT:  left  = false; break;
            case Input.Keys.D: case Input.Keys.RIGHT: right = false; break;
        }
        return false;
    }

    // ── Mouse ─────────────────────────────────────────────────────────────────

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) { mouseAttack = true; return true; }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) { mouseAttack = false; return true; }
        return false;
    }

    /** Scroll down = positive; scroll up = negative. */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrollDelta += (int) Math.signum(amountY);
        return true;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public Vector2 getMovementDirection() {
        direction.set(0, 0);
        if (up)    direction.y += 1f;
        if (down)  direction.y -= 1f;
        if (left)  direction.x -= 1f;
        if (right) direction.x += 1f;
        return direction;
    }

    /** Returns true once per SHIFT press. */
    public boolean isDashPressed() {
        if (dashPending) { dashPending = false; return true; }
        return false;
    }

    public boolean isMouseAttackPressed() { return mouseAttack; }

    /** Returns accumulated scroll delta and resets it to 0. */
    public int consumeScrollDelta() {
        int d = scrollDelta;
        scrollDelta = 0;
        return d;
    }

    public void reset() {
        up = down = left = right = mouseAttack = dashPending = false;
        scrollDelta = 0;
    }
}
