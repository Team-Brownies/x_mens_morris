package sprint3.test;

import org.junit.Before;
import org.junit.Test;

import sprint3.product.CheckMill;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Cell;

import static org.junit.Assert.*;

public class TestCheckMill {
    Cell[][] grid;
    @Before
    public void setUp() {
        NineMMGame game = new NineMMGame();
        grid = game.getGrid();
    }
    @Test
    public void testCorrectMillFormation1() {
        //precondition
        grid[0][0] = Cell.RED;
        grid[3][0] = Cell.RED;
        grid[6][0] = Cell.RED;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertTrue(checkMill.checkMillAllDirections(3,0));
    }

    @Test
    public void testInCorrectMillFormation1() {
        //precondition
        grid[0][0] = Cell.BLUE;
        grid[3][0] = Cell.RED;
        grid[6][0] = Cell.RED;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertFalse(checkMill.checkMillAllDirections(3,0));
    }

    @Test
    public void testCorrectMillFormation2() {
        //precondition
        grid[0][0] = Cell.RED;
        grid[0][3] = Cell.RED;
        grid[0][6] = Cell.RED;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertTrue(checkMill.checkMillAllDirections(0,3));
    }

    @Test
    public void testInCorrectMillFormation2() {
        //precondition
        grid[0][0] = Cell.RED;
        grid[0][3] = Cell.BLUE;
        grid[0][6] = Cell.RED;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertFalse(checkMill.checkMillAllDirections(0,3));
    }

    @Test
    public void testCorrectMillFormation3() {
        //precondition
        grid[0][0] = Cell.RED;
        grid[3][0] = Cell.RED;
        grid[6][0] = Cell.RED;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertTrue(checkMill.checkMillAllDirections(0,0));
    }

    @Test
    public void testInCorrectMillFormation3() {
        //precondition
        grid[0][0] = Cell.BLUE;
        grid[3][0] = Cell.RED;
        grid[6][0] = Cell.BLUE;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertFalse(checkMill.checkMillAllDirections(0,0));
    }

    @Test
    public void testCorrectMillFormation4() {
        //precondition
        grid[0][0] = Cell.RED;
        grid[0][3] = Cell.RED;
        grid[0][6] = Cell.RED;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertTrue(checkMill.checkMillAllDirections(0,6));
    }

    @Test
    public void testInCorrectMillFormation4() {
        //precondition
        grid[0][0] = Cell.BLUE;
        grid[0][3] = Cell.RED;
        grid[0][6] = Cell.RED;
        CheckMill checkMill = new CheckMill(grid);
        //test assertions
        assertFalse(checkMill.checkMillAllDirections(0,6));
    }

}