/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.cmu.cs.matgray.shared.interfaces.ScrabblePiece;

public class WildScrabblePiece implements ScrabblePiece {

    public WildScrabblePiece() {
    }

    int value;
    String letter = null;

    public WildScrabblePiece(int value) {
        this.value = value;
    }

    public boolean setLetter(String letter) {
        if (letter == null) {
            this.letter = null;
            return false;
        }
        letter = letter.toLowerCase();
        if (letter.matches("[a-z]")) {
            this.letter = letter;
            return true;
        }
        this.letter = null;
        return false;
    }

    @Override
    public String getLetter() {
        if (letter == null) {
            return "_";
        } else {
            return this.letter;
        }
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
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

    public boolean isSet() {
        return !(letter == null);
    }

    public void reset() {
        this.letter = null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WildScrabblePiece) {
            WildScrabblePiece scrabblePiece = (WildScrabblePiece) o;
            return this.getValue() == scrabblePiece.getValue() &&
                    this.getLetter().equals(scrabblePiece.getLetter());
        }
        return false;
    }
}
