package io.github.andreytondo.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import io.github.andreytondo.component.WeaponType;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.map.Room;

import java.util.List;

public class DebugRenderer {

    private boolean       enabled = false;
    private final BitmapFont font;
    private final Matrix4 screenMatrix = new Matrix4();

    public DebugRenderer() {
        font = new BitmapFont();
        font.setColor(Color.LIME);
    }

    public void toggle()       { enabled = !enabled; }
    public boolean isEnabled() { return enabled; }

    /**
     * Draw hitboxes. Must be called between sr.begin(Line) / sr.end().
     */
    public void drawHitboxes(ShapeRenderer sr, Player player, List<BaseActor> enemies,
                              List<Rectangle> walls, Room room) {
        if (!enabled) return;

        sr.setColor(0.45f, 0.45f, 0.45f, 0.7f);
        for (Rectangle w : walls) sr.rect(w.x, w.y, w.width, w.height);

        if (player.isActive()) {
            sr.setColor(Color.CYAN);
            sr.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        sr.setColor(Color.RED);
        for (BaseActor e : enemies) {
            if (e.isActive()) sr.rect(e.getX(), e.getY(), e.getWidth(), e.getHeight());
        }

        if (room.getExitDoor() != null) {
            sr.setColor(Color.GREEN);
            Rectangle d = room.getExitDoor();
            sr.rect(d.x, d.y, d.width, d.height);
        }
        if (room.getEntryDoor() != null) {
            sr.setColor(Color.BLUE);
            Rectangle d = room.getEntryDoor();
            sr.rect(d.x, d.y, d.width, d.height);
        }
    }

    /**
     * Draw projectile outlines. Must be called between sr.begin(Line) / sr.end().
     */
    public void drawProjectileHitboxes(ShapeRenderer sr, ProjectileSystem ps) {
        if (!enabled) return;
        ps.renderOutlines(sr);
    }

    /**
     * Draw debug text overlay. Must be called between batch.begin() / batch.end()
     * with a screen-space projection matrix.
     */
    public void drawDebugHud(SpriteBatch batch, Player player, List<BaseActor> enemies,
                              ProjectileSystem ps, WeaponType weapon, Room room) {
        if (!enabled) return;

        int alive = 0;
        for (BaseActor e : enemies) { if (e.isActive()) alive++; }

        float lineH = 18f;
        float x     = 10f;
        float y     = Gdx.graphics.getHeight() - 10f;

        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),                          x, y); y -= lineH;
        font.draw(batch, String.format("Pos: (%.0f, %.0f)", player.getX(), player.getY()),     x, y); y -= lineH;
        font.draw(batch, String.format("HP:  %.0f / %.0f",
                         player.getHealth().getCurrentHealth(),
                         player.getHealth().getMaxHealth()),                                     x, y); y -= lineH;
        font.draw(batch, "Weapon: " + weapon.displayName,                                       x, y); y -= lineH;
        font.draw(batch, "Enemies: " + alive + " / " + enemies.size(),                         x, y); y -= lineH;
        font.draw(batch, "Projectiles: " + ps.activeCount(),                                   x, y); y -= lineH;
        font.draw(batch, "Room: " + room.getType().name() + (room.isCompleted() ? " [done]" : ""), x, y); y -= lineH;
        font.draw(batch, "[F3] debug on",                                                       x, y);
    }

    /**
     * Always-visible HUD: weapon name + HP.
     * Must be called between batch.begin() / batch.end() with screen-space projection.
     */
    public void drawAlwaysHud(SpriteBatch batch, WeaponType weapon, Player player) {
        float x = 10f;
        float y = 16f + 18f; // bottom-left area
        font.setColor(Color.WHITE);
        font.draw(batch,
            String.format("[Scroll] %s  |  HP: %.0f / %.0f",
                weapon.displayName,
                player.getHealth().getCurrentHealth(),
                player.getHealth().getMaxHealth()),
            x, y);
        font.setColor(Color.LIME); // restore for debug pass
    }

    /** Returns an orthographic projection matrix covering the screen. */
    public Matrix4 getScreenMatrix() {
        screenMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return screenMatrix;
    }

    public void dispose() { font.dispose(); }
}
