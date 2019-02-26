package com.mygdx.zombies.items;

import com.mygdx.zombies.states.Level;

/**
 * Abstract weapon interface for both ranged and melee weapons
 */
public interface Weapon {
	

	void use();
	
	/**
	 * Method to update weapon transformation
	 * @param x - the x position to move to
	 * @param y - the y position to move to
	 * @param rotation - the rotation to set
	 */
	void update(int x, int y, float rotation);
	
	/**
	 * Set the weapon level reference
	 * @param level - the level reference to change to
	 */
	void setLevel(Level level);
	
	/**
	 * Draw the weapon to the screen
	 */
	void render();
	
	/**
	 * Remove weapon clearing up memory
	 */
	void dispose();
}
