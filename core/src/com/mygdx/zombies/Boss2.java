package com.mygdx.zombies;

import com.mygdx.zombies.items.Projectile;
import com.mygdx.zombies.states.Level;

public class Boss2 extends Enemy {

    // Added for assessment 3

    private Level level;
    private int projectileSpawnStep;
    private int numberToSpawn;

    public Boss2(Level level, int x, int y) {
        super(level, x, y, "zombie/mikefreeman.png", 1, 50);

        this.level = level;
        projectileSpawnStep = 0;
        numberToSpawn = 1;
    }

    @Override
    public void setHealth(int health) {
        super.setHealth(health);

        int newHealthValue = getHealth();
        if(newHealthValue < 20)
            numberToSpawn = 5;
        else if(newHealthValue < 35)
            numberToSpawn = 3;
    }

    @Override
    public void update(boolean inLights) {
        super.update(inLights);

        final float bulletSpray = 0.1f;

        projectileSpawnStep++;
        if (projectileSpawnStep > 40) {
            projectileSpawnStep = 0;

            for (int i = 0; i < numberToSpawn; i++) {
                float shootAngle = (float) (angleToPlayerRadians + (Math.PI + i*bulletSpray - (bulletSpray*numberToSpawn)/2));
                level.getBulletsList().add(new Projectile(level, getPositionX(), getPositionY(), shootAngle, "zombie_projectile.png", 100, InfoContainer.BodyID.ZOMBIEPROJECTILE));
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

