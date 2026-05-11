package io.github.andreytondo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.screen.LoadingScreen;
import io.github.andreytondo.utils.Assets;

public class KitchenChaos extends Game {

    private AssetManager assets;

    @Override
    public void create() {
        assets = new AssetManager();
        assets.load(Assets.PLAYER,      Texture.class);
        assets.load(Assets.TOMATO,      Texture.class);
        assets.load(Assets.TOMATO_WALK, Texture.class);
        assets.load(Assets.TILE_FLOOR,  Texture.class);
        assets.load(Assets.SFX_DASH,    Sound.class);
        assets.load(Assets.SFX_HIT,     Sound.class);
        assets.load(Assets.SFX_DEATH,   Sound.class);
        assets.load(Assets.MUSIC,       Music.class);
        setScreen(new LoadingScreen(this, assets));
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
    }
}
