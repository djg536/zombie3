package com.mygdx.zombies.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.zombies.Goose;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Zombies;
import java.util.Random;


import java.util.ArrayList;

public class MiniGame extends State {

    // New class for assessment 3

    private Texture background;
    private int points;
    private ArrayList<Goose> geese;
    public long timeRemaining;
    private long originalTime;
    private OrthographicCamera camera;
    public long goosePopUpTime;
    public long spawnInterval;
    private boolean gameOver;
    // State to return to once minigame is complete
    private String returnState;
    private int windowWidth;
    private int windowHeight;

    MiniGame(String returnState) {
        this.returnState = returnState;
        background = new Texture("minigame/background.jpg");
        points = 0;
        gameOver = false;
        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();
        // Time in milliseconds, 60 second game
        timeRemaining = 60000;
        originalTime = System.currentTimeMillis();
        geese = new ArrayList<>();

        // Geese initially pop up for 3 seconds, pop up for less time as the game progresses
        goosePopUpTime = 3000;

        // Initally spawn geese every 2 seconds, spawn faster as the game progresses
        spawnInterval = 2000;

        // Camera used to translate mouse screen coordinates into world coordinates

        //#changed4 removed redundant resolution parameters passed to camera constructor as these are already passed with setToOrtho
        camera = new OrthographicCamera();
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        // Setting worldBatch to use world coordinates
        worldBatch.setProjectionMatrix(camera.combined);
    }

    /**
     * Constructor for the unit testing of MiniGame.
     * See class MiniGameTestable for more.
     * ONLY EVER USE FOR TESTING.
     */
    public MiniGame(int windowWidth, int windowHeight) {
        super(true);
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        points = 0;
        gameOver = false;
        timeRemaining = 60000;
        originalTime = System.currentTimeMillis();
        geese = new ArrayList<>();
        goosePopUpTime = 3000;
        spawnInterval = 2000;
    }

    /**
     * Adds a goose to the list of all geese in the game
     *
     * @param position where to spawn the goose
     */
    public void spawnGoose(Vector2 position, long popUpTime) {
        geese.add(new Goose(this, (int) position.x, (int) position.y, popUpTime));
    }

    /**
     * Getter for geese currently in the mini game.
     */
    public ArrayList<Goose> getGeese() {
        return this.geese;
    }


    /**
     * Randomly generates x and y positions that are visible on the screen.
     *
     * @return Vector2 containing x and y positions
     */
    public Vector2 generateSpawn() {
        // Generating random x and y from window width and height at which to spawn the goose
        int x = new Random().nextInt(windowWidth - 64) +32;
        int y = new Random().nextInt(windowHeight - 64) +32;

        return new Vector2(x, y);
    }

    @Override
    public void render() {
        //#changed4 Text positioning now utilises constants rather than e.g. Gdx.graphics.getWidth(), which has now fixed scaling
        if (gameOver) {
            UIBatch.begin();
            UIBatch.draw(background, 0, 0);
            Zombies.mainFont.draw(UIBatch, "Game Over", Zombies.InitialWindowWidth / 2.f, Zombies.InitialWindowHeight / 2.f + 32);
            Zombies.mainFont.draw(UIBatch, "Points: " + points, Zombies.InitialWindowWidth / 2.f, Zombies.InitialWindowHeight / 2.f);
            Zombies.mainFont.draw(UIBatch, "Press escape to exit.", Zombies.InitialWindowWidth / 2.f, Zombies.InitialWindowHeight / 2.f - 64);

            UIBatch.end();
        } else {
            // Render all UI
            UIBatch.begin();
            UIBatch.draw(background, 0, 0);
            Zombies.mainFont.draw(UIBatch, "Time remaining: " + this.timeRemaining / 1000.f, 16, Zombies.InitialWindowHeight - 16);
            Zombies.mainFont.draw(UIBatch, "Score: " + points, 16, Zombies.InitialWindowHeight - 64);
            UIBatch.end();

            // Render all geese
            worldBatch.begin();
            for (Goose goose : geese) {
                goose.render();
            }
            worldBatch.end();
        }
    }

