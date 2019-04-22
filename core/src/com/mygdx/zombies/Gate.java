package com.mygdx.zombies;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.zombies.entities.Entity;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * Gate object, provides a Box2D detection area, which if touched transports the user to a specified area
 */
public class Gate extends Entity {
	
	private StateID destination;
	private int entryID;
    private boolean playerColliding;
	
	/** Constructor for the gate
	 * @param world - the Box2D world the create the gate in
	 * @param rect - the rectangle marking the bounds of the Box2D detection area
	 * @param destination - the StateID representing the screen to go to when touched
	 * @param entryID - a level-unique entry id, which is used to indicate precisely which gate the player used
	 */
	public Gate(World world, Rectangle rect, StateID destination, int entryID) {
		this.destination = destination;
		this.entryID = entryID;
		
		FixtureDef fixtureDef = new FixtureDef() {
			{
				isSensor = true;
				//#changed4 added the following two lines to specify collision behavior
				filter.categoryBits = Zombies.gateFilter;
				filter.maskBits = Zombies.playerFilter;
			}
		};
		//Generate the Box2D detection area
		GenerateBodyRectangle(new Vector2(rect.width / Zombies.PhysicsDensity, rect.height / Zombies.PhysicsDensity),
				world, InfoContainer.BodyID.GATE, fixtureDef);	
		body.setTransform(rect.x / Zombies.PhysicsDensity, rect.y / Zombies.PhysicsDensity, 0);
	}

    /**
     * #changed4
     * @return true if player is colliding with a gate, false otherwise
     */
    public boolean isColliding() {
        return playerColliding;
    }
	
	public StateID getDestination() {
		return destination;
	}

    /**
     * Set whether player is over the gate
     * @param playerColliding whether the player is over the gate
     */
    void setPlayerColliding(boolean playerColliding) {
        this.playerColliding = playerColliding;
    }

    public int getEntryID() {
		return entryID;
	}
}
