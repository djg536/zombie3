package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.zombies.Zombies;

public class UDiedScreen extends State {

    // New class for assesment 3

    private Level level;

    /**
     * The constructor for the screen
     */
    UDiedScreen(Level level){
        super();
        this.level = level;
    }

    @Override
    public void update(float delta) {
        // If mouse button is pressed, return to the level
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
            StateManager.loadState(new Level(level.getPath(), level.getSpawnEntryID()));
        }
    }

    @Override
    public void render(){
        UIBatch.begin();
        //#changed4 Text positioning now utilises constants rather than e.g. Gdx.graphics.getWidth(), which has now fixed scaling
        Zombies.mainFont.draw(UIBatch, "You died", Zombies.InitialWindowWidth/2.f-100, Zombies.InitialWindowHeight/2.f);
        Zombies.mainFont.draw(UIBatch, "[click to continue]", Zombies.InitialWindowWidth/2.f-230, Zombies.InitialWindowHeight/2.f-100);
        UIBatch.end();
    }
}
