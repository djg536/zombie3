package com.mygdx.zombies;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Presets for AI movement implementing wander, seek, and arrive functionality.
 * #changed for Assessment 4:   Added this class to the game
 */
public class SteeringPresets {
    /**
     * Wander presets for AI movement
     * @param steeringEntity the character to move
     * @return wander preset
     */
    public static Wander<Vector2> getWander(Entity steeringEntity) {
        Wander<Vector2> wander = new Wander<>(steeringEntity)
                .setFaceEnabled(false)
                .setLimiter(new LinearAccelerationLimiter(0.1f))
                .setWanderOffset(3)
                .setWanderOrientation(5)
                .setWanderRadius(0.5f)
                .setWanderRate(MathUtils.PI2 * 8);
        return wander;
    }

    /**
     * Seek functionality for enemy movement
     * @param seeker the enemy character that will move
     * @param target the destination point
     * @return seek preset
     */
    public static Seek<Vector2> getSeek(Entity seeker, Entity target) {
        Seek<Vector2> seek = new Seek<>(seeker, target);
        return seek;
    }

    /**
     * Arrive functionality for enemy movement
     * @param runner the enemy character that will move
     * @param target the destination point
     * @return arrive preset
     */
    public static Arrive<Vector2> getArrive(Entity runner, Entity target) {
        Arrive<Vector2> arrive = new Arrive<>(runner, target)
                .setTimeToTarget(0.1f)
                .setArrivalTolerance(1f)
                .setDecelerationRadius(0.5f);
        return arrive;
    }
}
