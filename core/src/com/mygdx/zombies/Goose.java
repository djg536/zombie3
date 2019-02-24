package com.mygdx.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.zombies.states.MiniGame;

public class Goose extends Entity {

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
        this.popUpTime = popUpTime;
        timeRemaining = popUpTime;
        originalTime = System.currentTimeMillis();
        spawnAnimation();
        // Requires a simple box2d body for Entity to manage deletions
        GenerateBodyFromSprite(new World(new Vector2(0,0), false), sprite, null, new FixtureDef());
    }

    public void spawnAnimation() {
    }

    public void despawnAnimation() {
    }

    public void deathAnimation() {
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
        super.dispose();
        sprite.getTexture().dispose();
    }
}
