package io.github.andreytondo.component;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class PlayerInput extends InputAdapter {

    private boolean up, down, left, right, attack;
    private boolean dashPending;
    private final Vector2 direction = new Vector2();

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:           case Input.Keys.UP:    up    = true; break;
            case Input.Keys.S:           case Input.Keys.DOWN:  down  = true; break;
            case Input.Keys.A:           case Input.Keys.LEFT:  left  = true; break;
            case Input.Keys.D:           case Input.Keys.RIGHT: right = true; break;
            case Input.Keys.SPACE:                              attack = true; break;
            case Input.Keys.SHIFT_LEFT:  case Input.Keys.SHIFT_RIGHT: dashPending = true; return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W:    case Input.Keys.UP:    up    = false; break;
            case Input.Keys.S:    case Input.Keys.DOWN:  down  = false; break;
            case Input.Keys.A:    case Input.Keys.LEFT:  left  = false; break;
            case Input.Keys.D:    case Input.Keys.RIGHT: right = false; break;
            case Input.Keys.SPACE:                       attack = false; break;
        }
        return false;
    }

    public Vector2 getMovementDirection() {
        direction.set(0, 0);
        if (up)    direction.y += 1f;
        if (down)  direction.y -= 1f;
        if (left)  direction.x -= 1f;
        if (right) direction.x += 1f;
        return direction;
    }

    /** Returns true once per SHIFT press, then false until pressed again. */
    public boolean isDashPressed() {
        if (dashPending) {
            dashPending = false;
            return true;
        }
        return false;
    }

    public boolean isAttackPressed() {
        return attack;
    }

    public void reset() {
        up = down = left = right = attack = dashPending = false;
    }
}
