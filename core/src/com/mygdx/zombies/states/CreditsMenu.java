package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * Menu to display the game credits
 */
public class CreditsMenu extends State {
	
	private Texture background;
	private Button back;

	/**
	 * Constructor for the menu
	 */
	CreditsMenu() {
		super();
		//Load menu background
		background = new Texture("background.jpg");
		
		//Set up button
		back = new Button(UIBatch, 500, 10, "Back");
	}

	@Override
	public void render() {
		UIBatch.begin();
		UIBatch.draw(background, 0, 0);
		//Draw background
		back.render();
		//Draw title and credits text to the screen
		Zombies.titleFont.draw(UIBatch, "Credits", 500, 700);
		Zombies.creditsFont.draw(UIBatch, "This game was created by Yeezy Games \n& Geese Lightning"
				+ "\nDevelopment Team:"
				+ "\nGurveer Gawera, Billy Macleod, Rafee Jenkins,"
				+ "\nAndy McIsaac, Henry Gray, David Gillman,"
				+ "\nTom Elvidge, Jed Fenn, Mikito Leong,"
				+ "\nAga Nowak, Yuxi Zhang and Nikita Svyetov", 150, 550);
		UIBatch.end();
	}

	@Override
	public void update(float delta) {
		//Check for clicking of buttons
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			if (back.isHover()) {
				//Play sound and go to next screen
				Zombies.soundSelect.play();
				StateManager.loadState(StateID.MAINMENU, aliveNPC);
			}
		}
	}
	
	@Override
	public void dispose() {
		//#changed4 Added the following line to prevent memory leakage
		super.dispose();

		background.dispose();
		back.dispose();
	}
}
