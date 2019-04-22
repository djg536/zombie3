package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.mygdx.zombies.Zombies;

/**
 * Class for managing and switching game states
 */
public class StateManager {

	private static State currentState;

	//Enum of state ids, used to identify different types of state
	//#changed4 Removed the PAUSEMENU entry from the enum
	public enum StateID {
		MAINMENU, CREDITSMENU, OPTIONSMENU, ENDSCREEN, BRIEFINGSCREEN,
		PLAYERSELECTMENU, STAGE1, STAGE2, STAGE3,
		// Added new StateID's for assessment 3
		STAGE4, STAGE5, STAGE6, UDIED, UWIN,
		// Running the minigame from the main menu (MINIGAMEMM) or from ingame (MINIGAMEIG)
		MINIGAMEMM, MINIGAMEIG

	}

	//Enum of directions, used to point GatePointer towards gate, added for assessment3
	public enum GateDirection {
		UP,RIGHT,DOWN,LEFT
	}

	public static State getCurrentState() {
		return currentState;
	}

	/**
	 * Constructor for the state manager
	 */
	public StateManager() {
		//Load the main menu first, when the game starts
		currentState = new MainMenu();
	}

	/**
	 * Method is run when the game window is resized
	 * @param width - the new window width
	 * @param height - the new window height
	 */
	public void resize(int width, int height) {
		currentState.resize(width, height);
	}

	/**
	 * Load a new state, clearing the memory of the old state
	 * @param newState - the new state to load
	 */
	public static void loadState(State newState) {
		currentState.dispose();
		currentState = newState;
	}

	/**
	 * Load the state associated with the given state id
	 * @param stateID - the state id to identify the state to load
	 * @param aliveNPC - pass through how many NPCs are to go to the next state
	 */
	public static void loadState(StateID stateID, boolean aliveNPC) {
		loadState(stateID, -1, aliveNPC);
	}
	
	/**
	 * Load the state associated with the given state id and pass the entry id
	 * #changed4 removed the PAUSEMENU case from the switch statement
	 * #changed4 added aliveNPC parameter
	 * @param stateID - the state id to identify the state to load
	 * @param entryID - the entry id to pass
	 * @param aliveNPC - pass through how many NPCs are to go to the next state
	 */
	public static void loadState(StateID stateID, int entryID, boolean aliveNPC) {
		
		State tempState = null;
		//Switch statement to run unique load code for each state
		switch(stateID) {
			case MAINMENU:
				tempState = new MainMenu();
				break;
			case CREDITSMENU:
				tempState = new CreditsMenu();
				break;
			case OPTIONSMENU:
				tempState = new OptionsMenu();
				break;
			case ENDSCREEN:
				tempState = new EndScreen();
				break;
			case PLAYERSELECTMENU:
				tempState = new PlayerSelectMenu();
				break;
			case BRIEFINGSCREEN:
				tempState = new BriefingScreen();
				break;
			case STAGE1:
				tempState = new Level("World_One", entryID, aliveNPC);
				break;
			case STAGE2:
				tempState = new Level("World_Two", entryID, aliveNPC);
				break;
			case STAGE3:
				tempState = new Level("World_Three", entryID, aliveNPC);
				break;
			// Added more case statements for new StateID's on assessment 3
			case UDIED:
				tempState = new UDiedScreen((Level)currentState);
				break;
			case UWIN:
				tempState = new UWinScreen();
				break;
			case STAGE4:
				tempState = new Level("World_Four", entryID, aliveNPC);
				break;
			case STAGE5:
				tempState = new Level("World_Five", entryID, aliveNPC);
				break;
			case STAGE6:
				tempState = new Level("World_Six", entryID, aliveNPC);
				break;
            case MINIGAMEMM:
            	// Return to main game when done
                tempState = new MiniGame("menu");
                break;
			case MINIGAMEIG:
				// Restart stage 2 when done
				tempState = new MiniGame("game");
				break;
			default:
				Level.getLogger().severe("Error: Unrecognised gate destination");
				break;
		}

		loadState(tempState);
	}

	/**
	 * Update the current state
	 */
	public void gameLoop(float delta) {
		currentState.update(delta);
	}

	/**
	 * Render the current state
	 */
	public void render() {
		currentState.render();
		Gdx.graphics.setTitle(Zombies.windowTitle +" ["+Gdx.graphics.getFramesPerSecond() + "]");
	}

	/**
	 * Erase the current state and clean the memory
	 */
	public void dispose() {
		currentState.dispose();
	}
}
