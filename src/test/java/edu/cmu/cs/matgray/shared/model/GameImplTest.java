/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.exception.IllegalMoveException;
import edu.cmu.cs.matgray.shared.interfaces.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * GameImpl Tester.
 *
 * @author Mathew Gray
 */
public class GameImplTest {

    GameImpl game1;

    private void initializeGame1() throws IllegalMoveException {

        game1 = new GameImpl("game1");
        game1.addPlayer("fooName", "foo", "foo");
        game1.addPlayer("barName", "bar", "foo");

        Set<ScrabblePiece> pieces = new HashSet<ScrabblePiece>(20);

        for (int i = 0; i < 20; i++) {
            ScrabblePiece p = new ScrabblePieceImpl("a", 1);
            pieces.add(p);
        }

        game1.setAvailablePieces(pieces);

        Player bar = new PlayerImpl("barName", "bar", "foo", 0);

        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Location location = new LocationImpl(i, j);
                Slot s = new SlotImpl(Slot.SLOT_TYPE.REGULAR, -1);
                game1.getGameBoard().setSlotAtLocation(location, s);
            }
        }


        // Put h at (0,1)
        Location l = new LocationImpl(1, 0);
        ScrabblePiece h = new ScrabblePieceImpl("h", 2);

        game1.getGameBoard().getSlotAtLocation(l).setPiece(h, bar);

        // Put i at (1,1)
        Location l2 = new LocationImpl(1, 1);
        ScrabblePiece i = new ScrabblePieceImpl("i", 3);

        game1.getGameBoard().getSlotAtLocation(l2).setPiece(i, bar);

    }

    @Before
    public void before() throws IllegalMoveException {
        initializeGame1();
    }

    /**
     * Method: popPieces(int n)
     */
    @Test
    public void testPopPieces() {
        int n = game1.availablePieces().size();
        assertEquals(5, game1.popPieces(5).size());
        assertEquals(n - 5, game1.availablePieces().size());
    }

    /**
     * Method: getAvailableCards()
     */
    @Test
    public void testGetAvailableCards() {
        Card c1 = new CardImpl(Card.CARD_TYPE.ADD_ROOT, "", 10);
        Card c2 = new CardImpl(Card.CARD_TYPE.NEGATIVE_WORD, "", 5);
        Card c3 = new CardImpl(Card.CARD_TYPE.ADD_ROOT, "", 12);
        Set<Card> cards = new HashSet<Card>(3);
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);
        game1.setAvailableCards(cards);

        assertEquals(3, game1.getAvailableCards().size());
    }

    /**
     * Method: isOver()
     */
    @Test
    public void testIsOver() throws Exception {
        GameImpl game2 = new GameImpl();
        assertTrue(game2.isOver());
        game2.addPlayer("foo", "foo", "foo");
        List<ScrabblePiece> pieces = new ArrayList<ScrabblePiece>(1);
        pieces.add(new ScrabblePieceImpl());
        game2.getPlayers().get("foo").addToPieces(pieces);
        assertFalse(game2.isOver());
    }

    /**
     * Method: setAvailableCards(Set<Card> cards)
     */
    @Test
    public void testSetAvailableCards() throws Exception {
        Card c1 = new CardImpl(Card.CARD_TYPE.ADD_ROOT, "", 10);
        Card c2 = new CardImpl(Card.CARD_TYPE.NEGATIVE_WORD, "", 5);
        Card c3 = new CardImpl(Card.CARD_TYPE.ADD_ROOT, "", 12);
        Set<Card> cards = new HashSet<Card>(3);
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);
        game1.setAvailableCards(cards);
        assertEquals(3, game1.getAvailableCards().size());
        assertTrue(game1.getAvailableCards().contains(c1));
        assertTrue(game1.getAvailableCards().contains(c2));
        assertTrue(game1.getAvailableCards().contains(c3));

    }

    /**
     * Method: getName()
     */
    @Test
    public void testGetName() throws Exception {
        GameImpl game2 = new GameImpl("name");
        assertEquals("name", game2.getName());
    }

    /**
     * Method: availablePieces()
     */
    @Test
    public void testAvailablePieces() throws Exception {
        assertEquals(20, game1.availablePieces().size());
        GameImpl game2 = new GameImpl();
        assertEquals(0, game2.availablePieces().size());
    }

    /**
     * Method: setAvailablePieces(Set<ScrabblePiece> pieces)
     */
    @Test
    public void testSetAvailablePieces() throws Exception {
        GameImpl game2 = new GameImpl();
        assertEquals(0, game2.availablePieces().size());
        Set<ScrabblePiece> s = new HashSet<ScrabblePiece>();
        s.add(new ScrabblePieceImpl());
        s.add(new ScrabblePieceImpl());
        s.add(new ScrabblePieceImpl());
        s.add(new ScrabblePieceImpl());

        game2.setAvailablePieces(s);
        assertEquals(4, game2.availablePieces().size());
    }

    /**
     * Method: getPlayers()
     */
    @Test
    public void testGetPlayers() throws Exception {
        assertEquals(2, game1.getPlayers().size());
        Game game2 = new GameImpl();
        assertEquals(0, game2.getPlayers().size());
        game2.addPlayer("asd", "as", "foo");
        assertEquals(1, game2.getPlayers().size());
        assertTrue(game2.getPlayers().containsKey("as"));
    }

    /**
     * Method: nextTurn()
     */
    @Test
    public void testNextTurn() throws Exception {
        GameImpl game2 = new GameImpl();
        game2.addPlayer("foo", "foo", "foo");
        game2.addPlayer("bar", "bar", "foo");
        assertSame(game2.getCurrentTurn().getAndrewID(), "foo");
        game2.nextTurn();
        assertSame(game2.getCurrentTurn().getAndrewID(), "bar");
        game2.nextTurn();
        assertSame(game2.getCurrentTurn().getAndrewID(), "foo");
    }

    /**
     * Method: addPlayer(String name, String andrewID)
     */
    @Test
    public void testAddPlayer() throws Exception {
        GameImpl game2 = new GameImpl();
        game2.addPlayer("foo", "foo", "foo");
        assertNotNull(game2.getPlayers().get("foo"));
    }

    /**
     * Method: getCurrentTurn()
     */
    @Test
    public void testGetCurrentTurn() throws Exception {
        GameImpl game2 = new GameImpl();
        game2.addPlayer("foo", "foo", "foo");
        game2.addPlayer("bar", "bar", "foo");
        assertSame(game2.getCurrentTurn().getAndrewID(), "foo");
        game2.nextTurn();
        assertSame(game2.getCurrentTurn().getAndrewID(), "bar");
        game2.nextTurn();
        assertSame(game2.getCurrentTurn().getAndrewID(), "foo");
    }

    /**
     * Method: getGameBoard()
     */
    @Test
    public void testGetGameBoard() throws Exception {
        Board b = game1.getGameBoard();
        assertEquals("h",
                b.getSlotAtLocation(new LocationImpl(1, 0)).getPiece().getLetter());
    }

}
