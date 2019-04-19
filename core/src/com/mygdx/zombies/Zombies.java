package com.mygdx.zombies;

import java.util.Random;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.mygdx.zombies.states.StateManager;

/**
 * Base class for the game
 */
public class Zombies extends Game {

	public static final int InitialWindowWidth = 1280;
	public static final int InitialWindowHeight = 720;
	public static final int InitialViewportWidth = 1280;
	public static final int InitialViewportHeight = 720;
	public static final String windowTitle = "Silence of the Lamberts";
	public static final float WorldScale = 1.5f;
	public static final float PhysicsDensity = 100;
	public static BitmapFont mainFont;
	public static BitmapFont titleFont;
	public static BitmapFont pointsFont;
	public static BitmapFont creditsFont;
	public static BitmapFont gateFont;
	public static Sound soundShoot;
	public static Sound soundSelect;
	public static Sound soundLaser;
	public static Sound soundSwing;
	public static Sound soundAmmo;
	public static Sound soundPowerUp;
	//#changed4 added throwing sound
	public static Sound soundThrow;
	public static Sound soundAmbientWind;
	public static Sound soundEndMusic;
	public static Sound[] soundArrayZombie;
	public static Random random;
	private StateManager stateManager;

	// Collision masks. Can OR these together to combine.
	// Use categoryBits (1 default) and mask bits (-1 default)
	// (maskBitsA & categoryBitsB) && (categoryBitsA & maskBitsB);
	public static short playerFilter = 1;
	public static short projectileFilter = 2;
	//#changed4 added all of the below collision masks
	public static short zombieFilter = 4;
	public static short zombieProjectileFilter = 8;
	public static short wallFilter = 16;
	public static short pickupFilter = 32;
	public static short gateFilter = 64;
	public static short npcFilter = 128;
	

	/** Generate a BitmapFont using the given parameters
	 * @param name - the filename of the true type font
	 * @param size - the font size
	 * @return - the generated BitmapFont
	 */
	private static BitmapFont generateFont(String name, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(name));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();
		return font;
	}
	
	/** Calculate the angle between two points
	 * @param p1 - the first point
	 * @param p2 - the second point
	 * @return - the angle in radians
	 */
	public static double angleBetweenRads(Vector2 p1, Vector2 p2) {
		double diffX = p1.x - p2.x;
		double diffY = p1.y - p2.y;
		return Math.atan2(diffY, diffX);
	}
	
	/** Pythagorean algorithm to calculate the distance between two points
	 * @param p1 - the first point
	 * @param p2 - the second point
	 * @return - the distance between the two points
	 */
	public static double distanceBetween(Vector2 p1, Vector2 p2) {
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}

	/*
	 * Method called on game start
	 */
	@Override
	public void create() {
		//Initialise Box2D physics engine
		Box2D.init();
		//Create new random number generator
		random = new Random();
		
		//Create statemanager
		stateManager = new StateManager();
		
		//Generate fonts and store in memory
		mainFont = Zombies.generateFont("NESCyrillic.ttf", 55);
		titleFont = Zombies.generateFont("Amatic-Bold.ttf", 150);
		pointsFont = Zombies.generateFont("KaushanScript-Regular.otf", 50);
		creditsFont = Zombies.generateFont("SourceSansPro-Regular.otf", 50);
		gateFont = Zombies.generateFont("NESCyrillic.ttf", 30);
		
		//Load sounds into memory
		soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/gun.wav"));
		soundSwing = Gdx.audio.newSound(Gdx.files.internal("sounds/swing.wav"));
		soundSelect = Gdx.audio.newSound(Gdx.files.internal("sounds/select.wav"));
		soundLaser = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
		soundAmmo = Gdx.audio.newSound(Gdx.files.internal("sounds/ammo.wav"));
		soundPowerUp = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup.wav"));
		//#changed4 added throwing sound
		soundThrow = Gdx.audio.newSound(Gdx.files.internal("sounds/throw.wav"));
		soundAmbientWind = Gdx.audio.newSound(Gdx.files.internal("sounds/wind.mp3"));
		soundEndMusic = Gdx.audio.newSound(Gdx.files.internal("sounds/alligator_crawl.mp3"));
		
		soundArrayZombie = new Sound[8];
		for(int i = 0; i<soundArrayZombie.length; i++)
			soundArrayZombie[i] = Gdx.audio.newSound(Gdx.files.internal(String.format("sounds/zombie%d.wav", i+1)));
	}

	/*
	 * Method to perform a single render call
	 */
	@Override
	public void render() {
		//Update current state of StateManager
		stateManager.gameLoop(Gdx.graphics.getDeltaTime());
		//Set default background colour
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Render current state of StateManager
		stateManager.render();
	}

	/*
	 * Run when the game is closed, clearing the memory
	 */
	@Override
	public void dispose() {
		stateManager.dispose();
		mainFont.dispose();
		titleFont.dispose();
		pointsFont.dispose();
		creditsFont.dispose();
		soundShoot.dispose();
		soundSwing.dispose();
		soundSelect.dispose();
		soundLaser.dispose();
		soundAmmo.dispose();
		soundPowerUp.dispose();
		//#changed4 disposed of throwing sound
		soundThrow.dispose();
		soundAmbientWind.dispose();
		soundEndMusic.dispose();
		for(Sound sound : soundArrayZombie)
			sound.dispose();
	}

	/*
	 * Run when the game menu is resized
	 */
	@Override
	public void resize(int width, int height) {
		stateManager.resize(width, height);
	}
}
