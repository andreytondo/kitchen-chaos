package io.github.andreytondo.utils;

public class Constants {
    public static final float WORLD_WIDTH = 1920f;
    public static final float WORLD_HEIGHT = 1080f;

    public static final float PLAYER_SIZE             = WORLD_WIDTH * 0.025f;  // ~32px at 1280
    public static final float PLAYER_SPEED            = WORLD_WIDTH * 0.172f;  // ~220px/s at 1280
    public static final float PLAYER_HEALTH           = 100F;
    public static final float PLAYER_DASH_MULTIPLIER  = 3.5f;
    public static final float PLAYER_DASH_DURATION    = 0.2f;   // segundos
    public static final float PLAYER_DASH_COOLDOWN    = 1.0f;   // segundos
}
