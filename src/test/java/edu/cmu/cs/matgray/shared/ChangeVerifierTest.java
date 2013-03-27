/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared;

import edu.cmu.cs.matgray.shared.exception.IllegalMoveException;
import edu.cmu.cs.matgray.shared.interfaces.*;
import edu.cmu.cs.matgray.shared.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * ChangeVerifier Tester.
 *
 * @author Mathew Gray
 */
public class ChangeVerifierTest {

    GameImpl game1;
    ChangeRequest game1Request1;

    private void initializeGame1() throws IllegalMoveException {

        game1 = new GameImpl("game1");
        game1.addPlayer("fooName", "foo", "foo");
        game1.addPlayer("barName", "bar", "bar");

        Set<ScrabblePiece> pieces = new HashSet<ScrabblePiece>(20);

        for (int i = 0; i < 20; i++) {
            ScrabblePiece p = new ScrabblePieceImpl("a", 1);
            pieces.add(p);
        }

        game1.setAvailablePieces(pieces);

        game1Request1 = new ChangeRequestImpl(game1, "foo");

        Player foo = new PlayerImpl("fooName", "foo", "bar", 0);

        Player bar = new PlayerImpl("barName", "bar", "bar", 0);

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

        game1.getGameBoard().setSlotAtLocation(l, new SlotImpl(Slot.SLOT_TYPE
                .ROOT, 1));

        game1.getGameBoard().getSlotAtLocation(l).setPiece(h, bar);

        // Put i at (1,1)
        Location l2 = new LocationImpl(1, 1);
        ScrabblePiece i = new ScrabblePieceImpl("i", 3);

        game1.getGameBoard().getSlotAtLocation(l2).setPiece(i, bar);

        /**
         * Foo adds:
         * M (0,1)
         * N (2,1)
         * E (3,1)
         */

        Location l3 = new LocationImpl(0, 1);
        Location l4 = new LocationImpl(2, 1);
        Location l5 = new LocationImpl(3, 1);

        ScrabblePiece m = new ScrabblePieceImpl("m", 2);
        ScrabblePiece n = new ScrabblePieceImpl("n", 2);
        ScrabblePiece e = new ScrabblePieceImpl("e", 2);

        resetFooPieces();

        game1Request1.addToBoard(l3, m);
        game1Request1.addToBoard(l4, n);
        game1Request1.addToBoard(l5, e);
    }

    private void resetFooPieces() {
        Player foo = game1.getPlayers().get("foo");
        ScrabblePiece m = new ScrabblePieceImpl("m", 2);
        ScrabblePiece n = new ScrabblePieceImpl("n", 2);
        ScrabblePiece e = new ScrabblePieceImpl("e", 2);

        List<ScrabblePiece> fooPieces = new ArrayList<ScrabblePiece>(3);
        fooPieces.add(m);
        fooPieces.add(n);
        fooPieces.add(e);

        foo.addToPieces(fooPieces);
    }

    @Before
    public void before() throws Exception {
        initializeGame1();
    }

    /**
     * Method: addToDictionary(String s)
     */
    @Test
    public void testAddToDictionary() {
        ChangeVerifier changeVerifier = new ChangeVerifier();
        Set<String> invalidWords = changeVerifier.getInvalidWords(game1
                .getGameBoard(), game1Request1);

        assertEquals(2, invalidWords.size());
        assertTrue(invalidWords.contains("mine"));
        assertTrue(invalidWords.contains("hi"));

        changeVerifier.addToDictionary("mine");
        changeVerifier.addToDictionary("hi");

        invalidWords = changeVerifier.getInvalidWords(game1
                .getGameBoard(), game1Request1);

        assertEquals(0, invalidWords.size());
    }

    /**
     * Method: getInvalidWords(Board board, ChangeRequest changeRequest)
     */
    @Test
    public void testGetInvalidWords() {
        ChangeVerifier changeVerifier = new ChangeVerifier();
        Set<String> invalidWords = changeVerifier.getInvalidWords(game1
                .getGameBoard(), game1Request1);

        assertEquals(2, invalidWords.size());
        assertTrue(invalidWords.contains("mine"));
        assertTrue(invalidWords.contains("hi"));
    }

    /**
     * Method: areValidChanges(ChangeRequest changeRequest, Player player, Board board)
     */
    @Test
    public void testAreValidChanges() throws IllegalMoveException {
        ChangeVerifier changeVerifier = new ChangeVerifier();
        Player foo = game1.getPlayers().get("foo");
        boolean b = changeVerifier.areValidChanges(game1Request1, foo,
                game1.getGameBoard());
        assertTrue(b);

        Location notConnected = new LocationImpl(6, 6);
        ScrabblePiece piece = new ScrabblePieceImpl("a", 1);

        game1Request1.addToBoard(notConnected, piece);
        b = changeVerifier.areValidChanges(game1Request1, foo,
                game1.getGameBoard());
        assertFalse(b);

        Board b2 = new BoardImpl();
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                b2.setSlotAtLocation(new LocationImpl(i, j),
                        new SlotImpl(Slot.SLOT_TYPE.REGULAR, 1));
            }
        }
        Game g = new GameImpl(b2);
        g.addPlayer("foo", "foo", "foo");
        Player fooPlayer = g.getPlayers().get("foo");
        ChangeRequest gcr = new ChangeRequestImpl(g, "foo");
        fooPlayer.addToCards(Card.CARD_TYPE.ADD_ROOT);
        gcr.addCardToBoard(new LocationImpl(0, 0),
                new CardImpl(Card.CARD_TYPE.ADD_ROOT, "root", 10));
        gcr.addToBoard(new LocationImpl(0, 0), new ScrabblePieceImpl("a", 100));
        assertTrue(new ChangeVerifier().areValidChanges(gcr, fooPlayer, b2));
    }
}
