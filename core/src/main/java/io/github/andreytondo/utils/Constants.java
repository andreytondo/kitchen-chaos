package io.github.andreytondo.utils;

public class Constants {
    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    // Player — expressed as fractions of world size so they scale with it
    public static final float PLAYER_SIZE  = WORLD_WIDTH * 0.025f;  // ~32px at 1280
    public static final float PLAYER_SPEED = WORLD_WIDTH * 0.172f;  // ~220px/s at 1280
}
