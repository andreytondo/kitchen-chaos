package io.github.andreytondo.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.map.Room;
import io.github.andreytondo.system.ProjectileSystem;

import java.util.List;

public class EnemySpawner {

    private final Pool<TomatoEnemy>  tomatoPool;
    private final Pool<RangedEnemy>  rangedPool;
    private final List<BaseActor>    activeEnemies;
    private final BaseActor          player;
    private final Texture            walkSheet;
    private final Sound              deathSound;
    private final ProjectileSystem   projectileSystem;

    public EnemySpawner(Pool<TomatoEnemy> tomatoPool, Pool<RangedEnemy> rangedPool,
                        List<BaseActor> activeEnemies, BaseActor player,
                        Texture walkSheet, Sound deathSound,
                        ProjectileSystem projectileSystem) {
        this.tomatoPool      = tomatoPool;
        this.rangedPool      = rangedPool;
        this.activeEnemies   = activeEnemies;
        this.player          = player;
        this.walkSheet       = walkSheet;
        this.deathSound      = deathSound;
        this.projectileSystem = projectileSystem;
    }

    public void spawnRoom(Room room) {
        freeAll();
        for (Room.SpawnData data : room.getSpawnList()) {
            BaseActor enemy = create(data);
            if (enemy != null) activeEnemies.add(enemy);
        }
    }

    private BaseActor create(Room.SpawnData data) {
        return switch (data.type) {
            case TOMATO -> {
                TomatoEnemy t = tomatoPool.obtain();
                t.init(data.x, data.y);
                yield t;
            }
            case RANGED -> {
                RangedEnemy r = rangedPool.obtain();
                r.init(data.x, data.y);
                yield r;
            }
            case CHICKEN  -> new ChickenEnemy (data.x, data.y, player, walkSheet, deathSound);
            case BROCCOLI -> new BroccoliEnemy(data.x, data.y, player, walkSheet, deathSound, projectileSystem);
            case ONION    -> new OnionEnemy   (data.x, data.y, player, walkSheet, deathSound);
            case SHRIMP   -> new ShrimpEnemy  (data.x, data.y, player, walkSheet, deathSound);
            case BOSS     -> new BossEnemy    (data.x, data.y, player, walkSheet, deathSound, projectileSystem);
        };
    }

    public void freeAll() {
        for (BaseActor e : activeEnemies) {
            if (e instanceof TomatoEnemy t) tomatoPool.free(t);
            else if (e instanceof RangedEnemy r) rangedPool.free(r);
            // Other enemies are not pooled — GC handles them
        }
        activeEnemies.clear();
    }
}
