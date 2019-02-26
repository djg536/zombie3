package com.mygdx.zombies.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.Level;
import static com.mygdx.zombies.InfoContainer.BodyID.PROJECTILE;

/**
 * Ranged weapon class which fires projectiles
 */
public class RangedWeapon implements Weapon {

	private int shootDelay;
	private int timerTicks;
	private Level level;
	private Projectile.ProjectileType projectileType;
	private static boolean firing;
	
	/**
	 * Constructor for the ranged weapon
	 * @param level - the level to create the weapon in
	 * @param shootDelay - the reload delay between firing projectiles
	 * @param projectileType - the type of projectile, which determines attribute values
	 */
	public RangedWeapon(Level level, int shootDelay, Projectile.ProjectileType projectileType) {
		
		this.level = level;		
		this.shootDelay = shootDelay;
		this.projectileType = projectileType;
		
		//Initialise shoot timing values
		timerTicks = 0;
		firing = false;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	/**Method to check whether the weapon is firing
	 * @return true if the ranged weapon is firing
	 */
	public boolean isFiring() {
		return firing;
	}
	
	/**
	 * Fires a projectile, if the weapon is loaded
	 */
	@Override
	public void use() {
		if(timerTicks == 0) {
			timerTicks++;
			Vector2 pos = level.getPlayer().getHandsPosition();
			level.getBulletsList().add(new Projectile(level, (int)pos.x + level.getPlayer().getPositionX(), (int)pos.y + level.getPlayer().getPositionY(),
					(float)(level.getPlayer().getAngleRadians() + Math.PI/2), projectileType));
			firing = true;
		}
	}
	
	/**
	 * Method to update shoot timer
	 */
	@Override
	public void update(int x, int y, float rotation) {
		
		if(timerTicks > 0)
			timerTicks++;
		if(timerTicks >= shootDelay) {
			timerTicks = 0;
			firing = false;
		}
	}

	@Override
	public void render() {
	}
	
	@Override
	public void dispose() {		
	}
}
