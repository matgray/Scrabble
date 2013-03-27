/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.interfaces.UserInfo;

public class UserInfoImpl implements UserInfo {

    public UserInfoImpl() {
    }

    String name = null;
    String department = null;

    public UserInfoImpl(String name, String department) {
        this.name = name;
        this.department = department;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDepartment() {
        return this.department;
    }
}
