package io.github.andreytondo.utils;

public class Constants {
    public static final float WORLD_WIDTH = 1920f;
    public static final float WORLD_HEIGHT = 1080f;

    public static final float PLAYER_SIZE = WORLD_WIDTH * 0.083f;  // ~160px (2.5x sprite size)
    public static final float PLAYER_SPEED = WORLD_WIDTH * 0.172f;  // ~220px/s at 1280
    public static final float PLAYER_HEALTH = 1000F;
    public static final float PLAYER_DASH_MULTIPLIER = 3.5f;
    public static final float PLAYER_DASH_DURATION = 0.2f;   // segundos
    public static final float PLAYER_DASH_COOLDOWN = 1.0f;   // segundos
    public static final float PLAYER_ATTACK_DAMAGE = 25f;
    public static final float PLAYER_ATTACK_RANGE = PLAYER_SIZE * 2f;
    public static final float PLAYER_ATTACK_COOLDOWN = 0.5f;

    public static final float TOMATO_SIZE = WORLD_WIDTH * 0.075f;  // ~96px at 1920
    public static final float TOMATO_SPEED = WORLD_WIDTH * 0.172f;  // ~220px/s at 1280
    public static final float TOMATO_HEALTH = 100F;

    public static final float TILE_SIZE = 128f;

    public static final int   ROOM_COLS             = 15;
    public static final int   ROOM_ROWS             = 10;
    public static final float ROOM_WIDTH            = ROOM_COLS * TILE_SIZE;
    public static final float ROOM_HEIGHT           = ROOM_ROWS * TILE_SIZE;

    public static final float RANGED_SIZE           = WORLD_WIDTH * 0.065f;
    public static final float RANGED_SPEED          = WORLD_WIDTH * 0.10f;
    public static final float RANGED_HEALTH         = 60f;
    public static final float RANGED_PREFERRED_DIST = ROOM_WIDTH * 0.25f;

    // Chicken (Frango Zumbi) — slow tank
    public static final float CHICKEN_SIZE   = TILE_SIZE * 1.1f;
    public static final float CHICKEN_SPEED  = WORLD_WIDTH * 0.07f;
    public static final float CHICKEN_HEALTH = 300f;

    // Broccoli (Brócolis Atirador) — stationary ranged shooter
    public static final float BROCCOLI_SIZE   = TILE_SIZE * 0.8f;
    public static final float BROCCOLI_HEALTH = 80f;

    // Onion (Cebola Explosiva) — fast suicide bomber
    public static final float ONION_SIZE             = WORLD_WIDTH * 0.055f;
    public static final float ONION_SPEED            = WORLD_WIDTH * 0.22f;
    public static final float ONION_HEALTH           = 60f;
    public static final float ONION_EXPLOSION_RADIUS = TILE_SIZE * 1.2f;
    public static final float ONION_EXPLOSION_DAMAGE = 40f;

    // Shrimp (Camarão Veloz) — tiny fast swarm
    public static final float SHRIMP_SIZE   = WORLD_WIDTH * 0.04f;
    public static final float SHRIMP_SPEED  = WORLD_WIDTH * 0.25f;
    public static final float SHRIMP_HEALTH = 30f;

    // Boss (Chef Tomate Bravão)
    public static final float BOSS_SIZE   = TILE_SIZE * 2f;
    public static final float BOSS_SPEED  = WORLD_WIDTH * 0.09f;
    public static final float BOSS_HEALTH = 500f;

    // Projectile
    public static final float PROJECTILE_SPEED = WORLD_WIDTH * 0.37f;
}
