/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * WildScrabblePiece Tester.
 *
 * @author Mathew Gray
 */
public class WildScrabblePieceTest {

    WildScrabblePiece wp;

    @Before
    public void before() throws Exception {
        wp = new WildScrabblePiece(99);
    }

    /**
     * Method: setLetter(String letter)
     */
    @Test
    public void testSetLetter() throws Exception {
        wp.setLetter("h");
        assertSame("h", wp.getLetter());
        assertEquals(99, wp.getValue());
    }

    /**
     * Method: getLetter()
     */
    @Test
    public void testGetLetter() throws Exception {
        wp.setLetter("i");
        assertSame("i", wp.getLetter());
    }

    /**
     * Method: getValue()
     */
    @Test
    public void testGetValue() throws Exception {
        WildScrabblePiece wp2 = new WildScrabblePiece(123);
        assertEquals(123, wp2.getValue());
    }

    /**
     * Method: isSet()
     */
    @Test
    public void testIsSet() throws Exception {
        wp.setLetter("a");
        assertTrue(wp.isSet());

        WildScrabblePiece wp2 = new WildScrabblePiece(123);

        assertFalse(wp2.isSet());
    }

    /**
     * Method: reset()
     */
    @Test
    public void testReset() throws Exception {
        WildScrabblePiece wp2 = new WildScrabblePiece(123);
        wp2.setLetter("t");
        assertTrue(wp2.isSet());
        wp2.reset();
        assertFalse(wp2.isSet());
        assertSame("_", wp.getLetter());
    }

    /**
     * Method: equals(Object o)
     */
    @Test
    public void testEquals() throws Exception {
        wp.reset();
        WildScrabblePiece wp2 = new WildScrabblePiece(99);
        assertTrue(wp.equals(wp2));
        wp.setLetter("a");
        assertFalse(wp.equals(wp2));
    }
}
