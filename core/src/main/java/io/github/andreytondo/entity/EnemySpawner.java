package io.github.andreytondo.entity;

import com.badlogic.gdx.utils.Pool;
import io.github.andreytondo.map.Room;

import java.util.List;

public class EnemySpawner {

    private final Pool<TomatoEnemy> tomatoPool;
    private final Pool<RangedEnemy> rangedPool;
    private final List<BaseActor>   activeEnemies;

    public EnemySpawner(Pool<TomatoEnemy> tomatoPool, Pool<RangedEnemy> rangedPool, List<BaseActor> activeEnemies) {
        this.tomatoPool    = tomatoPool;
        this.rangedPool    = rangedPool;
        this.activeEnemies = activeEnemies;
    }

    public void spawnRoom(Room room, Player player) {
        freeAll();
        for (Room.SpawnData data : room.getSpawnList()) {
            BaseActor enemy;
            if (data.type == Room.EnemyType.TOMATO) {
                TomatoEnemy t = tomatoPool.obtain();
                t.init(data.x, data.y);
                enemy = t;
            } else {
                RangedEnemy r = rangedPool.obtain();
                r.init(data.x, data.y);
                enemy = r;
            }
            activeEnemies.add(enemy);
        }
    }

    public void freeAll() {
        for (BaseActor e : activeEnemies) {
            if (e instanceof TomatoEnemy t) tomatoPool.free(t);
            else if (e instanceof RangedEnemy r) rangedPool.free(r);
        }
        activeEnemies.clear();
    }
}
