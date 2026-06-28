package io.github.andreytondo.system;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.entity.EnemySpawner;
import io.github.andreytondo.entity.Player;
import io.github.andreytondo.map.Room;
import io.github.andreytondo.map.RoomType;
import io.github.andreytondo.screen.GameOverScreen;
import io.github.andreytondo.utils.Constants;

import java.util.List;

public class RoomProgressionSystem {

    private final List<Room>   rooms;
    private final EnemySpawner spawner;
    private final Game         game;
    private final AssetManager assets;
    private int     currentIndex     = 0;
    private boolean transitionedFlag = false;

    public RoomProgressionSystem(List<Room> rooms, EnemySpawner spawner,
                                  Game game, AssetManager assets) {
        this.rooms   = rooms;
        this.spawner = spawner;
        this.game    = game;
        this.assets  = assets;
    }

    public void checkRoomComplete(List<BaseActor> enemies) {
        if (currentRoom().isCompleted()) return;
        for (BaseActor e : enemies) {
            if (e.isActive()) return;
        }
        currentRoom().markCompleted();
    }

    public void checkDoorTransition(Player player) {
        Room room = currentRoom();
        if (!room.isExitOpen()) return;

        float px = player.getX() + player.getWidth()  / 2f;
        float py = player.getY() + player.getHeight() / 2f;
        if (!room.getExitDoor().contains(px, py)) return;

        currentIndex++;
        if (currentIndex >= rooms.size()) {
            game.setScreen(new GameOverScreen(game, assets));
            return;
        }

        // Reposition player at the left-side entry point
        player.getPosition().set(
            Constants.TILE_SIZE * 1.5f,
            Constants.ROOM_HEIGHT / 2f - Constants.PLAYER_SIZE / 2f
        );

        spawner.spawnRoom(currentRoom());

        // Heal the player when entering a healing room
        if (currentRoom().getType() == RoomType.HEALING) {
            player.getHealth().heal(player.getHealth().getMaxHealth() * 0.3f);
        }

        transitionedFlag = true;
    }

    /** Returns true (and resets the flag) if a room transition just occurred. */
    public boolean consumeTransition() {
        boolean t = transitionedFlag;
        transitionedFlag = false;
        return t;
    }

    public Room currentRoom() { return rooms.get(currentIndex); }
}
