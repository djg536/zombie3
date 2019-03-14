package com.mygdx.zombies.states;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.zombies.*;
import com.mygdx.zombies.items.*;
import com.mygdx.zombies.states.StateManager.StateID;
import com.mygdx.zombies.states.StateManager.GateDirection;
import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Level class to handle the main gameplay
 */
public class Level extends State {

	private Player player;
	private ArrayList<Enemy> enemiesList;
	//#changed4 removed the second projectile list for zombie projectiles as now they are combined
	private ArrayList<Projectile> bulletsList;
	private ArrayList<PickUp> pickUpsList;
	private World box2dWorld;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private RayHandler rayHandler;
	private ArrayList<PointLight> lightsList;
	private ArrayList<NPC> npcsList;
	private ArrayList<Gate> gatesList;
	private ArrayList<GatePointer> gatePointerList;
	private String path;
	private int spawnEntryID;
	private Box2DDebugRenderer box2DDebugRenderer;
	private boolean gamePaused;
    //#changed4 added pauseMenu attribute
	private PauseMenu pauseMenu;
    //#changed4 made the following line private
	private ArrayList<Point> potentialCureSpawnPointList;
	private CustomContactListener customContactListener;
    private static Logger logger;
    private static Handler handler;
    private boolean antidoteSpawn;

	/**
	 * Constructor for the level
	 * 
	 * @param path - filename of .tmx file for tiled grid
	 * @param spawnEntryID - the id of an entry to spawn the player at
	 */
	public Level(String path, int spawnEntryID) {
		super();	
		
		this.path = path;
		this.spawnEntryID = spawnEntryID;
		
		bulletsList = new ArrayList<>();
		enemiesList = new ArrayList<>();
		pickUpsList = new ArrayList<>();
		npcsList = new ArrayList<>();
		gatesList = new ArrayList<>();
		gatePointerList = new ArrayList<>();
		potentialCureSpawnPointList = new ArrayList<>();

        initLogging();

		String mapFile = String.format("stages/%s.tmx", path);

		map = new TmxMapLoader().load(mapFile);
		renderer = new OrthogonalTiledMapRenderer(map, Zombies.WorldScale);

		box2dWorld = new World(new Vector2(0, 0), true);
		box2DDebugRenderer = new Box2DDebugRenderer();

		MapBodyBuilder.buildShapes(map, Zombies.PhysicsDensity / Zombies.WorldScale, box2dWorld);
					
		loadGates();
		loadPlayer(spawnEntryID);
		loadObjects();
		initLights();

		//There is a 3/10 chance that the cure power up will be spawned (in stages with spawn points)
		if(Math.random() < 0.3)
            spawnCurePowerUp();
        else
            logger.fine("Will not attempt to spawn cure item due to random probability");
								
		camera = new OrthographicCamera();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		customContactListener = new CustomContactListener(this);
		box2dWorld.setContactListener(customContactListener);

		gamePaused = false;

        pauseMenu = new PauseMenu(this);
        
        antidoteSpawn = true;
	}

	public static Logger getLogger() {
	    return logger;
    }

