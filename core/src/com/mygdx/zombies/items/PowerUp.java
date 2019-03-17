package com.mygdx.zombies.items;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.zombies.Enemy;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.Level;
import java.util.ArrayList;

/**
 * Power up class for storing power up attributes
 */
public class PowerUp {
	
	private int speedBoost;
	private int healthBoost;
	private int stealthBoost;
	private boolean cure;
	private boolean antidote;
	
	/**
	 * The constructor for the power up
	 * @param speedBoost - the extra speed to give to the player
	 * @param healthBoost - the amount of health to give to the player
	 * @param stealthBoost - the stealth boost to give to the player
	 * #changed4 added cure and antidote parameters
	 */
	public PowerUp(int speedBoost, int healthBoost, int stealthBoost, boolean cure, boolean antidote) {
		this.speedBoost = speedBoost+1;
		this.healthBoost = healthBoost;
		this.stealthBoost = stealthBoost;
		this.cure = cure;
		this.antidote = antidote;
	}


	/**
	 * Apply the cure around the player
	 * @param level - an instance of level containing a player instance to apply to
	 * #changed4 added this method
	 */
	public void applyCure(Level level) {

		//Turn nearby zombies into NPCs
        ArrayList<Enemy> enemiesList = level.getEnemiesList();
        Player player = level.getPlayer();

        for(Enemy enemy : enemiesList)
            if (Zombies.distanceBetween(new Vector2(enemy.getPositionX(), enemy.getPositionY()), new Vector2(player.getPositionX(), player.getPositionY())) < 500) {
                enemy.enableSpawnNpcOnDeath();
                enemy.getInfo().flagForDeletion();
            }
	}
	
	public void applyAntidote(Level level) {
		Player player = level.getPlayer();
		player.setZombie(false);
	}

	public int getSpeedBoost() {
		return speedBoost;
	}
	
	public int getHealthBoost() {
		return healthBoost;
	}
	
	public int getStealthBoost() {
		return stealthBoost;
	}

	public boolean isCure() { return cure; }
	
	public boolean isAntidote() { return antidote; }
}
