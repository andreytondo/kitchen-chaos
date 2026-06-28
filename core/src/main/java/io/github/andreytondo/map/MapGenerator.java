package io.github.andreytondo.map;

import io.github.andreytondo.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    private static final int SPAWN_COL_MIN = 2;
    private static final int SPAWN_COL_MAX = Constants.ROOM_COLS - 3;
    private static final int SPAWN_ROW_MIN = 2;
    private static final int SPAWN_ROW_MAX = Constants.ROOM_ROWS - 3;

    public List<Room> generate(int numRooms, Random rng) {
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            boolean  hasEntry = i > 0;
            boolean  hasExit  = i < numRooms - 1;
            RoomType type     = roomTypeFor(i, numRooms);
            Room     room     = new Room(hasEntry, hasExit, type);
            if (type != RoomType.HEALING) populateEnemies(room, i, numRooms, rng, type);
            rooms.add(room);
        }
        return rooms;
    }

    // Layout: …COMBAT…, HEALING, BOSS
    private RoomType roomTypeFor(int index, int total) {
        if (index == total - 1)             return RoomType.BOSS;
        if (total >= 4 && index == total - 2) return RoomType.HEALING;
        return RoomType.COMBAT;
    }

    private void populateEnemies(Room room, int roomIndex, int totalRooms, Random rng, RoomType type) {
        if (type == RoomType.BOSS) {
            // Boss room: 1 boss in the centre + 2 shrimp escorts
            float cx = (Constants.ROOM_COLS / 2f - 1) * Constants.TILE_SIZE;
            float cy = (Constants.ROOM_ROWS / 2f - 1) * Constants.TILE_SIZE;
            room.addSpawn(Room.EnemyType.BOSS,   cx,                  cy);
            room.addSpawn(Room.EnemyType.SHRIMP, randomX(rng),        randomY(rng));
            room.addSpawn(Room.EnemyType.SHRIMP, randomX(rng),        randomY(rng));
            return;
        }

        // COMBAT rooms
        switch (roomIndex) {
            case 0 -> {
                // Tutorial room: tomatoes only
                room.addSpawn(Room.EnemyType.TOMATO, randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.TOMATO, randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.TOMATO, randomX(rng), randomY(rng));
            }
            case 1 -> {
                // Introduce new enemy types
                room.addSpawn(Room.EnemyType.TOMATO,   randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.TOMATO,   randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.RANGED,   randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.CHICKEN,  randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.ONION,    randomX(rng), randomY(rng));
            }
            default -> {
                // Harder mix for later combat rooms
                int base = 3 + roomIndex;
                for (int i = 0; i < base / 2; i++)
                    room.addSpawn(Room.EnemyType.TOMATO,   randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.RANGED,   randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.CHICKEN,  randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.BROCCOLI, randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.ONION,    randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.SHRIMP,   randomX(rng), randomY(rng));
                room.addSpawn(Room.EnemyType.SHRIMP,   randomX(rng), randomY(rng));
            }
        }
    }

    private float randomX(Random rng) {
        int col = SPAWN_COL_MIN + rng.nextInt(SPAWN_COL_MAX - SPAWN_COL_MIN + 1);
        return col * Constants.TILE_SIZE;
    }

    private float randomY(Random rng) {
        int row = SPAWN_ROW_MIN + rng.nextInt(SPAWN_ROW_MAX - SPAWN_ROW_MIN + 1);
        return row * Constants.TILE_SIZE;
    }
}
