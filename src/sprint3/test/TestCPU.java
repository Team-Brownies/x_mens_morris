//package sprint3.test;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import sprint3.product.Cell;
//import sprint3.product.Game.GameState;
//import sprint3.product.Game.NineMMGame;
//import sprint3.product.Player.CPUPlayer;
//import sprint3.product.Player.HumanPlayer;
//
//import static org.junit.Assert.*;
//
//public class TestCPU {
//
//	private NineMMGame board;
//
//	@Before
//	public void setUp() throws Exception {
//		board = new NineMMGame();
//		board.setRedPlayer(new HumanPlayer('R', board));
//		board.setBluePlayer(new CPUPlayer('B', board, true));
//		board.getTurnPlayer().placePiece(0, 0);//red
//		board.getTurnPlayer().placePiece(6, 0);//blue
//		board.getTurnPlayer().placePiece(0, 6);//red
//		board.getTurnPlayer().placePiece(6, 6);//blue
//	}
//
//	private void placeRest() {
//		board.getTurnPlayer().placePiece(6, 3);//red
//		board.getTurnPlayer().placePiece(0, 3);//blue
//		board.getTurnPlayer().placePiece(1, 1);//red
//		board.getTurnPlayer().placePiece(5, 1);//blue
//
//		board.getTurnPlayer().placePiece(1, 5);//red
//		board.getTurnPlayer().placePiece(5, 5);//blue
//		board.getTurnPlayer().placePiece(3, 0);//red
//		board.getTurnPlayer().placePiece(3, 6);//blue
//
//		board.getTurnPlayer().placePiece(4, 2);//red
//		board.getTurnPlayer().placePiece(2, 2);//blue
//		board.getTurnPlayer().placePiece(4, 4);//red
//		board.getTurnPlayer().placePiece(2, 4);//blue
//
//		board.getTurnPlayer().placePiece(2, 3);//red
//		board.getTurnPlayer().placePiece(4, 3);//blue
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	// acceptance criterion 12.1
//	@Test
//	public void testFromMillPlacing() {
//		assertEquals("", board.getTurnPlayer().getColor(), 'R');
//		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.PLACING);
//		Cell cell = board.getCell(6, 3);
//		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
//
//		board.getBluePlayer().setCpu(true);
//		board.getTurnPlayer().placePiece(1, 1);//red
//
//		assertEquals("", board.getCell(6, 3), Cell.BLUE);
//	}
//
//
//	@Test
//	public void testFromMillMoving() {
//		placeRest();
//		assertEquals("", board.getTurnPlayer().getColor(), 'R');
//		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.MOVING);
//		Cell cell = board.getCell(5, 3);
//		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
//
//		board.getBluePlayer().setCpu(true);
//		board.getTurnPlayer().movePiece(3, 1, 3, 0);//red
//
//		cell = board.getCell(4, 3);
//		assertEquals("", board.getCell(5, 3), Cell.BLUE);
//		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
//	}
//
//	// acceptance criterion 12.2
//	@Test
//	public void testFromBlockPlacing() {
//		assertEquals("", board.getTurnPlayer().getColor(), 'R');
//		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.PLACING);
//		Cell cell = board.getCell(0, 3);
//		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
//
//		board.getBluePlayer().setCpu(true);
//		board.getTurnPlayer().placePiece(6, 3);//red
//
//		assertEquals("", board.getCell(0, 3), Cell.BLUE);
//	}
//	@Test
//	public void testFromBlockMoving() {
//		placeRest();
//		assertEquals("", board.getTurnPlayer().getColor(), 'R');
//		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.MOVING);
//		Cell cell = board.getCell(5, 3);
//		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
//
//		board.getBluePlayer().setCpu(true);
//		board.getTurnPlayer().movePiece(5, 3, 6, 3);//red
//
//		cell = board.getCell(0, 3);
//		assertEquals("", board.getCell(1, 3), Cell.BLUE);
//		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
//	}
//
//	// acceptance criterion 12.3
//	@Test
//	public void testFromRemovePlace() {
//		board.getBluePlayer().setCpu(true);
//		board.getTurnPlayer().placePiece(3, 0);//red
//
//		assertEquals("", board.getCell(6, 3), Cell.BLUE);
//		// 0,0 or 0,6 will get removed
//		Cell cell = board.getCell(0, 0);
//		boolean firstCell = cell == Cell.EMPTY || cell == Cell.MOVEVALID;
//		cell = board.getCell(0, 6);
//		boolean secondCell = cell == Cell.EMPTY || cell == Cell.MOVEVALID;
//		assertTrue("",firstCell || secondCell);
//	}
//	@Test
//	public void testFromRemoveMoving() {
//		placeRest();
//
//		board.getBluePlayer().setCpu(true);
//		board.getTurnPlayer().movePiece(3, 1, 3, 0);//red
//
//		// 1,1 or 1,5 will get removed
//		Cell cell = board.getCell(1, 1);
//		boolean firstCell = cell == Cell.EMPTY || cell == Cell.MOVEVALID;
//		cell = board.getCell(1, 5);
//		boolean secondCell = cell == Cell.EMPTY || cell == Cell.MOVEVALID;
//		assertTrue("",firstCell || secondCell);
//	}
//}
