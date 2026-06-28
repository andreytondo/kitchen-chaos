package io.github.andreytondo.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.entity.Projectile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileSystem {

    private final List<Projectile> pool   = new ArrayList<>();
    private final List<Projectile> active = new ArrayList<>();

    private static final Color PLAYER_COLOR = new Color(1f, 1f, 0f, 1f);
    private static final Color ENEMY_COLOR  = new Color(1f, 0.4f, 0f, 1f);

    public void fire(float x, float y, float dirX, float dirY,
                     float speed, float damage, float maxRange, boolean fromPlayer) {
        Projectile p = pool.isEmpty() ? new Projectile() : pool.remove(pool.size() - 1);
        p.init(x, y, dirX, dirY, speed, damage, maxRange, fromPlayer);
        active.add(p);
    }

    public void update(float delta, Player player, List<BaseActor> enemies, List<Rectangle> walls) {
        // Reusable enemy bounds to avoid allocating per-frame
        Rectangle eb = new Rectangle();
        Rectangle pb = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        Iterator<Projectile> it = active.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update(delta);
            if (!p.isActive()) { recycle(p); it.remove(); continue; }

            Rectangle b = p.getBounds();

            // Wall collision
            for (Rectangle wall : walls) {
                if (b.overlaps(wall)) { p.deactivate(); break; }
            }
            if (!p.isActive()) { recycle(p); it.remove(); continue; }

            if (p.fromPlayer) {
                for (BaseActor enemy : enemies) {
                    if (!enemy.isActive()) continue;
                    eb.set(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                    if (b.overlaps(eb)) {
                        enemy.takeDamage(p.damage);
                        p.deactivate();
                        break;
                    }
                }
            } else if (player.isActive()) {
                pb.set(player.getX(), player.getY(), player.getWidth(), player.getHeight());
                if (b.overlaps(pb)) {
                    player.takeDamage(p.damage);
                    p.deactivate();
                }
            }

            if (!p.isActive()) { recycle(p); it.remove(); }
        }
    }

    /** Draw projectile circles. Must be called between sr.begin(Filled) / sr.end(). */
    public void renderFilled(ShapeRenderer sr) {
        float radius = Projectile.SIZE / 2f;
        for (Projectile p : active) {
            sr.setColor(p.fromPlayer ? PLAYER_COLOR : ENEMY_COLOR);
            sr.circle(p.position.x, p.position.y, radius, 8);
        }
    }

    /** Draw projectile outlines for debug. Must be called between sr.begin(Line) / sr.end(). */
    public void renderOutlines(ShapeRenderer sr) {
        sr.setColor(Color.WHITE);
        float half = Projectile.SIZE / 2f;
        for (Projectile p : active) {
            sr.rect(p.position.x - half, p.position.y - half, Projectile.SIZE, Projectile.SIZE);
        }
    }

    public void clear() {
        pool.addAll(active);
        active.clear();
    }

    public int activeCount() { return active.size(); }

    private void recycle(Projectile p) { pool.add(p); }
}
