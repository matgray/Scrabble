/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.cmu.cs.matgray.shared.interfaces.ScrabblePiece;

public class ScrabblePieceImpl implements ScrabblePiece {
    private String letter;
    private int value;


    /**
     * Initialize a new scrabble piece
     *
     * @param letter the letter of the piece
     * @param value  the value of the letter
     */
    public ScrabblePieceImpl(String letter, int value) {
        this.letter = letter;
        this.value = value;
    }

    public ScrabblePieceImpl() {
    }

    @Override
    public String getLetter() {
        return this.letter;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScrabblePieceImpl) {
            ScrabblePieceImpl scrabblePiece = (ScrabblePieceImpl) o;
            return this.getValue() == scrabblePiece.getValue() && this.getLetter()
                    .equals
                            (scrabblePiece.getLetter());
        }
        return false;
    }

    public SafeHtml getHTML() {
        final String letter = this.getLetter();
        final int value = this.getValue();
        return new SafeHtml() {
            @Override
            public String asString() {
                return "<div style=\"position: relative; width: 100%;\">" +
                        letter +
                        "<div style=\"" +
                        "position: absolute;" +
                        "top: 8px;" +
                        "right: -5px;" +
                        "width: 100%;" +
                        "font-size:50%;" +
                        "text-align:right;\"><br>" +
                        +value +
                        "</div></div>";
            }
        };
    }
}
