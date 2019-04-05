package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.entities.Player;
import com.mygdx.zombies.Zombies;
import static com.mygdx.zombies.Zombies.soundSelect;

public class PauseMenu extends State {

    // Added for assessment 3

    private Texture background;
    private Texture logo;
    private Button exit;
    private Button resume;
    private Level level;


    /**
     * Constructor for the pause menu
     * @param level - a Level instance
     * #changed4 added the level parameter
     */
    PauseMenu(Level level){
        super();

        this.level = level;

        background = new Texture("background.jpg");
        logo = new Texture("logo.png");

        resume = new Button(UIBatch, 500, 350, "Resume");
        exit = new Button(UIBatch, 500, 150, "Exit");
    }

    @Override
    public void render(){
        UIBatch.begin();
        UIBatch.draw(background, 0, 0);

        resume.render();
        exit.render();
        Zombies.titleFont.draw(UIBatch, "Silence Of The Lamberts", 225, 650);
        UIBatch.draw(logo, 1050, 10);
        UIBatch.end();
    }

    @Override
    public void update(float delta) {
        //Code to handle button click events
        //#changed4 removed super.dispose() calls to fix crashing
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
            if (resume.isHover()) {
                soundSelect.play();
                level.resumeGame();
            }
            else if (exit.isHover()) {
                level.resumeGame();
                soundSelect.play();
                //Return to Main Menu
                Zombies.soundAmbientWind.stop();

                Player.health = null;
                Player.weapon = null;
                Player.setCounter(null);

                StateManager.loadState(StateManager.StateID.MAINMENU, aliveNPC);
            }
        }
    }

    @Override
    public void dispose(){

        //#changed4 added the following three lines
        super.dispose();
        background.dispose();
        logo.dispose();

        resume.dispose();
        exit.dispose();
    }
}
