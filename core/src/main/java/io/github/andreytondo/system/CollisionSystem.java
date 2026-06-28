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
        // Two passes: second pass catches corners where first push creates a new overlap
        if (player.isActive()) { pushActorOutOfWalls(player, walls); pushActorOutOfWalls(player, walls); }
        for (BaseActor e : enemies) {
            if (e.isActive()) { pushActorOutOfWalls(e, walls); pushActorOutOfWalls(e, walls); }
        }
    }

    private static void pushApart(BaseActor a, BaseActor b) {
        float ax = a.getCollisionX() + a.getCollisionWidth()  / 2f;
        float ay = a.getCollisionY() + a.getCollisionHeight() / 2f;
        float bx = b.getCollisionX() + b.getCollisionWidth()  / 2f;
        float by = b.getCollisionY() + b.getCollisionHeight() / 2f;

        float overlapX = (a.getCollisionWidth()  / 2f + b.getCollisionWidth()  / 2f) - Math.abs(ax - bx);
        float overlapY = (a.getCollisionHeight() / 2f + b.getCollisionHeight() / 2f) - Math.abs(ay - by);

        if (overlapX <= 0 || overlapY <= 0) return;

        // Push both actors half the overlap each so neither gets shoved into a wall alone
        float halfX = overlapX / 2f;
        float halfY = overlapY / 2f;
        if (overlapX < overlapY) {
            if (ax < bx) { a.getPosition().x -= halfX; b.getPosition().x += halfX; }
            else          { a.getPosition().x += halfX; b.getPosition().x -= halfX; }
        } else {
            if (ay < by) { a.getPosition().y -= halfY; b.getPosition().y += halfY; }
            else          { a.getPosition().y += halfY; b.getPosition().y -= halfY; }
        }
    }

    private static void pushActorOutOfWalls(BaseActor actor, List<Rectangle> walls) {
        for (Rectangle wall : walls) {
            float ax = actor.getCollisionX();
            float ay = actor.getCollisionY();
            float aw = actor.getCollisionWidth();
            float ah = actor.getCollisionHeight();

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
        }
    }
}
