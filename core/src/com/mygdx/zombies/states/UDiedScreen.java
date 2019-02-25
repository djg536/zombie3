package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.zombies.Zombies;

public class UDiedScreen extends State {

    // New class for assesment 3

    private static Level level;

    /**
     * The constructor for the screen
     */
    public UDiedScreen(Level level){
        super();
        this.level = level;
    }

    @Override
    public void update() {
        // If mouse button is pressed, return to the level
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
            StateManager.loadState(new Level(level.getPath(), level.getSpawnEntryID()));
        }
    }

    @Override
    public void render(){
        UIBatch.begin();
        Zombies.mainFont.draw(UIBatch, "You died", Zombies.InitialWindowWidth/2-100, Zombies.InitialWindowHeight/2);
        Zombies.mainFont.draw(UIBatch, "[click to continue]", Zombies.InitialWindowWidth/2-230, Zombies.InitialWindowHeight/2-100);
        UIBatch.end();
    }
}
