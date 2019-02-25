package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Zombies;

public class UWinScreen extends State {

    // New class for assessment 3

    private int score;
    Player player;
    private float pointsfromtime;
    private float totalpoints;

    public UWinScreen(int score){
        super();
        this.score = score;
        Zombies.soundAmbientWind.stop();
        Zombies.soundEndMusic.loop();

        //*Code for Assessment 3
        //Calculates Player points
        if (player.counter >= 600){
            pointsfromtime = 0;
        } else  {
            pointsfromtime = 600 - player.counter;
        }

        totalpoints = player.points + pointsfromtime;
        //*Code for Assessment 3
    }

    @Override
    public void update() {
        // If mouse button is pressed, return to main menu
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
            StateManager.loadState(new MainMenu());
            player.points = 0;
            player.counter = null;
        }
    }

    @Override
    public void render(){
        UIBatch.begin();
        Zombies.mainFont.draw(UIBatch, "YOU WIN!", Zombies.InitialWindowWidth/2-200, Zombies.InitialWindowHeight/2+100);
        Zombies.mainFont.draw(UIBatch, "Bonus Points: " + String.valueOf((int)pointsfromtime),Zombies.InitialWindowWidth/2-200, Zombies.InitialWindowHeight/2+25);
        Zombies.mainFont.draw(UIBatch, "Total Score: " + String.valueOf((int)totalpoints), Zombies.InitialWindowWidth/2-200, Zombies.InitialWindowHeight/2-25);
        Zombies.mainFont.draw(UIBatch, "[click to continue]", Zombies.InitialWindowWidth/2-230, Zombies.InitialWindowHeight/2-100);
        UIBatch.end();
    }
}
