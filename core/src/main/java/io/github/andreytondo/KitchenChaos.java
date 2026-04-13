package io.github.andreytondo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.andreytondo.screen.MainMenuScreen;
import io.github.andreytondo.utils.Assets;

public class KitchenChaos extends Game {

    private AssetManager assets;

    @Override
    public void create() {
        assets = new AssetManager();
        assets.load(Assets.PLAYER, Texture.class);
        assets.load(Assets.TOMATO, Texture.class);
        assets.finishLoading();
        setScreen(new MainMenuScreen(this, assets));
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
    }
}
