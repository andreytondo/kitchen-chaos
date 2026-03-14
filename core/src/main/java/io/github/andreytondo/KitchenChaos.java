package io.github.andreytondo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import io.github.andreytondo.screen.GameScreen;

public class KitchenChaos extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
