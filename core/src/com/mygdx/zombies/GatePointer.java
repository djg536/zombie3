package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.zombies.states.Level;
import com.mygdx.zombies.states.StateManager.GateDirection;

/**
 * added for assessment 3, simple arrow to point at gate to next level
 */

public class GatePointer extends Entity{

    private SpriteBatch spriteBatch;
    private Sprite sprite;
    private boolean visible;



    /**
     * constructor for the pointer
     * @param level the level instance to spawn the pointer in.
     * @param x the x spawn coordinate
     * @param y the y spawn coordinate
     * @param SpritePath the file path of the sprite file to use
     */
    public GatePointer(Level level, int x, int y, String SpritePath,GateDirection direction){
        spriteBatch = level.getWorldBatch();
        sprite = new Sprite(new Texture(Gdx.files.internal(SpritePath)));
        sprite.setPosition(x, y);
        switch(direction){
            case UP:
                //no rotation needed
            break;
            case RIGHT:
                sprite.rotate(270f);
               break;
            case DOWN:
                sprite.rotate(180f);
               break;
            case LEFT:
                sprite.rotate(90f);
               break;
        }
    }

    public void update(boolean gateOpen){
        visible = gateOpen;
    }

    public void render() {
        if (visible){
            sprite.draw(spriteBatch);
        }
    }
}
