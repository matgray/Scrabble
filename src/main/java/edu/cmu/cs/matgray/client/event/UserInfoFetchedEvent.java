/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.GwtEvent;
import edu.cmu.cs.matgray.shared.interfaces.UserInfo;

/**
 * Event for when a user's information is read from the CMU LDAP server
 */
public class UserInfoFetchedEvent extends GwtEvent<UserInfoFetchedEventHandler> {
    public static final Type<UserInfoFetchedEventHandler> TYPE =
            new Type<UserInfoFetchedEventHandler>();

    private UserInfo info;

    public UserInfoFetchedEvent(UserInfo info) {
        this.info = info;
    }

    public UserInfo getUserInfo() {
        return this.info;
    }

    @Override
    public Type<UserInfoFetchedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UserInfoFetchedEventHandler handler) {
        handler.onUserInfoRetrieved(this);
    }
}