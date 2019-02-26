package com.mygdx.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.zombies.states.MiniGame;

public class Goose {

    // New class for assessment 3

    public long timeRemaining;
    public static long popUpTime;
    private long originalTime;
    private Sprite sprite;
    private SpriteBatch spriteBatch;

    public Goose(MiniGame miniGame, int x, int y, long popUpTime) {
        spriteBatch = miniGame.getWorldBatch();
        sprite = new Sprite(new Texture("minigame/whosagooseboy.png"));
        sprite.setPosition(x, y);
        sprite.flip(false, true);

        // Time in milliseconds
        Goose.popUpTime = popUpTime;
        timeRemaining = popUpTime;
        originalTime = System.currentTimeMillis();
    }

    /**
     * Getter for x position of sprite.
     *
     * @return x coordinate as a float relative to the SpriteBatch origin.
     */
    public float getX() {
        return sprite.getX();
    }

    /**
     * Getter for width of sprite.
     *
     * @return width of sprite texture as a float.
     */
    public float getWidth() {
        return sprite.getWidth();
    }

    /**
     * Getter for y position of sprite.
     *
     * @return y coordinate as a float relative to the SpriteBatch origin.
     */
    public float getY() {
        return sprite.getY();
    }

    /**
     * Getter for height of sprite.
     *
     * @return height of sprite texture as a float.
     */
    public float getHeight() {
        return sprite.getHeight();
    }

    /**
     * Updates the timeRemaining for the goose.
     * Once timeRemaining is below zero MiniGame will remove it.
     */
    public void update() {
        long newTime = System.currentTimeMillis();
        timeRemaining -= (newTime - originalTime);
        originalTime = newTime;
    }

    public void render() {
        sprite.draw(spriteBatch);
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}
