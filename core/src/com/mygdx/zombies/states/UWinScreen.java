package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Zombies;

public class UWinScreen extends State {

    // New class for assessment 3

    private float pointsFromTime;
    private float totalPoints;

    UWinScreen(){
        super();
        Zombies.soundAmbientWind.stop();
        Zombies.soundEndMusic.loop();

        //*Code for Assessment 3
        //Calculates Player points
        if (Player.counter >= 600){
            pointsFromTime = 0;
        } else  {
            pointsFromTime = 600 - Player.counter;
        }

        totalPoints = Player.points + pointsFromTime;
        //*Code for Assessment 3
    }

    @Override
    public void update() {
        // If mouse button is pressed, return to main menu
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
            StateManager.loadState(new MainMenu());
            Player.points = 0;
            Player.counter = null;
        }
    }

    @Override
    public void render(){
        UIBatch.begin();
        Zombies.mainFont.draw(UIBatch, "YOU WIN!", Zombies.InitialWindowWidth/2.f-200, Zombies.InitialWindowHeight/2.f+100);
        Zombies.mainFont.draw(UIBatch, "Bonus Points: " + (int) pointsFromTime,Zombies.InitialWindowWidth/2.f-200, Zombies.InitialWindowHeight/2.f+25);
        Zombies.mainFont.draw(UIBatch, "Total Score: " + (int) totalPoints, Zombies.InitialWindowWidth/2.f-200, Zombies.InitialWindowHeight/2.f-25);
        Zombies.mainFont.draw(UIBatch, "[click to continue]", Zombies.InitialWindowWidth/2.f-230, Zombies.InitialWindowHeight/2.f-100);
        UIBatch.end();
    }
}
