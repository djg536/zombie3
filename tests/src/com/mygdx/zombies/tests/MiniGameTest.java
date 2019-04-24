package com.mygdx.zombies.tests;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.zombies.Goose;
import com.mygdx.zombies.states.MiniGame;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import static com.mygdx.zombies.states.State.enableTestingMode;
import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class MiniGameTest {

    // New class for assessment 3
    public MiniGameTest() {
        enableTestingMode();
    }

    @Test
    // Test U1
    public void geeseIsInitiallyEmpty() {
        MiniGame miniGame = new MiniGame(1920, 1080);
        ArrayList<Goose> initialGeese = miniGame.getGeese();
        assertEquals("List of initial geese should be empty", 0, initialGeese.size());
    }

    @Test
    // Test U2
    public void spawnGooseAddsToGeese() {
        MiniGame miniGame = new MiniGame(1920, 1080);
        int initialGeeseSize = miniGame.getGeese().size();
        miniGame.spawnGoose(new Vector2(0,0), 1000);
        int newGeeseSize = miniGame.getGeese().size();
        assertNotEquals("The list of geese should have size one greater after spawning a goose.", newGeeseSize, initialGeeseSize);
    }

    @Test
    // Test U3
    public void generateSpawnIsInWindow() {
        MiniGame miniGame = new MiniGame(1920,1080);
        // Tries 100 spawn points
        for (int i = 0 ; i <= 100 ; i++) {
            Vector2 spawn = miniGame.generateSpawn();
            assertTrue("x is within bounds.", spawn.x >= 32 && spawn.x <= 1888);
            assertTrue("y is within bounds.", spawn.y >= 32 && spawn.y <= 1048);
        }
    }

    @Test
    // Test U4
    public void goosePopUpTimeDecreasesAsGameProgresses() {
        MiniGame miniGame = new MiniGame(1920,1080);

        long popUpTimeOne = miniGame.goosePopUpTime;
        miniGame.timeRemaining -= 20000;
        miniGame.updateGoosePopUpTime();
        long popUpTimeTwo = miniGame.goosePopUpTime;
        miniGame.timeRemaining -= 20000;
        miniGame.updateGoosePopUpTime();
        long popUpTimeThree = miniGame.goosePopUpTime;
        miniGame.timeRemaining -= 10000;
        miniGame.updateGoosePopUpTime();
        long popUpTimeFour = miniGame.goosePopUpTime;

        assertTrue("goosePopUpTime is greater at 60s than 40s.", popUpTimeOne > popUpTimeTwo);
        assertTrue("goosePopUpTime is greater at 40s than 20s.", popUpTimeTwo > popUpTimeThree);
        assertTrue("goosePopUpTime is greater at 20s than 10s.", popUpTimeThree > popUpTimeFour);
    }

    @Test
    // Test U5
    public void spawnIntervalDecreasesAsGameProgresses() {
        MiniGame miniGame = new MiniGame(1920,1080);

        long spawnIntervalOne = miniGame.spawnInterval;
        miniGame.timeRemaining -= 20000;
        miniGame.updateSpawnInterval();
        long spawnIntervalTwo = miniGame.spawnInterval;
        miniGame.timeRemaining -= 20000;
        miniGame.updateSpawnInterval();
        long spawnIntervalThree = miniGame.spawnInterval;
        miniGame.timeRemaining -= 10000;
        miniGame.updateSpawnInterval();
        long spawnIntervalFour = miniGame.spawnInterval;

        assertTrue("spawnInterval is greater at 60s than 40s.",spawnIntervalOne > spawnIntervalTwo);
        assertTrue("spawnInterval is greater at 40s than 20s.", spawnIntervalTwo > spawnIntervalThree);
        assertTrue("spawnInterval is greater at 20s than 10s.", spawnIntervalThree > spawnIntervalFour);
    }

    @Test
    // Test U6
    public void calcPoints200() {
        MiniGame miniGame = new MiniGame(1920,1080);
        Goose goose = new Goose(miniGame, 0, 0, miniGame.goosePopUpTime);
        long gooseTimeRemaining = goose.timeRemaining;
        long points = miniGame.calcPoints(goose);
        assertEquals("Goose should have maximum time remaining.", gooseTimeRemaining, miniGame.goosePopUpTime);
        assertEquals("Should get maximum points for clicking a goose instantly.", 200, points);
    }

    @Test
    // Test U7
    public void calcPoints150() {
        MiniGame miniGame = new MiniGame(1920, 1080);
        Goose goose = new Goose(miniGame, 0, 0, miniGame.goosePopUpTime);
        goose.timeRemaining -= 1500;
        long gooseTimeRemaining = goose.timeRemaining;
        long points = miniGame.calcPoints(goose);
        assertEquals("1.5s passed, time remaining should be 1.5s.", gooseTimeRemaining, miniGame.goosePopUpTime - 1500);
        assertEquals("Should get base 100 points and an extra 50 points clicking it after half the time had passed.",
                150, points);
    }

    @Test
    // Test U8
    public void calcPoints100() {
        MiniGame miniGame = new MiniGame(1920, 1080);
        Goose goose = new Goose(miniGame, 0, 0, miniGame.goosePopUpTime);
        goose.timeRemaining -= 2990;
        long gooseTimeRemaining = goose.timeRemaining;
        long points = miniGame.calcPoints(goose);
        assertEquals("2990ms passed, time remaining should be 10ms", gooseTimeRemaining, miniGame.goosePopUpTime - 2990);
        assertEquals("Should get only the base 100 points.", 100, points);
    }

    @Test
    // Test U9
    public void gooseUpdateTest() throws InterruptedException {
        MiniGame miniGame = new MiniGame(1920, 1080);
        Goose goose = new Goose(miniGame, 0, 0, 3000);

        long initialTimeRemaining = goose.timeRemaining;
        // Allow enough time to pass for time to have changed
        Thread.sleep(100);
        goose.update();
        long newTimeRemaining = goose.timeRemaining;

        assertNotEquals("Updating a goose decrements timeRemaining.", initialTimeRemaining, newTimeRemaining);
    }

    @Test
    // Test U10
    public void gooseDespawnsAfterPopUpTime() throws InterruptedException {
        MiniGame miniGame = new MiniGame(1920,1080);
        miniGame.spawnGoose(new Vector2(0,0), 1000);
        int initialGeeseSize = miniGame.getGeese().size();

        Thread.sleep(2000);
        miniGame.update(1);
        int newGeeseSize = miniGame.getGeese().size();

        assertNotEquals("Goose should be removed from geese when expired.", initialGeeseSize, newGeeseSize);
    }
}
