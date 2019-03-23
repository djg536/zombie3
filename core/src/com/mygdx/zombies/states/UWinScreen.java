package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Zombies;

import static com.mygdx.zombies.states.StateManager.StateID.MAINMENU;

public class UWinScreen extends State {

    // New class for assessment 3

    //#changed4 updated attribute names to utilise Camel Casing
    private float pointsFromTime;
    private float totalPoints;

    private Texture banner;

    UWinScreen(){
        super();
        banner = new Texture("win.png"); //*Code for Assessment 4
        Zombies.soundAmbientWind.stop();
        Zombies.soundEndMusic.loop();

        //*Code for Assessment 3
        //Calculates Player points
        if (Player.getCounter() >= 600){
            pointsFromTime = 0;
        } else  {
            pointsFromTime = 600 - Player.getCounter();
        }

        totalPoints = Player.getPoints() + pointsFromTime + (Level.getDeliveredNPCs() * 1000);
        //*Code for Assessment 3
    }

    @Override
    public void update(float delta) {
        // If mouse button is pressed, return to main menu
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
            StateManager.loadState(MAINMENU, aliveNPC);
            Player.setPoints(0);
            Player.setCounter(null);
        }
    }

    @Override
    public void render(){
        UIBatch.begin();
        //#changed4 UI element positioning now utilises constants rather than e.g. Gdx.graphics.getWidth(), which has now fixed scaling
        UIBatch.draw(banner, Zombies.InitialWindowWidth/2f-banner.getWidth()/2f, Zombies.InitialWindowHeight-banner.getHeight()-15); //#changed4
        Zombies.mainFont.draw(UIBatch, "Bonus Points: " + (int) pointsFromTime,Zombies.InitialWindowWidth/2f-200, Zombies.InitialWindowHeight/2f-75);
        Zombies.mainFont.draw(UIBatch, "Recovered NPCs: " + Level.getDeliveredNPCs() + " * 1000", Zombies.InitialWindowWidth/2f-200, Zombies.InitialWindowHeight/2f-125);
        Zombies.mainFont.draw(UIBatch, "Total Score: " + (int) totalPoints, Zombies.InitialWindowWidth/2f-200, Zombies.InitialWindowHeight/2f-175);
        Zombies.mainFont.draw(UIBatch, "[click to continue]", Zombies.InitialWindowWidth/2f-230, Zombies.InitialWindowHeight/2f-250);
        UIBatch.end();
    }


    @Override
    public void dispose() {

        //#changed4 Added this method to prevent memory leakage and to stop the music playing when disposed
        super.dispose();
        Zombies.soundEndMusic.stop();
        banner.dispose();
    }
}
