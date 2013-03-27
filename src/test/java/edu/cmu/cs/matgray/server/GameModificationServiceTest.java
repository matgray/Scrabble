/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.server;

import edu.cmu.cs.matgray.shared.exception.IllegalMoveException;
import edu.cmu.cs.matgray.shared.interfaces.*;
import edu.cmu.cs.matgray.shared.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

/**
 * GameModificationService Tester.
 *
 * @author Mathew Gray
 */
public class GameModificationServiceTest {

    GameImpl game1;
    ChangeRequestImpl game1Request1;

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

        game1Request1 = new ChangeRequestImpl(game1, "foo");

        Player foo = new PlayerImpl("fooName", "foo", "foo", 0);

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
    public void before() throws IllegalMoveException {
        initializeGame1();
    }

    /**
     * Method: placePieces(Game g, ChangeRequest changes)
     */
    @Test
    public void testPlacePieces() throws Exception {

        GameModificationService.placePieces(game1, game1Request1, "foo");

        /**
         * Make sure that the pieces were placed on the board.
         */
        Location x = new LocationImpl(0, 1);
        Location y = new LocationImpl(2, 1);
        Location z = new LocationImpl(3, 1);

        assertEquals(0, game1.getPlayers().get("foo").getPieces().size());
        assertSame("m", game1.getGameBoard().getSlotAtLocation(x).getPiece().
                getLetter());
        assertSame("n", game1.getGameBoard().getSlotAtLocation(y).getPiece()
                .getLetter());
        assertSame("e", game1.getGameBoard().getSlotAtLocation(z).getPiece()
                .getLetter());

        ChangeRequest game1Request2 = new ChangeRequestImpl(game1, "foo");
        game1Request2.addCardToBoard(x, new CardImpl(Card.CARD_TYPE
                .ADD_ROOT, "", 10));
        GameModificationService.placePieces(game1, game1Request2, "foo");
        assertEquals(Card.CARD_TYPE.ADD_ROOT, game1.getGameBoard()
                .getSlotAtLocation(x).getCard().getCardType());

        /**
         * Undo placement for future tests
         */
        game1.getGameBoard().getSlotAtLocation(x).setPiece(null, null);
        game1.getGameBoard().getSlotAtLocation(y).setPiece(null, null);
        game1.getGameBoard().getSlotAtLocation(z).setPiece(null, null);
    }

    /**
     * Method: refillPieces(Game game)
     */
    @Test
    public void testRefillPieces() {
        resetFooPieces();
        assertEquals(3, game1.getPlayers().get("foo").getPieces().size());
        assertEquals(0, game1.getPlayers().get("bar").getPieces().size());

        GameModificationService.refillPieces(game1);

        assertEquals(Game.PIECES_PER_PLAYER, game1.getPlayers().get("foo").getPieces().size());
        assertEquals(Game.PIECES_PER_PLAYER, game1.getPlayers().get("bar").getPieces().size());
        assertEquals(20 - (2 * Game.PIECES_PER_PLAYER - (3)),
                game1.availablePieces().size());
    }

    /**
     * Method: additionalPoints(Board board, ChangeRequest changeRequest)
     */
    @Test
    public void testAdditionalPoints() throws IllegalMoveException {
        Player foo = game1.getPlayers().get("foo");
        assertEquals(0, foo.getScore());
        int score = GameModificationService.additionalPoints(
                game1.getGameBoard(),
                game1Request1, "foo");

        assertEquals(9, score);

        Location negLoc = new LocationImpl(3, 1);

        game1.getGameBoard().getSlotAtLocation(negLoc).setCard(new CardImpl
                (Card.CARD_TYPE.NEGATIVE_WORD, "", 1));

        score = GameModificationService.additionalPoints(
                game1.getGameBoard(),
                game1Request1, "foo");

        assertEquals(-9, score);

        /**
         * Make sure that double letters/words only count for the person that
         * lands on it
         */
        Location doubleLocation = new LocationImpl(1, 1);

        Slot oldS = game1.getGameBoard().getSlotAtLocation(doubleLocation);

        Slot s = new SlotImpl(Slot.SLOT_TYPE.FACTOR_LETTER, 2);

        s.setPiece(oldS.getPiece(), s.getOwner());

        game1.getGameBoard().setSlotAtLocation(doubleLocation, s);

        score = GameModificationService.additionalPoints(
                game1.getGameBoard(),
                game1Request1, "foo");

        assertEquals(-9, score);

        /**
         * Test factor letters
         */

        Game game2 = new GameImpl();
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                game2.getGameBoard().setSlotAtLocation(new LocationImpl(i, j),
                        new SlotImpl(Slot.SLOT_TYPE.REGULAR, 1));
            }
        }
        game2.getGameBoard().setSlotAtLocation(new LocationImpl(0, 0),
                new SlotImpl(Slot.SLOT_TYPE.FACTOR_LETTER, 10));
        game2.addPlayer("p", "p", "p");
        ChangeRequest pChange = new ChangeRequestImpl(game2, "p");
        pChange.addToBoard(new LocationImpl(0, 0),
                new ScrabblePieceImpl("a", 10));

        assertEquals(100, GameModificationService.additionalPoints(game2
                .getGameBoard(), pChange, "p"));

        /**
         * Test factor words
         */
        game2.getGameBoard().setSlotAtLocation(new LocationImpl(1, 0),
                new SlotImpl(Slot.SLOT_TYPE.FACTOR_WORD, 10));

        pChange.addToBoard(new LocationImpl(1, 0), new ScrabblePieceImpl("b",
                10));

        assertEquals(1100, GameModificationService.additionalPoints(game2.getGameBoard(),
                pChange, "p"));
    }

    @Test
    public void testPurchaseCard() {
        game1.addPlayer("bar", "bar", "bar");
        Player p = game1.getPlayers().get("bar");

        assertEquals(0, p.getCards().get(Card.CARD_TYPE.NEGATIVE_WORD).intValue());

        Card c = new CardImpl(Card.CARD_TYPE.NEGATIVE_WORD, "neg", 5);
        Set<Card> sc = new HashSet<Card>(1);
        sc.add(c);

        game1.setAvailableCards(sc);

        GameModificationService.purchaseCard(game1, "bar",
                Card.CARD_TYPE.NEGATIVE_WORD);

        assertEquals(1, p.getCards().get(Card.CARD_TYPE.NEGATIVE_WORD).intValue());

        assertEquals(-5, p.getScore());
    }
}