	private void initLogging() {
        try {
            handler = new FileHandler("zombies.log");
            handler.setFormatter(new SimpleFormatter());
            logger = Logger.getLogger("com.mygdx.zombies");
            logger.setLevel(java.util.logging.Level.ALL);
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	String getPath() {
		return path;
	}
	
	int getSpawnEntryID() {
		return spawnEntryID;
	}
	
	public ArrayList<Enemy> getEnemiesList() {
		return enemiesList;
	}
	
	public ArrayList<Projectile> getBulletsList() {
		return bulletsList;
	}

	public ArrayList<NPC> getNPCsList() {return npcsList; }
	
	/**
	 * Load the player in the position associated with spawnEntryID
	 * @param spawnEntryID - the id of the entry to spawn the player at
	 */
	private void loadPlayer(int spawnEntryID) {
		int x, y;
		x = y = 300;
				
		if(spawnEntryID != -1)
		{
			MapObjects objects = map.getLayers().get("Entries").getObjects();
			//Parse entries until an entry with the correct ID is found
			//Then get x and y coordinates and supply to player
			for(MapObject object : objects) {	
				MapProperties p = object.getProperties();
				int entryID = (Integer) p.get("EntryID");		
				if(entryID==spawnEntryID) {
					x = ((Float) p.get("x")).intValue();
					y = ((Float) p.get("y")).intValue();			
					break;
				}
			}
		}	
		
		x*= Zombies.WorldScale;
		y*= Zombies.WorldScale;

        logger.fine("Attempting to insert player at position (" + x + ", " + y + ")");
		player = new Player(this, x, y);
	}
	
	/**
	 * Method to parse map gates
	 */
	private void loadGates() {
		
		//Gte gates layer
		MapObjects objects = map.getLayers().get("Gates").getObjects();
		
		for(MapObject object : objects) {
			
			//Get object properties
			MapProperties p = object.getProperties();
			int x = ((Float) p.get("x")).intValue();
			int y = ((Float) p.get("y")).intValue();
			int width = ((Float) p.get("width")).intValue();
			int height = ((Float) p.get("height")).intValue();
			String destination = (String) p.get("Destination");
			int entryID = (Integer) p.get("EntryID");
			
			//Scale coordinates
			x*= Zombies.WorldScale;
			y*= Zombies.WorldScale;
			
			//Add the gate to the world
			Gate gate = new Gate(box2dWorld, new Rectangle(x, y, width, height),
					StateID.valueOf(destination), entryID);
			gatesList.add(gate);
		}
	}

	/**
	 * Method to parse map objects, such as power ups, weapons, enemies and NPCs
	 */
	private void loadObjects() {
		
		//Get objects layer
		MapObjects objects = map.getLayers().get("Objects").getObjects();
		
		//Iterate objects
		for(MapObject object : objects) {
			
			//Retrieve properties
			MapProperties p = object.getProperties();
			int x = ((Float) p.get("x")).intValue();
			int y = ((Float) p.get("y")).intValue();
			
			//Scale coordinates
			x*= Zombies.WorldScale;
			y*= Zombies.WorldScale;

			//Added the object, using the name as an identifier
            String name = object.getName();
            logger.fine("Found object " + name + " at position (" + x + ", " + y + ")");

			switch(name) {
				case "powerUpHealth":
					pickUpsList.add(new PickUp(this, x, y, "pickups/heart.png",
							new PowerUp(0, 2, 0, false, false), InfoContainer.BodyID.PICKUP));
				break;
				
				case "powerUpSpeed":
					pickUpsList.add(new PickUp(this, x, y, "pickups/speed.png",
							new PowerUp(1, 0, 0, false, false), InfoContainer.BodyID.PICKUP));
				break;
				
				case "powerUpStealth":
					pickUpsList.add(new PickUp(this, x, y, "pickups/stealth.png",
							new PowerUp(0, 0, 1, false, false), InfoContainer.BodyID.PICKUP));
				break;
				
				case "lasergun":
					pickUpsList.add(new PickUp(this, x, y, "pickups/pistol.png",
							new RangedWeapon(this, 10, Projectile.ProjectileType.LASER), InfoContainer.BodyID.WEAPON));
				break;
				
				case "pistol":
					pickUpsList.add(new PickUp(this, x, y, "pickups/pistol.png",
							new RangedWeapon(this, 15, Projectile.ProjectileType.BULLET), InfoContainer.BodyID.WEAPON));
				break;
				
				case "sword":
					pickUpsList.add(new PickUp(this, x, y, "sword.png",
							new MeleeWeapon(worldBatch), InfoContainer.BodyID.WEAPON));
				break;
				
				case "zombie1":
					enemiesList.add(new Enemy(this, x, y, "zombie/zombie1.png", 6, 5));
				break;
				
				case "zombie2":
					enemiesList.add(new Enemy(this, x, y, "zombie/zombie2.png", 5, 15));
				break;
				
				case "zombie3":
					enemiesList.add(new Enemy(this, x, y, "zombie/zombie3.png", 10, 5));
				break;
				
				case "NPC":
					npcsList.add(new NPC(this, x, y));
				break;
				
				case "boss1":
					enemiesList.add(new Boss1(this, x, y));
				break;

				case "boss2":
					enemiesList.add(new Boss2(this, x,y));
				break;

				// code for assessment 3
				case "gatePointer":
					String direction = (String) p.get("Direction");
					gatePointerList.add(new GatePointer(this,x,y,"gatePointer.png", GateDirection.valueOf(direction) ));
				break;

				case "potentialCureSpawnPoint":
					potentialCureSpawnPointList.add(new Point(x, y));
				break;

				default:
					logger.severe("Error importing stage: unrecognised object '" + name + "'");
					throw new IllegalArgumentException();
			}
		}
	}
	
	public void loadAntidote(){
		
		int x = player.getPositionX();
		int y = player.getPositionX();
		
		pickUpsList.add(new PickUp(this, x+10, y+10, "pickups/cure.png",
				new PowerUp(0, 0, 0, false, true), InfoContainer.BodyID.PICKUP));
	}

	/**
	 * Spawn the cure power up randomly in one of the potential cure spawn points
	 * Must be called after loadObjects() or potentialCureSpawnPointList will not be populated
	 */
	private void spawnCurePowerUp() {
		//Randomly select a spawn point in the stage.
		//Must be called after loadObjects() to ensure that any potential spawn points for the cure have been loaded

		//If this stage has no potential cure spawn points, do not attempt to spawn cure
		if(potentialCureSpawnPointList.isEmpty()) {
		    logger.fine("Not spawning the cure as the level has no cure spawn points loaded");
            return;
        }

		//Select a random one of the loaded potential cure spawn points to spawn the cure in
		int randomSpawnIndex = (int) ((potentialCureSpawnPointList.size()-1)*Math.random());
		Point spawnPoint = potentialCureSpawnPointList.get(randomSpawnIndex);
		pickUpsList.add(new PickUp(this, spawnPoint.x, spawnPoint.y, "pickups/cure.png",
				new PowerUp(0, 0, 0, true, false), InfoContainer.BodyID.PICKUP));
		logger.fine("Successfully spawned the cure item at " + spawnPoint.toString());
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width * Zombies.InitialViewportWidth / (float) Zombies.InitialViewportWidth;
		camera.viewportHeight = height;
		camera.update();
	}

	/**
	 * added for assessment 3, return true if level is finished
	 */
	public boolean isGateOpen(){
		//gate is open if there are no remaining enemies
		return enemiesList.size() == 0;
	}

	/**
	 * Initialise lighting, loading from the map
	 */
	private void initLights() {
		
		//Set up rayhandler
		rayHandler = new RayHandler(box2dWorld);
		rayHandler.setShadows(true);
		rayHandler.setAmbientLight(.4f);
		lightsList = new ArrayList<>();
		
		//Parse tiled map light objects
		MapObjects objects = map.getLayers().get("Lights").getObjects();
				
				for(MapObject object : objects) {
					
					//Get object properties
					MapProperties p = object.getProperties();
					int x = ((Float) p.get("x")).intValue();
					int y = ((Float) p.get("y")).intValue();
					
					x *=  Zombies.WorldScale;
					y *=  Zombies.WorldScale;
					
					
					Color color;
					int distance;
					
					//Set attributes based on light type
                    String name = object.getName();
                    logger.fine("Attempting to insert light " + name + " at position (" + x + ", " + y + ")");

					switch(name) {
						case "street":
							color = Color.ORANGE;
							distance = 250;
							break;
						case "security":
							color = Color.CYAN;
							distance = 120;
							break;
						case "red":
							color = Color.FIREBRICK;
							distance = 80;
							break;
						case "torch":
							color = Color.GREEN;
							distance = 80;
							break;
						default:
                            logger.severe("Error importing stage: unrecognised light '" + name + "'");
							throw new IllegalArgumentException();
					}		
					
					//Add light to list
					lightsList.add(new PointLight(rayHandler, 20, color, distance, x, y));
				}
	}
	
	/**
	 * @return true if the player is within the radius of any of the lights
	 */
	private boolean inLights() {
		//Iterate through lights
		for (PointLight light : lightsList)
			//Calculate distance between each light and player
			if (Zombies.distanceBetween(new Vector2(player.getPositionX(), player.getPositionY()), new Vector2(light.getX(), light.getY()))
					< light.getDistance())
				return true;
		return false;
	}

	/**
	 * Call to draw level to screen
	 */
	@Override
	public void render() {
		//*Code for Assessment 3*
	    if(gamePaused){
	        //#changed4 Removed code from here which instantiated pauseMenu with every call to render(), moved to constructor
            pauseMenu.render();
            return;
        }

        //Render map
        renderer.setView(camera);
	    renderer.render();


		//Render world
		worldBatch.setProjectionMatrix(camera.combined);
		worldBatch.begin();
		//Draw player
		player.render();
		//Draw mobs and game objects
		for (int i = 0; i < enemiesList.size(); i++)
			enemiesList.get(i).render();
		for (Projectile bullet : bulletsList)
			bullet.render();
		for(PickUp pickUp : pickUpsList)
			pickUp.render();
		for(NPC npc : npcsList)
			npc.render();
		for (GatePointer gatePointer : gatePointerList)
			gatePointer.render();
		worldBatch.end();
		
		//if(antidote != null) {
		//	antidote.render();
		//}
		
		//Render lighting
		rayHandler.render();
		
		//Render HUD
		UIBatch.begin();
		player.hudRender();
		UIBatch.end();

		//Enable this line to show Box2D physics debug info
        box2DDebugRenderer.render(box2dWorld, camera.combined.scl(Zombies.PhysicsDensity));
	}
		
	public World getBox2dWorld() {
		return box2dWorld;
	}

	@Override
	public void update(float delta) {
		//Method to update everything in the state

        if(gamePaused) {
            pauseMenu.update(delta);
            return;
        }

            //Update the camera position
            camera.position.set(player.getPositionX(), player.getPositionY(), 0);
            camera.update();

            //Update Box2D physics
            box2dWorld.step(1 / 60f, 6, 2);

            //Update mobs
            for (int i = 0; i < enemiesList.size(); i++)
                enemiesList.get(i).update(this.inLights());
            for (NPC npc : npcsList)
                npc.update(delta);
            //Update GatePointer, added for assessment 3
            for (GatePointer pointer : gatePointerList)
                pointer.update(player.getGate());

            //Remove deletion flagged objects
            Entity.removeDeletionFlagged(enemiesList);
            Entity.removeDeletionFlagged(bulletsList);
            Entity.removeDeletionFlagged(pickUpsList);
            Entity.removeDeletionFlagged(npcsList);
            Entity.removeDeletionFlagged(gatesList);

            //Update Box2D lighting
            rayHandler.setCombinedMatrix(camera);
            rayHandler.update();



		//Update player
		player.update(camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)));

        //*Code for Assessment 3*
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            gamePaused = true;
            System.out.println("Game is paused");
        }
        //*Code for Assessment 3*
        
        if(player.isZombie() && antidoteSpawn) {
        	loadAntidote();
        	antidoteSpawn = false;
        }
	}
	
	public Player getPlayer() {
		return player;
	}

	/**
	 * #changed4 Added this method to allow the game to be resumed whilst restricting mutation of gamePaused
	 */
	void resumeGame() {
		gamePaused = false;
	}

	@Override
	public void dispose() {
		super.dispose();
		//Clean up memory
		rayHandler.dispose();
		renderer.dispose();
		map.dispose();

		//Force step here to prevent concurrency synchronisation issues with Box2D thread when disposing
        box2dWorld.step(0, 1, 1);

        Array<Body> bodies = new Array<>();
        box2dWorld.getBodies(bodies);
        for(Body body : bodies)
            box2dWorld.destroyBody(body);

        if(handler != null) {
            logger.fine("Disposed of level successfully");
            handler.close();
        }

		//box2DDebugRenderer.dispose();
	}
}
