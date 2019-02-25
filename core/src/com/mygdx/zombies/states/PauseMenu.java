package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Zombies;
import static com.mygdx.zombies.Zombies.soundSelect;

public class PauseMenu extends State {

    // Added for assessment 3

    private Texture background;
    private Texture logo;
    private Button exit;
    private Button resume;
    private Level level;


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
    public void update() {
        //Code to handle button click events
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
            if (resume.isHover()) {
                soundSelect.play();
                super.dispose();
                level.resumeGame();
            }
            else if (exit.isHover()) {
                level.resumeGame();
                soundSelect.play();
                //Return to Main Menu
                super.dispose();
                Zombies.soundAmbientWind.stop();

                Player.health = null;
                Player.weapon = null;
                Player.counter = null;

                StateManager.loadState(StateManager.StateID.MAINMENU);
            }
        }
    }

    @Override
    public void dispose(){
        background.dispose();
        logo.dispose();
        resume.dispose();
        exit.dispose();
    }
}
