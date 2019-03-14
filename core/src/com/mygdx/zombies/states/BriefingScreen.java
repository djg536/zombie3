package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * Screen which explains to the player the premise with text and artwork
 */
public class BriefingScreen extends State {

	private Sprite banner;

	//#changed4 added cure texture to the screen
	private Texture cure;
	
	/**
	 * The constructor for the screen
	 */
	BriefingScreen() {
		super();
		//Load and set up artwork
		banner = new Sprite(new Texture("header.jpg"));
		banner.setScale(4.3f);		
		banner.setPosition(0, Zombies.InitialWindowHeight-banner.getHeight());

		//#changed4 added loading of the cure texture
		cure = new Texture("pickups/cure.png");
	}

	@Override
	public void update(float delta) {
		//If left mouse button pressed, go to the next screen
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			StateManager.loadState(StateID.PLAYERSELECTMENU);
		}
	}
	
	@Override
	public void render() {
		UIBatch.begin();
		//Draw artwork
		banner.draw(UIBatch);
		//Draw text which explains the premise
		Zombies.mainFont.draw(UIBatch, "The apocalyse has arrived, and The University of\n"
									 + "York is now swarming with zombies. There are few\n"
									 + "survivors, but the fight is not over yet.\n\n"
									 + "Rumour has it that there is a    cure, somewhere.\n"
									 + "Be careful!", 20, 340);
		Zombies.mainFont.draw(UIBatch, "Click to continue", 424, 50);

		//#changed4 added drawing of the cure texture
		UIBatch.draw(cure, 750, 132);

		UIBatch.end();
	}
	
	@Override
	public void dispose() {
		//#changed4 Added the following line to prevent memory leakage
		super.dispose();

		//Clean up memory
		banner.getTexture().dispose();
		cure.dispose();
	}
}
