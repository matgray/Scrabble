/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import java.io.Serializable;

public interface UserInfo extends Serializable {
    String getName();

    String getDepartment();
}
