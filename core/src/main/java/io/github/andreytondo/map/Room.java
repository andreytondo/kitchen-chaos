package io.github.andreytondo.map;

import com.badlogic.gdx.math.Rectangle;
import io.github.andreytondo.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room {

    public enum EnemyType { TOMATO, RANGED }

    public static class SpawnData {
        public final EnemyType type;
        public final float x;
        public final float y;

        SpawnData(EnemyType type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }

    private final boolean[][] wallGrid;
    private final List<Rectangle> wallRects;
    private final List<SpawnData> spawnList = new ArrayList<>();
    private final Rectangle exitDoor;
    private final Rectangle entryDoor;
    private boolean completed = false;

    Room(boolean hasEntryDoor, boolean hasExitDoor) {
        int cols = Constants.ROOM_COLS;
        int rows = Constants.ROOM_ROWS;
        float t = Constants.TILE_SIZE;

        wallGrid = new boolean[cols][rows];
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                wallGrid[c][r] = (c == 0 || c == cols - 1 || r == 0 || r == rows - 1);
            }
        }

        // Cut door openings: 2-tile gap centered on each wall
        int doorRow = rows / 2 - 1;  // rows 4 and 5 for a 10-row room
        if (hasExitDoor) {
            wallGrid[cols - 1][doorRow]     = false;
            wallGrid[cols - 1][doorRow + 1] = false;
        }
        if (hasEntryDoor) {
            wallGrid[0][doorRow]     = false;
            wallGrid[0][doorRow + 1] = false;
        }

        // Pre-build wall rects (one per wall cell)
        wallRects = new ArrayList<>();
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (wallGrid[c][r]) {
                    wallRects.add(new Rectangle(c * t, r * t, t, t));
                }
            }
        }

        exitDoor  = hasExitDoor  ? new Rectangle((cols - 1) * t, doorRow * t, t, 2 * t) : null;
        entryDoor = hasEntryDoor ? new Rectangle(0,              doorRow * t, t, 2 * t) : null;
    }

    void addSpawn(EnemyType type, float x, float y) {
        spawnList.add(new SpawnData(type, x, y));
    }

    public boolean isWall(int col, int row) {
        return wallGrid[col][row];
    }

    public boolean[][] getWallGrid() {
        return wallGrid;
    }

    public List<Rectangle> getWallRects() {
        return Collections.unmodifiableList(wallRects);
    }

    public List<SpawnData> getSpawnList() {
        return Collections.unmodifiableList(spawnList);
    }

    public Rectangle getExitDoor() {
        return exitDoor;
    }

    public Rectangle getEntryDoor() {
        return entryDoor;
    }

    public boolean isExitOpen() {
        return completed && exitDoor != null;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        completed = true;
    }
}
