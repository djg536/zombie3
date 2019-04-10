package com.mygdx.zombies.entities;

//#changed4 Removed unused imports
import com.mygdx.zombies.items.Projectile;
import com.mygdx.zombies.states.Level;

public class Boss2 extends Enemy {

    // Added for assessment 3

    private Level level;

    //#changed4 put projectileSpawnStep in Camel Case
    private int projectileSpawnStep;
    private int numberToSpawn;

    public Boss2(Level level, int x, int y) {
        super(level, x, y, "zombie/mikefreeman.png", 1, 50);

        this.level = level;
        projectileSpawnStep = 0;
        numberToSpawn = 1;
    }

    /**
     * Set the Boss health
     * @param health - the value to set the health to
     * #changed4 Added this method so numberToSpawn is only updated when health changed, moved here from update method
     */
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
    public void update(boolean inLights, float delta) {
        super.update(inLights, delta);

        //#changed4 Rewrote the code in this method to be more efficient and easier to read - replaced multiple IF statements with a single FOR loop
        //          Moved code to update numberToSpawn to setHealth as it is only updated when health is set

        final float bulletSpray = 0.1f;

        projectileSpawnStep++;
        if (projectileSpawnStep > 40) {
            projectileSpawnStep = 0;

            for (int i = 0; i < numberToSpawn; i++) {
                float shootAngle = (float) (angleToPlayerRadians + (Math.PI + i*bulletSpray - (bulletSpray*numberToSpawn)/2));
                level.getBulletsList().add(new Projectile(level, getPositionX(), getPositionY(), shootAngle, Projectile.ProjectileType.ZOMBIEPOTION));
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

