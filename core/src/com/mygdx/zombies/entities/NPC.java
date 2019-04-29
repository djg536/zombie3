package com.mygdx.zombies.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.InfoContainer;
import com.mygdx.zombies.SteeringPresets;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.items.Projectile;
import com.mygdx.zombies.states.Level;

/**
 * NPC character which follows the player and will die if damaged by enemy attacks
 */
public class NPC extends Entity {
	
	private SpriteBatch spriteBatch;
	private Sprite sprite;
	private Player player;
	private Integer health;

	private int projectileSpawnStep;
	private Level level;
	private double angleToZombieRadians;


	/** Constructor for the NPC class
	 * @param level - the level to spawn the NPC in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 */
	public NPC(Level level, int x, int y) {
		this.level = level;
		//Add sprite
		spriteBatch = level.getWorldBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal("npc.png")));
		
		//Add box2d body
		FixtureDef fixtureDef = new FixtureDef() {
			{
				density = 40;
				friction = 0.5f;
				restitution = 0f;
				//#changed4 added collision with walls, players, zombies and zombie bullets
				filter.categoryBits = Zombies.npcFilter;
				filter.maskBits = (short) (Zombies.playerFilter | Zombies.wallFilter |
						Zombies.zombieFilter | Zombies.zombieProjectileFilter | Zombies.npcFilter);
			}
		};
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, InfoContainer.BodyID.NPC, fixtureDef);
		body.setTransform(x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, 0);
		body.setLinearDamping(4);
		body.setFixedRotation(true);
				
		this.player = level.getPlayer();
		//#changed4 set aliveNPC to true when NPC is instantiated
		level.setAliveNPC(true);
		if (health == null)
			health = 10;

		projectileSpawnStep = 0;
	}
	
	/** Set the health to the given value, removing the NPC if health is depleted
	 * #changed4 set flag aliveNPC to false if health <= 0
	 * @param health - the value to set the health to
	 */
	public void setHealth(int health) {
		this.health = health;
		if(health <= 0) {
			getInfo().flagForDeletion();
			level.setAliveNPC(false);
		}
	}
	
	public Integer getHealth() {
		return health;
	}
	
	/**
	 * Method to update position and rotation
	 */
	public void update(float delta) {
		super.update(delta);
		Vector2 bodyPosition = body.getPosition().scl(Zombies.PhysicsDensity);
		double angleRads = Zombies.angleBetweenRads(new Vector2(player.getPositionX(), player.getPositionY()), bodyPosition);
		float angleDegrees = (float) Math.toDegrees(angleRads);
		
		//Move towards the player until close by, #changed4 using gdx-AI
		this.steeringBehavior = SteeringPresets.getArrive(this, player);
		this.currentMode = SteeringState.ARRIVE;

		sprite.setPosition(bodyPosition.x - sprite.getWidth()/2, bodyPosition.y - sprite.getHeight()/2);
		sprite.setRotation(angleDegrees);
		//#changed4 added NPC shooting ability - shoots at closest zombie to player every few seconds as extra 'protection'
		final float bulletSpray = 0.1f;

		projectileSpawnStep++;
		if (projectileSpawnStep > 120) {
			projectileSpawnStep = 0;
			double enemyDistance = Integer.MAX_VALUE;
			for (Enemy enemy : level.getEnemiesList()){ // NPC shoots closest enemy to player
				Enemy closestEnemy;
				if (enemy.getDistanceToPlayer() < enemyDistance) {
					enemyDistance = enemy.getDistanceToPlayer();
					closestEnemy = enemy;
					angleToZombieRadians = Zombies.angleBetweenRads(new Vector2(getPositionX(), getPositionY()),
							new Vector2(closestEnemy.getPositionX(), closestEnemy.getPositionY()));
				}
			}

			float shootAngle = (float) (angleToZombieRadians + (Math.PI + bulletSpray - (bulletSpray) / 2));
			if (level.getEnemiesList().size() != 0) // if there are zombies still alive
				level.getBulletsList().add(new Projectile(level, getPositionX(), getPositionY(), shootAngle, Projectile.ProjectileType.BULLET));
		}
	}
	
	public void render() {
		sprite.draw(spriteBatch);
	}
	
	public void dispose() {
		super.dispose();
		sprite.getTexture().dispose();
		//#changed4 NPCs turn into zombies when they die
		level.getEnemiesList().add(new Enemy(level, getPositionX(), getPositionY(), "zombie/zombie1.png", 6, 5));
		level.setAliveNPC(false	);
	}
}
