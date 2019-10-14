package pt.technic.apps.minesfinder.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.technic.apps.minesfinder.Minefield;

/**
 *
 * @author Gabriel Massadas
 */
public class MinefieldTest {

    Minefield minefield;

    public MinefieldTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        minefield = new Minefield(9, 10, 11);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateValidMinefield() {
        assertEquals("Invalid minefield width", 9, minefield.getWidth());
        assertEquals("Invalid minefield height", 10, minefield.getHeight());
        assertEquals("Invalid minefield mines counter", 11, minefield.getNumMines());
    }

    @Test
    public void testMinefieldWithoutMines() {
        try {
            Minefield minefield = new Minefield(10, 15, 0);
            fail("Created minefield without mines");
        } catch (Exception e) {

        }
    }

    @Test
    public void testMinefieldInicialCovered() {
        for (int col = 0; col < minefield.getWidth(); col++) {
            for (int line = 0; line < minefield.getHeight(); line++) {
                assertEquals("One or more cell's were uncovered",
                        minefield.COVERED,
                        minefield.getGridState(col, line));
            }
        }
    }

    @Test
    public void testRevealCell() {
        minefield.revealGrid(1, 1);
        assertNotEquals("Cell not revealed", minefield.COVERED, minefield.getGridState(1, 1));
    }

    @Test
    public void testFirstplayFree() {
        for (int i = 0; i < 10; i++) {
            Minefield minefield = new Minefield(2, 2, 3);
            minefield.revealGrid(1, 1);
            assertNotEquals("Lost at first play", minefield.BUSTED,
                    minefield.getGridState(1, 1));
            assertTrue("Cell not revealed", minefield.getGridState(1, 1) < 9);
        }
    }

    @Test
    public void testhitMine() {
        Minefield minefield = new Minefield(2, 2, 2);

        assertNotEquals("Game was finished before start", true, minefield.isGameFinished());
        minefield.revealGrid(1, 1);
        assertNotEquals("Lost at first play", minefield.BUSTED, minefield.getGridState(1, 1));

        int play = 0;
        if (minefield.hasMine(0, 0)) {
            minefield.revealGrid(0, 0);
        } else {
            minefield.revealGrid(0, 1);
            play = 1;
        }
        assertEquals("Mine didn't blow after click", minefield.BUSTED, minefield.getGridState(0, play));

        assertEquals("Game didn't ended after blow", true, minefield.isGameFinished());
        assertEquals("Player didn't lost after blow", true, minefield.isPlayerDefeated());

        minefield.revealGrid(0, (play + 1) % 2);
        assertEquals("Cell has revealed after game finished", minefield.COVERED, minefield.getGridState(0, (play + 1) % 2));
    }

    @Test
    public void testMinesAround() {
        Minefield minefield = new Minefield(2, 2, 3);
        minefield.revealGrid(0, 0);
        assertEquals("Didn't count mines correctly", 3, minefield.getGridState(0, 0));
        minefield = new Minefield(3, 3, 7);
        minefield.revealGrid(1, 1);
        assertEquals("Didn't count mines correctly", 7, minefield.getGridState(1, 1));
        minefield = new Minefield(3, 3, 8);
        minefield.revealGrid(1, 1);
        assertEquals("Didn't count mines correctly", 8, minefield.getGridState(1, 1));
    }

    @Test
    public void testRevealCellNeighbors() {
        Minefield minefield = new Minefield(10, 10, 1);
        minefield.revealGrid(5, 5);
        if (minefield.getGridState(5, 5) != minefield.EMPTY) {
            minefield.revealGrid(0, 0);
        }
        int covered = 0;
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (minefield.getGridState(x, y) == minefield.COVERED) {
                    covered++;
                }
            }
        }
        assertTrue("Didn't reveal all cell Neighbors", covered > 0 && covered < 5);
    }

    @Test
    public void testVictory() {
        Minefield minefield = new Minefield(2, 2, 3);
        minefield.revealGrid(1, 1);
        assertTrue("Player didn't win", minefield.isGameFinished() && !minefield.isPlayerDefeated());
    }

    @Test
    public void testMarkCell() {
        minefield.setMineMarked(1, 1);
        assertEquals("Cell hasn't marked", minefield.getGridState(1, 1), minefield.MARKED);
    }

    @Test
    public void testQuestionCell() {
        minefield.setMineQuestion(1, 1);
        assertEquals("Cell hasn't questioned", minefield.getGridState(1, 1), minefield.QUESTION);
    }

    @Test
    public void testUnmarkCell() {
        minefield.setMineMarked(1, 1);
        minefield.setMineCovered(1, 1);
        assertEquals("Cell mark wasn't removed", minefield.getGridState(1, 1), minefield.COVERED);
    }

    private void waitTime(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Logger.getLogger(MinefieldTest.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    private void winGame(Minefield minefield) {
        for (int i = 0; i < minefield.getHeight(); i++) {
            for (int j = 0; j
                    < minefield.getWidth(); j++) {
                if (!minefield.hasMine(j, i)) {
                    minefield.revealGrid(j, i);
                }
            }
        }
    }

    @Test
    public void testGameTimeDuration() {
        Minefield minefield = new Minefield(9, 9, 10);
        assertEquals("Game duration doesn't start at 0", 0, minefield.getGameDuration());
        minefield.revealGrid(0, 0);
        waitTime(10);
        long time1 = minefield.getGameDuration();
        assertTrue("O jogo não está a contar o tempo", time1 > 0);
        waitTime(10);
        long time2 = minefield.getGameDuration();
        assertTrue("O jogo não está a contar o tempo", time2 > time1
        );
        winGame(minefield);
        assertTrue(minefield.isGameFinished()&& !minefield.isPlayerDefeated());
        long time3 = minefield.getGameDuration();
        waitTime(10);
        long time4 = minefield.getGameDuration();
        assertEquals("O tempo continua a contar mesmo depois de terminado", time3, time4);
    }
}
