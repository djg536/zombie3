package com.mygdx.zombies.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.zombies.Zombies;

/**
 * Generic state class, used for menus, screens and levels
 */
public class State {

	SpriteBatch worldBatch;
	protected SpriteBatch UIBatch;
	protected boolean aliveNPC;
	private static boolean testing = false;

	/**
	 * Constructor for the state class
	 */
	public State() {
	    if(testing)
	        return;
		worldBatch = new SpriteBatch();
		UIBatch = new SpriteBatch();
		//Resize to account for window dimensions
		resize();
	}

    public SpriteBatch getWorldBatch() {
		return worldBatch;
	}
	
	public SpriteBatch getUIBatch() {
		return UIBatch;
	}

    /**
     * Enables a special mode for unit testing only
     */
	public static void enableTestingMode() {
        testing = true;
    }

	/**
	 * Virtual render method
	 */
	public void render() {
	}

	
	/**
	 * Virtual update method
	 */
	public void update(float delta) {
	}
	
	/**
	 * Method to scale the user interface based on the window size
	 */
	private void resize() {
		UIBatch.getProjectionMatrix()
			.setToOrtho2D(0, 0, Zombies.InitialWindowWidth, Zombies.InitialWindowHeight);
	}

	/**
	 * Method is run when window is resized
	 * @param width - the new window width
	 * @param height - the new window height
	 */
	public void resize(int width, int height) {
		resize();
	}

	public void dispose() {
		//Clean up memory
		worldBatch.dispose();
		UIBatch.dispose();
	}
}
