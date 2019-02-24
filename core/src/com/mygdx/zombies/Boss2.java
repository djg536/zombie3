package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.zombies.items.Projectile;
import com.mygdx.zombies.items.ZombieProjectile;
import com.mygdx.zombies.states.Level;
import com.mygdx.zombies.states.StateManager;

public class Boss2 extends Enemy {

    // Added for assessment 3

    private Level level;
    private SpriteBatch spriteBatch;
    private Player player;
    private int projectilespawnstep;
    private double playerangle;
    private double attackangle;

    public Boss2(Level level, int x, int y) {
        super(level, x, y, "zombie/mikefreeman.png", 0, 50);

        this.level = level;
        spriteBatch = level.getWorldBatch();
        projectilespawnstep = 0;


    }

    @Override
    public void update(boolean inLights) {
        super.update(inLights);

        projectilespawnstep++;
        if (projectilespawnstep > 40) {
            projectilespawnstep = 0;
            if (getHealth() > 35) {
                ZombieProjectile zombieprojectile = new ZombieProjectile(level, getPositionX(), getPositionY(), (float) (angleToPlayerRadians + Math.PI), "zombie_projectile.png", 100);
                level.getBullets2List().add(zombieprojectile);
            } else if (35 >= getHealth() && (getHealth() > 20)) {
                attackangle = 0.90;
                for (int i = 0; i < 3; i++) {
                    ZombieProjectile zombieprojectile = new ZombieProjectile(level, getPositionX(), getPositionY(), (float) (angleToPlayerRadians + (Math.PI * attackangle)), "zombie_projectile.png", 100);
                    level.getBullets2List().add(zombieprojectile);
                    attackangle += 0.1;
                }
            } else {
                attackangle = 0.80;
                for (int i = 0; i < 5; i++) {
                    ZombieProjectile zombieprojectile = new ZombieProjectile(level, getPositionX(), getPositionY(), (float) (angleToPlayerRadians + (Math.PI * attackangle)), "zombie_projectile.png", 100);
                    level.getBullets2List().add(zombieprojectile);
                    attackangle += 0.1;
                }
            }
        }
    }


    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void render() {
        super.render();
    }
}

