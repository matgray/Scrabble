/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import com.google.gwt.safehtml.shared.SafeHtml;

import java.io.Serializable;

public interface ScrabblePiece extends Serializable {

    /**
     * Each scrabble piece is a letter
     *
     * @return the letter of the scrabble piece
     */
    public String getLetter();

    public int getValue();

    public SafeHtml getHTML();
}
