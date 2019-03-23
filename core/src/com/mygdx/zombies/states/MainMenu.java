package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * The main menu class, with buttons linking to various functionality
 */
public class MainMenu extends State {

	private Button play;
	private Button exit;
	private Button options;
	private Button credits;
	// Added minigame button for assessment 3
	private Button minigame;
	private Texture background;
	private Texture logo;

	/**
	 * Constructor for the main menu
	 */
	MainMenu() {
		super();
		//Load textures
		background = new Texture("background.jpg");
		logo = new Texture("logo.png");
		
		//Initialise buttons
		play = new Button(UIBatch, 325, 350, "Play");
		exit = new Button(UIBatch, 675, 200, "Exit");
		options = new Button(UIBatch, 675, 350, "Options");
		credits = new Button(UIBatch, 325, 200, "Credits");
		minigame = new Button(UIBatch, 500, 50, "Mini-game");
	}

	@Override
	public void render() {
		UIBatch.begin();
		UIBatch.draw(background, 0, 0);
		//Render buttons
		play.render();
		exit.render();
		options.render();
		credits.render();
		minigame.render();
		//Render textures
		Zombies.titleFont.draw(UIBatch, "Silence Of The Lamberts", 225, 650);
		UIBatch.draw(logo, 1050, 10);
		UIBatch.end();		
	}

	@Override
	public void update(float delta) {
		//Code to handle button click events
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			if (play.isHover()) {
				Zombies.soundSelect.play();
				//Start playing ambient sound
				Zombies.soundAmbientWind.loop();
				StateManager.loadState(StateID.BRIEFINGSCREEN, aliveNPC);
			}
			else if (credits.isHover()) {
				Zombies.soundSelect.play();
				StateManager.loadState(StateID.CREDITSMENU, aliveNPC);
			}
			else if (options.isHover()) {
				Zombies.soundSelect.play();
				StateManager.loadState(StateID.OPTIONSMENU, aliveNPC);
			}
			else if (exit.isHover()) {
				//Quit the game
				Gdx.app.exit();
			}
			// Added minigame button for assessment 3
			else if (minigame.isHover()) {
				Zombies.soundSelect.play();
				StateManager.loadState(StateID.MINIGAMEMM, aliveNPC);
			}
		}
	}

	@Override
	public void dispose() {
		//Clear the memory

		//#changed4 Added the following line to prevent memory leakage
		super.dispose();

		play.dispose();
		exit.dispose();
		options.dispose();
		credits.dispose();
		background.dispose();
		logo.dispose();
	}
}
