package com.mygdx.zombies;

import java.util.ArrayList;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Generic Entity class for handling safe flag deletion system
 */
public class Entity implements Steerable<Vector2> {

	private World box2dWorld;
	protected Body body;
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());
	private boolean independentFacing = false;

	public enum SteeringState {WANDER, SEEK, ARRIVE}
	SteeringState currentMode = SteeringState.WANDER;
	SteeringBehavior<Vector2> steeringBehavior;
	private float boundingRadius = 100f;
	private boolean tagged = true;
	private float zeroThreshold = 0.01f;
	private float maxLinearAcceleration = 20f;
	private float maxAngularSpeed = 200f;
	private float maxAngularAcceleration = 20f;
	private float maxLinearSpeed = 2f;



	/** Generic method to generate a Box2D physics rectangle object based on given parameters
	 * @param dimens - the dimensions of the rectangle
	 * @param box2dWorld - the world to create the rectangle in
	 * @param bodyID - defines the types of object, used in collision events
	 * @param fixtureDef - the Box2D fixture definition to use
	 */
	void GenerateBodyRectangle(Vector2 dimens, World box2dWorld, InfoContainer.BodyID bodyID, FixtureDef fixtureDef) {
		this.box2dWorld = box2dWorld;
		body = box2dWorld.createBody(new BodyDef() {
			{
				type = BodyDef.BodyType.DynamicBody;
			}
		});
		final PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(dimens.x, dimens.y);
		fixtureDef.shape = polyShape;
		body.createFixture(fixtureDef);
		body.setUserData(new InfoContainer(bodyID, this));
		polyShape.dispose();
	}
	
	/** Method to generate Box2D physics rectangle from a sprite
	 * @param box2dWorld - the world to create the rectangle in
	 * @param sprite - the sprite to build the rectangle around
	 * @param bodyID - defines the types of object, used in collision events
	 * @param fixtureDef - the Box2D fixture definition to use
	 */
	protected void GenerateBodyFromSprite(World box2dWorld, Sprite sprite, InfoContainer.BodyID bodyID, FixtureDef fixtureDef) {
		
		Vector2 dimens = new Vector2(sprite.getWidth() / 2 / Zombies.PhysicsDensity,
				sprite.getHeight() / 2 / Zombies.PhysicsDensity);
		GenerateBodyRectangle(dimens, box2dWorld, bodyID, fixtureDef);
	}
	
	/**
	 * Delete the entity, clearing the memory
	 */
	public void dispose() {
		box2dWorld.destroyBody(body);
	}
	
	/**
	 * @return the associated custom user data for this entity
	 */
	public InfoContainer getInfo() {
		return (InfoContainer)body.getUserData();
	}
	
	/** Remove any objects that are deletion flagged from the given list
	 * @param lst - the list to remove flagged objects from
	 */
	public static <T extends Entity> void removeDeletionFlagged(ArrayList<T> lst) {
		//Iterate through list
		for(int i = 0; i< lst.size(); i++) {
			T entity = lst.get(i);
			InfoContainer info = entity.getInfo();
			if(info != null && info.isDeletionFlagged()) {
				entity.dispose();
				lst.remove(entity);
				//Step index back by one to account for removal from list
				i--;
			}
		}
	}

	public int getPositionX() {
		return (int) (body.getPosition().x * Zombies.PhysicsDensity);
	}

	public int getPositionY() {
		return (int) (body.getPosition().y * Zombies.PhysicsDensity);
	}


	public void update(float delta) {

		if (steeringBehavior != null) {
			steeringBehavior.calculateSteering(steeringOutput);
			applySteering(steeringOutput, delta);
		}
	}

	/**
	 * #changed4 added gdx-AI movement
	 * Apply assigned steering functionality to the character
	 * @param steering steering behaviour to apply
	 * @param delta time update
	 */
	protected void applySteering(SteeringAcceleration<Vector2> steering, float delta) {
		boolean anyAccelerations = false;
		// Update position and linear velocity
		if (!steeringOutput.linear.isZero()) {
			body.applyForceToCenter(steeringOutput.linear, true);
			body.applyLinearImpulse(steeringOutput.linear, body.localPoint2, true);
			anyAccelerations = true;
		}
		//Update orientation and angular velocity
		if (isIndependentFacing()) {
			if (steeringOutput.angular != 0) {
				body.applyTorque(steeringOutput.angular, true);
				anyAccelerations = true;
			}
		} else {
			Vector2 linearVelocity = getLinearVelocity();
			if (!linearVelocity.isZero(getZeroLinearSpeedThreshold())) {
				float newOrientation = vectorToAngle(linearVelocity);
				body.setAngularVelocity((newOrientation - getAngularVelocity()) * delta);
				body.setTransform(body.getPosition(), newOrientation);
			}
		}

		if (anyAccelerations) {
			Vector2 velocity = body.getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			float maxLinearSpeed = getMaxLinearSpeed();
			if (currentSpeedSquare > (maxLinearSpeed*maxLinearSpeed)) {
				body.setLinearVelocity(velocity.scl(maxLinearSpeed/(float)Math.sqrt(currentSpeedSquare)));
			}
			float maxAngVelocity = getMaxAngularSpeed();
			if (body.getAngularVelocity() > maxAngVelocity) {
				body.setAngularVelocity(maxAngVelocity);
			}
		}
	}

	private boolean isIndependentFacing() {
		return independentFacing;
	}

	public void setIndependentFacing(boolean independentFacing) {
		this.independentFacing = independentFacing;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		return zeroThreshold;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		zeroThreshold = value;
	}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public void setOrientation(float orientation) {
		body.setTransform(getPosition(), orientation);
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return (float) Math.atan2(-vector.x, vector.y);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		outVector.x = -(float) Math.sin(angle);
		outVector.y = (float) Math.cos(angle);
		return outVector;
	}

	@Override
	public Location<Vector2> newLocation() {
		return null;
	}
}
