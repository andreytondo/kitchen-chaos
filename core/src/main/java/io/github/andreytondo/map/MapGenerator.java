package io.github.andreytondo.map;

import io.github.andreytondo.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    // Interior spawn area: stay away from walls and their adjacent tiles
    private static final int SPAWN_COL_MIN = 2;
    private static final int SPAWN_COL_MAX = Constants.ROOM_COLS - 3;
    private static final int SPAWN_ROW_MIN = 2;
    private static final int SPAWN_ROW_MAX = Constants.ROOM_ROWS - 3;

    public List<Room> generate(int numRooms, Random rng) {
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            boolean hasEntry = i > 0;
            boolean hasExit  = i < numRooms - 1;
            Room room = new Room(hasEntry, hasExit);
            populateEnemies(room, i, numRooms, rng);
            rooms.add(room);
        }
        return rooms;
    }

    private void populateEnemies(Room room, int roomIndex, int totalRooms, Random rng) {
        int baseCount = 3 + roomIndex;
        boolean isLastRoom = roomIndex == totalRooms - 1;
        if (isLastRoom) baseCount += 2;

        int tomatoCount  = baseCount;
        int rangedCount  = 0;

        if (roomIndex > 0) {
            rangedCount = 1 + (roomIndex / 2);
            tomatoCount = baseCount - rangedCount;
            if (tomatoCount < 1) tomatoCount = 1;
        }
        if (isLastRoom && rangedCount == 0) rangedCount = 1;

        for (int i = 0; i < tomatoCount; i++) {
            room.addSpawn(Room.EnemyType.TOMATO, randomX(rng), randomY(rng));
        }
        for (int i = 0; i < rangedCount; i++) {
            room.addSpawn(Room.EnemyType.RANGED, randomX(rng), randomY(rng));
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