    /**
     * @param goose - goose object to be checked
     * @return boolean true if the mouse is over the goose, false otherwise
     */
    private boolean mouseOverGoose(Goose goose) {
        // Converting mouse screen coordinates into world coordinates
        Vector3 mouseWorldCoords = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        float mouseX = mouseWorldCoords.x;
        float mouseY = mouseWorldCoords.y;

        // Goose position is the bottom left of the sprite.
        boolean mouseXinRange = mouseX >= goose.getX() && mouseX <= (goose.getX() + goose.getWidth());
        boolean mouseYinRange = mouseY >= goose.getY() && mouseY <= (goose.getY() + goose.getHeight());

        return mouseXinRange && mouseYinRange;
    }

    /**
     * Calculates percentage time remaining and rounds to 2 sig figs. Then adds the base points of 100.
     * E.g. if a goose with popUpTime 1000 (1s) had timeRemaining 120 (0.12s) then there should be 110 points.
     *
     * @param goose to calculate extra points for if it was shot now.
     * @return  points as an int in range 100 to 200
     */
    public int calcPoints(Goose goose) {
        int points = Math.round(((float) goose.timeRemaining / (float) Goose.popUpTime) *10) * 10;
        points += 100;
        return points;
    }


    /**
     * Reduce goosePopUpTime as game progresses.
     */
    public void updateGoosePopUpTime() {
        if (timeRemaining < 60000 && timeRemaining > 45000) {
            goosePopUpTime = 3000;
        }
        if (timeRemaining < 45000 && timeRemaining > 30000) {
            goosePopUpTime = 2500;
        }
        if (timeRemaining < 30000 && timeRemaining > 15000) {
            goosePopUpTime = 2000;
        }
        if (timeRemaining < 15000 && timeRemaining > 0) {
            goosePopUpTime = 1500;
        }
    }

    /**
     * Reduce spawnInterval as game progresses.
     */
    public void updateSpawnInterval() {
        if (timeRemaining < 60000 && timeRemaining > 45000) {
            spawnInterval = 2000;
        }
        if (timeRemaining < 45000 && timeRemaining > 30000) {
            spawnInterval = 1750;
        }
        if (timeRemaining < 30000 && timeRemaining > 15000) {
            spawnInterval = 1500;
        }
        if (timeRemaining < 15000 && timeRemaining > 0) {
            spawnInterval = 1250;
        }
    }

    @Override
    public void update(float delta) {
        if (gameOver) {
            // Press escape to exit
            if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
                if (returnState.equals("menu")) {
                    StateManager.loadState(StateManager.StateID.MAINMENU, aliveNPC);
                } else if (returnState.equals("game")) {
                    Player.setPoints(Player.getPoints() + points);
                    StateManager.loadState(StateManager.StateID.STAGE2, 0, aliveNPC);
                }
            }
        } else {
            // Update geese
            for (Goose goose : geese) {
                goose.update();
            }

                for (int i = 0; i < geese.size(); i++) {
                    Goose goose = geese.get(i);

                    // Check if the mouse is over any of the geese
                    if (goose.timeRemaining <= 0            // Left click to shoot
                            || (mouseOverGoose(goose) && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched())) {

                        // Delete any goose that are shot
                        goose.dispose();
                        geese.remove(i);
                        i--;

                        // Adds the score for killing the goose
                        points += calcPoints(goose);
                    }
            }

            // Reduce time remaining for mini game.
            long newTime = System.currentTimeMillis();
            this.timeRemaining -= newTime - originalTime;
            originalTime = newTime;

            // Game over
            if (timeRemaining < 0) {
                gameOver = true;
            }

            // Randomly spawn geese every spawnInterval as long as there are no more than 2 on screen at any time.
            if (timeRemaining / 100 % Math.round(spawnInterval / 100.f) == 0 && geese.size() < 2 && new Random().nextBoolean()) {
                spawnGoose(generateSpawn(), goosePopUpTime);
            }

            // Increase difficulty with time
            updateGoosePopUpTime();
            updateSpawnInterval();
        }
    }

    /**
     * Dispose method to clear the memory when instance no longer needed
     * #changed4 Added this method to reduce memory leakage
     */
    @Override
    public void dispose() {
        super.dispose();
        background.dispose();
        for (Goose goose : geese) {
            goose.dispose();
        }
    }
}
