package io.github.andreytondo.system;

import com.badlogic.gdx.math.Rectangle;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.Player;

import java.util.List;

public class CollisionSystem {

    public void resolve(Player player, List<BaseActor> enemies, List<Rectangle> walls) {
        for (BaseActor enemy : enemies) {
            if (player.isActive() && enemy.isActive()) pushApart(player, enemy);
        }
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                BaseActor a = enemies.get(i), b = enemies.get(j);
                if (a.isActive() && b.isActive()) pushApart(a, b);
            }
        }
        if (player.isActive()) pushActorOutOfWalls(player, walls);
        for (BaseActor e : enemies) {
            if (e.isActive()) pushActorOutOfWalls(e, walls);
        }
    }

    private static void pushApart(BaseActor a, BaseActor b) {
        float ax = a.getX() + a.getWidth()  / 2f;
        float ay = a.getY() + a.getHeight() / 2f;
        float bx = b.getX() + b.getWidth()  / 2f;
        float by = b.getY() + b.getHeight() / 2f;

        float overlapX = (a.getWidth()  / 2f + b.getWidth()  / 2f) - Math.abs(ax - bx);
        float overlapY = (a.getHeight() / 2f + b.getHeight() / 2f) - Math.abs(ay - by);

        if (overlapX <= 0 || overlapY <= 0) return;

        if (overlapX < overlapY) {
            if (ax < bx) b.getPosition().x += overlapX;
            else         b.getPosition().x -= overlapX;
        } else {
            if (ay < by) b.getPosition().y += overlapY;
            else         b.getPosition().y -= overlapY;
        }
    }

    private static void pushActorOutOfWalls(BaseActor actor, List<Rectangle> walls) {
        float ax = actor.getX();
        float ay = actor.getY();
        float aw = actor.getWidth();
        float ah = actor.getHeight();

        for (Rectangle wall : walls) {
            float overlapX = Math.min(ax + aw, wall.x + wall.width)  - Math.max(ax, wall.x);
            float overlapY = Math.min(ay + ah, wall.y + wall.height) - Math.max(ay, wall.y);

            if (overlapX <= 0 || overlapY <= 0) continue;

            if (overlapX < overlapY) {
                float actorCX = ax + aw / 2f;
                float wallCX  = wall.x + wall.width / 2f;
                if (actorCX < wallCX) actor.getPosition().x -= overlapX;
                else                  actor.getPosition().x += overlapX;
            } else {
                float actorCY = ay + ah / 2f;
                float wallCY  = wall.y + wall.height / 2f;
                if (actorCY < wallCY) actor.getPosition().y -= overlapY;
                else                  actor.getPosition().y += overlapY;
            }

            ax = actor.getX();
            ay = actor.getY();
        }
    }
}
